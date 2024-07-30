/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.services;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.enums.AppFromEnum;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.enums.OrderLogEnum;
import com.mailvor.enums.PayTypeEnum;
import com.mailvor.modules.activity.service.MwStoreBargainUserService;
import com.mailvor.modules.activity.service.MwStorePinkService;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.order.domain.MwStoreOrderCartInfo;
import com.mailvor.modules.order.dto.OrderExtendDto;
import com.mailvor.modules.order.param.ComputeOrderParam;
import com.mailvor.modules.order.service.MwStoreOrderCartInfoService;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.service.dto.ProductAttrDto;
import com.mailvor.modules.order.service.dto.ProductDto;
import com.mailvor.modules.order.vo.OrderCartInfoVo;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.mp.service.WeixinPayService;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName 订单提供者服务
 * @author huangyu
 * @Date 2020/6/22
 **/
@Slf4j
@Service
@AllArgsConstructor
public class OrderSupplyService {

    private final MwStoreOrderService storeOrderService;
    private final MwStoreOrderCartInfoService orderCartInfoService;
    private final WeixinPayService weixinPayService;


    /**
     * 返回订单产品信息
     * @param unique 订单唯一值
     * @return OrderCartInfoVo
     */
    public OrderCartInfoVo getProductOrder(String unique){
        MwStoreOrderCartInfo orderCartInfo = orderCartInfoService.findByUni(unique);

        MwStoreCartQueryVo cartInfo = JSONObject.parseObject(orderCartInfo.getCartInfo(),
                MwStoreCartQueryVo.class);

        ProductDto productDTO = new ProductDto();
        productDTO.setImage(cartInfo.getProductInfo().getImage());
        productDTO.setPrice(cartInfo.getProductInfo().getPrice().doubleValue());
        productDTO.setStoreName(cartInfo.getProductInfo().getStoreName());
        if(ObjectUtil.isNotEmpty(cartInfo.getProductInfo().getAttrInfo())){
            ProductAttrDto productAttrDTO = new ProductAttrDto();
            productAttrDTO.setImage(cartInfo.getProductInfo().getAttrInfo().getImage());
            productAttrDTO.setPrice(cartInfo.getProductInfo().getAttrInfo().getPrice().doubleValue());
            productAttrDTO.setProductId(cartInfo.getProductInfo().getAttrInfo().getProductId());
            productAttrDTO.setSku(cartInfo.getProductInfo().getAttrInfo().getSku());
            productDTO.setAttrInfo(productAttrDTO);
        }


        return OrderCartInfoVo.builder()
                .bargainId(cartInfo.getBargainId())
                .cartNum(cartInfo.getCartNum())
                .combinationId(cartInfo.getCombinationId())
                .orderId(storeOrderService.getById(orderCartInfo.getOid()).getOrderId())
                .seckillId(cartInfo.getSeckillId())
                .productInfo(productDTO)
                .build();
    }

    /**
     * 订单检测
     * @param uid uid
     * @param key 缓存值
     * @param param ComputeOrderParam
     * @return map
     */
    public Map<String,Object> check(Long uid,String key, ComputeOrderParam param){
        Map<String,Object> map = Maps.newHashMap();
        if(StrUtil.isBlank(key)) {
            throw new MshopException("参数错误");
        }
        MwStoreOrderQueryVo storeOrder = storeOrderService.getOrderInfo(key,uid);
        if(ObjectUtil.isNotNull(storeOrder)){

            OrderExtendDto orderExtendDTO = OrderExtendDto.builder()
                    .key(key)
                    .orderId(storeOrder.getOrderId())
                    .build();
            map.put("status", OrderLogEnum.EXTEND_ORDER.getValue());
            map.put("result", orderExtendDTO);
            map.put("msg",OrderLogEnum.EXTEND_ORDER.getDesc());
        }

        return map;
    }

