/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import cn.hutool.core.util.ObjectUtil;
import com.mailvor.api.MshopException;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.domain.MwStoreBargain;
import com.mailvor.modules.activity.service.MwStoreBargainService;
import com.mailvor.modules.activity.service.dto.MwStoreBargainQueryCriteria;
import com.mailvor.modules.aop.ForbidSubmit;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author huangyu
* @date 2019-12-22
*/
@Api(tags = "商城:砍价管理")
@RestController
@RequestMapping("api")
public class StoreBargainController {

    private final MwStoreBargainService mwStoreBargainService;

    public StoreBargainController(MwStoreBargainService mwStoreBargainService) {
        this.mwStoreBargainService = mwStoreBargainService;
    }

    @Log("查询砍价")
    @ApiOperation(value = "查询砍价")
    @GetMapping(value = "/mwStoreBargain")
    @PreAuthorize("hasAnyRole('admin','MWSTOREBARGAIN_ALL','MWSTOREBARGAIN_SELECT')")
    public ResponseEntity getMwStoreBargains(MwStoreBargainQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreBargainService.queryAll(criteria,pageable),HttpStatus.OK);
    }



    @Log("修改砍价")
    @ApiOperation(value = "修改砍价")
    @PutMapping(value = "/mwStoreBargain")
    @PreAuthorize("hasAnyRole('admin','MWSTOREBARGAIN_ALL','MWSTOREBARGAIN_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreBargain resources){
        if(resources.getBargainMinPrice().compareTo(resources.getBargainMaxPrice()) >= 0){
            throw new MshopException("单次砍最低价不能高于单次砍最高价");
        }
        if(resources.getMinPrice().compareTo(resources.getPrice()) >= 0){
            throw new MshopException("允许砍到最低价不能高于砍价金额");
        }
        if(ObjectUtil.isNull(resources.getId())){
            return new ResponseEntity<>(mwStoreBargainService.save(resources),HttpStatus.CREATED);
        }else{
            mwStoreBargainService.saveOrUpdate(resources);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @ForbidSubmit
    @Log("删除砍价")
    @ApiOperation(value = "删除砍价")
    @DeleteMapping(value = "/mwStoreBargain/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTOREBARGAIN_ALL','MWSTOREBARGAIN_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        mwStoreBargainService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
