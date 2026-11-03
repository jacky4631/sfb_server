/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.waimai.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.mailvor.common.json.JsonObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.meituan.MeituanService;
import com.mailvor.modules.meituan.param.MeituanGoodsParam;
import com.mailvor.modules.meituan.param.MeituanLinkParam;
import com.mailvor.modules.meituan.param.MeituanOrderParam;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataQueryCriteria;
import com.mailvor.modules.user.domain.MwUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
@Tag(name = "用户:用户中心")
public class MeituanController {
    private final MeituanService meituanService;
    private final MwSystemGroupDataService mwSystemGroupDataService;

    @PostMapping("/mt/goods")
    @Operation(summary = "获取商品", description = "获取商品")
    public ApiResult<Object> cities(@RequestBody MeituanGoodsParam body) {
        return ApiResult.ok(meituanService.goodsList(body));
    }

    @PostMapping("/mt/order/cps")
    @Operation(summary = "获取cps订单", description = "获取cps订单")
    public ApiResult<Object> orderCPS(@RequestBody MeituanOrderParam body) {
        return ApiResult.ok(meituanService.order(body));
    }

    @UserCheck
    @GetMapping("/mt/activity/code")
    @Operation(summary = "会场转链", description = "会场转链")
    public ApiResult<Object> activityCode(@RequestParam String activityId, @RequestParam(required = false,defaultValue = "4") Integer linkType) throws Exception {
        MwUser mwUser = LocalUser.getUser();
        Long uid = 0L;
        if(mwUser != null) {
            uid = mwUser.getUid();
        }
        MeituanLinkParam param = new MeituanLinkParam();
        param.setActId(activityId);
        param.setLinkType(linkType);
        JsonObject res = meituanService.getCode(param, uid);
        res.put("miniProgramPath",  res.getString("data"));
        return ApiResult.ok(res);
    }

    @Operation(summary = "查询美团活动列表")
    @GetMapping(value = "/mt/activity/list")
    public ApiResult<Object> getActivityList(@RequestParam(value = "page",defaultValue = "1") int page,
                                                @RequestParam(value = "limit",defaultValue = "100") int limit) {
        MwSystemGroupDataQueryCriteria criteria = new MwSystemGroupDataQueryCriteria();
        criteria.setGroupName(MT_GROUP_NAME_ACTIVITY_LIST);
        criteria.setStatus(1);
        Sort sort = Sort.by(Sort.Direction.ASC, "sort");
        Pageable pageableT = PageRequest.of(page-1, limit, sort);
        Map<String, Object> map = mwSystemGroupDataService.list(criteria, pageableT);
        List<JsonObject> list = (ArrayList)map.get("content");
        List<JsonObject> filterList = new ArrayList<>();
        LocalDateTime now = LocalDateTimeUtil.now();
        for(JsonObject obj : list) {
            String endTimeStr = obj.getString("endTime");
            LocalDateTime endTime = LocalDateTimeUtil.parse(endTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if(now.isBefore(endTime)){
                filterList.add(obj);
            }
        }
        map.put("content", filterList);
        return ApiResult.ok(map);
    }
}

