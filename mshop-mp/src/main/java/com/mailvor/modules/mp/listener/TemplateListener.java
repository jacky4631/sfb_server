/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.listener;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.PayTypeEnum;
import com.mailvor.event.TemplateBean;
import com.mailvor.event.TemplateEvent;
import com.mailvor.event.TemplateListenEnum;
import com.mailvor.modules.activity.domain.MwUserExtract;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.customer.domain.MwStoreCustomer;
import com.mailvor.modules.customer.service.MwStoreCustomerService;
import com.mailvor.modules.mp.service.WeiXinSubscribeService;
import com.mailvor.modules.mp.service.WeixinPayService;
import com.mailvor.modules.mp.service.WeixinTemplateService;
import com.mailvor.modules.pay.service.PayExtractService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author huangyu
 * 异步监听模板通知事件
 */
@Slf4j
@Component
public class TemplateListener implements SmartApplicationListener {
    @Autowired
    private MwUserService userService;
    @Autowired
    private WeixinTemplateService weixinTemplateService;
    @Autowired
    private WeixinPayService weixinPayService;
    @Autowired
    private WeiXinSubscribeService weiXinSubscribeService;
    @Autowired
    private MwUserExtractService mwUserExtractService;
    @Autowired
    private MwStoreCustomerService mwStoreCustomerService;
    @Resource
    private PayExtractService payExtractService;

    @Resource
    private MwUserBillService userBillService;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        return aClass == TemplateEvent.class;
    }

    @Async
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        //转换事件类型
        TemplateEvent templateEvent = (TemplateEvent) applicationEvent;
        //获取注册用户对象信息
        TemplateBean templateBean = templateEvent.getTemplateBean();
        log.info("2.用户提现申请异步通知：{}", JSON.toJSONString(templateBean));
        switch (TemplateListenEnum.toType(templateBean.getTemplateType())) {
            case TYPE_1:
                weixinTemplateService.paySuccessNotice(templateBean.getOrderId()
                        , templateBean.getPrice(), templateBean.getUid());
                weiXinSubscribeService.paySuccessNotice(templateBean.getOrderId()
                        , templateBean.getPrice(), templateBean.getUid());
                /**************给客服发送消息**************/
                try {
                    List<MwStoreCustomer> mwStoreCustomers = mwStoreCustomerService.lambdaQuery().eq(MwStoreCustomer::getIsEnable, ShopConstants.MSHOP_ONE_NUM).list();
                    mwStoreCustomers.forEach(msg -> {
                        if (StrUtil.isNotBlank(msg.getOpenId())) {
                         weixinTemplateService.paySuccessNoticeToKefu(templateBean.getOrderId()
                                 , templateBean.getPrice(), msg.getOpenId());
                        }
                    });
                } catch (Exception e) {
                    log.error("消息发送失败:{}", e);
                }
                break;
            case TYPE_2:
                //处理退款与消息
                if (PayTypeEnum.WEIXIN.getValue().equals(templateBean.getPayType())) {
                    BigDecimal bigDecimal = new BigDecimal("100");
                    int payPrice = bigDecimal.multiply(new BigDecimal(templateBean.getPrice())).intValue();
                    weixinPayService.refundOrder(templateBean.getOrderId(), payPrice);
                }

                weixinTemplateService.refundSuccessNotice("您的订单退款申请被通过，钱款将很快还至您的支付账户。",templateBean.getOrderId(), templateBean.getPrice(),
                        templateBean.getUid(), templateBean.getTime());
                break;
            case TYPE_3:
                weixinTemplateService.deliverySuccessNotice(templateBean.getOrderId(), templateBean.getDeliveryName(),
                        templateBean.getDeliveryId(), templateBean.getUid());
                break;
            case TYPE_4:
                weixinTemplateService.rechargeSuccessNotice(templateBean.getTime(), templateBean.getPrice(),
                        templateBean.getUid());
                break;
            case TYPE_7:
                //使用MQ延时消息
                //mqProducer.sendMsg("mshop-topic", templateBean.getOrderId());
                log.info("投递延时订单id： [{}]：", templateBean.getOrderId());
                break;
            case TYPE_8:
                //提现
                MwUserExtract resources = mwUserExtractService.getById(templateBean.getExtractId());
                Boolean success = false;
                String failMsg = "";
                MwUser user = userService.getById(resources.getUid());
                if (user != null) {
                    //校验订单流水和订单创建的时间是否一致，金额是否一致 时间一致
                    MwUserBill userBill = userBillService.getOne(new LambdaQueryWrapper<MwUserBill>()
                            .eq(MwUserBill::getLinkId,resources.getId().toString()));
                    if(userBill == null || !resources.getBalance().equals(userBill.getNumber())
                            || !resources.getCreateTime().equals(userBill.getOrderCreateTime())) {
                        failMsg = "没有找到提现流水bill";
                    } else {
                        if(PayTypeEnum.WEIXIN.getValue().equals(resources.getExtractType()) ||
                                PayTypeEnum.ALI.getValue().equals(resources.getExtractType()) ||
                                PayTypeEnum.BANK.getValue().equals(resources.getExtractType())) {
                            try {

                                //银行卡提现
                                Map<String,Object> map = payExtractService.extract(resources);
                                if(map.get("errMsg") != null && StringUtils.isNotBlank((String)map.get("errMsg"))) {
                                    failMsg = (String)map.get("errMsg");
                                } else if(map.get("error_code") != null){
                                    //汇付提现
                                    failMsg = (String) map.get("error_msg");
                                } else {
                                    success = true;
                                }

                            } catch (WxPayException e) {
                                log.error("退款失败,原因:{}", e);
                                failMsg = e.getErrCodeDes();
                            } catch (Exception e) {
                                String message = e.getMessage();
                                log.error("提现失败,原因:{}", e);
                                if(message.length() > 255) {
                                    message = message.substring(0, 254);
                                }
                                failMsg = message;
                            }
                        } else {
                            failMsg = "未找到提现通道";
                        }
                    }

                }
                if (!success) {
                    //防止无限添加佣金
                    resources.setFailMsg(failMsg);
                    resources.setFailTime(new Date());
                    resources.setStatus(0);
                    mwUserExtractService.updateById(resources);
                }

                break;
            case TYPE_9:
                weixinTemplateService.refundSuccessNotice("您的订单退款申请已提交,等待审核。",templateBean.getOrderId(), templateBean.getPrice(),
                        templateBean.getUid(), templateBean.getTime());
                /**************给客服发送消息**************/
                try {
                    List<MwStoreCustomer> mwStoreCustomers = mwStoreCustomerService.lambdaQuery().eq(MwStoreCustomer::getIsEnable, ShopConstants.MSHOP_ONE_NUM).list();
                    mwStoreCustomers.forEach(msg -> {
                        if (StrUtil.isNotBlank(msg.getOpenId())) {
                            weixinTemplateService.refundSuccessNoticeToKefu("尊敬的客服,您有新的退款申请待处理!",templateBean.getOrderId()
                                    , templateBean.getPrice(), msg.getOpenId(), templateBean.getTime());
                        }
                    });
                } catch (Exception e) {
                    log.error("消息发送失败:{}", e);
                }
                break;
            default:
                //todo
        }


    }
}
