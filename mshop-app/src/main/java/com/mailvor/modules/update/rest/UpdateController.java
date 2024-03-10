/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.update.rest;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.shop.domain.MwAppVersion;
import com.mailvor.modules.shop.service.MwAppVersionService;
import com.mailvor.modules.utils.TkUtil;
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

    @Resource
    private MwAppVersionService versionService;

    @AnonymousAccess
    @AppLog(value = "app更新配置", type = 1)
    @GetMapping("/update/config")
    @ApiOperation(value = "app更新配置",notes = "app更新配置")
    public ApiResult<MwAppVersion> updateConfig(){
        MwAppVersion appVersion;

        String updateKey = TkUtil.getMixedPlatformKey(UPDATE_CONFIG);
        Object objS = redisUtils.get(updateKey);
        if(objS == null) {
            appVersion = versionService.getOne(new LambdaQueryWrapper<MwAppVersion>()
                    .eq(MwAppVersion::getEnable, 1).eq(MwAppVersion::getPlatformName, PAY_NAME));
            if(appVersion != null) {
                redisUtils.set(updateKey, appVersion);
            }
        } else {
            appVersion = (MwAppVersion)objS;
        }
        return ApiResult.ok(appVersion);
    }

}

