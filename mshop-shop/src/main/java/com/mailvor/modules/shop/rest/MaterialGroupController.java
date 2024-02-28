/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwMaterialGroup;
import com.mailvor.modules.shop.service.MwMaterialGroupService;
import com.mailvor.modules.shop.service.dto.MwMaterialGroupQueryCriteria;
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
@Api(tags = "商城:素材分组管理")
@RestController
@RequestMapping("/api/materialgroup")
public class MaterialGroupController {

    private final MwMaterialGroupService mwMaterialGroupService;

    public MaterialGroupController(MwMaterialGroupService mwMaterialGroupService) {
        this.mwMaterialGroupService = mwMaterialGroupService;
    }



    @GetMapping(value = "/page")
    @Log("查询素材分组")
    @ApiOperation("查询素材分组")
    public ResponseEntity<Object> getMwMaterialGroups(MwMaterialGroupQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwMaterialGroupService.queryAll(criteria,pageable),HttpStatus.OK);
    }


    @GetMapping(value = "/list")
    @Log("查询所有素材分组")
    @ApiOperation("查询所有素材分组")
    public ResponseEntity<Object> getMwMaterialGroupsList(MwMaterialGroupQueryCriteria criteria){
        return new ResponseEntity<>(mwMaterialGroupService.queryAll(criteria),HttpStatus.OK);
    }


    @PostMapping
    @Log("新增素材分组")
    @ApiOperation("新增素材分组")
    public ResponseEntity<Object> create(@Validated @RequestBody MwMaterialGroup resources){
        return new ResponseEntity<>(mwMaterialGroupService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @PutMapping
    @Log("修改素材分组")
    @ApiOperation("修改素材分组")
    public ResponseEntity<Object> update(@Validated @RequestBody MwMaterialGroup resources){
        mwMaterialGroupService.saveOrUpdate(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除素材分组")
    @ApiOperation("删除素材分组")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteAll(@PathVariable String id) {
        mwMaterialGroupService.removeById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
