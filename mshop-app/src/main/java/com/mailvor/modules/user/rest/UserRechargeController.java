/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.enums.PayTypeEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.AppFromEnum;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.shop.domain.MwSystemGroupData;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataQueryCriteria;
import com.mailvor.modules.shop.vo.MwSystemGroupDataVo;
import com.mailvor.modules.tools.domain.AlipayConfig;
import com.mailvor.modules.tools.domain.vo.TradeVo;
import com.mailvor.modules.tools.service.AlipayConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.param.RechargeCouponParam;
import com.mailvor.modules.user.param.RechargeParam;
import com.mailvor.modules.user.param.RechargeResultParam;
import com.mailvor.modules.user.service.MwSystemUserLevelService;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.mp.service.WeixinPayService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.mailvor.modules.user.service.dto.UserLevelDto;
import com.mailvor.modules.user.vo.MwSystemUserLevelQueryVo;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "用户充值", tags = "用户:用户充值")
public class UserRechargeController {

    private final MwUserRechargeService userRechargeService;
    private final WeixinPayService weixinPayService;
    private final MwSystemGroupDataService systemGroupDataService;
    private final MwSystemUserLevelService systemUserLevelService;

    private final AlipayConfigService alipayConfigService;
    private final AliPayService aliPayService;

    private final RedisUtils redisUtil;
    /**
     * 充值方案
     */
    @GetMapping("/recharge/index")
    @ApiOperation(value = "充值方案",notes = "充值方案",response = ApiResult.class)
    public ApiResult<Object> getWays(){
        MwSystemGroupDataQueryCriteria queryCriteria = new MwSystemGroupDataQueryCriteria();
        queryCriteria.setGroupName(ShopConstants.MSHOP_RECHARGE_PRICE_WAYS);
        queryCriteria.setStatus(ShopCommonEnum.IS_STATUS_1.getValue());
        List<MwSystemGroupData> mwSystemGroupDataList = systemGroupDataService.queryAll(queryCriteria);

        List<MwSystemGroupDataVo> systemGroupDataVoList = mwSystemGroupDataList.stream().map(s->{
            MwSystemGroupDataVo systemGroupDataVo = new MwSystemGroupDataVo();
            BeanUtil.copyProperties(s,systemGroupDataVo,"value");
            systemGroupDataVo.setValue(JSON.parseObject(s.getValue()));
            return systemGroupDataVo;
        }).collect(Collectors.toList());

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("recharge_price_ways",systemGroupDataVoList);
        return ApiResult.ok(map);
    }

