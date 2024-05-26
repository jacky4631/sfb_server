/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.enums.PayTypeEnum;
import com.mailvor.modules.energy.dto.MonthCardConfigDto;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.pay.adapay.AdaPayService;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.pay.allinpay.syb.SybService;
import com.mailvor.modules.pay.dto.OrderDto;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.enums.PayChannelEnum;
import com.mailvor.modules.pay.param.PayBankBindConfirmParam;
import com.mailvor.modules.pay.param.PayBankBindParam;
import com.mailvor.modules.pay.param.PayChannelParam;
import com.mailvor.modules.pay.param.PayConfirmParam;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.wechat.WechatPayService;
import com.mailvor.modules.pay.yeepay.YeePayService;
import com.mailvor.modules.pay.ysepay.YsePayConfig;
import com.mailvor.modules.pay.ysepay.YsePayService;
import com.mailvor.modules.shop.domain.MwSystemGroupData;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.shop.service.dto.PayConfigDto;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.*;
import com.mailvor.modules.user.service.dto.UserLevelDto;
import com.mailvor.modules.user.vo.MwSystemUserLevelQueryVo;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import com.yinsheng.command.QueryBankListRespCommand;
import com.yinsheng.command.QueryCardRespCommand;
import com.yinsheng.command.scancode.FrontCodePayRespCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.constant.SystemConfigConstants.PAY_CONFIG;

/**
 * <p>
 * 用户充值 前端控制器
 * </p>
 *
 * @author huangyu
 * @since 2020-03-01
 */
@Slf4j
@RestController
@Api(value = "用户充值", tags = "用户:用户充值")
public class PayController {
    @Resource
    private MwUserRechargeService userRechargeService;
    @Resource
    private MwSystemGroupDataService systemGroupDataService;
    @Resource
    private MwSystemUserLevelService systemUserLevelService;
    @Resource
    private MwPayChannelService payChannelService;

    @Resource
    private MwUserService userService;

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
    private MwUserCardService cardService;

    @Resource
    private MwUserBankService bankService;

    @Resource
    private RedisUtils redisUtil;

    @Resource
    private MwSystemConfigService systemConfigService;

