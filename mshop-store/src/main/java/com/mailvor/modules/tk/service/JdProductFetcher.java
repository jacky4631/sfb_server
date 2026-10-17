package com.mailvor.modules.tk.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.http.HtmlUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 京东商品标题抓取
 *
 * 历史说明：原方案为带登录态 Cookie（thor）请求 PC 端 item.jd.com 并解析 <title>。
 * 2026-10 京东 PC 商品页改为客户端渲染 SPA 空壳，<title> 恒为首页通用标题，方案失效；
 * 现改为请求移动端 item.m.jd.com/product/{skuId}.html，服务端渲染、无需 Cookie，
 * 从页面内嵌 JSON 取 skuName，取不到再回退解析 <title>。
 */
@Slf4j
public class JdProductFetcher {

    /** 从任意京东链接中提取数字 skuId，如 item.jd.com/100307412176.html */
    private static final Pattern SKU_ID_PATTERN = Pattern.compile("(\\d{6,})\\.html");

    /** 移动端商品页内嵌 JSON 的 skuName 字段 */
    private static final Pattern SKU_NAME_PATTERN = Pattern.compile("skuName\":\"(.*?)\"");

    /** 分享文案里被「」/“”包裹的商品标题，如 「【鱼油NO.1】斯维诗4倍深海鱼油」 */
    private static final Pattern SHARE_TITLE_PATTERN = Pattern.compile("[「“]([^」”]+)[」”]");

    /** 京东分享短链，如 https://3.jd.hk/10-3zJkl、https://u.jd.com/xxxxx */
    private static final Pattern SHORT_LINK_PATTERN = Pattern.compile(
            "https?://(?:3\\.jd\\.hk|u\\.jd\\.com|3\\.cn|s\\.cn|short\\.m\\.jd\\.com)/\\S+");

    /** 移动端 UA，京东页面会校验 UA */
    private static final String MOBILE_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) "
            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1";

    public static String getTitle(String url) {
        if (url == null || url.trim().isEmpty()) return null;
        String title = extractShareTitle(url);
        if(title == null) {
            title = fetchHtml(url);
        }
        if(title != null) {
            // 由于使用title直接查询会非常不准，目前测试下来，只取前15位字符比较准
            if(title.length() > 30) {
                title = title.substring(0, 20);
            } else if(title.length() > 15) {
                title = title.substring(0, 15);
            }
        }
        return title;
    }

    /**
     * 抽取京东分享文案里「」/“”包裹的商品标题，并去掉开头的中文括号营销标签（如【鱼油NO.1】）。
     * 抽取不到标题时返回完整内容，由调用方直接作为关键词使用。
     */
    public static String extractShareTitle(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        Matcher matcher = SHARE_TITLE_PATTERN.matcher(content);
        if (!matcher.find()) {
            return null;
        }
        String title = matcher.group(1).trim();
        if (title.isEmpty()) {
            return null;
        }
        // 去掉开头的中文括号营销标签，如【鱼油NO.1】
        title = title.replaceFirst("^(【[^】]*】)+", "").trim();
        return title.isEmpty() ? null : title;
    }

    /**
     * 请求移动端商品页 HTML，解析商品标题
     */
    private static String fetchHtml(String url) {
        String skuId = resolveSkuId(url);
        if (skuId == null) {
            log.warn("无法从链接中解析京东 skuId: {}", url);
            return null;
        }
        String mobileUrl = "https://item.m.jd.com/product/" + skuId + ".html";
        try (HttpResponse response = HttpRequest.get(mobileUrl)
                .setFollowRedirects(true)
                .header("User-Agent", MOBILE_UA)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Referer", mobileUrl)
                .timeout(5000)
                .execute()) {

            if (response.getStatus() == HttpStatus.HTTP_OK) {
                String html = response.body();
                return parseProductInfo(html);
            } else {
                log.warn("京东移动端商品页请求失败，状态码：{}，url：{}", response.getStatus(), mobileUrl);
            }
        } catch (Exception e) {
            log.error("京东商品标题抓取失败，url：{}", mobileUrl, e);
        }
        return null;
    }

    /**
     * 解析 skuId：先直接从长链/纯数字里提；提不到再当分享短链处理，
     * 跟随 302 重定向，从最终落地地址（带 xxx.html）里提取 skuId。
     */
    private static String resolveSkuId(String content) {
        String skuId = extractSkuId(content);
        if (skuId != null) {
            return skuId;
        }
        Matcher matcher = SHORT_LINK_PATTERN.matcher(content);
        if (matcher.find()) {
            return resolveSkuIdByRedirect(matcher.group());
        }
        return null;
    }

    /**
     * 跟随短链 302 重定向，逐跳从当前 URL 提取 skuId（落地地址带 xxx.html 即为真实商品）。
     * 注意：只从 URL 提取，绝不从响应 body 正则提 skuId（商品页 body 内大量推荐位数字会误抓）。
     */
    private static String resolveSkuIdByRedirect(String shortUrl) {
        String current = shortUrl;
        for (int i = 0; i < 5 && current != null && !current.isEmpty(); i++) {
            String skuId = extractSkuId(current);
            if (skuId != null) {
                return skuId;
            }
            try (HttpResponse response = HttpRequest.get(current)
                    .setFollowRedirects(false)
                    .header("User-Agent", MOBILE_UA)
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .timeout(5000)
                    .execute()) {
                int status = response.getStatus();
                if (status >= 300 && status < 400) {
                    current = toAbsoluteUrl(current, response.header("Location"));
                } else {
                    return null;
                }
            } catch (Exception e) {
                log.error("京东短链重定向解析失败，url：{}", current, e);
                return null;
            }
        }
        return null;
    }

    /** 把可能为相对路径的 Location 补全为绝对 URL */
    private static String toAbsoluteUrl(String base, String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }
        if (location.startsWith("http://") || location.startsWith("https://")) {
            return location;
        }
        if (location.startsWith("/")) {
            Matcher m = Pattern.compile("^(https?://[^/]+)").matcher(base);
            return m.find() ? m.group(1) + location : null;
        }
        int idx = base.lastIndexOf('/');
        return idx > 0 ? base.substring(0, idx + 1) + location : location;
    }

    /**
     * 从链接中提取数字 skuId（长链 xxx.html 或用户直粘的纯数字）
     */
    private static String extractSkuId(String url) {
        if (url == null) {
            return null;
        }
        Matcher matcher = SKU_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // 纯数字串（用户直接粘 skuId）
        if (url.matches("\\d{6,}")) {
            return url;
        }
        return null;
    }

    /**
     * 优先取内嵌 JSON 的 skuName（无后缀），取不到再解析 <title>
     */
    private static String parseProductInfo(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        Matcher matcher = SKU_NAME_PATTERN.matcher(html);
        if (matcher.find() && !matcher.group(1).isEmpty()) {
            return matcher.group(1);
        }
        Matcher titleMatcher = Pattern.compile("<title[^>]*>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(html);
        String title = titleMatcher.find() ? HtmlUtil.unescape(titleMatcher.group(1)) : null;
        if (title != null) {
            return title.replace("【图片 价格 品牌 评论】-京东", "")
                    .replace("【图片 价格 品牌 报价】-京东", "")
                    .replace("【行情 报价 价格 评测】-京东", "")
                    .trim();
        }
        return null;
    }
}
