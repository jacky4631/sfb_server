/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service;

import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.mp.config.WxMpConfiguration;
import com.mailvor.modules.mp.domain.MwWechatTemplate;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.WechatUserDto;
import com.mailvor.modules.mp.enums.WechatTempateEnum;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.ShopKeyUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName 微信公众号模板通知
 * @author huangyu
 * @Date 2020/6/27
 **/
@Slf4j
@Service
public class WeixinTemplateService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MwWechatTemplateService mwWechatTemplateService;


    @Resource
    private MwUserUnionService userUnionService;


    /**
     * 充值成功通知
     * @param time 时间
     * @param price 金额
     * @param uid uid
     */
    public void rechargeSuccessNotice(String time,String price,Long uid){
        String openid = this.getUserOpenid(uid);

        if(StrUtil.isBlank(openid)) {
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("first","您的账户金币发生变动，详情如下：");
        map.put("keyword1","充值");
        map.put("keyword2",time);
        map.put("keyword3",price);
        map.put("remark", ShopConstants.MSHOP_WECHAT_PUSH_REMARK);
        String tempId = this.getTempId(WechatTempateEnum.RECHARGE_SUCCESS.getValue());
        if(StrUtil.isNotBlank(tempId)) {
            this.sendWxMpTemplateMessage( openid, tempId, this.getSiteUrl()+"/user/account",map);
        }
    }


    /**
     * 支付成功通知
     * @param orderId 订单号
     * @param price 金额
     * @param uid uid
     */
    public void paySuccessNotice(String orderId,String price,Long uid){

        String openid = this.getUserOpenid(uid);

        if(StrUtil.isBlank(openid)) {
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("first","您的订单已支付成功，我们会尽快为您发货。");
        //订单号
        map.put("keyword1",orderId);
        map.put("keyword2",price);
        map.put("remark",ShopConstants.MSHOP_WECHAT_PUSH_REMARK);
        String tempId = this.getTempId(WechatTempateEnum.PAY_SUCCESS.getValue());
        if(StrUtil.isNotBlank(tempId)) {
            this.sendWxMpTemplateMessage( openid,tempId, this.getSiteUrl()+"/order/detail/"+orderId,map);
        }
    }


    /**
     * 支付成功通知给客服
     *
     * @param orderId
     * @param price
     * @param openId
     */
    public void paySuccessNoticeToKefu(String orderId,String price,String openId) {
        Map<String, String> map = new HashMap<>();
        map.put("first", "尊敬的客服,您有新订单了");
        map.put("keyword1",orderId);
        map.put("keyword2",price);
        map.put("remark",ShopConstants.MSHOP_WECHAT_PUSH_REMARK);
        String tempId = this.getTempId(WechatTempateEnum.PAY_SUCCESS.getValue());
        String appId=RedisUtil.get(ShopKeyUtils.getWxAppAppId());
        if(StrUtil.isNotBlank(tempId)) {
            if(StrUtil.isBlank(appId)){
                this.sendWxMpTemplateMessage( openId,tempId, this.getSiteUrl()+"/order/detail/"+orderId,map);
            }else{
                WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram();
                miniProgram.setAppid(RedisUtil.get(ShopKeyUtils.getWxAppAppId()));
                miniProgram.setPagePath("pages/orderAdmin/AdminOrder/index?oid=" + orderId);
                this.sendWxMpTemplateMessageToWx(openId, tempId, miniProgram, map);
            }
        }



    }


    /**
     * 退款成功通知
     * @param orderId 订单号
     * @param price 金额
     * @param uid uid
     * @param time 时间
     */
    public void refundSuccessNotice(String title,String orderId,String price,Long uid,String time){

        String openid = this.getUserOpenid(uid);

        if(StrUtil.isBlank(openid)) {
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("first",title);
        //订单号
        map.put("keyword1",orderId);
        map.put("keyword2",price);
        map.put("keyword3", time);
        map.put("remark",ShopConstants.MSHOP_WECHAT_PUSH_REMARK);
        String tempId = this.getTempId(WechatTempateEnum.REFUND_SUCCESS.getValue());
        if(StrUtil.isNotBlank(tempId)) {
            this.sendWxMpTemplateMessage( openid,tempId, this.getSiteUrl()+"/order/detail/"+orderId,map);
        }
    }

    /**
     * 发送退款申请给客服
     * @param orderId 订单号
     * @param price 金额
     * @param openId openId
     * @param time 时间
     */
    public void refundSuccessNoticeToKefu(String title,String orderId,String price,String openId,String time){

        Map<String,String> map = new HashMap<>();
        map.put("first",title);
        //订单号
        map.put("keyword1",orderId);
        map.put("keyword2",price);
        map.put("keyword3", time);
        map.put("remark",ShopConstants.MSHOP_WECHAT_PUSH_REMARK);
        String tempId = this.getTempId(WechatTempateEnum.REFUND_SUCCESS.getValue());
        String appId=RedisUtil.get(ShopKeyUtils.getWxAppAppId());
        if(StrUtil.isNotBlank(tempId)) {
            if(StrUtil.isBlank(appId)){
                this.sendWxMpTemplateMessage( openId,tempId, this.getSiteUrl()+"/order/detail/"+orderId,map);
            }else{
                WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram();
                miniProgram.setAppid(RedisUtil.get(ShopKeyUtils.getWxAppAppId()));
                miniProgram.setPagePath("pages/orderAdmin/AdminOrder/index?oid=" + orderId);
                this.sendWxMpTemplateMessageToWx(openId, tempId, miniProgram, map);
            }
        }
    }

    /**
     * 发货成功通知
     * @param orderId 单号
     * @param deliveryName 快递公司
     * @param deliveryId 快递单号
     * @param uid uid
     */
    public void deliverySuccessNotice(String orderId,String deliveryName,
                                      String deliveryId,Long uid){

        String openid = this.getUserOpenid(uid);

        if(StrUtil.isEmpty(openid)) {
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("first","亲，宝贝已经启程了，好想快点来到你身边。");
        map.put("keyword2",deliveryName);
        map.put("keyword1",orderId);
        map.put("keyword3",deliveryId);
        map.put("remark",ShopConstants.MSHOP_WECHAT_PUSH_REMARK);
        String tempId = this.getTempId(WechatTempateEnum.DELIVERY_SUCCESS.getValue());
        if(StrUtil.isNotBlank(tempId)) {
            this.sendWxMpTemplateMessage( openid,tempId, this.getSiteUrl()+"/order/detail/"+orderId,map);
        }
    }


    /**
     * 构建微信模板通知
     * @param openId 单号
     * @param templateId 模板id
     * @param url 跳转url
     * @param map map内容
     * @return String
     */
    private String sendWxMpTemplateMessage(String openId, String templateId, String url, Map<String,String> map){
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(templateId)
                .url(url)
                .build();
        map.forEach( (k,v)-> { templateMessage.addData(new WxMpTemplateData(k, v, "#000000"));} );
        String msgId = null;
        WxMpService wxService = WxMpConfiguration.getWxMpService();
        try {
            msgId =   wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return msgId;
    }




    public String sendWxMpTemplateMessageToWx(String openId, String templateId, WxMpTemplateMessage.MiniProgram miniProgram, Map<String, String> map) {
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(templateId)
                .miniProgram(miniProgram)
                .build();
        map.forEach((k, v) -> {
            templateMessage.addData(new WxMpTemplateData(k, v, "#000000"));
        });
        String msgId = null;
        WxMpService wxService = WxMpConfiguration.getWxMpService();
        try {
            msgId = wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
        }
        return msgId;
    }


    /**
     * 获取模板消息id
     * @param key 模板key
     * @return string
     */
    private String getTempId(String key){
        MwWechatTemplate mwWechatTemplate = mwWechatTemplateService.lambdaQuery()
                .eq(MwWechatTemplate::getType,"template")
                .eq(MwWechatTemplate::getTempkey,key)
                .one();
        if (mwWechatTemplate == null) {
            throw new MshopException("请后台配置key:" + key + "模板消息id");
        }

        if(ShopCommonEnum.IS_STATUS_0.getValue().equals(mwWechatTemplate.getStatus())){
            return "";
        }

        return mwWechatTemplate.getTempid();
    }

    /**
     * 返回H5 url
     * @return url
     */
    private String getSiteUrl(){
        String apiUrl = redisUtils.getY(ShopKeyUtils.getSiteUrl());
        if(StrUtil.isBlank(apiUrl)){
            return "";
        }
        return apiUrl;
    }

    /**
     * 获取openid
     * @param uid uid
     * @return String
     */
    private String getUserOpenid(Long uid){
        MwUserUnion userUnion = userUnionService.getOne(uid);
        if(userUnion == null) {
            return "";
        }

        WechatUserDto wechatUserDto = userUnion.getWxProfile();
        if(wechatUserDto == null) {
            return "";
        }
        if(StrUtil.isBlank(wechatUserDto.getOpenid())) {
            return "";
        }
        return wechatUserDto.getOpenid();

    }


}