    /**
     * 公众号充值/H5充值
     */
    @AppLog(value = "公众号充值", type = 1)
    @AuthCheck
    @PostMapping("/recharge/wechat")
    @ApiOperation(value = "公众号充值/H5充值",notes = "公众号充值/H5充值",response = ApiResult.class)
    public ApiResult<Map<String,Object>> add(@Valid @RequestBody RechargeParam param){
        MwUser user = LocalUser.getUser();

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("type",param.getFrom());
        MwSystemGroupData systemGroupData = systemGroupDataService.getById(param.getRecharId());
        if(systemGroupData == null) {
            throw new MshopException("充值方案不存在");
        }

        JSONObject jsonObject = JSON.parseObject(systemGroupData.getValue());
        String price = jsonObject.getString("price");
        String orderSn = userRechargeService
                .addRecharge(user,price, PayTypeEnum.WEIXIN.getValue(),
                        0, param.getPlatform(), param.getRecharId(), 0L);

        if(AppFromEnum.WEIXIN_H5.getValue().equals(param.getFrom())){
            WxPayMwebOrderResult result = (WxPayMwebOrderResult)weixinPayService
                    .unifyPay(orderSn,param.getFrom(), BillDetailEnum.TYPE_1.getValue(),"H5充值");
            map.put("data",result.getMwebUrl());
        }else if(AppFromEnum.ROUNTINE.getValue().equals(param.getFrom())){
            WxPayMpOrderResult wxPayMpOrderResult = (WxPayMpOrderResult)weixinPayService
                    .unifyPay(orderSn,param.getFrom(), BillDetailEnum.TYPE_1.getValue(),"小程序充值");
            Map<String,String> jsConfig = new HashMap<>();
            jsConfig.put("timeStamp",wxPayMpOrderResult.getTimeStamp());
            jsConfig.put("appId",wxPayMpOrderResult.getAppId());
            jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
            jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
            jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
            jsConfig.put("signType",wxPayMpOrderResult.getSignType());
            map.put("data",jsConfig);
        }else if(AppFromEnum.APP.getValue().equals(param.getFrom())){
            WxPayAppOrderResult wxPayAppOrderResult = (WxPayAppOrderResult)weixinPayService
                    .unifyPay(orderSn,param.getFrom(), BillDetailEnum.TYPE_1.getValue(),"app充值");
            Map<String,String> jsConfig = new HashMap<>();
            jsConfig.put("partnerid",wxPayAppOrderResult.getPartnerId());
            jsConfig.put("appid",wxPayAppOrderResult.getAppId());
            jsConfig.put("prepayid",wxPayAppOrderResult.getPrepayId());
            jsConfig.put("package",wxPayAppOrderResult.getPackageValue());
            jsConfig.put("noncestr",wxPayAppOrderResult.getNonceStr());
            jsConfig.put("timestamp",wxPayAppOrderResult.getTimeStamp());
            jsConfig.put("sign",wxPayAppOrderResult.getSign());
            map.put("data",jsConfig);
        }else{
            WxPayMpOrderResult result = (WxPayMpOrderResult)weixinPayService
                    .unifyPay(orderSn,param.getFrom(), BillDetailEnum.TYPE_1.getValue(),"公众号充值");
            Map<String,String> config = new HashMap<>();
            config.put("timestamp",result.getTimeStamp());
            config.put("appId",result.getAppId());
            config.put("nonceStr",result.getNonceStr());
            config.put("package",result.getPackageValue());
            config.put("signType",result.getSignType());
            config.put("paySign",result.getPaySign());
            map.put("data",config);
        }




        return ApiResult.ok(map);
    }




    /**
     * app充值
     */
    @AppLog(value = "微信app充值", type = 1)
    @AuthCheck
    @PostMapping("/recharge/app/wechat")
    @ApiOperation(value = "app充值",notes = "app充值",response = ApiResult.class)
    public ApiResult<Map<String,Object>> wechatPay(@Valid @RequestBody RechargeParam param){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("type",param.getFrom());

        Map<String,String> data = recharge(param, PayTypeEnum.WEIXIN.getValue());

        WxPayAppOrderResult wxPayAppOrderResult = (WxPayAppOrderResult)weixinPayService
                .unifyPay(data.get("orderSn"),param.getFrom(), BillDetailEnum.TYPE_1.getValue(), "加盟店铺");
        Map<String,String> jsConfig = new HashMap<>();
        jsConfig.put("partnerid",wxPayAppOrderResult.getPartnerId());
        jsConfig.put("appid",wxPayAppOrderResult.getAppId());
        jsConfig.put("prepayid",wxPayAppOrderResult.getPrepayId());
        jsConfig.put("package",wxPayAppOrderResult.getPackageValue());
        jsConfig.put("noncestr",wxPayAppOrderResult.getNonceStr());
        jsConfig.put("timestamp",wxPayAppOrderResult.getTimeStamp());
        jsConfig.put("sign",wxPayAppOrderResult.getSign());
        map.put("data",jsConfig);

        return ApiResult.ok(map);
    }