    /**
     * 支付
     * @param map     map
     * @param orderId 订单号
     * @param uid     uid
     * @param payType 支付方式
     * @param from 来源
     * @param orderDTO orderDTO
     * @return map
     */
    public Map<String, Object> goPay(Map<String, Object> map, String orderId, Long uid, String payType,
                                     String from, OrderExtendDto orderDTO){
        switch (PayTypeEnum.toType(payType)){
            case WEIXIN:
                Map<String,String> jsConfig = new HashMap<>();
                if(AppFromEnum.WEIXIN_H5.getValue().equals(from)){
                    WxPayMwebOrderResult wxPayMwebOrderResult = (WxPayMwebOrderResult)weixinPayService
                            .unifyPay(orderId,from, BillDetailEnum.TYPE_3.getValue(),"H5商品购买");

                    log.info("wxPayMwebOrderResult:{}",wxPayMwebOrderResult);
                    jsConfig.put("mweb_url",wxPayMwebOrderResult.getMwebUrl());
                    orderDTO.setJsConfig(jsConfig);
                    map.put("result",orderDTO);
                    map.put("status","WECHAT_H5_PAY");
                    map.put("payMsg","订单创建成功");
                    return map;
                } else if(AppFromEnum.ROUNTINE.getValue().equals(from)){
                    map.put("status","WECHAT_PAY");
                    WxPayMpOrderResult wxPayMpOrderResult = (WxPayMpOrderResult)weixinPayService
                            .unifyPay(orderId,from, BillDetailEnum.TYPE_3.getValue(),"小程序商品购买");
                    jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                    jsConfig.put("timeStamp",wxPayMpOrderResult.getTimeStamp());
                    jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                    jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                    jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                    jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                    orderDTO.setJsConfig(jsConfig);
                    map.put("payMsg","订单创建成功");
                    map.put("result",orderDTO);
                    return map;
                }else if(AppFromEnum.APP.getValue().equals(from)){//app支付
                    map.put("status","WECHAT_APP_PAY");
                    WxPayAppOrderResult wxPayAppOrderResult = (WxPayAppOrderResult)weixinPayService
                            .unifyPay(orderId,from, BillDetailEnum.TYPE_3.getValue(),"APP商品购买");
                    jsConfig.put("appid",wxPayAppOrderResult.getAppId());
                    jsConfig.put("partnerid",wxPayAppOrderResult.getPartnerId());
                    jsConfig.put("prepayid",wxPayAppOrderResult.getPrepayId());
                    jsConfig.put("package",wxPayAppOrderResult.getPackageValue());
                    jsConfig.put("noncestr",wxPayAppOrderResult.getNonceStr());
                    jsConfig.put("timestamp",wxPayAppOrderResult.getTimeStamp());
                    jsConfig.put("sign",wxPayAppOrderResult.getSign());
                    orderDTO.setJsConfig(jsConfig);
                    map.put("result",orderDTO);
                    map.put("payMsg","订单创建成功");
                    return map;
                }else if(AppFromEnum.PC.getValue().equals(from)){ //扫码支付
                    map.put("status","WECHAT_PC_PAY");
                    WxPayNativeOrderResult wxPayNativeOrderResult = (WxPayNativeOrderResult)weixinPayService
                            .unifyPay(orderId,from, BillDetailEnum.TYPE_3.getValue(),"pc商品购买");
                    jsConfig.put("codeUrl",wxPayNativeOrderResult.getCodeUrl());
                    orderDTO.setJsConfig(jsConfig);
                    map.put("result",orderDTO);
                    map.put("payMsg","订单创建成功");
                    return map;
                }
                else{//公众号
                    map.put("status","WECHAT_PAY");
                    WxPayMpOrderResult wxPayMpOrderResult = (WxPayMpOrderResult)weixinPayService
                            .unifyPay(orderId,from, BillDetailEnum.TYPE_3.getValue(),"公众号商品购买");

                    log.info("WxPayMpOrderResult:{}",wxPayMpOrderResult);

                    jsConfig.put("appId",wxPayMpOrderResult.getAppId());
                    jsConfig.put("timestamp",wxPayMpOrderResult.getTimeStamp());
                    jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
                    jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
                    jsConfig.put("signType",wxPayMpOrderResult.getSignType());
                    jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
                    orderDTO.setJsConfig(jsConfig);
                    map.put("result",orderDTO);
                    map.put("payMsg","订单创建成功");

                    return map;
                }
            case YUE:
                storeOrderService.yuePay(orderId,uid);
                map.put("payMsg","余额支付成功");
                return map;
            case INTEGRAL:
                storeOrderService.integralPay(orderId,uid);
                map.put("payMsg","积分兑换成功");
                return map;
            default:
        }

        map.put("payMsg","订单生成失败");
        return map;
    }




}
