/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;


import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwMaterial;
import com.mailvor.modules.shop.service.MwMaterialService;
import com.mailvor.modules.shop.service.dto.MwMaterialQueryCriteria;
import com.mailvor.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
* @date 2020-01-09
*/
@Api(tags = "商城：素材管理管理")
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    private final MwMaterialService mwMaterialService;

    public MaterialController(MwMaterialService mwMaterialService) {
        this.mwMaterialService = mwMaterialService;
    }



    @GetMapping(value = "/page")
    @Log("查询素材管理")
    @ApiOperation("查询素材管理")
    public ResponseEntity<Object> getMwMaterials(MwMaterialQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwMaterialService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增素材管理")
    @ApiOperation("新增素材管理")
    public ResponseEntity<Object> create(@Validated @RequestBody MwMaterial resources){
        resources.setCreateId(SecurityUtils.getUsername());
        return new ResponseEntity<>(mwMaterialService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改素材管理")
    @ApiOperation("修改素材管理")
    public ResponseEntity<Object> update(@Validated @RequestBody MwMaterial resources){
        mwMaterialService.saveOrUpdate(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除素材管理")
    @ApiOperation("删除素材管理")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteAll(@PathVariable String id) {
        mwMaterialService.removeById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
