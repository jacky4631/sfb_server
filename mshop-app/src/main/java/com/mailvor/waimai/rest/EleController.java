/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.waimai.rest;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.ele.EleService;
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

import java.io.IOException;

import static com.mailvor.modules.meituan.constants.MeituanConstants.MT_GROUP_NAME_ACTIVITY_LIST;

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
public class EleController {
    private static TimedCache<String, Object> timedCache = CacheUtil.newTimedCache(30*60*1000);

    private final EleService eleService;
    private final MwSystemGroupDataService mwSystemGroupDataService;


    @PostMapping("/ele/activity/code")
    @ApiOperation(value = "会场转链",notes = "会场转链")
    public ApiResult<Object> activityCode(@RequestBody JSONObject body) throws IOException {
        return ApiResult.ok(eleService.contentParse(body.getString("activityId"), body.getLong("uid")));
    }

    @ApiOperation(value = "查询美团活动列表")
    @GetMapping(value = "/ele/activity/list")
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

