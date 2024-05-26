/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import cn.hutool.core.util.ObjectUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.shop.service.dto.MwSystemConfigQueryCriteria;
import com.mailvor.modules.mp.config.WxMpConfiguration;
import com.mailvor.modules.mp.config.WxMaConfiguration;
import com.mailvor.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author huangyu
* @date 2019-10-10
*/
@Api(tags = "商城:配置管理")
@RestController
@RequestMapping("api")
public class SystemConfigController {

    private final MwSystemConfigService mwSystemConfigService;

    public SystemConfigController(MwSystemConfigService mwSystemConfigService) {
        this.mwSystemConfigService = mwSystemConfigService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwSystemConfig")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMCONFIG_ALL','MWSYSTEMCONFIG_SELECT')")
    public ResponseEntity getMwSystemConfigs(MwSystemConfigQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwSystemConfigService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增或修改")
    @ApiOperation(value = "新增或修改")
    @PostMapping(value = "/mwSystemConfig")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMCONFIG_ALL','MWSYSTEMCONFIG_CREATE')")
    public ResponseEntity create(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        jsonObject.forEach(
                (key,value)->{
                    MwSystemConfig mwSystemConfig = mwSystemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                            .eq(MwSystemConfig::getMenuName,key));
                    MwSystemConfig mwSystemConfigModel = new MwSystemConfig();
                    mwSystemConfigModel.setMenuName(key);
                    mwSystemConfigModel.setValue(value.toString());
                    //重新配置微信相关
                    if(SystemConfigConstants.WECHAT_APPID.equals(key)){
                        WxMpConfiguration.removeWxMpService();
                        WxMaConfiguration.removeWxMaService();
                    }

                    if(SystemConfigConstants.EXP_APPID.equals(key)){
                        RedisUtil.del(ShopConstants.MSHOP_EXPRESS_SERVICE);
                    }
                    RedisUtil.set(key,value.toString(),0);
                    if(ObjectUtil.isNull(mwSystemConfig)){
                        mwSystemConfigService.save(mwSystemConfigModel);
                    }else{
                        mwSystemConfigModel.setId(mwSystemConfig.getId());
                        mwSystemConfigService.saveOrUpdate(mwSystemConfigModel);
                    }
                }
        );

        return new ResponseEntity(HttpStatus.CREATED);
    }



}
