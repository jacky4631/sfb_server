package com.mailvor.modules.tk.util;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.tk.vo.DySearchVO;
import com.mailvor.modules.tk.vo.GoodsParseVo;
import com.mailvor.modules.tk.vo.TkParseVO;
import com.mailvor.modules.tk.vo.jd.JdKuGoodsDetailVO;
import com.mailvor.modules.tk.vo.pdd.PddSearchVO;
import com.mailvor.modules.tk.vo.vip.VipGoodsDetailVO;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Link util.
 * @author Administrator
 */
public class LinkUtil {

    private static final String tbLongUrlReg = "var\\s+url\\s*=\\s*['\"]([^'\"]+)['\"]";

    private static Pattern tbLongUrlPattern = Pattern.compile(tbLongUrlReg);

    private static Pattern tbShortUrlPattern = Pattern.compile("https://[^ \t\n\f\r\"']+");
    private static Pattern vipShortUrlPattern = Pattern.compile("https://[^\\s]+");


    private static String extractVipShortUrl(String tbCnText) {
        // 正则表达式：匹配以https开头的URL，直到空格或标点符号结束
        Matcher matcher = vipShortUrlPattern.matcher(tbCnText);

        if (matcher.find()) {
            return matcher.group(); // 返回匹配到的完整URL
        }
        return null;
    }
    private static String extractShortUrl(String tbCnText) {
        // 正则表达式：匹配以https开头的URL，直到空格或标点符号结束
        Matcher matcher = tbShortUrlPattern.matcher(tbCnText);

        if (matcher.find()) {
            return matcher.group(); // 返回匹配到的完整URL
        }
        return null;
    }
    public static String covertToLongUrl(String tbCnText) {
        String shortUrl = extractShortUrl(tbCnText);
        Matcher matcher = tbLongUrlPattern.matcher(convertToOrigUrl(shortUrl));

        if (matcher.find()) {
            // 提取第1个捕获组的内容（即 URL 部分）
            return matcher.group(1);
        }
        return null;
    }
    public static String covertToVipLongUrl(String vipShortUrl) {
        String shortUrl = extractVipShortUrl(vipShortUrl);
        return convertToOrigUrl(shortUrl);
    }

    private static boolean isRedirect(int statusCode) {
        return statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307 || statusCode == 308;
    }

