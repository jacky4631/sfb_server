/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwSystemStore;
import com.mailvor.modules.shop.domain.MwSystemStoreStaff;
import com.mailvor.modules.shop.service.MwSystemStoreService;
import com.mailvor.modules.shop.service.MwSystemStoreStaffService;
import com.mailvor.modules.shop.service.dto.MwSystemStoreStaffDto;
import com.mailvor.modules.shop.service.dto.MwSystemStoreStaffQueryCriteria;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
* @author huangyu
* @date 2020-03-22
*/
@Api(tags = "门店店员管理")
@RestController
@RequestMapping("/api/mwSystemStoreStaff")
public class SystemStoreStaffController {

    private final MwSystemStoreStaffService mwSystemStoreStaffService;
    private final MwSystemStoreService mwSystemStoreService;

    private final IGenerator generator;

    public SystemStoreStaffController(MwSystemStoreService mwSystemStoreService, MwSystemStoreStaffService mwSystemStoreStaffService, IGenerator generator) {
        this.mwSystemStoreService = mwSystemStoreService;
        this.mwSystemStoreStaffService = mwSystemStoreStaffService;
        this.generator = generator;
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('mwSystemStoreStaff:list')")
    public void download(HttpServletResponse response, MwSystemStoreStaffQueryCriteria criteria) throws IOException {
        mwSystemStoreStaffService.download(generator.convert(mwSystemStoreStaffService.queryAll(criteria), MwSystemStoreStaffDto.class), response);
    }

    @GetMapping
    @Log("查询门店店员")
    @ApiOperation("查询门店店员")
    @PreAuthorize("@el.check('mwSystemStoreStaff:list')")
    public ResponseEntity<Object> getMwSystemStoreStaffs(MwSystemStoreStaffQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwSystemStoreStaffService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增门店店员")
    @ApiOperation("新增门店店员")
    @PreAuthorize("@el.check('mwSystemStoreStaff:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwSystemStoreStaff resources){
        MwSystemStore systemStore = mwSystemStoreService.getOne(Wrappers.<MwSystemStore>lambdaQuery()
                .eq(MwSystemStore::getId,resources.getStoreId()));
        resources.setStoreName(systemStore.getName());
        return new ResponseEntity<>(mwSystemStoreStaffService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改门店店员")
    @ApiOperation("修改门店店员")
    @PreAuthorize("@el.check('mwSystemStoreStaff:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwSystemStoreStaff resources){
        MwSystemStore systemStore = mwSystemStoreService.getOne(Wrappers.<MwSystemStore>lambdaQuery()
                .eq(MwSystemStore::getId,resources.getStoreId()));
        resources.setStoreName(systemStore.getName());
        mwSystemStoreStaffService.saveOrUpdate(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除门店店员")
    @ApiOperation("删除门店店员")
    @PreAuthorize("@el.check('mwSystemStoreStaff:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        mwSystemStoreStaffService.removeByIds(new ArrayList<>(Arrays.asList(ids)));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
