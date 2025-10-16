/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.alibaba.fastjson.JSON;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.request.query.BigFieldGoodsReq;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.request.query.RankGoodsReq;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.BigfieldQueryResult;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.RankGoodsQueryResult;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderRowReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowQueryResult;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.request.get.PromotionCodeReq;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.GetResult;
import com.jd.open.api.sdk.request.kplunion.UnionOpenGoodsBigfieldQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenGoodsRankQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderRowQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenPromotionCommonGetRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderRowQueryResponse;
import com.mailvor.modules.tk.config.JdConfig;
import com.mailvor.modules.tk.param.QueryJdParam;
import com.mailvor.modules.tk.param.jd.GoodsListJDParam;
import com.mailvor.modules.tk.vo.jd.JdUnionCommonGoodsListVO;
import com.mailvor.modules.tk.vo.jd.JdUnionCommonGoodsWordVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 *
 * @author shenji
 * @date 2022/10/27
 */
@Slf4j
@Component
public class JdService {

    @Resource
    private JdConfig jdConfig;

    @SneakyThrows
    private RankGoodsQueryResult listRankUnion(GoodsListJDParam param){
        JdClient client = getJdClient();
        UnionOpenGoodsRankQueryRequest request=new UnionOpenGoodsRankQueryRequest();
        RankGoodsReq rankGoodsReq=new RankGoodsReq();
        rankGoodsReq.setRankId(200000L);
        rankGoodsReq.setSortType(3);
        rankGoodsReq.setPageIndex(param.getPageId());
        rankGoodsReq.setPageSize(param.getPageSize());
        request.setRankGoodsReq(rankGoodsReq);
        request.setVersion("1.0");
        return client.execute(request).getQueryResult();
    }

    public JdUnionCommonGoodsListVO listRank(GoodsListJDParam param) {
        RankGoodsQueryResult queryResult = listRankUnion(param);
        return JdUnionCommonGoodsListVO.convert(queryResult);

    }
    @SneakyThrows
    private BigfieldQueryResult goodsDetailUnion(String itemId) {
        JdClient client = getJdClient();
        UnionOpenGoodsBigfieldQueryRequest request=new UnionOpenGoodsBigfieldQueryRequest();
        BigFieldGoodsReq goodsReq=new BigFieldGoodsReq();
        goodsReq.setItemIds(Arrays.asList(itemId).toArray(new String[0]));
        request.setGoodsReq(goodsReq);
        request.setVersion("1.0");
        return client.execute(request).getQueryResult();
    }

    //    public JdKuCommonSearchListVO goodsDetail(String itemId) {
//        BigfieldQueryResult queryResult = goodsDetailUnion(itemId);
//        //todo 后续实现
//    }
    @SneakyThrows
    private GetResult goodsWordUnion(String itemId, String couponUrl, String uid) {
        //无权限无法调用
        JdClient client=new DefaultJdClient(jdConfig.getServer(),
                null,
                jdConfig.getAppKey(),jdConfig.getAppSecret());
        UnionOpenPromotionCommonGetRequest request=new UnionOpenPromotionCommonGetRequest();
        PromotionCodeReq promotionCodeReq=new PromotionCodeReq();
        promotionCodeReq.setMaterialId(itemId);
        promotionCodeReq.setSiteId(jdConfig.getSiteId());
        promotionCodeReq.setSubUnionId(uid);
        if(StringUtils.isNotBlank(couponUrl)){
            promotionCodeReq.setCouponUrl(couponUrl);
        }
        //生成短链接
        promotionCodeReq.setCommand(1);
        promotionCodeReq.setSceneId(1);
        request.setPromotionCodeReq(promotionCodeReq);
        request.setVersion("1.0");
        return client.execute(request).getGetResult();
    }

    public JdUnionCommonGoodsWordVO goodsWord(String itemId, String couponUrl, String uid) {
        GetResult getResult = goodsWordUnion(itemId, couponUrl, uid);
        return JdUnionCommonGoodsWordVO.builder()
                .link(getResult.getData().getClickURL())
                .pwd(getResult.getData().getJCommand())
                .build();
    }

