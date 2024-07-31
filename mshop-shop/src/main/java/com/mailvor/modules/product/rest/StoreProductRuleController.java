/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.rest;

import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.product.domain.MwStoreProductRule;
import com.mailvor.modules.product.service.MwStoreProductRuleService;
import com.mailvor.modules.product.service.dto.MwStoreProductRuleQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
* @author huangyu
* @date 2020-06-28
*/
@AllArgsConstructor
@Api(tags = "商城：sku规则管理")
@RestController
@RequestMapping("/api/mwStoreProductRule")
public class StoreProductRuleController {

    private final MwStoreProductRuleService mwStoreProductRuleService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwStoreProductRule:list')")
    public void download(HttpServletResponse response, MwStoreProductRuleQueryCriteria criteria) throws IOException {
        mwStoreProductRuleService.download(mwStoreProductRuleService.queryAll(criteria) , response);
    }

    @GetMapping
    @Log("查询sku规则")
    @ApiOperation("查询sku规则")
    @PreAuthorize("@el.check('admin','mwStoreProductRule:list')")
    public ResponseEntity<Object> getMwStoreProductRules(MwStoreProductRuleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreProductRuleService.queryAll(criteria,pageable),HttpStatus.OK);
    }


    @PostMapping("/save/{id}")
    @Log("新增/修改sku规则")
    @ApiOperation("新增/修改sku规则")
    @PreAuthorize("hasAnyRole('admin','mwStoreProductRule:add','mwStoreProductRule:edit')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwStoreProductRule resources, @PathVariable Integer id){
        if(id != null && id > 0){
            resources.setId(id);
            mwStoreProductRuleService.updateById(resources);
        }else{
            mwStoreProductRuleService.save(resources);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @ForbidSubmit
    @Log("删除sku规则")
    @ApiOperation("删除sku规则")
    @PreAuthorize("@el.check('admin','mwStoreProductRule:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        mwStoreProductRuleService.removeByIds(new ArrayList<>(Arrays.asList(ids)));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
