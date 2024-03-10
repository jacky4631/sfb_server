/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwAppVersion;
import com.mailvor.modules.shop.service.MwAppVersionService;
import com.mailvor.modules.shop.service.dto.MwAppVersionDto;
import com.mailvor.modules.shop.service.dto.MwAppVersionQueryCriteria;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.constant.SystemConfigConstants.UPDATE_CONFIG;

/**
* @author lioncity
* @date 2020-12-09
*/
@AllArgsConstructor
@Api(tags = "app版本控制管理")
@RestController
@RequestMapping("/api/mwAppVersion")
public class MwAppVersionController {

    private final MwAppVersionService versionService;
    private final IGenerator generator;

    @Resource
    private RedisUtils redisUtils;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwAppVersion:list')")
    public void download(HttpServletResponse response, MwAppVersionQueryCriteria criteria) throws IOException {
        criteria.setPlatformName(PAY_NAME);
        versionService.download(generator.convert(versionService.queryAll(criteria), MwAppVersionDto.class), response);
    }

    @GetMapping
    @Log("查询app版本控制")
    @ApiOperation("查询app版本控制")
    @PreAuthorize("@el.check('admin','mwAppVersion:list')")
    public ResponseEntity<PageResult<MwAppVersionDto>> getMwAppVersions(MwAppVersionQueryCriteria criteria, Pageable pageable){
        criteria.setPlatformName(PAY_NAME);
        return new ResponseEntity<>(versionService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @PostMapping
    @Log("新增app版本控制")
    @ApiOperation("新增app版本控制")
    @PreAuthorize("@el.check('admin','mwAppVersion:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwAppVersion resources){
        //如果新增的版本是启用，需要关闭所有其他版本
        if(resources.getEnable() == 1) {
            MwAppVersion appVersion = new MwAppVersion();
            appVersion.setEnable(0);
            appVersion.setPlatformName(PAY_NAME);
            versionService.update(appVersion, new LambdaQueryWrapper<>());
        }
        resources.setPlatformName(PAY_NAME);
        redisUtils.del(TkUtil.getMixedPlatformKey(UPDATE_CONFIG));
        return new ResponseEntity<>(versionService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @PutMapping
    @Log("修改app版本控制")
    @ApiOperation("修改app版本控制")
    @PreAuthorize("@el.check('admin','mwAppVersion:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwAppVersion resources){
        //如果编辑的版本是启用，需要关闭所有其他版本
        if(resources.getEnable() == 1) {
            MwAppVersion appVersion = new MwAppVersion();
            appVersion.setPlatformName(PAY_NAME);
            appVersion.setEnable(0);
            versionService.update(appVersion, new LambdaQueryWrapper<>());
        }
        resources.setPlatformName(PAY_NAME);
        versionService.updateById(resources);
        redisUtils.del(TkUtil.getMixedPlatformKey(UPDATE_CONFIG));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除app版本控制")
    @ApiOperation("删除app版本控制")
    @PreAuthorize("@el.check('admin','mwAppVersion:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        Arrays.asList(ids).forEach(id->{
            versionService.removeById(id);
        });
        redisUtils.del(TkUtil.getMixedPlatformKey(UPDATE_CONFIG));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