    public OrderRowQueryResult order(QueryJdParam param) {
        JdClient client = getJdClient();
        UnionOpenOrderRowQueryRequest request=new UnionOpenOrderRowQueryRequest();
        OrderRowReq orderReq=new OrderRowReq();
        orderReq.setStartTime(param.getStartTime());
        orderReq.setEndTime(param.getEndTime());
        orderReq.setType(param.getType());
        orderReq.setFields("goodsInfo");
        orderReq.setPageIndex(param.getPageNo());
        orderReq.setPageSize(param.getPageSize());
        request.setOrderReq(orderReq);
        request.setVersion("1.0");
        UnionOpenOrderRowQueryResponse response= null;
        try {
            response = client.execute(request);
            return response.getQueryResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private JdClient getJdClient() {
        JdClient client=new DefaultJdClient(jdConfig.getServer(),
                null,
                jdConfig.getAppKey(),jdConfig.getAppSecret());
        return client;
    }
    public static void main(String[] args) throws Exception {

        String url = "https://api.jd.com/routerjson";
        String clientId = "a5a67f64a5491868c8436301";
        String clientSecret = "d22224c5a8bb6e638cb06";
//        PopClient client = new PopHttpClient(clientId, clientSecret);
//
//        PddDdkOrderListRangeGetRequest request = new PddDdkOrderListRangeGetRequest();
//        request.setEndTime("2022-06-23 23:00:00");
//        request.setPageSize(300);
//        request.setStartTime("2022-06-23 00:00:00");
//        PddDdkOrderListRangeGetResponse response = client.syncInvoke(request);
//        System.out.println(JsonUtil.transferToJson(response));

        JdClient client=new DefaultJdClient(url, null, clientId,clientSecret);
//        UnionOpenGoodsRankQueryRequest request=new UnionOpenGoodsRankQueryRequest();
//        RankGoodsReq rankGoodsReq=new RankGoodsReq();
//        rankGoodsReq.setRankId(200000L);
//        rankGoodsReq.setSortType(3);
//        rankGoodsReq.setPageIndex(1);
//        rankGoodsReq.setPageSize(10);
//        request.setRankGoodsReq(rankGoodsReq);
//        request.setVersion("1.0");
//        UnionOpenGoodsRankQueryResponse response = client.execute(request);
//        System.out.println(JSON.toJSONString(response.getQueryResult()));

//        UnionOpenGoodsBigfieldQueryRequest request=new UnionOpenGoodsBigfieldQueryRequest();
//        BigFieldGoodsReq goodsReq=new BigFieldGoodsReq();
//        goodsReq.setItemIds(Arrays.asList("28HYqoPfcmj38vCmWNacVosZ_3bnREOCd6cyii0Eb7l").toArray(new String[0]));
//        request.setGoodsReq(goodsReq);
//        request.setVersion("1.0");
//        System.out.println(JSON.toJSONString(client.execute(request).getQueryResult()));

//        UnionOpenPromotionCommonGetRequest request=new UnionOpenPromotionCommonGetRequest();
//        PromotionCodeReq promotionCodeReq=new PromotionCodeReq();
//        promotionCodeReq.setMaterialId("CGVm8tBjeQAy37yXTeYN9Nw6_3GHvBWMeRevlvCwYT8");
//        promotionCodeReq.setSiteId("4100889962");
//        promotionCodeReq.setSubUnionId("1");
//        promotionCodeReq.setCouponUrl("https://coupon.m.jd.com/coupons/show.action?linkKey=AAROH_xIpeffAs_-naABEFoex3wz1P2XHbwb2i6uQXWVbRKXorkR6Rf_siHaV3y2-SKueMvRcbdaBaAD7Tk4dKAIm65O_w");
//        promotionCodeReq.setSceneId(1);
//        request.setPromotionCodeReq(promotionCodeReq);
//        request.setVersion("1.0");
//        System.out.println(JSON.toJSONString(client.execute(request).getGetResult()));

        UnionOpenOrderRowQueryRequest request=new UnionOpenOrderRowQueryRequest();
        OrderRowReq orderReq=new OrderRowReq();
        orderReq.setStartTime("2024-12-07 13:08:00");
        orderReq.setEndTime("2024-12-07 13:10:00");
        orderReq.setType(3);
        orderReq.setPageIndex(1);
        orderReq.setPageSize(10);
        request.setOrderReq(orderReq);
        request.setVersion("1.0");
        UnionOpenOrderRowQueryResponse response=client.execute(request);
        System.out.println(JSON.toJSONString(response.getQueryResult()));
    }
}
