/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import com.mailvor.dozer.service.IGenerator;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwSystemStore;
import com.mailvor.modules.shop.service.MwSystemStoreService;
import com.mailvor.modules.shop.service.dto.MwSystemStoreDto;
import com.mailvor.modules.shop.service.dto.MwSystemStoreQueryCriteria;
import com.mailvor.utils.location.GetTencentLocationVO;
import com.mailvor.utils.location.LocationUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
* @date 2020-03-03
*/
@Api(tags = "门店管理")
@RestController
@RequestMapping("/api/mwSystemStore")
public class SystemStoreController {

    private final MwSystemStoreService mwSystemStoreService;
    private final IGenerator generator;
    public SystemStoreController(MwSystemStoreService mwSystemStoreService, IGenerator generator) {
        this.mwSystemStoreService = mwSystemStoreService;
        this.generator = generator;
    }


    @Log("所有门店")
    @ApiOperation("所有门店")
    @GetMapping(value = "/all")
    @PreAuthorize("@el.check('mwSystemStore:list')")
    public ResponseEntity<Object>  getAll(MwSystemStoreQueryCriteria criteria) {
        return new ResponseEntity<>(mwSystemStoreService.queryAll(criteria),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('mwSystemStore:list')")
    public void download(HttpServletResponse response, MwSystemStoreQueryCriteria criteria) throws IOException {
        mwSystemStoreService.download(generator.convert(mwSystemStoreService.queryAll(criteria), MwSystemStoreDto.class), response);
    }

    @GetMapping
    @Log("查询门店")
    @ApiOperation("查询门店")
    @PreAuthorize("@el.check('mwSystemStore:list')")
    public ResponseEntity<Object> getMwSystemStores(MwSystemStoreQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwSystemStoreService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping(value = "/getL")
    @Log("获取经纬度")
    @ApiOperation("获取经纬度")
    @PreAuthorize("@el.check('mwSystemStore:getl')")
    public ResponseEntity<GetTencentLocationVO> create(@Validated @RequestBody String jsonStr) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String addr = jsonObject.getString("addr");
        GetTencentLocationVO locationVO = LocationUtils.getLocation(addr);
        if(locationVO.getStatus()!=0){
            throw new BadRequestException(locationVO.getMessage());
        }
        return new ResponseEntity<>(locationVO, HttpStatus.CREATED);
    }

    @ForbidSubmit
    @PostMapping
    @Log("新增门店")
    @ApiOperation("新增门店")
    @PreAuthorize("@el.check('mwSystemStore:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwSystemStore resources){
        return new ResponseEntity<>(mwSystemStoreService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @PutMapping
    @Log("修改门店")
    @ApiOperation("修改门店")
    @PreAuthorize("@el.check('mwSystemStore:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwSystemStore resources){
        mwSystemStoreService.saveOrUpdate(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除门店")
    @ApiOperation("删除门店")
    @PreAuthorize("@el.check('mwSystemStore:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        mwSystemStoreService.removeByIds(new ArrayList<>(Arrays.asList(ids)));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
