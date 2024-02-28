/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.update.rest;


import com.alibaba.fastjson.JSONObject;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.constant.SystemConfigConstants.UPDATE_CONFIG;

/**
 * <p>
 * 用户提现 前端控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-11-11
 */
@Slf4j
@RestController
@Api(value = "app更新", tags = "APP:app更新")
public class UpdateController {
    @Resource
    private RedisUtils redisUtils;


    @AnonymousAccess
    @AppLog(value = "app更新配置", type = 1)
    @GetMapping("/update/config")
    @ApiOperation(value = "app更新配置",notes = "app更新配置")
    public ApiResult<JSONObject> updateConfig(){
        String updateKey = UPDATE_CONFIG + "_" + PAY_NAME;
        Object objS = redisUtils.get(updateKey);
        if(objS == null) {
            objS = redisUtils.get(UPDATE_CONFIG);
        }
        JSONObject obj = (JSONObject) objS;
        return ApiResult.ok(obj);
    }

}

