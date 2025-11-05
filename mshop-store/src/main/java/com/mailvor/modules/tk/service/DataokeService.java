/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mailvor.modules.tk.config.DataokeConfig;
import com.mailvor.modules.tk.config.JdConfig;
import com.mailvor.modules.tk.config.PddConfig;
import com.mailvor.modules.tk.param.*;
import com.mailvor.modules.tk.util.DataokeApi;
import com.mailvor.modules.tk.util.DataokeApiClient;
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
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mailvor.modules.tk.util.DataokeApi.*;
import static com.mailvor.modules.utils.TkUtil.EXCLUDE_KEY_WROD_LIST;
import static com.mailvor.modules.utils.TkUtil.hasWord;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2019/04/24 14:55
 * @description:
 */
@Slf4j
@Component
public class DataokeService {
    private static TypeReference<DataokeResVo<GoodsListVo>> goodsListTypeRef = new TypeReference<DataokeResVo<GoodsListVo>>(GoodsListVo.class){};
    private static TypeReference<DataokeResVo<GoodsDetailVo>> goodsDetailTypeRef = new TypeReference<DataokeResVo<GoodsDetailVo>>(GoodsDetailVo.class){};
    private static TypeReference<DataokeResVo<GoodsWordVo>> goodsWordTypeRef = new TypeReference<DataokeResVo<GoodsWordVo>>(GoodsWordVo.class){};
    private static TypeReference<DataokeResVo<GoodsParseVo>> goodsParseTypeRef = new TypeReference<DataokeResVo<GoodsParseVo>>(GoodsParseVo.class){};
    private static TypeReference<TreeMap<String, Object>> mapTypeReference = new TypeReference<TreeMap<String, Object>>() {};
    private static TypeReference<DataokeResVo<ParseContentVo>> parseContentTypeRef = new TypeReference<DataokeResVo<ParseContentVo>>(ParseContentVo.class){};

    private String regStr = "((http|https)://)([\\w-]+\\.)+[\\w$]+(\\/[\\w-?=&./]*)?";

    private Pattern pattern = Pattern.compile(regStr);

    @Resource
    private DataokeConfig config;

    @Resource
    private PddConfig pddConfig;

    @Resource
    private JdConfig jdConfig;

    @Resource
    private PddService pddService;