    private static String convertToOrigUrl(String shortUrl) {
        // 模拟浏览器请求头（关键：避免被淘宝反爬拦截）
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("Connection", "keep-alive");

        HttpURLConnection conn = null;
        try {
            URL url = new URL(shortUrl);
            conn = (HttpURLConnection) url.openConnection();

            // 设置请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // 禁用自动重定向，手动处理
            conn.setInstanceFollowRedirects(false);

            int statusCode = conn.getResponseCode();
            if (isRedirect(statusCode)) {
                // 从响应头获取重定向URL
                String location = conn.getHeaderField("Location");
                if (location != null) {
                    return location; // 长链接
                }
            } else if (statusCode == 200) {
                // 直接读取响应体（适用于部分非重定向场景）
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            }
            return null;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    public static TkParseVO parseDyKuCmsDetail(DySearchVO detail, String goodsId, String origContent, String buyLink) {
        return TkParseVO.builder()
                .img(detail.getImg())
                .title(detail.getTitle())
                .endPrice(detail.getEndPrice())
                .startPrice(detail.getStartPrice())
                .feeRatio(detail.getFeeRatio())
                .itemUrl(buyLink)
                .platType(4)
                .originContent(origContent)
                .goodsId(goodsId)
                .coupon(detail.getStartPrice() - detail.getEndPrice())
                .sales(detail.getSales())
                .build();
    }
    public static TkParseVO parseJdKuDetail(JdKuGoodsDetailVO detail, String origContent) {
        return TkParseVO.builder()
                .img(detail.getImg())
                .title(detail.getTitle())
                .endPrice(detail.getEndPrice())
                .startPrice(detail.getStartPrice())
                .feeRatio(detail.getFeeRatio())
                .platType(2)
                .originContent(origContent)
                .goodsId(detail.getGoodsId())
                .coupon(detail.getStartPrice() - detail.getEndPrice())
                .sales(detail.getSales())
                .build();
    }
    public static String parseDyLink(String dyLink) throws UnsupportedEncodingException {
        if(StringUtils.isBlank(dyLink)){
            return null;
        }
        dyLink = URLDecoder.decode(dyLink, "UTF-8");
        String dyAddiStr = dyLink.substring(dyLink.indexOf("request_additions=") + "request_additions=".length());
        JSONObject dyAddi = JSON.parseObject(dyAddiStr);
        String goodsId = dyAddi.getString("product_id");
        return goodsId;
    }

    public static TkParseVO parsePddDetail(PddSearchVO detail, String origContent) {
        return TkParseVO.builder()
                .img(detail.getImg())
                .title(detail.getTitle())
                .endPrice(detail.getCoupon() != null ? (detail.getEndPrice() - detail.getCoupon()) : detail.getEndPrice())
                .startPrice(detail.getStartPrice())
                .feeRatio(detail.getFeeRatio())
//                .itemUrl(detail.get)
                .platType(3)
                .originContent(origContent)
                .goodsId(detail.getGoodsId())
                .coupon(detail.getCoupon())
                .sales(detail.getSales())
                .build();
    }
    public static TkParseVO parseDtkGoodsParse(GoodsParseVo detail, String origContent) {
        // 券后价格目前默认是 endPrice-coupon，但是有时候会变成负数，这时候直接使用endPrice
        return TkParseVO.builder()
                .img(detail.getMainPic())
                .title(detail.getOriginInfo().getTitle())
                .endPrice(detail.getRealPostFee())
                .startPrice(detail.getRealPostFee())
                .feeRatio(Double.parseDouble(detail.getCommissionRate()))
                .itemUrl(detail.getOriginUrl())
                .platType(1)
                .originContent(origContent)
                .goodsId(detail.getGoodsId())
                .coupon(0.00)
                .sales("0")
                .shortUrl(detail.getShortUrl())
                .shortPwd(detail.getShortTpwd())
                .build();
    }
    public static JSONObject parseDyDetail(JSONObject dyDetail, String goodsId, String origContent, String dyPassword) {
        JSONObject dyData = new JSONObject();
        dyData.put("parseStatus", 3);
        dyData.put("originContent", origContent);
        dyData.put("platType", "dy");
        dyData.put("itemId", goodsId);
        dyData.put("itemLink", dyDetail.getString("detailUrl"));
        dyData.put("itemName", dyDetail.getString("title"));
        dyData.put("mainPic", dyDetail.getString("cover"));
        dyData.put("originalPrice", dyDetail.getDouble("price") + dyDetail.getDouble("couponPrice"));
        dyData.put("actualPrice", dyDetail.getDouble("price"));
        dyData.put("couponPrice", dyDetail.getDouble("couponPrice"));
        dyData.put("commissionRate", dyDetail.getDouble("cosRatio")/100);
        dyData.put("commissionAmount", dyDetail.getDouble("cosFee"));
        dyData.put("parsedContent", dyPassword);
        return dyData;
    }
    public static String parseVipLink(String vipLink) {
        if(StringUtils.isBlank(vipLink)){
            return null;
        }
        if(vipLink.contains("mst.vip.com")) {
            String parseContent = ReUtil.get("goodsId=\\d{10,25}", vipLink, 0);
            return parseContent.split("=")[1];
        } else {
            String url = vipLink.split("\\.html\\?")[0];
            return url.substring(url.lastIndexOf("-") + 1);
        }

    }


    public static JSONObject parseVipDetail(VipGoodsDetailVO vipDetail, String goodsId, String origContent, String buyLink) {
        JSONObject dyData = new JSONObject();
        Double origPrice = vipDetail.getStartPrice();
        Double actPrice = vipDetail.getEndPrice();
        dyData.put("parseStatus", 3);
        dyData.put("originContent", origContent);
        dyData.put("platType", "vip");
        dyData.put("itemId", goodsId);
        dyData.put("itemLink", vipDetail.getDestUrl());
        dyData.put("itemName", vipDetail.getTitle());
        dyData.put("mainPic", vipDetail.getImg());
        dyData.put("originalPrice", origPrice);
        dyData.put("actualPrice", actPrice);
        dyData.put("couponPrice", origPrice - actPrice);
        dyData.put("commissionRate", vipDetail.getFeeRatio()/100);
        dyData.put("commissionAmount", vipDetail.getFee());
        dyData.put("parsedContent", buyLink);
        return dyData;
    }


    public static TkParseVO parseVipKuDetail(VipGoodsDetailVO detail, String goodsId, String origContent, String buyLink) {
        return TkParseVO.builder()
                .img(detail.getImg())
                .title(detail.getTitle())
                .endPrice(detail.getEndPrice())
                .startPrice(detail.getStartPrice())
                .feeRatio(detail.getFeeRatio())
                .itemUrl(buyLink)
                .platType(5)
                .originContent(origContent)
                .goodsId(goodsId)
                .coupon(detail.getStartPrice() - detail.getEndPrice())
                .sales(detail.getSales())
                .build();
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        String pa = LinkUtil.parseDyLink("snssdk1128://ec_goods_detail/?promotion_id=3495731132006296371&kol_id=99514375927&source_page=outer_product_share&request_additions=%7B%22enter_from%22%3A%22outer_product_share%22%2C%22sec_author_id%22%3A%22MS4wLjABAAAA2I9NdgAKZrz9e0tLm1csyDMNqLESPDm34TdYYqXe8-I%22%2C%22cps_track%22%3A%22ClMI972T3PICEOgIGihkeV8xMDcwNTYxNDMyMDQ3MzA4MTQ3NTJfMzczN18xODQ3Mzk1OTYyIg43NzMwMjZfMjhfMDIwMDAAOgBCB01idEs1SDFIABIMCISHieT_9Y-3MBAC%22%2C%22product_id%22%3A3489796786346935172%7D");
        System.out.println(pa);
        pa = LinkUtil.parseVipLink("https://m.vip.com/product-1710615488-6919190883148094528.html?nmsns=shop_iphone-7.81.2-link&nst=product&nsbc=&nct=link&ncid=077f877d71a19ee0dd096be34e0d3a3074df66ed&nabtid=13&nuid=&nchl_param=share:077f877d71a19ee0dd096be34e0d3a3074df66ed:1667057660560&mars_cid_a=077f877d71a19ee0dd096be34e0d3a3074df66ed&chl_type=share");
        System.out.println(pa);
        String content = "https://mst.vip.com/x6M88WQc9Abq5h0CHNg1uw.php?wapid=mst_100099181&amp;_src=mst&amp;extra_banner=115099181&amp;nova=1&amp;nova_platform=1&amp;mst_page_type=guide&amp;goodsId=6920488271067019663&amp;brandId=1712388815&amp;actType=normal&amp;launchId=&amp;nmsns=shop_android-9.7.6-link&amp;nst=product&amp;nsbc=&amp;nct=link&amp;ncid=0cab4d50-cf65-343b-8f94-1d56adb43b0d&amp;nabtid=13&amp;nuid=64484205&amp;nchl_param=share:0cab4d50-cf65-343b-8f94-1d56adb43b0d:1693994114511&amp;mars_cid_a=0cab4d50-cf65-343b-8f94-1d56adb43b0d&amp;chl_type=share";
        String goodsId = ReUtil.get("goodsId=\\d{15,25}", content, 0);
        System.out.println(goodsId);
    }

}
