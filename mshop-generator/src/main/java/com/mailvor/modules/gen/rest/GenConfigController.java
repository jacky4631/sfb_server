/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.gen.rest;

import com.mailvor.modules.gen.domain.GenConfig;
import com.mailvor.modules.gen.service.GenConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zheng Jie
 * @date 2019-01-14
 */
@RestController
@RequestMapping("/api/genConfig")
@Api(tags = "系统：代码生成器配置管理")
public class GenConfigController {

    private final GenConfigService genConfigService;

    public GenConfigController(GenConfigService genConfigService) {
        this.genConfigService = genConfigService;
    }

    @ApiOperation("查询")
    @GetMapping(value = "/{tableName}")
    public ResponseEntity<Object> get(@PathVariable String tableName){
        return new ResponseEntity<>(genConfigService.find(tableName), HttpStatus.OK);
    }

    @ApiOperation("修改")
    @PutMapping
    public ResponseEntity<Object> emailConfig(@Validated @RequestBody GenConfig genConfig){
        return new ResponseEntity<>(genConfigService.update(genConfig.getTableName(), genConfig),HttpStatus.OK);
    }
}
