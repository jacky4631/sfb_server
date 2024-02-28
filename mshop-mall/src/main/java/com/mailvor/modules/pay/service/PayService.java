package com.mailvor.modules.pay.service;

import com.alibaba.fastjson.JSON;
import com.mailvor.api.MshopException;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.modules.activity.domain.MwUserExtract;
import com.mailvor.modules.pay.adapay.AdaPayService;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.pay.allinpay.syb.SybService;
import com.mailvor.modules.pay.domain.MwPayChannel;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.wechat.WechatPayService;
import com.mailvor.modules.pay.yeepay.YeePayService;
import com.mailvor.modules.pay.ysepay.YsePayService;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.*;
import com.mailvor.modules.utils.RsaUtil;
import com.yeepay.yop.sdk.service.account.response.PayOrderResponse;
import com.yinsheng.command.wallet.WithdrawRespCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.mailvor.modules.utils.PayUtil.*;

@Component
@Slf4j
public class PayService {

    @Resource
    private MwUserRechargeService userRechargeService;
    @Resource
    private MwUserLevelService userLevelService;
    @Resource
    private MwUserService userService;

    @Resource
    private MwPayChannelService payChannelService;

    @Resource
    private IGenerator generator;
    @Value("${rsa.private_key}")
    private String privateKey;

    @Resource
    private SybService sybService;
    @Resource
    private AdaPayService adaPayService;
    @Resource
    private AliPayService aliPayService;
    @Resource
    private WechatPayService wechatPayService;
    @Resource
    private YeePayService yeePayService;

    @Resource
    private YsePayService ysePayService;

    @Resource
    private MwUserBankService userBankService;

    @Resource
    private MwUserExtraService userExtraService;

    public MwUserRecharge getRecharge(String orderId) {
        //处理充值
        MwUserRecharge userRecharge = userRechargeService.getInfoByOrderId(orderId);
        if(userRecharge == null) {
            throw new MshopException("订单不存在");
        }
        if(OrderInfoEnum.PAY_STATUS_1.getValue().equals(userRecharge.getPaid())) {
            throw new MshopException("订单已支付");
        }
        return userRecharge;
    }

    public PayChannelDto getChannel(MwUserRecharge recharge) {

        MwPayChannel payChannel = payChannelService.getById(recharge.getChannelId());
        if(payChannel == null || payChannel.getCertProfileEnc() ==null) {
            throw new MshopException("通道不存在");
        }
        PayChannelDto channelDto = generator.convert(payChannel, PayChannelDto.class);
        channelDto.setCertProfile(RsaUtil.decrypt(new String(payChannel.getCertProfileEnc()), privateKey));

        return channelDto;
    }

    public void setUserLevel(String orderId) {
        //处理充值
        MwUserRecharge userRecharge = userRechargeService.getInfoByOrderId(orderId);
        if(userRecharge != null) {
            if(!OrderInfoEnum.PAY_STATUS_1.getValue().equals(userRecharge.getPaid())){
                //更新订单状态
                userRechargeService.updateRecharge(userRecharge);
                //如果是体验卡，修改用户体验卡级别
                if(userRecharge.getType() == 1) {
                    userExtraService.setUserLevel(userRecharge.getUid(), userRecharge.getGrade(), userRecharge.getPlatform());
                } else if(userRecharge.getType() == 2){
                    //设置用户月卡级别
                    userLevelService.setUserLevelMonth(userRecharge.getUid(), userRecharge.getGrade(), userRecharge.getPlatform());
                } else {
                    //设置用户级别
                    userLevelService.setUserLevel(userRecharge.getUid(), userRecharge.getGrade(), userRecharge.getPlatform());
                }
                //会员一二级分销
                userService.gainParentMoney(userRecharge.getUid(), userRecharge.getPrice(),
                        userRecharge.getOrderId(), userRecharge.getPayTime(), userRecharge.getPlatform(),userRecharge.getType());
            }
        }
    }

    public Map<String,Object> extract(MwUserExtract userExtract) throws Exception {

        Map<String,Object> extractRes = new HashMap<>();
        PayChannelDto payChannel = payChannelService.getExtractChannel(privateKey);
        if(payChannel == null) {
            extractRes.put("errMsg", "没有可选的通道");
            return extractRes;
        }
        String key = payChannel.getChannelKey();
        switch (key) {
            case CHANNEL_KEY_ADAPAY:
//                extractRes = adaPayService.extract(card, userExtract, payChannel);
//                log.info("提现结果: {}  res: {}", JSON.toJSONString(userExtract), JSON.toJSONString(extractRes));
                break;
            case CHANNEL_KEY_ALLINPAY:
//                Map<String,String> res = sybService.alipay(channelDto, orderId, price);
//                log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(res));
                break;
            case CHANNEL_KEY_ALIPAY:
//                Map<String,String> alipayRes = aliPayService.alipay(channelDto, orderId, price);
//                log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(alipayRes));
                break;
            case CHANNEL_KEY_WECHATPAY:
//                Map<String, Object> wechatPayRes = wechatPayService.pay(channelDto, orderId, price);
//                log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(wechatPayRes));
                break;
            case CHANNEL_KEY_YEEPAY_BANK:
                PayOrderResponse yeePayRes = yeePayService
                        .extract(payChannel,userExtract.getId().toString(),userExtract.getExtractPrice());
                log.info("易宝提现 param: {}  res: {}", JSON.toJSONString(userExtract), JSON.toJSONString(yeePayRes));
                String returnCode = yeePayRes.getResult().getReturnCode();
                if(!"UA00000".equals(returnCode)) {
                    extractRes.put("errMsg", yeePayRes.getResult().getReturnMsg());
                }
                break;
            case CHANNEL_KEY_YSEPAY_BANK_BIND:
                MwUserBank userBank = userBankService.findOne(userExtract.getUid(), userExtract.getBankCode());
                MwUserExtra userExtra = userExtraService.getById(userExtract.getUid());
                WithdrawRespCommand resp = ysePayService.extract(payChannel, userExtract.getId().toString(), userExtract.getExtractPrice(), userExtra.getMerchantNo(), userBank.getLinkId());
                if(resp == null) {
                    extractRes.put("errMsg", "银盛提现失败：需要查询银盛个人账户余额后谨慎操作");
                } else {
                    if(!"COM000".equals(resp.getSubCode())){
                        extractRes.put("errMsg", "银盛提现失败："+ resp.getSubMsg());
                    }
                }
        }
        return extractRes;
    }
}
