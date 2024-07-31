/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.order.domain.MwExpress;
import com.mailvor.modules.order.service.MwExpressService;
import com.mailvor.modules.order.service.dto.MwExpressQueryCriteria;
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
* @author huangyu
* @date 2019-12-12
*/
@Api(tags = "商城：快递管理")
@RestController
@RequestMapping("api")
public class ExpressController {


    private final MwExpressService mwExpressService;

    public ExpressController(MwExpressService mwExpressService) {
        this.mwExpressService = mwExpressService;
    }

    @Log("查询快递")
    @ApiOperation(value = "查询快递")
    @GetMapping(value = "/mwExpress")
    @PreAuthorize("hasAnyRole('admin','MWEXPRESS_ALL','MWEXPRESS_SELECT')")
    public ResponseEntity getMwExpresss(MwExpressQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwExpressService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增快递")
    @ApiOperation(value = "新增快递")
    @PostMapping(value = "/mwExpress")
    @PreAuthorize("hasAnyRole('admin','MWEXPRESS_ALL','MWEXPRESS_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwExpress resources){
        return new ResponseEntity<>(mwExpressService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @Log("修改快递")
    @ApiOperation(value = "修改快递")
    @PutMapping(value = "/mwExpress")
    @PreAuthorize("hasAnyRole('admin','MWEXPRESS_ALL','MWEXPRESS_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwExpress resources){
        mwExpressService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除快递")
    @ApiOperation(value = "删除快递")
    @DeleteMapping(value = "/mwExpress/{id}")
    @PreAuthorize("hasAnyRole('admin','MWEXPRESS_ALL','MWEXPRESS_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        mwExpressService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
