/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.user.service.MwSystemUserLevelService;
import com.mailvor.modules.user.service.dto.MwSystemUserLevelQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author mazhongjun
* @date 2019-12-04
*/
@Api(tags = "商城：用户等级管理")
@RestController
@RequestMapping("api")
public class SystemUserLevelController {

    private final MwSystemUserLevelService mwSystemUserLevelService;

    public SystemUserLevelController(MwSystemUserLevelService mwSystemUserLevelService) {
        this.mwSystemUserLevelService = mwSystemUserLevelService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwSystemUserLevel")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERLEVEL_ALL','MWSYSTEMUSERLEVEL_SELECT')")
    public ResponseEntity getMwSystemUserLevels(MwSystemUserLevelQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwSystemUserLevelService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增")
    @ApiOperation(value = "新增")
    @PostMapping(value = "/mwSystemUserLevel")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERLEVEL_ALL','MWSYSTEMUSERLEVEL_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwSystemUserLevel resources){
        return new ResponseEntity<>(mwSystemUserLevelService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @Log("修改")
    @ApiOperation(value = "修改")
    @PutMapping(value = "/mwSystemUserLevel")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERLEVEL_ALL','MWSYSTEMUSERLEVEL_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwSystemUserLevel resources){
        mwSystemUserLevelService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwSystemUserLevel/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERLEVEL_ALL','MWSYSTEMUSERLEVEL_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        mwSystemUserLevelService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
