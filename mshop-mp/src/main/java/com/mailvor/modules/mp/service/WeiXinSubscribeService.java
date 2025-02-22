package com.mailvor.modules.mp.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.mp.domain.MwWechatTemplate;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.WechatUserDto;
import com.mailvor.modules.mp.enums.WechatTempateEnum;
import com.mailvor.modules.mp.config.WxMaConfiguration;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序订阅消息通知
 */
@Service
public class WeiXinSubscribeService {
    @Resource
    private MwUserUnionService userUnionService;
    @Autowired
    private MwWechatTemplateService mwWechatTemplateService;
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
            this.sendSubscribeMsg( openid, tempId, "/user/account",map);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Map<String,String> map = new HashMap<>();
        map.put("amount1",price);
        map.put("date2", simpleDateFormat.format(new Date()));
        map.put("character_string3",orderId);
        map.put("time4",simpleDateFormat.format(new Date()));
        map.put("thing5","mshop购买商品");
        String tempId = this.getTempId(WechatTempateEnum.PAY_SUCCESS.getValue());
        if(StrUtil.isNotBlank(tempId)) {
            this.sendSubscribeMsg( openid,tempId, "/order/detail/"+orderId,map);
        }
    }

    /**
     * 退款成功通知
     * @param orderId 订单号
     * @param price 金额
     * @param uid uid
     * @param time 时间
     */
    public void refundSuccessNotice(String orderId,String price,Long uid,String time){

        String openid = this.getUserOpenid(uid);

        if(StrUtil.isBlank(openid)) {
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("first","您的订单退款申请被通过，钱款将很快还至您的支付账户。");
        //订单号
        map.put("keyword1",orderId);
        map.put("keyword2",price);
        map.put("keyword3", time);
        map.put("remark",ShopConstants.MSHOP_WECHAT_PUSH_REMARK);
        String tempId = this.getTempId(WechatTempateEnum.REFUND_SUCCESS.getValue());
        if(StrUtil.isNotBlank(tempId)) {
            this.sendSubscribeMsg( openid,tempId, "/order/detail/"+orderId,map);
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
            this.sendSubscribeMsg( openid,tempId, "/order/detail/"+orderId,map);
        }
    }


    /**
     * 构建小程序一次性订阅消息
     * @param openId 单号
     * @param templateId 模板id
     * @param page 跳转页面
     * @param map map内容
     * @return String
     */
    private void sendSubscribeMsg(String openId, String templateId, String page, Map<String,String> map){
        WxMaSubscribeMessage wxMaSubscribeMessage = WxMaSubscribeMessage.builder()
                .toUser(openId)
                .templateId(templateId)
                .page(page)
                .build();
        map.forEach( (k,v)-> { wxMaSubscribeMessage.addData(new WxMaSubscribeMessage.MsgData(k, v));} );
        WxMaService wxMaService = WxMaConfiguration.getWxMaService();
        try {
            wxMaService.getMsgService().sendSubscribeMsg(wxMaSubscribeMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模板消息id
     * @param key 模板key
     * @return string
     */
    private String getTempId(String key){
        MwWechatTemplate mwWechatTemplate = mwWechatTemplateService.lambdaQuery()
                .eq(MwWechatTemplate::getType,"subscribe")
                .eq(MwWechatTemplate::getTempkey,key)
                .one();
        if (mwWechatTemplate == null) {
            throw new MshopException("请后台配置key:" + key + "订阅消息id");
        }
        if(ShopCommonEnum.IS_STATUS_0.getValue().equals(mwWechatTemplate.getStatus())){
            return "";
        }
        return mwWechatTemplate.getTempid();
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
        if(StrUtil.isBlank(wechatUserDto.getRoutineOpenid())) {
            return "";
        }
        return wechatUserDto.getRoutineOpenid();

    }
}
