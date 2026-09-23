/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtk.api.client.DtkApiClient;
import com.dtk.api.request.mastertool.*;
import com.dtk.api.request.putstorage.DtkGoodsDetailsRequest;
import com.dtk.api.request.putstorage.DtkGoodsListRequest;
import com.dtk.api.request.putstorage.DtkJdCommodityDetailsRequest;
import com.dtk.api.request.putstorage.DtkPddGoodsDetailsRequest;
import com.dtk.api.request.search.DtkGetDtkSearchGoodsRequest;
import com.dtk.api.request.search.DtkPddOrderIncrementSearchRequest;
import com.dtk.api.request.special.*;
import com.dtk.api.utils.SignMd5Util;
import com.mailvor.modules.tk.config.DataokeConfig;
import com.mailvor.modules.tk.config.JdConfig;
import com.mailvor.modules.tk.config.PddConfig;
import com.mailvor.modules.tk.param.*;
import com.mailvor.modules.tk.util.DtkResponseConverter;
import com.mailvor.modules.tk.vo.*;
import com.mailvor.modules.tk.vo.pdd.PddSearchDataVO;
import com.mailvor.modules.tk.vo.pdd.PddSearchListVO;
import com.mailvor.modules.tk.vo.vip.VipGoodsDetailDataVo;
import com.mailvor.modules.tk.vo.vip.VipGoodsDetailVO;
import com.mailvor.modules.tk.vo.vip.VipSearchListVO;
import com.mailvor.modules.tk.vo.vip.VipWordCodeVO;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mailvor.modules.utils.TkUtil.EXCLUDE_KEY_WROD_LIST;
import static com.mailvor.modules.utils.TkUtil.hasWord;

/**
 * 大淘客服务 - 基于新SDK实现
 */
@Slf4j
@Component
public class DataokeService {
    private static TypeReference<DataokeResVo<GoodsParseVo>> goodsParseTypeRef =
        new TypeReference<DataokeResVo<GoodsParseVo>>(GoodsParseVo.class){};

    private String regStr = "((http|https)://)([\\w-]+\\.)+[\\w$]+(\\/[\\w-?=&./]*)?";
    private Pattern pattern = Pattern.compile(regStr);

    @Resource
    private DataokeConfig config;

    @Resource
    private DtkApiClient dtkApiClient;

    @Resource
    private PddConfig pddConfig;

    @Resource
    private JdConfig jdConfig;

    @Resource
    private PddService pddService;

    // ==================== 商品列表 ====================

    public JSONObject goodsList(GoodsListParam param) {
        DtkGoodsListRequest request = new DtkGoodsListRequest();
        if (param.getPageId() != null) request.setPageId(param.getPageId().toString());
        if (param.getPageSize() != null) request.setPageSize(param.getPageSize());
        if (param.getSort() != null) request.setSort(param.getSort());
        if (param.getCids() != null) request.setCids(param.getCids());
        if (param.getSubcid() != null) {
            try { request.setSubcid(Integer.parseInt(param.getSubcid())); }
            catch (NumberFormatException e) { log.warn("subcid转换失败: {}", param.getSubcid()); }
        }
        if (param.getPriceLowerLimit() != null) request.setPriceLowerLimit(new java.math.BigDecimal(param.getPriceLowerLimit()));
        if (param.getPriceUpperLimit() != null) request.setPriceUpperLimit(new java.math.BigDecimal(param.getPriceUpperLimit()));
        if (param.getCouponPriceLowerLimit() != null) request.setCouponPriceLowerLimit(new java.math.BigDecimal(param.getCouponPriceLowerLimit()));
        if (param.getCommissionRateLowerLimit() != null) request.setCommissionRateLowerLimit(new java.math.BigDecimal(param.getCommissionRateLowerLimit()));

        JSONObject jsonObject = DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
        // 过滤敏感词
        if (jsonObject.containsKey("data") && jsonObject.getJSONObject("data").containsKey("list")) {
            JSONArray resData = jsonObject.getJSONObject("data").getJSONArray("list");
            List list = resData.stream().filter(e -> {
                JSONObject obj = (JSONObject) e;
                return !hasWord(obj.getString("dtitle"));
            }).collect(Collectors.toList());
            jsonObject.getJSONObject("data").put("list", list);
        }
        return jsonObject;
    }

