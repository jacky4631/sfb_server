/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.mailvor.api.BusinessException;
import com.mailvor.api.MshopException;
import com.mailvor.enums.AppFromEnum;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.pay.wechat.WechatPayService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.WechatUserDto;
import com.mailvor.utils.IpUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.ShopKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @ClassName 微信支付WeixinPayService
 * @author huangyu
 * @Date 2020/6/27
 **/
@Service
@Slf4j
public class WeixinPayService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MwStoreOrderService storeOrderService;
    @Autowired
    private MwUserRechargeService userRechargeService;

    @Resource
    private MwUserUnionService userUnionService;
    @Resource
    private WechatPayService wechatPayService;
    /**
     * 统一支付入口
     * @param orderId 单号
     * @param from 来源
     * @param attach 备注 普通支付还是充值
     * @param body 内容
     * @return Object
     */
    public Object unifyPay(String orderId, String from, String attach, String body) {
        long uid = 0;
        int payPrice = 0;
        BigDecimal bigDecimal = new BigDecimal(100);
        //普通支付
        if(BillDetailEnum.TYPE_3.getValue().equals(attach)){
            MwStoreOrderQueryVo orderInfo = storeOrderService.getOrderInfo(orderId,null);
            if(ObjectUtil.isNull(orderInfo)) {
                throw new MshopException("订单不存在");
            }

            if(orderInfo.getPayIntegral().compareTo(BigDecimal.ZERO) <= 0) {
                throw new MshopException("该支付无需支付");
            }

            uid = orderInfo.getUid().intValue();
            //计算分
            payPrice = bigDecimal.multiply(orderInfo.getPayIntegral()).intValue();
        }else{ //充值
            MwUserRecharge userRecharge = userRechargeService.getOne(Wrappers.<MwUserRecharge>lambdaQuery()
                    .eq(MwUserRecharge::getOrderId,orderId));
            if(userRecharge == null) {
                throw new BusinessException("充值订单不存在");
            }

            if(userRecharge.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
                throw new MshopException("该订单已支付");
            }
            uid = userRecharge.getUid();
            payPrice = bigDecimal.multiply(userRecharge.getPrice()).intValue();
        }


        MwUserUnion userUnion = userUnionService.getOne(uid);
        if(userUnion == null) {
            throw new MshopException("用户错误");
        }


        WechatUserDto wechatUserDto = userUnion.getWxProfile();

        WxPayService wxPayService = wechatPayService.getAppWxPayService();

        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setOutTradeNo(orderId);
        orderRequest.setTotalFee(payPrice);
        orderRequest.setSpbillCreateIp(IpUtil.getLocalIP());
        orderRequest.setNotifyUrl(this.getApiUrl() + "/api/wechat/notify");
        orderRequest.setBody(body);
        orderRequest.setAttach(attach);

        if(AppFromEnum.WEIXIN_H5.getValue().equals(from)){
            orderRequest.setTradeType("MWEB");
        }else if(AppFromEnum.APP.getValue().equals(from)){
            orderRequest.setTradeType("APP");
        }else if(AppFromEnum.PC.getValue().equals(from)){
            orderRequest.setTradeType("NATIVE");
            orderRequest.setProductId( UUID.fastUUID().toString());
        } else{
            orderRequest.setTradeType("JSAPI");
            if(AppFromEnum.ROUNTINE.getValue().equals(from)){
                orderRequest.setOpenid(wechatUserDto.getRoutineOpenid());
            }else {
                orderRequest.setOpenid(wechatUserDto.getOpenid());
            }
        }
        try {
            return wxPayService.createOrder(orderRequest);
        }catch (WxPayException e) {
            log.info("支付错误信息：{}",e.getMessage());
            throw new BusinessException(e.getMessage());
        }


    }

    /**
     * 退款
     * @param orderId orderId
     * @param refundFee totalFee 单位分
     */
    public void refundOrder(String orderId, Integer refundFee) {

        MwStoreOrderQueryVo orderInfo = storeOrderService.getOrderInfo(orderId,null);
        WxPayService wxPayService = wechatPayService.getAppWxPayService();
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        BigDecimal bigDecimal = new BigDecimal("100");
        int totalFee = bigDecimal.multiply(orderInfo.getPayIntegral()).intValue();
        //订单总金额
        wxPayRefundRequest.setTotalFee(totalFee);
        wxPayRefundRequest.setOutTradeNo(orderId);
        //生成退款单号
        String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();
        wxPayRefundRequest.setOutRefundNo(orderSn);
        //退款金额
        wxPayRefundRequest.setRefundFee(refundFee);
        wxPayRefundRequest.setNotifyUrl(this.getApiUrl() + "/api/notify/refund");
        try {
            wxPayService.refundV2(wxPayRefundRequest);
        } catch (WxPayException e) {
            log.info("退款错误信息：{}",e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 返回H5 url
     * @return url
     */
    private String getApiUrl(){
        String apiUrl = redisUtils.getY(ShopKeyUtils.getApiUrl());
        if(StrUtil.isBlank(apiUrl)){
            throw new MshopException("请配置移动端api地址");
        }
        return apiUrl;
    }


}
