package com.mailvor.modules.pay.service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.mailvor.modules.activity.domain.MwUserExtract;
import com.mailvor.modules.pay.adapay.AdaPayService;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.pay.allinpay.syb.SybService;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.enums.PayChannelEnum;
import com.mailvor.modules.pay.wechat.WechatPayService;
import com.mailvor.modules.pay.yeepay.YeePayService;
import com.mailvor.modules.pay.ysepay.YsePayService;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserBankService;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.yeepay.yop.sdk.service.account.response.PayOrderResponse;
import com.yinsheng.command.wallet.WithdrawRespCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PayExtractService {

    @Resource
    private MwPayChannelService payChannelService;

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

    public Map<String,Object> extract(MwUserExtract userExtract) throws AlipayApiException, WxPayException {

        Map<String,Object> extractRes = new HashMap<>();
        PayChannelDto payChannel = payChannelService.getExtractChannel(userExtract.getExtractType());
        if(payChannel == null) {
            extractRes.put("errMsg", "没有可选的通道");
            return extractRes;
        }
        String key = payChannel.getChannelKey();
        switch (PayChannelEnum.toKey(key)) {
            case ADAPAY:
//                extractRes = adaPayService.extract(card, userExtract, payChannel);
//                log.info("提现结果: {}  res: {}", JSON.toJSONString(userExtract), JSON.toJSONString(extractRes));
                break;
            case ALLINPAY:
//                Map<String,String> res = sybService.alipay(channelDto, orderId, price);
//                log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(res));
                break;
            case ALIPAY:
                AlipayFundTransUniTransferResponse alipayRes = aliPayService.fund(payChannel,
                        userExtract.getAlipayCode(), userExtract.getExtractPrice().toString());
                log.info("支付宝提现 param: {}  res: {}", JSON.toJSONString(userExtract), JSON.toJSONString(alipayRes));
                if(!"SUCCESS".equals(alipayRes.getStatus())) {
                    extractRes.put("errMsg", alipayRes.getMsg() + " subMsg:" + alipayRes.getSubMsg());
                }
                break;
            case WECHATPAY:
                EntPayResult wechatPayRes = wechatPayService.extract(payChannel,
                        userExtract.getWechat(), userExtract.getRealName(),
                        userExtract.getExtractPrice().multiply(new BigDecimal(100)).intValue());
                log.info("微信提现 param: {}  res: {}", JSON.toJSONString(userExtract), JSON.toJSONString(wechatPayRes));
                if(!"SUCCESS".equals(wechatPayRes.getReturnCode())) {
                    extractRes.put("errMsg", JSON.toJSONString(wechatPayRes));
                }
                break;
            case YEEPAY_BANK:
                PayOrderResponse yeePayRes = yeePayService
                        .extract(payChannel,userExtract.getId().toString(),userExtract.getExtractPrice());
                log.info("易宝提现 param: {}  res: {}", JSON.toJSONString(userExtract), JSON.toJSONString(yeePayRes));
                String returnCode = yeePayRes.getResult().getReturnCode();
                if(!"UA00000".equals(returnCode)) {
                    extractRes.put("errMsg", yeePayRes.getResult().getReturnMsg());
                }
                break;
            case YSEPAY_BANK_BIND:
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
