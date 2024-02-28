/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.user.domain.MwSystemUserTask;
import com.mailvor.modules.user.service.MwSystemUserTaskService;
import com.mailvor.modules.user.service.dto.MwSystemUserTaskQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Api(tags = "商城:用户任务管理")
@RestController
@RequestMapping("api")
public class SystemUserTaskController {

    private final MwSystemUserTaskService mwSystemUserTaskService;

    public SystemUserTaskController(MwSystemUserTaskService mwSystemUserTaskService) {
        this.mwSystemUserTaskService = mwSystemUserTaskService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwSystemUserTask")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERTASK_ALL','MWSYSTEMUSERTASK_SELECT')")
    public ResponseEntity getMwSystemUserTasks(MwSystemUserTaskQueryCriteria criteria,
                                               Pageable pageable){
        Sort sort = Sort.by(Sort.Direction.ASC, "level_id");
        Pageable pageableT = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                sort);
        return new ResponseEntity(mwSystemUserTaskService.queryAll(criteria,pageableT),
                HttpStatus.OK);
    }

    @Log("新增")
    @ApiOperation(value = "新增")
    @PostMapping(value = "/mwSystemUserTask")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERTASK_ALL','MWSYSTEMUSERTASK_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwSystemUserTask resources){
        return new ResponseEntity(mwSystemUserTaskService.save(resources),HttpStatus.CREATED);
    }

    @Log("修改")
    @ApiOperation(value = "修改")
    @PutMapping(value = "/mwSystemUserTask")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERTASK_ALL','MWSYSTEMUSERTASK_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwSystemUserTask resources){
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        mwSystemUserTaskService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwSystemUserTask/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMUSERTASK_ALL','MWSYSTEMUSERTASK_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        mwSystemUserTaskService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