    public JSONObject goodsList(GoodsListParam param) {

        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.GOODS_LIST.getUrl(), DataokeApi.GOODS_LIST.getVersion(), paraMap);
        JSONObject jsonObject = JSON.parseObject(data);
        JSONArray resData = jsonObject.getJSONObject("data").getJSONArray("list");
        List list = resData.stream().filter(e->{
            JSONObject obj = (JSONObject) e;
            String title = obj.getString("dtitle");
            if(hasWord(title)){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        jsonObject.getJSONObject("data").put("list", list);
        return jsonObject;
    }
    public DataokeResVo<GoodsListVo> goodsVOS(GoodsListParam param) {

        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(GOODS_LIST.getUrl(), GOODS_LIST.getVersion(), paraMap);
        try{
            return JSON.parseObject(data, goodsListTypeRef);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject goodsSearch(GoodsSearchParam param) {
        String keyWord = param.getKeyWords().toLowerCase();
        if(TkUtil.hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(param.getKeyWords().toLowerCase())){
            JSONObject res = new JSONObject();
            res.put("data", new JSONArray());
            return res;
        }
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.TB_SEARCH.getUrl(), DataokeApi.TB_SEARCH.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject goodsDetail(String goodsId) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("goodsId", goodsId);
        String data = getData(DataokeApi.GOODS_DETAIL.getUrl(), DataokeApi.GOODS_DETAIL.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject goodsWord(String goodsId, String pid, String channelId) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("goodsId", goodsId);
        if(StringUtils.isNotBlank(pid)) {
            paraMap.put("pid", pid);
        }
        if(StringUtils.isNotBlank(channelId)) {
            paraMap.put("channelId", channelId);
        }
        String data = getData(DataokeApi.GOODS_WORD.getUrl(), DataokeApi.GOODS_WORD.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public DataokeResVo<GoodsParseVo> goodsParse(String content, String pid, String channelId) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("content", content);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(pid)) {
            paraMap.put("pid", pid);
        }
//         大淘客接口问题 这里传渠道id就无法解析
        if(org.apache.commons.lang3.StringUtils.isNotBlank(channelId)) {
            paraMap.put("channelId", channelId);
        }
        String data = getData(DataokeApi.GOODS_PARSE.getUrl(), DataokeApi.GOODS_PARSE.getVersion(), paraMap);
        return JSON.parseObject(data, goodsParseTypeRef);
    }
    public JSONObject getCategory() {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        String data = getData(DataokeApi.GOODS_CATEGORY.getUrl(), DataokeApi.GOODS_CATEGORY.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject getCommentList(GoodsCommentParam param) {

        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.GOODS_COMMENT.getUrl(), DataokeApi.GOODS_COMMENT.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject getTopic() {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        String data = getData(DataokeApi.TOPIC_LIST2.getUrl(), DataokeApi.TOPIC_LIST2.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject getBanner() {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        String data = getData(DataokeApi.BANNER.getUrl(), DataokeApi.BANNER.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject getTbActivityList(TbActivityListParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.TB_ACTIVITY_LIST.getUrl(), DataokeApi.TB_ACTIVITY_LIST.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject parseTbActivity(TbActivityParseParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.TB_ACTIVITY_PARSE.getUrl(), DataokeApi.TB_ACTIVITY_PARSE.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject parseContent(ParseContentParam param) {
        if(param.getContent()!= null && param.getContent().contains("yangkeduo.com")) {
            try {
                //修复ios 拼多多查券无法识别的问题，goods_id必须放在?后面
                param.setContent(parsePddContent(param.getContent()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.GOODS_PARSE_ALL.getUrl(), DataokeApi.GOODS_PARSE_ALL.getVersion(), paraMap);
        return JSON.parseObject(data);
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
    public static void main(String[] args) throws UnsupportedEncodingException {
        DataokeService service = new DataokeService();
        String content = service.parsePddContent(
//                "https://mobile.yangkeduo.com/goods.html?goods_id=410160347693&page_from=29&pxq_secret_key=33TRW3AEOYKNICYPDAMRKZYAIBORSSAIYRVGAPUEYLIBJYGXXFWQ&share_uin=OK3ATMDGM5DQLD75P3625MMMGY_GEXDA&refer_share_id=e7fa4c701d3545a096709154e1b3f17c&refer_share_uin=OK3ATMDGM5DQLD75P3625MMMGY_GEXDA&refer_share_channel=copy_link&refer_share_form=text"
        "https://mobile.yangkeduo.com/goods2.html?refer_share_id=7hRYk1RcMeDNOBGy1590mIysmB5AZ60n&refer_share_channel=copy_link&pxq_secret_key=RFY2XNUVPFJEMXA6PPGD6UXLEBZYQJYTBWKFKECUFP3BG444E5UQ&_wvx=10&_x_ddjb_act=%7B%22st%22%3A%221%22%7D&_x_ddjb_id=1784892_252288888%7CCC_230129_1784892_252288888_7b316846d52696aba7de6707cc9488b5&_x_ddjb_gs=%7B%22gs_src%22%3A%221%22%2C%22gs_scn%22%3A%223%22%7D&_wv=41729&share_uin=FEHOG3PNIIGI24R2DELMGM34YU_GEXDA&page_from=29&refer_share_uin=FEHOG3PNIIGI24R2DELMGM34YU_GEXDA&_x_customParameters=%7B%22uid%22%3A%226%22%7D&goods_id=410160347693"
        );
        System.out.println(content);
    }

    protected String getData(String url, String version, TreeMap<String, Object> paraMap) {
        return DataokeApiClient.sendReq(
                url,
                config.getKey(),
                config.getSecret(),
                version,
                paraMap);
    }

    public JSONObject goodsDetailJD(String goodsId, String itemId) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        if(StringUtils.isNotBlank(goodsId) && !"0".equals(goodsId) && !goodsId.equals(itemId)) {
            paraMap.put("skuIds", goodsId);
        }
        if(StringUtils.isNotBlank(itemId)) {
            paraMap.put("itemIds", itemId);
        }
        String data = getData(DataokeApi.JD_GOODS_DETAIL.getUrl(), DataokeApi.JD_GOODS_DETAIL.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public VipGoodsDetailDataVo goodsDetailVIP(String goodsId, String openId) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("goodsIdList", "[\"" + goodsId + "\"]");
        JSONObject request = new JSONObject();

        request.put("openId", openId);
        request.put("realCall", "true");
        paraMap.put("request", request.toJSONString());
//        paraMap.put("chanTag", "default_pid");

        String data = getData(DataokeApi.VIP_GOODS_DETAIL.getUrl(), DataokeApi.VIP_GOODS_DETAIL.getVersion(), paraMap);
        VipGoodsDetailDataVo dataVo = JSON.parseObject(data, VipGoodsDetailDataVo.class);
        if(CollectionUtils.isNotEmpty(dataVo.getData())) {
            VipGoodsDetailVO detailVO = dataVo.getData().get(0);
            detailVO.setShopName(detailVO.getStoreName());
            dataVo.setGoods(detailVO);
            dataVo.setData(null);
        }

        return dataVo;
    }

    public JSONObject goodsWordJD(String itemUrl, String couponUrl, String pid) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("materialId", itemUrl);
        if(StringUtils.isNotBlank(couponUrl)) {
            paraMap.put("couponUrl", couponUrl);
        }
        if(pid != null) {
            paraMap.put("positionId", pid);
        }
        paraMap.put("unionId", jdConfig.getUnionId());
        String data = getData(DataokeApi.JD_GOODS_WORD.getUrl(), DataokeApi.JD_GOODS_WORD.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject parseUrlJD(String itemUrl) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("url", itemUrl);
        paraMap.put("unionId", jdConfig.getUnionId());
        String data = getData(DataokeApi.JD_PARSE_URL.getUrl(), DataokeApi.JD_PARSE_URL.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject goodsDetailPDD(String goodsSign) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("goodsSign", goodsSign);
        String data = getData(DataokeApi.PDD_GOODS_DETAIL.getUrl(), DataokeApi.PDD_GOODS_DETAIL.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject goodsWordPDD(String goodsSign,Long uid) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("pid", pddConfig.getPid());
        paraMap.put("goodsSign", goodsSign);
        if(uid != null) {
            int auth = pddService.authQuery(uid);
            if(auth == 1) {
                paraMap.put("customParameters", pddConfig.getParam(uid));
            }
        }
        String data = getData(DataokeApi.PDD_GOODS_WORD.getUrl(), DataokeApi.PDD_GOODS_WORD.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject goodsSimilarList(String id, String size) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("id", id);
        paraMap.put("size", size);
        String data = getData(DataokeApi.GOODS_SIMILAR.getUrl(), DataokeApi.GOODS_SIMILAR.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject ddq(String roundTime) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        if(StringUtils.isNotBlank(roundTime)) {
            paraMap.put("roundTime", roundTime);
        }
        String data = getData(DataokeApi.TB_DDQ.getUrl(), DataokeApi.TB_DDQ.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject rankingList(RankingListParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.TB_RANK_LIST.getUrl(), DataokeApi.TB_RANK_LIST.getVersion(), paraMap);
        JSONObject jsonObject = JSON.parseObject(data);
        JSONArray resData = jsonObject.getJSONArray("data");
        List list = resData.stream().filter(e->{
            JSONObject obj = (JSONObject) e;
            String title = obj.getString("title");
            if(hasWord(title)){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        jsonObject.put("data", list);
        return jsonObject;
    }
    public JSONObject dyGoodsSearch(GoodsSearchDyParam param) {
        if(StringUtils.isNotBlank(param.getTitle())) {
            String keyWord = param.getTitle().toLowerCase();
            if(TkUtil.hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(keyWord)){
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("list", new JSONArray());
                data.put("total", 0);
                res.put("data", data);
                return res;
            }
        }
        param.setAppkey(config.getKey());

        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.DY_GOODS_SEARCH.getUrl(), DataokeApi.DY_GOODS_SEARCH.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject dyGoodsDetail(String goodsId) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("productIds", goodsId);
        paraMap.put("appkey", config.getKey());
        String data = getData(DataokeApi.DY_GOODS_DETAIL.getUrl(), DataokeApi.DY_GOODS_DETAIL.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject dyWord(String productUrl, String externalInfo) {
        if(productUrl.contains("pick_source")) {
            productUrl = productUrl.substring(0, productUrl.lastIndexOf("&"));
        }
        if(!productUrl.startsWith("http")) {

            Matcher matcher = pattern.matcher(productUrl);
            if (matcher.find( )) {
                productUrl = matcher.group(0);
            } else {
                return new JSONObject();
            }
        }

        int splitIndex = productUrl.indexOf("&");
        if(splitIndex > 0) {
            productUrl = productUrl.substring(0, splitIndex);
        }
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("productUrl", productUrl);
        paraMap.put("externalInfo", externalInfo);
        String data = getData(DataokeApi.DY_WORD.getUrl(), DataokeApi.DY_WORD.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject shopConvert(String shopId, String shopName, String pid, String channelId) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("sellerId", shopId);
        paraMap.put("shopName", shopName);
        paraMap.put("appkey", config.getKey());
        if(StringUtils.isNotBlank(pid)) {
            paraMap.put("pid", pid);
        }
        if(StringUtils.isNotBlank(channelId)) {
            paraMap.put("relationId", channelId);
        }
        String data = getData(DataokeApi.TB_SHOP_CONVERT.getUrl(), DataokeApi.TB_SHOP_CONVERT.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public JSONObject getBrandList(Integer cid, Integer pageId) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("cid", cid.toString());
        paraMap.put("pageId", pageId.toString());
        paraMap.put("pageSize", "10");
        paraMap.put("appkey", config.getKey());
        String data = getData(DataokeApi.TB_BRAND_LIST.getUrl(), DataokeApi.TB_BRAND_LIST.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject getBrandGoodsList(String brandId, Integer pageId, Integer pageSize) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("brandId", brandId);
        paraMap.put("pageId", pageId.toString());
        paraMap.put("pageSize", pageSize.toString());
        paraMap.put("appkey", config.getKey());
        String data = getData(DataokeApi.TB_BRAND_GOODS_LIST.getUrl(), DataokeApi.TB_BRAND_GOODS_LIST.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public VipSearchListVO goodsListVip(GoodsListVipParam param) {
        //唯品会结构和统一结构相同，无需转换
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.VIP_GOODS_LIST.getUrl(), DataokeApi.VIP_GOODS_LIST.getVersion(), paraMap);
        return JSON.parseObject(data,VipSearchListVO.class);
    }


    public VipWordCodeVO goodsWordVIP(String itemUrl, String statParam, JSONObject urlGenRequest) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("urlList", "[\"" + itemUrl + "\"]");
        if(org.apache.commons.lang3.StringUtils.isNotBlank(statParam)) {
            paraMap.put("statParam", statParam);
        }
        paraMap.put("urlGenRequest", urlGenRequest.toJSONString());
        String data = getData(DataokeApi.VIP_GOODS_WORD.getUrl(), DataokeApi.VIP_GOODS_WORD.getVersion(), paraMap);
        VipWordCodeVO codeVO = JSON.parseObject(data, VipWordCodeVO.class);
        if(codeVO != null &&codeVO.getData() != null && CollectionUtils.isNotEmpty(codeVO.getData().getList())) {
            codeVO.setWord(codeVO.getData().getList().get(0));
            codeVO.getData().setList(null);
        }
        return codeVO;
    }


    public PddSearchListVO goodsListPdd(GoodsListPddParam param) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(param.getKeyword())) {
            String keyWord = param.getKeyword().toLowerCase();
            if(hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(keyWord)){
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

        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.PDD_GOODS_SEARCH.getUrl(), DataokeApi.PDD_GOODS_SEARCH.getVersion(), paraMap);
        return JSON.parseObject(data, PddSearchListVO.class);

    }
    public JSONObject goodsCatePdd(Integer parentId) {

        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("parentId", parentId.toString());
        String data = getData(DataokeApi.PDD_GOODS_CATE.getUrl(), DataokeApi.PDD_GOODS_CATE.getVersion(), paraMap);
        return JSON.parseObject(data);
    }
    public JSONObject vipGoodsSearch(GoodsSearchVipParam param) {
        if(StringUtils.isNotBlank(param.getKeyword())) {
            String keyWord = param.getKeyword().toLowerCase();
            if(TkUtil.hasWord(keyWord) || EXCLUDE_KEY_WROD_LIST.contains(keyWord)){
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("goodsInfoList", new JSONArray());
                data.put("total", 0);
                res.put("data", data);
                return res;
            }
        }
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DataokeApi.VIP_GOODS_SEARCH.getUrl(), DataokeApi.VIP_GOODS_SEARCH.getVersion(), paraMap);
        return JSON.parseObject(data);
    }

    public TBResVo queryTBList(QueryTBParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(TB_QUERY_ORDER.getUrl(), TB_QUERY_ORDER.getVersion(), paraMap);
        log.warn("*淘宝订单："+ data);
        try{
            return JSON.parseObject(data, TBResVo.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public JdResVo queryJdList(QueryJdParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(JD_QUERY_ORDER.getUrl(), JD_QUERY_ORDER.getVersion(), paraMap);
        log.warn("*京东订单："+ data);
        if(StringUtils.isBlank(data)){
            return null;
        }
        try{
            return JSON.parseObject(data, JdResVo.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public VipResVo queryVipList(QueryVipParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(VIP_QUERY_ORDER.getUrl(), VIP_QUERY_ORDER.getVersion(), paraMap);
        log.warn("*唯品会订单："+ data);
        if(StringUtils.isBlank(data)){
            return null;
        }
        try{
            return JSON.parseObject(data, VipResVo.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    protected static String DY_ORDER_QUERY_URL = "%s?date_type=%s&size=%s&page=%s&order_type=%s&search_start_time=%s&search_end_time=%s&app_token=%s";
    public DyResVo queryDyList(QueryDyParam param) {
//        String data = "";
//        try {
//            String url = String.format(DY_ORDER_QUERY_URL, param.getUrl(),
//                    param.getDataType(), param.getSize(), param.getPage(),param.getOrderType(),
//                    URLEncoder.encode(param.getSearchStartTime(), "UTF-8"),
//                    URLEncoder.encode(param.getSearchEndTime(),"UTF-8"), param.getAppToken());
//
//
//            data = HttpUtil.httpGetRequest(url);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(DY_QUERY_ORDER.getUrl(), DY_QUERY_ORDER.getVersion(), paraMap);
        log.warn("*抖音订单："+ data);
        try{
            return JSON.parseObject(data, DyResVo.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * 不可用 大淘客的订单采集有bug 使用 {@link PddService#queryPddOrderList(QueryPddParam)}
     *
     * @param param the param
     * @return the pdd res vo
     */
    public PddResVo queryPddList(QueryPddParam param) {
        TreeMap<String, Object> paraMap = JSON.parseObject(JSON.toJSONString(param), mapTypeReference);
        String data = getData(PDD_QUERY_ORDER.getUrl(), PDD_QUERY_ORDER.getVersion(), paraMap);
        log.warn("拼多多订单："+ data);
        return JSON.parseObject(data, PddResVo.class);
    }
}