    @AppLog(value = "支付宝app充值", type = 1)
    @AuthCheck
    @ApiOperation("支付宝支付")
    @PostMapping(value = "/recharge/app/alipay")
    public ApiResult<String> aliPay(@Valid @RequestBody RechargeParam param) throws Exception{
        Map<String,String> map = recharge(param, PayTypeEnum.ALI.getValue());

        AlipayConfig alipay = aliPayService.getAlipayConfig();
        TradeVo trade = new TradeVo();
        trade.setTotalAmount(map.get("price"));
        trade.setOutTradeNo(map.get("orderSn"));
        trade.setSubject("加盟星选会员");
        trade.setBody("加盟星选会员");
        log.info("加盟星选会员{}", JSON.toJSONString(trade));
        String payUrl = alipayConfigService.toPayAsApp(alipay,trade);
        return ApiResult.ok(payUrl);
    }

    @AppLog(value = "ios充值", type = 1)
    @AuthCheck
    @ApiOperation("IOS支付")
    @PostMapping(value = "/recharge/app/ios")
    public ApiResult<Map<String,String>> iosPay(@Valid @RequestBody RechargeParam param) throws Exception{
        Map<String,String> map = recharge(param, PayTypeEnum.IOS.getValue());
        return ApiResult.ok(map);
    }
    protected Map<String,String> recharge(RechargeParam param,String rechargeType) {
        MwUser user = LocalUser.getUser();

        MwSystemGroupData systemGroupData = systemGroupDataService.getById(param.getRecharId());
        if(systemGroupData == null) {
            throw new MshopException("充值方案不存在");
        }
        UserLevelDto userLevelDto = systemUserLevelService.getLevelInfo(param.getPlatform());
        MwSystemUserLevelQueryVo userLevelQueryVo = userLevelDto.getList().stream().filter(mwSystemUserLevelQueryVo -> mwSystemUserLevelQueryVo.getRechargeId().equals(param.getRecharId())).findFirst().orElse(null);

        JSONObject jsonObject = JSON.parseObject(systemGroupData.getValue());
        String price = jsonObject.getString("price");
        String orderSn = userRechargeService.addRecharge(
                user,price,rechargeType, userLevelQueryVo.getGrade(), param.getPlatform(), param.getRecharId(), 0L);

        Map<String,String> map = new LinkedHashMap<>();
        map.put("orderSn",orderSn);
        map.put("price",price);
        return map;
    }

    @AuthCheck
    @PostMapping("/recharge/coupon")
    @ApiOperation(value = "获取当前会员支付优惠金额",notes = "获取当前会员支付优惠金额")
    public ApiResult<Object> payCoupon(@Valid @RequestBody RechargeCouponParam param){
        MwUser mwUser = LocalUser.getUser();

        String key = OrderUtil.getCouponKey(param.getPlatform(), mwUser.getUid());
        Object couponObj = redisUtil.get(key);
        Map data = new HashMap();
        BigDecimal coupon;
        if(couponObj == null) {
            MwSystemGroupData systemGroupData = systemGroupDataService.getById(param.getRechargeId());
            if(systemGroupData == null) {
                throw new MshopException("充值方案不存在");
            }
            JSONObject jsonObject = JSON.parseObject(systemGroupData.getValue());
            String price = jsonObject.getString("price");
            Double priceD = Double.parseDouble(price);
            Double scale = OrderUtil.randomNumber(1,3);
            coupon = NumberUtil.round(NumberUtil.div(NumberUtil.mul(priceD, scale), 100),2);
            redisUtil.set(key, coupon, 24*3600);

        } else {
            coupon = (BigDecimal) couponObj;
        }
        data.put("coupon", coupon);
        return ApiResult.ok(data);
    }

    @AuthCheck
    @PostMapping("/recharge/result")
    @ApiOperation(value = "记录银行卡支付结果",notes = "记录银行卡支付结果")
    public ApiResult<Object> rechargeResult(@Valid @RequestBody RechargeResultParam param){
        MwUserRecharge recharge = userRechargeService.getOne(Wrappers.<MwUserRecharge>lambdaQuery()
                .eq(MwUserRecharge::getOrderId, param.getOrderId()));
        if(recharge != null) {
            recharge.setResult(param.getResult());
            userRechargeService.updateById(recharge);
        }
        return ApiResult.ok("ok");
    }
}

