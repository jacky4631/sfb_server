/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;
import java.util.Arrays;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.aop.ForbidSubmit;
import lombok.AllArgsConstructor;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.product.domain.MwStoreProductRelation;
import com.mailvor.modules.product.service.MwStoreProductRelationService;
import com.mailvor.modules.product.service.dto.MwStoreProductRelationQueryCriteria;
import com.mailvor.modules.product.service.dto.MwStoreProductRelationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.mailvor.domain.PageResult;
/**
 * @author huangyu
 * @date 2020-09-03
 */
@AllArgsConstructor
@Api(tags = "ProductRelation管理")
@RestController
@RequestMapping("/api/mwStoreProductRelation")
public class MwStoreProductRelationController {

    private final MwStoreProductRelationService mwStoreProductRelationService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwStoreProductRelation:list')")
    public void download(HttpServletResponse response, MwStoreProductRelationQueryCriteria criteria) throws IOException {
        mwStoreProductRelationService.download(generator.convert(mwStoreProductRelationService.queryAll(criteria), MwStoreProductRelationDto.class), response);
    }

    @GetMapping
    @Log("查询ProductRelation")
    @ApiOperation("查询ProductRelation")
    @PreAuthorize("@el.check('admin','mwStoreProductRelation:list')")
    public ResponseEntity<PageResult<MwStoreProductRelationDto>> getMwStoreProductRelations(MwStoreProductRelationQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreProductRelationService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增ProductRelation")
    @ApiOperation("新增ProductRelation")
    @PreAuthorize("@el.check('admin','mwStoreProductRelation:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwStoreProductRelation resources){
        return new ResponseEntity<>(mwStoreProductRelationService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改ProductRelation")
    @ApiOperation("修改ProductRelation")
    @PreAuthorize("@el.check('admin','mwStoreProductRelation:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwStoreProductRelation resources){
        mwStoreProductRelationService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除ProductRelation")
    @ApiOperation("删除ProductRelation")
    @PreAuthorize("@el.check('admin','mwStoreProductRelation:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            mwStoreProductRelationService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
