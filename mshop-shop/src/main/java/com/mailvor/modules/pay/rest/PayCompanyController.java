/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.rest;

import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.pay.rest.param.PayCompanyEditParam;
import com.mailvor.modules.pay.rest.param.PayCompanyParam;
import com.mailvor.modules.pay.service.MwPayCompanyService;
import com.mailvor.modules.pay.domain.MwPayCompany;
import com.mailvor.modules.pay.dto.PayCompanyQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author huangyu
 * @date 2023-04-11
 */
@Api(tags = "商城：数据配置管理")
@RestController
@RequestMapping("api")
public class PayCompanyController {

    @Resource
    private MwPayCompanyService payCompanyService;

    @Resource
    private IGenerator generator;

    @Log("查询数据配置")
    @ApiOperation(value = "查询数据配置")
    @GetMapping(value = "/paycompany")
    @PreAuthorize("hasAnyRole('admin','PAYCOMPANY_ALL','PAYCOMPANY_SELECT')")
    public ResponseEntity getPayCompanyList(PayCompanyQueryCriteria criteria,
                                            Pageable pageable) {
        return new ResponseEntity<>(payCompanyService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增通道")
    @ApiOperation(value = "新增通道")
    @PostMapping(value = "/paycompany")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @PreAuthorize("hasAnyRole('admin','PAYCOMPANY_ALL','PAYCOMPANY_CREATE')")
    public ResponseEntity create(@Valid @RequestBody PayCompanyParam param) {

        MwPayCompany payChannel = generator.convert(param, MwPayCompany.class);

        return new ResponseEntity<>(payCompanyService.save(payChannel), HttpStatus.CREATED);
    }

    @ForbidSubmit
    @Log("修改数据配置")
    @ApiOperation(value = "修改数据配置")
    @PutMapping(value = "/paycompany")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @PreAuthorize("hasAnyRole('admin','PAYCOMPANY_ALL','PAYCOMPANY_EDIT')")
    public ResponseEntity update(@Valid @RequestBody PayCompanyEditParam param) {

        MwPayCompany payChannel = generator.convert(param, MwPayCompany.class);

        payCompanyService.saveOrUpdate(payChannel);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除数据配置")
    @ApiOperation(value = "删除数据配置")
    @DeleteMapping(value = "/paycompany/{id}")
    @PreAuthorize("hasAnyRole('admin','PAYCOMPANY_ALL','PAYCOMPANY_DELETE')")
    public ResponseEntity delete(@PathVariable Long id) {
        payCompanyService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