    public DataokeResVo<GoodsListVo> goodsVOS(GoodsListParam param) {
        JSONObject result = goodsList(param);
        try {
            return JSON.parseObject(JSON.toJSONString(result),
                new TypeReference<DataokeResVo<GoodsListVo>>(GoodsListVo.class){});
        } catch (Exception e) {
            log.error("goodsVOS转换失败", e);
        }
        return null;
    }

    // ==================== 商品搜索 ====================

    public JSONObject goodsSearch(GoodsSearchParam param) {
        String keyWord = param.getKeyWords().toLowerCase();
        if (TkUtil.hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(param.getKeyWords().toLowerCase())) {
            JSONObject res = new JSONObject();
            res.put("data", new JSONArray());
            return res;
        }
        DtkGetDtkSearchGoodsRequest request = new DtkGetDtkSearchGoodsRequest();
        request.setKeyWords(param.getKeyWords());
        if (param.getPageNo() != null) request.setPageId(param.getPageNo().toString());
        if (param.getPageSize() != null) request.setPageSize(param.getPageSize());
        if (param.getSort() != null) request.setSort(param.getSort());
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 商品详情 ====================

    public JSONObject goodsDetail(String goodsId) {
        DtkGoodsDetailsRequest request = new DtkGoodsDetailsRequest();
        request.setGoodsId(goodsId);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 高效转链 ====================

    public JSONObject goodsWord(String goodsId, String pid, String channelId) {
        DtkGetPrivilegeLinkRequest request = new DtkGetPrivilegeLinkRequest();
        request.setGoodsId(goodsId);
        if (StringUtils.isNotBlank(pid)) request.setPid(pid);
        if (StringUtils.isNotBlank(channelId)) request.setChannelId(channelId);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 万能解析 ====================

    public DataokeResVo<GoodsParseVo> goodsParse(String content, String pid, String channelId) {
        DtkParseContentRequest request = new DtkParseContentRequest();
        request.setContent(content);
        JSONObject jsonObject = DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
        return JSON.parseObject(JSON.toJSONString(jsonObject), goodsParseTypeRef);
    }

    // ==================== 超级分类 ====================

    public JSONObject getCategory() {
        DtkGetSuperCategoryRequest request = new DtkGetSuperCategoryRequest();
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 商品评论 ====================

    public JSONObject getCommentList(GoodsCommentParam param) {
        DtkGoodsCommentListRequest request = new DtkGoodsCommentListRequest();
        if (param.getGoodsId() != null) request.setGoodsId(param.getGoodsId());
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 专题/轮播/活动 ====================

    public JSONObject getTopic() {
        DtkActivityCatalogueRequest request = new DtkActivityCatalogueRequest();
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    public JSONObject getBanner() {
        DtkCarouselMapResponseRequest request = new DtkCarouselMapResponseRequest();
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    public JSONObject getTbActivityList(TbActivityListParam param) {
        DtkGetTbTopicListRequest request = new DtkGetTbTopicListRequest();
        if (param.getType() != null) request.setType(param.getType());
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    public JSONObject parseTbActivity(TbActivityParseParam param) {
        DtkActivityLinkRequest request = new DtkActivityLinkRequest();
        request.setPromotionSceneId(param.getPromotionSceneId());
        if (StringUtils.isNotBlank(param.getPid())) request.setPid(param.getPid());
        if (StringUtils.isNotBlank(param.getRelationId())) request.setRelationId(param.getRelationId());
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 剪切板解析 ====================

    public JSONObject parseContent(ParseContentParam param) {
        if (param.getContent() != null && param.getContent().contains("yangkeduo.com")) {
            try {
                param.setContent(parsePddContent(param.getContent()));
            } catch (UnsupportedEncodingException e) {
                log.error("解析PDD内容失败", e);
            }
        }
        DtkParseContentRequest request = new DtkParseContentRequest();
        request.setContent(param.getContent());
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    protected String parsePddContent(String content) throws UnsupportedEncodingException {
        int goodsIdIndex = content.lastIndexOf("&amp;goods_id=");
        if(goodsIdIndex > 0) {
            content = URLDecoder.decode(content, "utf-8").replace("&amp;", "&");
            String goodsIdParam = content.substring(content.lastIndexOf("&goods_id=")).replace("&", "");
            content = content.replace(".html?", ".html?" + goodsIdParam + "&");
        }
        return content;
    }

    // ==================== 相似商品 ====================

    public JSONObject goodsSimilarList(String id, String size) {
        DtkListSimilerGoodsByOpenRequest request = new DtkListSimilerGoodsByOpenRequest();
        request.setId(Integer.parseInt(id));
        request.setSize(Integer.parseInt(size));
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 咚咚抢 ====================

    public JSONObject ddq(String roundTime) {
        DtkDdqGoodsListRequest request = new DtkDdqGoodsListRequest();
        if (StringUtils.isNotBlank(roundTime)) request.setRoundTime(roundTime);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 排行榜 ====================

    public JSONObject rankingList(RankingListParam param) {
        DtkGetRankingListRequest request = new DtkGetRankingListRequest();
        if (param.getRankType() != null) request.setRankType(param.getRankType());
        if (param.getPageId() != null) request.setPageId(param.getPageId().toString());
        if (param.getPageSize() != null) request.setPageSize(param.getPageSize());

        JSONObject jsonObject = DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
        JSONArray resData = normalizeArray(jsonObject.get("data"));
        if(resData == null) {
            log.warn("大淘客榜单 data 节点异常, request={}", JSON.toJSONString(param));
            jsonObject.put("data", new JSONArray());
            return jsonObject;
        }
        JSONArray list = new JSONArray();
        list.addAll(resData.stream()
                .filter(JSONObject.class::isInstance)
                .map(JSONObject.class::cast)
                .filter(obj -> !hasWord(obj.getString("title")))
                .collect(Collectors.toList()));
        jsonObject.put("data", list);
        return jsonObject;
    }

    private JSONArray normalizeArray(Object data) {
        if(data instanceof JSONArray) return (JSONArray) data;
        if(data == null) return null;
        if(data instanceof JSONObject) {
            JSONArray array = new JSONArray();
            array.add(data);
            return array;
        }
        try {
            return JSON.parseArray(JSON.toJSONString(data));
        } catch (Exception e) {
            log.warn("转换数组节点失败: {}", data, e);
            return null;
        }
    }

    // ==================== 店铺转链 ====================

    public JSONObject shopConvert(String shopId, String shopName, String pid, String channelId) {
        DtkShopConvertRequest request = new DtkShopConvertRequest();
        request.setSellerId(shopId);
        request.setShopName(shopName);
        if (StringUtils.isNotBlank(pid)) request.setPid(pid);
        if (StringUtils.isNotBlank(channelId)) request.setRelationId(channelId);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 品牌 ====================

    public JSONObject getBrandList(Integer cid, Integer pageId) {
        DtkGetColumnListRequest request = new DtkGetColumnListRequest();
        request.setCid(cid);
        request.setPageId(pageId.toString());
        request.setPageSize(10);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    public JSONObject getBrandGoodsList(String brandId, Integer pageId, Integer pageSize) {
        DtkGetBrandGoodsListRequest request = new DtkGetBrandGoodsListRequest();
        request.setBrandId(brandId);
        request.setPageId(pageId.toString());
        request.setPageSize(pageSize);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 京东 ====================

    public JSONObject goodsDetailJD(String goodsId, String itemId) {
        DtkJdCommodityDetailsRequest request = new DtkJdCommodityDetailsRequest();
        if (StringUtils.isNotBlank(goodsId) && !"0".equals(goodsId) && !goodsId.equals(itemId)) {
            request.setSkuIds(goodsId);
        }
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    public JSONObject goodsWordJD(String itemUrl, String couponUrl, String pid) {
        DtkJdCommodityTransformLinkRequest request = new DtkJdCommodityTransformLinkRequest();
        request.setMaterialId(itemUrl);
        request.setUnionId(jdConfig.getUnionId());
        if (StringUtils.isNotBlank(couponUrl)) request.setCouponUrl(couponUrl);
        if (pid != null) {
            try { request.setPositionId(Long.parseLong(pid)); }
            catch (NumberFormatException e) { log.warn("pid转换失败: {}", pid); }
        }
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    public JSONObject parseUrlJD(String itemUrl) {
        DtkJdLinkAnalysisRequest request = new DtkJdLinkAnalysisRequest();
        request.setUrl(itemUrl);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 拼多多 ====================

    public JSONObject goodsDetailPDD(String goodsSign) {
        DtkPddGoodsDetailsRequest request = new DtkPddGoodsDetailsRequest();
        request.setGoodsSign(goodsSign);
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    public JSONObject goodsWordPDD(String goodsSign, Long uid) {
        DtkPddTransformLinkRequest request = new DtkPddTransformLinkRequest();
        request.setPid(pddConfig.getPid());
        request.setGoodsSign(goodsSign);
        if (uid != null) {
            int auth = pddService.authQuery(uid);
            if (auth == 1) request.setCustomParameters(pddConfig.getParam(uid));
        }
        return DtkResponseConverter.toFullJsonObject(dtkApiClient.execute(request));
    }

    // ==================== 唯品会 (无SDK对应接口，使用直接HTTP调用) ====================

    public VipSearchListVO goodsListVip(GoodsListVipParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        String data = executeRawApi("/vip/search-by-keywords", "v1.0.0", paraMap);
        return JSON.parseObject(data, VipSearchListVO.class);
    }

    public VipGoodsDetailDataVo goodsDetailVIP(String goodsId, String openId) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("goodsIdList", "[\"" + goodsId + "\"]");
        JSONObject request = new JSONObject();
        request.put("openId", openId);
        request.put("realCall", "true");
        paraMap.put("request", request.toJSONString());
        String data = executeRawApi("/vip/goods-detail", "v1.0.0", paraMap);
        VipGoodsDetailDataVo dataVo = JSON.parseObject(data, VipGoodsDetailDataVo.class);
        if (CollectionUtils.isNotEmpty(dataVo.getData())) {
            VipGoodsDetailVO detailVO = dataVo.getData().get(0);
            detailVO.setShopName(detailVO.getStoreName());
            dataVo.setGoods(detailVO);
            dataVo.setData(null);
        }
        return dataVo;
    }

    public VipWordCodeVO goodsWordVIP(String itemUrl, String statParam, JSONObject urlGenRequest) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("urlList", "[\"" + itemUrl + "\"]");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(statParam)) {
            paraMap.put("statParam", statParam);
        }
        paraMap.put("urlGenRequest", urlGenRequest.toJSONString());
        String data = executeRawApi("/vip/promote/link", "v1.0.0", paraMap);
        VipWordCodeVO codeVO = JSON.parseObject(data, VipWordCodeVO.class);
        if (codeVO != null && codeVO.getData() != null && CollectionUtils.isNotEmpty(codeVO.getData().getList())) {
            codeVO.setWord(codeVO.getData().getList().get(0));
            codeVO.getData().setList(null);
        }
        return codeVO;
    }

    // ==================== 拼多多搜索/分类 (无SDK对应接口) ====================

    public PddSearchListVO goodsListPdd(GoodsListPddParam param) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(param.getKeyword())) {
            String keyWord = param.getKeyword().toLowerCase();
            if (hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(keyWord)) {
                PddSearchListVO res = new PddSearchListVO();
                PddSearchDataVO data = new PddSearchDataVO();
                data.setList(new ArrayList<>(0));
                data.setTotal(0);
                res.setCode(0);
                res.setMsg("成功");
                res.setData(data);
                return res;
            }
        }
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        String data = executeRawApi("/dels/pdd/goods/search", "v2.0.0", paraMap);
        return JSON.parseObject(data, PddSearchListVO.class);
    }

    public JSONObject goodsCatePdd(Integer parentId) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("parentId", parentId.toString());
        return JSON.parseObject(executeRawApi("/dels/pdd/category/search", "v1.0.0", paraMap));
    }

    public JSONObject vipGoodsSearch(GoodsSearchVipParam param) {
        if (StringUtils.isNotBlank(param.getKeyword())) {
            String keyWord = param.getKeyword().toLowerCase();
            if (TkUtil.hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(keyWord)) {
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("goodsInfoList", new JSONArray());
                data.put("total", 0);
                res.put("data", data);
                return res;
            }
        }
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        return JSON.parseObject(executeRawApi("/vip/search-by-keywords", "v1.0.0", paraMap));
    }

    // ==================== 订单查询 ====================

    public TBResVo queryTBList(QueryTBParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        String data = executeRawApi("/tb-service/get-order-details", "v1.0.0", paraMap);
        log.warn("*淘宝订单：" + data);
        try {
            return JSON.parseObject(data, TBResVo.class);
        } catch (Exception e) {
            log.error("淘宝订单解析失败", e);
        }
        return null;
    }

    public JdResVo queryJdList(QueryJdParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        String data = executeRawApi("/dels/jd/order/get-official-order-list", "v2.0.0", paraMap);
        log.warn("*京东订单：" + data);
        if (StringUtils.isBlank(data)) return null;
        try {
            return JSON.parseObject(data, JdResVo.class);
        } catch (Exception e) {
            log.error("京东订单解析失败", e);
        }
        return null;
    }

    public VipResVo queryVipList(QueryVipParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        String data = executeRawApi("/vip/order-list", "v1.0.0", paraMap);
        log.warn("*唯品会订单：" + data);
        if (StringUtils.isBlank(data)) return null;
        try {
            return JSON.parseObject(data, VipResVo.class);
        } catch (Exception e) {
            log.error("唯品会订单解析失败", e);
        }
        return null;
    }

    // ==================== 抖音 (不同域名，使用直接HTTP调用) ====================

    private static final String DY_BASE_URL = "https://openapiv2.dataoke.com";

    public JSONObject dyGoodsSearch(GoodsSearchDyParam param) {
        if (StringUtils.isNotBlank(param.getTitle())) {
            String keyWord = param.getTitle().toLowerCase();
            if (TkUtil.hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(keyWord)) {
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("list", new JSONArray());
                data.put("total", 0);
                res.put("data", data);
                return res;
            }
        }
        param.setAppkey(config.getKey());
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        return JSON.parseObject(executeRawFullUrl(DY_BASE_URL + "/tiktok/tiktok-materials-products-search", "v1.0.0", paraMap));
    }

    public JSONObject dyGoodsDetail(String goodsId) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("productIds", goodsId);
        paraMap.put("appkey", config.getKey());
        return JSON.parseObject(executeRawFullUrl(DY_BASE_URL + "/tiktok/tiktok-materials-products-details", "v1.0.0", paraMap));
    }

    public JSONObject dyWord(String productUrl, String externalInfo) {
        if (productUrl.contains("pick_source")) {
            productUrl = productUrl.substring(0, productUrl.lastIndexOf("&"));
        }
        if (!productUrl.startsWith("http")) {
            Matcher matcher = pattern.matcher(productUrl);
            if (matcher.find()) {
                productUrl = matcher.group(0);
            } else {
                return new JSONObject();
            }
        }
        int splitIndex = productUrl.indexOf("&");
        if (splitIndex > 0) {
            productUrl = productUrl.substring(0, splitIndex);
        }
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("productUrl", productUrl);
        paraMap.put("externalInfo", externalInfo);
        return JSON.parseObject(executeRawFullUrl(DY_BASE_URL + "/open-api/tiktok-kol-product-share", "v1.0.0", paraMap));
    }

    public DyResVo queryDyList(QueryDyParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        String data = executeRawFullUrl(DY_BASE_URL + "/open-api/tiktok/order-list", "v1.0.0", paraMap);
        log.warn("*抖音订单：" + data);
        try {
            return JSON.parseObject(data, DyResVo.class);
        } catch (Exception e) {
            log.error("抖音订单解析失败", e);
        }
        return null;
    }

    // ==================== 拼多多订单 (保留直接调用，因注释标记不可用) ====================

    /**
     * 不可用 大淘客的订单采集有bug 使用 {@link PddService#queryPddOrderList(QueryPddParam)}
     */
    public PddResVo queryPddList(QueryPddParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param),
            new TypeReference<TreeMap<String, Object>>() {});
        String data = executeRawApi("/dels/pdd/order/incrementSearch", "v1.0.0", paraMap);
        log.warn("拼多多订单：" + data);
        return JSON.parseObject(data, PddResVo.class);
    }

    // ==================== 通用API调用辅助方法 ====================

    /**
     * 通过新SDK签名机制调用大淘客API（无SDK Request类的接口使用此方法）
     * @param path API路径，如 /vip/search-by-keywords
     * @param version API版本号
     * @param paraMap 请求参数
     * @return API响应JSON字符串
     */
    protected String executeRawApi(String path, String version, TreeMap<String, Object> paraMap) {
        return executeRawFullUrl("https://openapi.dataoke.com/api" + path, version, paraMap);
    }

    /**
     * 通过完整URL调用大淘客API（用于不同基础域名的接口如抖音）
     */
    protected String executeRawFullUrl(String fullUrl, String version, TreeMap<String, Object> paraMap) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("version", version);
        params.put("appKey", config.getKey());
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            if (entry.getValue() != null) {
                params.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        // 生成签名
        String urlParams = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !String.valueOf(e.getValue()).isEmpty())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        String sign = SignMd5Util.sign(urlParams, config.getSecret());
        params.put("sign", sign);
        // URL编码关键词
        if (params.containsKey("keyWords")) {
            try {
                params.put("keyWords", URLEncoder.encode(String.valueOf(params.get("keyWords")), "UTF-8"));
            } catch (Exception e) {
                log.warn("keyWords编码失败", e);
            }
        }
        return HttpUtil.get(fullUrl, params);
    }
}