    /**
     *   通道选型算法
     *   优选剩余额度比例高的，额度差距在10%以内的，优选总额度越高的
     *   回调时扣除额度，如果低于一定值，暂停通道
     *
     *   通道绑定用户
     *   无绑定时，按照上面算法选通道
     *   有绑定时，校验通道是否有效，
     *        有效 继续支付
     *        无效，优选同主体同平台通道，次选同主体不同平台通道，如果都无效，换绑定，app端提示合同重签
     *
     * */
    @AppLog(value = "app支付", type = 1)
    @AuthCheck
    @ApiOperation("app支付")
    @Transactional
    @PostMapping(value = "/pay/channel")
    public ApiResult<Map<String, Object>> aliPay(@Valid @RequestBody PayChannelParam param, HttpServletRequest request) throws Exception{
        if(param.getPayType() == 4 && param.getBankId() == null) {
            throw new MshopException("bankId必须存在");
        }
        if(param.getType() == null) {
            param.setType(0);
        }
        MwUser loginUser = LocalUser.getUser();
        Long checkUid;
        if(param.getUid() != null && param.getUid() > 0) {
            checkUid = param.getUid();
        } else {
            checkUid = loginUser.getUid();
        }
        MwUser checkUser = userService.getById(checkUid);
        Integer curLevel = TkUtil.getLevel(param.getPlatform(), checkUser);
        Date expired = TkUtil.getExpired(param.getPlatform(), checkUser);
        if(expired != null) {
            long betweenDay = DateUtil.betweenDay(expired, new Date(), false);
            //校验会员到期是否超过31天 超过无法续费，为了月卡用户可以续费
            //如果是月卡 不允许加盟， 年卡 允许加盟
            Integer type = param.getType();
            if(curLevel != null && curLevel == 5) {
                if((type == null || type == 0) && betweenDay > 31) {
                    throw new MshopException("已经是会员，无法再次支付");
                }
                if(type != null && type == 2) {
                    throw new MshopException("已经是会员，无法再次支付");
                }
            }
        }
        PayChannelDto payChannel = payChannelService.channelDto(loginUser.getUid(), param.getPayType());

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }
        //提示签署合同 直接返还
        if(param.getUid() != null && param.getUid() > 0) {
            MwUserCardQueryVo cardQueryVo = cardService.getUserCardById(checkUid);
            if(cardQueryVo == null || StringUtils.isBlank(cardQueryVo.getContractPath())) {
                throw new MshopException("用户未实名认证");
            }
        }
        MwUserCardQueryVo cardQueryVo = cardService.getUserCardById(loginUser.getUid());
        if(cardQueryVo == null || StringUtils.isBlank(cardQueryVo.getContractPath())) {
            throw new MshopException("需要实名认证签署合同后方可加盟");
        }
        try {
            OrderDto orderDto = recharge(param, payChannel.getId(), checkUid, param.getType());
            //支付
            String key = payChannel.getChannelKey();

            Map<String, Object> data = new HashMap<>();
            String orderId = orderDto.getOrderSn();
            String price = orderDto.getPrice();
            switch (PayChannelEnum.toKey(key)) {
                case ADAPAY:
                    Map<String,Object> adaPayRes = adaPayService.alipay(request, payChannel, orderId, price);
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(adaPayRes));
                    if("failed".equals(adaPayRes.get("status"))) {
                        //todo
                    } else {
                        data.put("payInfo", "alipays://platformapi/startapp?saId=10000007&qrcode="
                                + ((JSONObject)adaPayRes.get("expend")).get("pay_info").toString());
                    }
                    break;
                case ALLINPAY:
                    Map<String,String> res = sybService.alipay(payChannel, orderId, price);
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(res));
                    data.put("payInfo", res.get("payinfo"));
                    break;
                case ALIPAY:
                    Map<String,String> alipayRes = aliPayService.alipay(payChannel, orderId, price);
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(alipayRes));
                    data.put("payInfo", alipayRes.get("payInfo"));
                    break;
                case ALIPAY_WEB:
                    Map<String,String> alipayWebRes = aliPayService.alipayWeb(payChannel, orderId, price);
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(alipayWebRes));
                    data.put("payInfo", alipayWebRes.get("payInfo"));
                    break;
                case WECHATPAY:
                    Map<String, Object> wechatPayRes = wechatPayService.pay(payChannel, orderId, price);
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(wechatPayRes));
                    data = wechatPayRes;
                    break;
                case YEEPAY_BANK:
                    MwUser payUser = userService.getById(loginUser.getUid());
                    Map<String, Object> yeeBankRes = yeePayService.bankPay(payChannel, orderId, price,payUser.getPhone(),cardQueryVo, param.getType());
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(yeeBankRes));
                    data = yeeBankRes;
                    break;
                case ADAPAY_BANK:
                    Map<String, Object> adaBankRes = adaPayService.bankPay(payChannel, orderId, price,loginUser.getUid());
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(adaBankRes));
                    data = adaBankRes;
                    break;
                case YSEPAY_BANK_BIND:
                    MwUserBank userBank = bankService.getById(param.getBankId());
                    if(userBank == null) {
                        throw new MshopException("银行卡不存在");
                    }
                    if(userBank.getSign() == 0) {
                        throw new MshopException("银行卡未签约");
                    }
                    Map<String, Object> ysePayRes = ysePayService.createProtocol(payChannel, orderId, price,loginUser.getUid(),userBank.getProtocolNo());
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(ysePayRes));
                    bankService.setDefault(loginUser.getUid(), userBank.getId());
                    data = ysePayRes;
                    break;
                case YSEPAY:
                    FrontCodePayRespCommand yseAliPay = ysePayService.alipay(payChannel, orderId, price);
                    log.info("param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(yseAliPay));
                    data.put("payInfo", "alipays://platformapi/startapp?saId=10000007&qrcode=" + yseAliPay.getQrCode());
                    break;
                case iOSPAY:
                    data.put("localOrderId", orderId);
                    break;
            }

            data.put("localOrderId", orderId);
            //生成支付信息
            return ApiResult.ok(data);

        }catch (Exception e) {
            e.printStackTrace();
            throw new MshopException(e.getMessage());
        }
    }
    @AppLog(value = "支付请求短信确认", type = 1)
    @AuthCheck
    @ApiOperation("支付请求短信确认")
    @Transactional
    @PostMapping(value = "/pay/confirm")
    public ApiResult<Map<String, Object>> payConfirm(@Valid @RequestBody PayConfirmParam param) {
        //只有银行卡绑卡支付需要验证码确认
        MwUserRecharge recharge = userRechargeService.getRecharge(param.getOrderId());
        PayChannelDto payChannel = payChannelService.getChannel(recharge.getChannelId());
        //支付
        String key = payChannel.getChannelKey();
        Map<String, Object> data = new HashMap<>();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:
                boolean ysePayConfirm = ysePayService.fastPayMsgVerify(payChannel, param.getBizSn(), param.getCode());
                log.info("payConfirm param: {}  res: {}", JSON.toJSONString(param), ysePayConfirm);
                data.put("confirm", ysePayConfirm);
                break;
        }
        //生成支付信息
        return ApiResult.ok(data);
    }

    protected String getPayType(Integer payType) {
        String payTypeValue;
        if(payType == 0) {
            payTypeValue = PayTypeEnum.IOS.getValue();;
        } else if(payType == 1) {
            payTypeValue = PayTypeEnum.ALI.getValue();
        } else if (payType == 2) {
            payTypeValue = PayTypeEnum.WEIXIN.getValue();
        } else if (payType == 3) {
            payTypeValue = PayTypeEnum.BANK.getValue();
        } else if (payType == 4) {
            payTypeValue = PayTypeEnum.BANK_BIND.getValue();
        } else {
            payTypeValue = PayTypeEnum.UNIONPAY.getValue();
        }
        return payTypeValue;
    }
    protected OrderDto recharge(PayChannelParam param, Long channelId, Long uid, Integer type) {
        MwUser user = LocalUser.getUser();

        MwSystemGroupData systemGroupData = systemGroupDataService.getById(param.getRechargeId());
        if(systemGroupData == null) {
            throw new MshopException("充值方案不存在");
        }
        UserLevelDto userLevelDto = systemUserLevelService.getLevelInfo(param.getPlatform());
        MwSystemUserLevelQueryVo userLevelQueryVo = userLevelDto.getList().stream()
                .filter(mwSystemUserLevelQueryVo -> mwSystemUserLevelQueryVo.getRechargeId().equals(param.getRechargeId())).findFirst().orElse(null);
        BigDecimal priceB = BigDecimal.ZERO;
        if(type == 0) {
            //正式加盟
            JSONObject jsonObject = JSON.parseObject(systemGroupData.getValue());
            String price = jsonObject.getString("price");
            //计算优惠信息
            BigDecimal coupon = BigDecimal.ZERO;
            Object couponObj = redisUtil.get(OrderUtil.getCouponKey(param.getPlatform(), user.getUid()));
            if(couponObj != null) {
                coupon = (BigDecimal)couponObj;
            }

            priceB = new BigDecimal(price);
            PayConfigDto obj = systemConfigService.getAppPayConfig();
            Integer payType = param.getPayType();
            //是否支持满减
            if((payType == 1 && "1".equals(obj.getAc())) ||
                    (payType == 2 && "1".equals(obj.getWc())) ||
                    (payType == 3 && "1".equals(obj.getBc())) ||
                    (payType == 4 && "1".equals(obj.getBbc()))) {
                priceB = NumberUtil.sub(Double.parseDouble(price), coupon);
            }
        } else if (type == 2) {
            //月卡
            MonthCardConfigDto monthCardConfig = systemConfigService.getMonthCardConfig();
            priceB = monthCardConfig.getPrice();
        }

        String payTypeValue = getPayType(param.getPayType());

        String orderSn = userRechargeService.addRecharge(
                user,priceB.toString(),payTypeValue, userLevelQueryVo.getGrade(), param.getPlatform(),
                param.getRechargeId(), channelId, uid, type);

        return OrderDto.builder()
                .orderSn(orderSn)
                .price(priceB.toString()).build();
    }

    @AuthCheck
    @GetMapping("/pay/bind")
    @ApiOperation(value = "绑定支付渠道",notes = "绑定支付渠道",response = MwUserCardQueryVo.class)
    public ApiResult<String> bindChannel(@RequestParam Integer payType){

        // payType "支付类型 0=邀请一人升送1年 1=支付宝 2=微信 3=银行卡 其他未定义")

        MwUser mwUser = LocalUser.getUser();
        //todo 如果用户当前未绑定口令返回错误
        MwUser findUser = userService.getById(mwUser.getUid());
        if(StringUtils.isBlank(findUser.getCode())) {
            return ApiResult.fail("请绑定邀请口令");
        }
        //在用户点击支付时，如果没有绑定过支付渠道，绑定支付渠道
        if(payType != null && payType > 0) {
            payChannelService.channel(mwUser.getUid(), payType);
        }

        return ApiResult.ok("绑定成功");
    }

    @AppLog(value = "用户支付配置", type = 1)
    @AuthCheck
    @GetMapping("/pay/config")
    @ApiOperation(value = "用户支付配置",notes = "用户支付配置")
    public ApiResult<PayConfigDto> extractConfig(){
        return ApiResult.ok(systemConfigService.getAppPayConfig());
    }

    @AppLog(value = "银行卡绑定签约", type = 1)
    @AuthCheck
    @PostMapping("/pay/bank/bind")
    @ApiOperation(value = "银行卡绑定签约",notes = "银行卡绑定签约")
    public ApiResult payBankBind(@Valid @RequestBody PayBankBindParam param) throws Exception {
        if(!StringUtils.isPhone(param.getPhone())) {
            throw new MshopException("不是正确的手机号");
        }
        MwUser loginUser = LocalUser.getUser();

        PayChannelDto payChannel = payChannelService.channelDto(loginUser.getUid(), param.getPayType());

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }


        MwUserCardQueryVo userCard = cardService.getUserCardById(loginUser.getUid());
        String key = payChannel.getChannelKey();
        Map<String, Object> data = new HashMap<>();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:
                String requestNo = ysePayService.signProtocol(payChannel, param.getPhone(), userCard, param.getBankNo());
                log.info("payBankBind param: {}  res: {}", JSON.toJSONString(param), requestNo);
                data.put("requestNo", requestNo);
                break;
        }
        return ApiResult.ok(data);
    }
    @AppLog(value = "银行卡绑定签约短信确认", type = 1)
    @AuthCheck
    @PostMapping("/pay/bank/bind/confirm")
    @ApiOperation(value = "银行卡绑定签约短信确认",notes = "银行卡绑定签约短信确认")
    public ApiResult payBankBindConfirm(@Valid @RequestBody PayBankBindConfirmParam param){
        MwUser loginUser = LocalUser.getUser();
        PayChannelDto payChannel = payChannelService.channelDto(loginUser.getUid(), param.getPayType());

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }
        String key = payChannel.getChannelKey();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:
                ysePayService.signProtocolConfirm(payChannel, param.getRequestNo(), param.getCode());
                log.info("payBankBind param: {}", JSON.toJSONString(param));
                break;
        }
        return ApiResult.ok();

    }

    protected JSONObject getPayConfig() {
        Object appObj = redisUtil.get(PAY_CONFIG + "_" + PAY_NAME);
        if(appObj == null) {
            appObj = redisUtil.get(PAY_CONFIG);
        }
        return (JSONObject) appObj;
    }

    @AppLog(value = "查询银行卡信息", type = 1)
    @AuthCheck
    @GetMapping("/pay/bank/info")
    @ApiOperation(value = "查询银行卡信息",notes = "查询银行卡信息")
    public ApiResult payBankInfo(@RequestParam String bankNo){
        MwUser loginUser = LocalUser.getUser();
        PayChannelDto payChannel = payChannelService.channelDto(loginUser.getUid(), 4);

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }
        String key = payChannel.getChannelKey();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:

                YsePayConfig yesConfig = JSON.parseObject(payChannel.getCertProfile(), YsePayConfig.class);
                //查询银行卡
                QueryCardRespCommand cardResp = ysePayService.queryBankCard(yesConfig, bankNo);
                return ApiResult.ok(cardResp);
        }
        return ApiResult.ok();
    }

    @AppLog(value = "查询支持的银行卡列表", type = 1)
    @AuthCheck
    @GetMapping("/pay/bank/support")
    @ApiOperation(value = "查询支持的银行卡列表",notes = "查询支持的银行卡列表")
    public ApiResult payBankBindConfirm(){
        MwUser loginUser = LocalUser.getUser();
        PayChannelDto payChannel = payChannelService.channelDto(loginUser.getUid(), 4);

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }
        String key = payChannel.getChannelKey();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:

                YsePayConfig yesConfig = JSON.parseObject(payChannel.getCertProfile(), YsePayConfig.class);
                //查询银行卡
                QueryBankListRespCommand cardResp = ysePayService.queryBankList(yesConfig);
                return ApiResult.ok(cardResp.getBankList());
        }
        return ApiResult.ok();
    }

    public static void main(String[] args) {
        Date expired = DateUtil.offsetDay(new Date(), -50);
        long betweenDay = DateUtil.betweenDay(expired, new Date(), false);
        System.out.println(betweenDay);
    }
}

