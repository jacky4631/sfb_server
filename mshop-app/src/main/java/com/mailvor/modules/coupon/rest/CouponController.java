/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.coupon.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.util.NumberUtil;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.common.aop.NoRepeatSubmit;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.activity.service.MwStoreCouponIssueService;
import com.mailvor.modules.activity.service.MwStoreCouponUserService;
import com.mailvor.modules.activity.vo.StoreCouponUserVo;
import com.mailvor.modules.activity.vo.MwStoreCouponIssueQueryVo;
import com.mailvor.modules.activity.vo.MwStoreCouponUserQueryVo;
import com.mailvor.modules.coupon.param.MwStoreCouponQueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券
 * </p>
 *
 * @author huangyu
 * @since 2019-10-02
 */
@Slf4j
@RestController
@Tag(name = "营销:优惠券")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CouponController {

    private final MwStoreCouponIssueService couponIssueService;
    private final MwStoreCouponUserService storeCouponUserService;

    /**
     * 可领取优惠券列表
     */
    @AuthCheck
    @GetMapping("/coupons")
    @Parameters({
            @Parameter(name = "page", description = "页码,默认为1"),
            @Parameter(name = "limit", description = "页大小,默认为10"),
            @Parameter(name = "productId", description = "产品ID"),
            @Parameter(name = "type", description = "优惠券类型 0通用券 1商品券 2内部券")
    })
    @Operation(summary = "可领取优惠券列表", description = "可领取优惠券列表")
    public ApiResult<List<MwStoreCouponIssueQueryVo>> getList(@RequestParam(value = "page",defaultValue = "1") int page,
                                                              @RequestParam(value = "limit",defaultValue = "10") int limit,
                                                              @RequestParam(value = "productId",required = false) Long productId,
                                                              @RequestParam(value = "type",required = false) Integer type){
        Long uid = LocalUser.getUser().getUid();
        return ApiResult.ok(couponIssueService.getCouponList(page, limit,uid,productId,type));
    }

    /**
     * 领取优惠券
     */
    @AppLog(value = "领取优惠券", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/coupon/receive")
    @Operation(summary = "领取优惠券", description = "领取优惠券")
    public ApiResult<Boolean> receive(@Validated @RequestBody MwStoreCouponQueryParam param){
        Long uid = LocalUser.getUser().getUid();
        if(!NumberUtil.isNumber(param.getCouponId())){
           throw new MshopException("参数非法");
        }
        Integer couponId = Integer.valueOf(param.getCouponId());
        couponIssueService.issueUserCoupon(couponId,uid);
        return ApiResult.ok();
    }

    /**
     * 用户已领取优惠券
     */
    @AppLog(value = "查看已领取优惠券", type = 1)
    @AuthCheck
    @GetMapping("/coupons/user/{type}")
    @Operation(summary = "用户已领取优惠券", description = "用户已领取优惠券")
    public ApiResult<List<MwStoreCouponUserQueryVo>> getUserList(){
        Long uid = LocalUser.getUser().getUid();
        List<MwStoreCouponUserQueryVo> list = storeCouponUserService.getUserCoupon(uid);
        return ApiResult.ok(list);
    }

    /**
     * 用户已领取优惠券pc
     */
    @AppLog(value = "用户已领取优惠券pc", type = 1)
    @AuthCheck
    @GetMapping("/coupons/user/pc/{type}")
    @Operation(summary = "用户已领取优惠券pc", description = "用户已领取优惠券pc")
    public ApiResult<Object> getUserPCList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit, @PathVariable(value = "type") Integer type){
        Long uid = LocalUser.getUser().getUid();
        Map<String, Object> map = storeCouponUserService.getUserPCCoupon(uid,page,limit,type);
        Long total = (Long) map.get("total");
        Long totalPage = (Long) map.get("totalPage");
        return ApiResult.resultPage(total, totalPage.intValue(), map.get("list"));
    }

    /**
     * 优惠券 订单获取
     */
    @AuthCheck
    @GetMapping("/coupons/order/{cartIds}")
    @Parameters({
            @Parameter(name = "cartIds", description = "购物车ID,多个用,分割")
    })
    @Operation(summary = "优惠券订单获取", description = "优惠券订单获取")
    public ApiResult<List<StoreCouponUserVo>> orderCoupon(@PathVariable String cartIds){
        Long uid = LocalUser.getUser().getUid();
        return ApiResult.ok(storeCouponUserService.beUsableCouponList(uid,cartIds));
    }


}

