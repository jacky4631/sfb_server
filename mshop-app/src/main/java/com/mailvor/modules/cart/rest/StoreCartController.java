/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.cart.rest;

import com.mailvor.annotation.Limit;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.common.aop.NoRepeatSubmit;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.cart.param.CartIdsParm;
import com.mailvor.modules.cart.param.CartNumParam;
import com.mailvor.modules.cart.param.CartParam;
import com.mailvor.modules.cart.service.MwStoreCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * 购物车控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-25
 */
@Slf4j
@RestController
@Api(value = "购物车", tags = "商城:购物车")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreCartController {

    private final MwStoreCartService storeCartService;

    /**
     * 购物车 获取数量
     */
    @AuthCheck
    @GetMapping("/cart/count")
    @ApiOperation(value = "获取数量",notes = "获取数量")
    public ApiResult<Map<String,Object>> count(){
        Map<String,Object> map = new LinkedHashMap<>();
        Long uid = LocalUser.getUser().getUid();
        map.put("count",storeCartService.getUserCartNum(uid));
        return ApiResult.ok(map);
    }

    /**
     * 购物车 添加
     */
    @AppLog(value = "购物车 添加", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/cart/add")
    @ApiOperation(value = "添加购物车",notes = "添加购物车")
    @Limit(key = "cart_limit", period = 60, count = 30, name = "cartLimit", prefix = "mshop")
    public ApiResult<Map<String,Object>> add(@Validated @RequestBody CartParam cartParam){
        Map<String,Object> map = new LinkedHashMap<>();
        Long uid = LocalUser.getUser().getUid();
        map.put("cartId",storeCartService.addCart(uid,cartParam.getProductId(),cartParam.getCartNum(),
                cartParam.getUniqueId(),cartParam.getIsNew(),cartParam.getCombinationId(),
                cartParam.getSecKillId(),cartParam.getBargainId()));
        return ApiResult.ok(map);
    }


    /**
     * 购物车列表
     */
    @AppLog(value = "查看购物车列表", type = 1)
    @AuthCheck
    @GetMapping("/cart/list")
    @ApiOperation(value = "购物车列表",notes = "购物车列表")
    public ApiResult<Map<String,Object>> getList(){
        Long uid = LocalUser.getUser().getUid();
        return ApiResult.ok(storeCartService.getUserProductCartList(uid,"",null));
    }

    /**
     * 修改产品数量
     */
    @AppLog(value = "修改购物车产品数量", type = 1)
    @AuthCheck
    @PostMapping("/cart/num")
    @ApiOperation(value = "修改产品数量",notes = "修改产品数量")
    public ApiResult<Boolean> cartNum(@Validated @RequestBody CartNumParam param){
        Long uid = LocalUser.getUser().getUid();
        storeCartService.changeUserCartNum(param.getId(), param.getNumber(),uid);
        return ApiResult.ok();
    }

    /**
     * 购物车删除产品
     */
    @AppLog(value = "购物车删除产品", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/cart/del")
    @ApiOperation(value = "购物车删除产品",notes = "购物车删除产品")
    public ApiResult<Boolean> cartDel(@Validated @RequestBody CartIdsParm parm){
        Long uid = LocalUser.getUser().getUid();
        storeCartService.removeUserCart(uid, parm.getIds());
        return ApiResult.ok();
    }





}

