/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.waimai.rest;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.meituan.MeituanService;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import static com.mailvor.modules.meituan.constants.MeituanConstants.*;

/**
 * <p>
 * 用户控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-16
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "用户中心", tags = "用户:用户中心")
public class MeituanController {
    private static TimedCache<String, Object> timedCache = CacheUtil.newTimedCache(30*60*1000);

    private final MeituanService meituanService;
    private final MwSystemGroupDataService mwSystemGroupDataService;

    @GetMapping("/mt/provinces")
    @ApiOperation(value = "获取省份",notes = "获取省份")
    public ApiResult<Object> provinces(@RequestParam(required = false) String platformId) {
        String provinceKey = MT_URL_PROVINCE_ALL + "_" + platformId;
        Object provinces = timedCache.get(provinceKey);
        if(provinces == null) {
            provinces = meituanService.getProvinces(platformId);
            timedCache.put(provinceKey, provinces);
        }
        return ApiResult.ok(provinces);
    }

    @GetMapping("/mt/cities")
    @ApiOperation(value = "获取城市",notes = "获取城市")
    public ApiResult<Object> cities(@RequestParam(required = false) String platformId, @RequestParam String provinceId) {
        String cityKey = MT_URL_CITY_ALL + "_" + platformId + "_" + provinceId;
        Object provinces = timedCache.get(cityKey);
        if(provinces == null) {
            provinces = meituanService.getCities(platformId, provinceId);
            timedCache.put(cityKey, provinces);
        }
        return ApiResult.ok(provinces);
    }

    @GetMapping("/mt/categories")
    @ApiOperation(value = "获取类目",notes = "获取类目")
    public ApiResult<Object> cities(@RequestParam(required = false) String platformId,
                                    @RequestParam String cityId,
                                    @RequestParam(required = false) String cat0Id) {
        return ApiResult.ok(meituanService.getCategories(platformId, cityId, cat0Id));
    }

    @PostMapping("/mt/goods")
    @ApiOperation(value = "获取商品",notes = "获取商品")
    public ApiResult<Object> cities(@RequestBody JSONObject body) {
        return ApiResult.ok(meituanService.goodsList(body));
    }

    @PostMapping("/mt/order/cps")
    @ApiOperation(value = "获取cps订单",notes = "获取cps订单")
    public ApiResult<Object> orderCPS(@RequestBody JSONObject body) {
        return ApiResult.ok(meituanService.orderCPS(body));
    }

    @PostMapping("/mt/order/cpa")
    @ApiOperation(value = "获取cpa订单",notes = "获取cpa订单")
    public ApiResult<Object> orderCPA(@RequestBody JSONObject body) {
        return ApiResult.ok(meituanService.orderCPA(body));
    }

    @PostMapping("/mt/order/refund")
    @ApiOperation(value = "获取refund订单",notes = "获取refund订单")
    public ApiResult<Object> orderRefund(@RequestBody JSONObject body) {
        return ApiResult.ok(meituanService.orderRefund(body));
    }

    @PostMapping("/mt/activity/code")
    @ApiOperation(value = "会场转链",notes = "会场转链")
    public ApiResult<Object> activityCode(@RequestBody JSONObject body) {
        return ApiResult.ok(meituanService.getCode(body.getString("activityId"), body.getLong("uid")));
    }

    @ApiOperation(value = "查询美团活动列表")
    @GetMapping(value = "/mt/activity/list")
    public ApiResult<Object> getActivityList(@RequestParam(value = "page",defaultValue = "1") int page,
                                                @RequestParam(value = "limit",defaultValue = "10") int limit) {
        MwSystemGroupDataQueryCriteria criteria = new MwSystemGroupDataQueryCriteria();
        criteria.setGroupName(MT_GROUP_NAME_ACTIVITY_LIST);
        criteria.setStatus(1);
        Sort sort = Sort.by(Sort.Direction.ASC, "sort");
        Pageable pageableT = PageRequest.of(page-1, limit, sort);
        return ApiResult.ok(mwSystemGroupDataService.list(criteria, pageableT));
    }
}

