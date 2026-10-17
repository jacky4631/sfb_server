package com.mailvor.modules.tk.service;

import com.mailvor.modules.tk.config.PddConfig;
import com.mailvor.modules.tk.param.QueryPddParam;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.*;
import com.pdd.pop.sdk.http.api.pop.response.*;
import com.mailvor.modules.tk.vo.pdd.PddWordVO;
import com.mailvor.modules.tk.vo.pdd.PddGoodsDetailVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkOrderListRangeGetRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkRpPromUrlGenerateRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkMemberAuthorityQueryResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkRpPromUrlGenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2019/04/24 14:55
 * @description:
 */
@Slf4j
@Component
public class PddService {
    @Resource
    private PddConfig pddConfig;

    private PopClient client;

    protected PopClient getClient() {
        if(client == null) {
            client = new PopHttpClient(pddConfig.getClientId(), pddConfig.getClientSecret());
        }
        return client;
    }

    /**
     * 查询拼多多订单
     *
     * @param param the param
     * @return the pdd ddk order list range get response
     */
    public PddDdkOrderListRangeGetResponse queryPddOrderList(QueryPddParam param) {
        try {
            PddDdkOrderListRangeGetRequest request = new PddDdkOrderListRangeGetRequest();
            if(param.getLastOrderId() != null) {
                request.setLastOrderId(param.getLastOrderId());
            }
            request.setEndTime(param.getEndTime());
            request.setPageSize(param.getPageSize());
            request.setStartTime(param.getStartTime());

            return getClient().syncInvoke(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询是否授权
     *
     * @param uid the uid
     * @return the int
     */
    public int authQuery(Long uid) {
        try {
            PddDdkMemberAuthorityQueryRequest request = new PddDdkMemberAuthorityQueryRequest();
            request.setCustomParameters(pddConfig.getParam(uid));
            request.setPid(pddConfig.getPid());
            PddDdkMemberAuthorityQueryResponse response = getClient().syncInvoke(request);
            return response.getAuthorityQueryResponse().getBind();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<PddDdkRpPromUrlGenerateResponse.RpPromotionUrlGenerateResponseUrlListItem> auth(Long uid) {
        try {
            PddDdkRpPromUrlGenerateRequest request = new PddDdkRpPromUrlGenerateRequest();
            request.setChannelType(10);
            request.setGenerateShortUrl(true);
            request.setGenerateWeApp(true);
            request.setCustomParameters(pddConfig.getParam(uid));
            request.setPIdList(Collections.singletonList(pddConfig.getPid()));

            return getClient().syncInvoke(request).getRpPromotionUrlGenerateResponse().getUrlList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PddWordVO getSuperRedPackageLink(Long uid, String goodsId) {
        try {
            PddDdkRpPromUrlGenerateResponse res = generateLink(uid, 23, true, goodsId);

            List<PddDdkRpPromUrlGenerateResponse.RpPromotionUrlGenerateResponseUrlListItem> list =
                    res.getRpPromotionUrlGenerateResponse().getUrlList();
            if(CollectionUtils.isNotEmpty(list)) {
                return PddWordVO.builder()
                        .link(list.get(0).getShortUrl())
                        .longLink(list.get(0).getUrl())
                        .wechatPath(list.get(0).getWeAppInfo() == null ? null : list.get(0).getWeAppInfo().getPagePath())
                        .wechatId(list.get(0).getWeAppInfo() == null ? null : list.get(0).getWeAppInfo().getUserName())
                        .wechatAppId(list.get(0).getWeAppInfo() == null ? null : list.get(0).getWeAppInfo().getAppId())
                        .build();
            }
        } catch (Exception e) {
            log.error("拼多多获取超级红包异常", e);
        }
        return null;
    }


    private PddDdkRpPromUrlGenerateResponse generateLink(Long uid, int channelType, boolean authUid, String goodsId) throws Exception {
        PddDdkRpPromUrlGenerateRequest request = new PddDdkRpPromUrlGenerateRequest();
        request.setChannelType(channelType);
        if(authUid && uid != null) {
            int auth = authQuery(uid);
            if(auth == 1) {
                request.setCustomParameters(pddConfig.getParam(uid));
            }
        }
        // 如果channelType=23超级红包，并且goodsId不为空，设置超级红包转链
        if(channelType == 23 && StringUtils.isNoneBlank(goodsId)) {
            PddDdkRpPromUrlGenerateRequest.DiySpRedPacketParam param = new PddDdkRpPromUrlGenerateRequest.DiySpRedPacketParam();
            param.setGoodsSign(goodsId);
            request.setDiySpRedPacketParam(param);
        } else if (channelType == 3){
            request.setScratchCardAmount(ThreadLocalRandom.current().nextLong(200L, 10001L));
        }
        request.setGenerateShortUrl(true);
        request.setGenerateWeApp(true);
        request.setPIdList(Collections.singletonList(pddConfig.getPid()));
        PddDdkRpPromUrlGenerateResponse response = getClient().syncInvoke(request);
        return response;
    }

    public PddWordVO getGoodsWordById(Long uid, String goodsId) {

        // 如果开启了超级红包转链，优先使用超级红包
        if (pddConfig.getSuperRedPackage() != null && pddConfig.getSuperRedPackage() == 1) {
            PddWordVO redPkg = getSuperRedPackageLink(uid, goodsId);
            if (redPkg != null && StringUtils.isNotBlank(redPkg.getLink())) {
                return redPkg;
            }
            // 超级红包失败时降级到普通转链
        }
        PddDdkGoodsPromotionUrlGenerateRequest request = new PddDdkGoodsPromotionUrlGenerateRequest();
        if(uid != null) {
            int auth = authQuery(uid);
            if(auth == 1) {
                request.setCustomParameters(pddConfig.getParam(uid));
            }
        }
        request.setGenerateShortLink(true);
        request.setPId(pddConfig.getPid());
        request.setGenerateWeApp(true);

        List<PddDdkGoodsPromotionUrlGenerateRequest.GoodsGenUrlParamListItem> goodsGenUrlParamList = new ArrayList<>();

        PddDdkGoodsPromotionUrlGenerateRequest.GoodsGenUrlParamListItem item = new PddDdkGoodsPromotionUrlGenerateRequest.GoodsGenUrlParamListItem();
        item.setGoodsSign(goodsId);
        goodsGenUrlParamList.add(item);

        request.setGoodsGenUrlParamList(goodsGenUrlParamList);

        PddDdkGoodsPromotionUrlGenerateResponse response = null;
        try {
            response = getClient().syncInvoke(request);
            PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse res = response.getGoodsPromotionUrlGenerateResponse();
            List<PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem> list =
                    res.getGoodsPromotionUrlList();
            if(CollectionUtils.isNotEmpty(list)) {
                return PddWordVO.builder()
                        .link(list.get(0).getShortUrl())
                        .longLink(list.get(0).getUrl())
                        .wechatPath(list.get(0).getWeAppInfo() == null ? null : list.get(0).getWeAppInfo().getPagePath())
                        .wechatId(list.get(0).getWeAppInfo() == null ? null : list.get(0).getWeAppInfo().getUserName())
                        .wechatAppId(list.get(0).getWeAppInfo() == null ? null : list.get(0).getWeAppInfo().getAppId())
                        .build();
            }
        } catch (Exception e) {
            log.error("拼多多获取商品转链异常", e);
        }
        return new PddWordVO();
    }

    /**
     * 拼多多官方商品详情查询 pdd.ddk.goods.detail
     *
     * @param goodsSign 商品goodsSign
     * @return PddGoodsDetailVO 商品详情
     */
    public PddGoodsDetailVO getGoodsDetail(String goodsSign) {
        try {
                PddDdkGoodsDetailRequest request = new PddDdkGoodsDetailRequest();
            request.setGoodsSign(goodsSign);
            request.setPid(pddConfig.getPid());

            PddDdkGoodsDetailResponse response = getClient().syncInvoke(request);
            PddDdkGoodsDetailResponse.GoodsDetailResponse goodsDetailResponse = response.getGoodsDetailResponse();
            if (goodsDetailResponse == null || CollectionUtils.isEmpty(goodsDetailResponse.getGoodsDetails())) {
                log.warn("拼多多商品详情查询为空 goodsSign={}", goodsSign);
                return null;
            }

            PddDdkGoodsDetailResponse.GoodsDetailResponseGoodsDetailsItem item = goodsDetailResponse.getGoodsDetails().get(0);
            return convertToGoodsDetailVO(item);
        } catch (Exception e) {
            log.error("拼多多商品详情查询异常 goodsSign={}", goodsSign, e);
        }
        return null;
    }

    private PddGoodsDetailVO convertToGoodsDetailVO(PddDdkGoodsDetailResponse.GoodsDetailResponseGoodsDetailsItem item) {
        PddGoodsDetailVO vo = new PddGoodsDetailVO();
        vo.setPlatform("pdd");
        vo.setGoodsId(item.getGoodsSign());
        vo.setTitle(item.getGoodsDesc());
        vo.setDtitle(item.getGoodsName());
        vo.setSales(item.getSalesTip());
        vo.setShopName(item.getMallName());
        vo.setImg(item.getGoodsImageUrl());
        vo.setCateName(item.getOptName());
        // 价格：分→元
        if (item.getMinNormalPrice() != null) {
            vo.setStartPrice(item.getMinNormalPrice() / 100.0);
        }
        if (item.getMinGroupPrice() != null) {
            vo.setEndPrice(item.getMinGroupPrice() / 100.0);
        }
        // 佣金比例：千分比→百分比
        if (item.getPromotionRate() != null) {
            vo.setFeeRatio(item.getPromotionRate() / 10.0);
        }
        vo.setFee((vo.getEndPrice() == null ? 0 : vo.getEndPrice()) *
                (vo.getFeeRatio() == null ? 0 : vo.getFeeRatio()) / 100.0);
        // 轮播图 / 详情图
        vo.setBanners(item.getGoodsGalleryUrls());
        vo.setDetails(item.getGoodsGalleryUrls());
        // 优惠券：分→元
        if (item.getCouponDiscount() != null) {
            vo.setCoupon(item.getCouponDiscount() / 100.0);
        }
        if (item.getCouponMinOrderAmount() != null) {
            vo.setCouponLimit(item.getCouponMinOrderAmount() / 100.0);
        }
        // 优惠券时间：Unix时间戳→格式化字符串
        vo.setCouponStart(formatTimestamp(item.getCouponStartTime()));
        vo.setCouponEnd(formatTimestamp(item.getCouponEndTime()));
        // 店铺评分
        vo.setDescScore(item.getDescTxt());
        vo.setShipScore(item.getLgstTxt());
        vo.setServiceScore(item.getServTxt());
        // 店铺信息
        vo.setShopLogo(item.getMallImgUrl());
        if (item.getMallId() != null) {
            vo.setSellerId(String.valueOf(item.getMallId()));
        }
        vo.setLabel(item.getUnifiedTags());
        return vo;
    }

    private String formatTimestamp(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
