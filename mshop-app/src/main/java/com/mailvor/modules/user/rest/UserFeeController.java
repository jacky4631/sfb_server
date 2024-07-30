/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;


import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserFeeLogOptService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.vo.UserFeeQueryVo;
import com.mailvor.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
@Api(value = "用户预估收益", tags = "用户:用户预估收益")
public class UserFeeController {
    private final MwUserService mwUserService;
    private final MwUserFeeLogOptService feeLogOptService;
    private final RedisUtils redisUtils;

    @AuthCheck
    @GetMapping("/userfee")
    @ApiOperation(value = "获取用户预估收益",notes = "获取用户预估收益",response = UserFeeQueryVo.class)
    public ApiResult<Object> userFee(){
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(mwUserService.userFeeInfo(mwUser));
    }

    @AuthCheck
    @GetMapping("/userfeedetail")
    @ApiOperation(value = "获取用户详细预估收益",notes = "获取用户详细预估收益",response = Map.class)
    public ApiResult<Object> userFeeDetail(@RequestParam(value = "type",defaultValue = "1") int type,
                                           @RequestParam(value = "cid",defaultValue = "1") int cid,
                                           @RequestParam(value = "uid") Long uid){
        MwUser mwUser = LocalUser.getUser();
        Long curUid = mwUser.getUid();
        if(uid != null && uid > 0) {
            //todo 需要校验uid用户是否是用户
            curUid = uid;
        }
        return ApiResult.ok(feeLogOptService.userFeeDetail(curUid, cid, type));
    }

    @AuthCheck
    @GetMapping("/userfeee")
    public ApiResult<Object> userFeeE(){
        MwUser mwUser = LocalUser.getUser();
        Object obj = redisUtils.get("user:fee:e:" + mwUser.getUid());
        if(obj == null) {
            mwUser = mwUserService.getById(mwUser.getUid());
            obj = redisUtils.get("user:fee:e:" + mwUser.getPhone());
        }
        Map map = new HashMap();
        map.put("e", obj);
        return ApiResult.ok(map);
    }
}

