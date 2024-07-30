/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.customer.rest;
import java.util.Arrays;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.aop.ForbidSubmit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.modules.customer.domain.MwStoreCustomer;
import com.mailvor.modules.customer.service.MwStoreCustomerService;
import com.mailvor.modules.customer.service.dto.MwStoreCustomerDto;
import com.mailvor.modules.customer.service.dto.MwStoreCustomerQueryCriteria;
import com.mailvor.modules.logging.aop.log.Log;
import lombok.AllArgsConstructor;
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
* @author Bug
* @date 2020-12-10
*/
@AllArgsConstructor
@Api(tags = "customer管理")
@RestController
@RequestMapping("/api/mwStoreCustomer")
public class MwStoreCustomerController {

    private final MwStoreCustomerService mwStoreCustomerService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwStoreCustomer:list')")
    public void download(HttpServletResponse response, MwStoreCustomerQueryCriteria criteria) throws IOException {
        mwStoreCustomerService.download(generator.convert(mwStoreCustomerService.queryAll(criteria), MwStoreCustomerDto.class), response);
    }

    @GetMapping
    @Log("查询customer")
    @ApiOperation("查询customer")
    @PreAuthorize("@el.check('admin','mwStoreCustomer:list')")
    public ResponseEntity<PageResult<MwStoreCustomerDto>> getMwStoreCustomers(MwStoreCustomerQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreCustomerService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @PostMapping
    @Log("新增customer")
    @ApiOperation("新增customer")
    @PreAuthorize("@el.check('admin','mwStoreCustomer:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwStoreCustomer resources){
        Long count = mwStoreCustomerService.count(new LambdaQueryWrapper<MwStoreCustomer>().eq(MwStoreCustomer::getOpenId, resources.getOpenId()));
        if (count > 0) {
            throw new BadRequestException("当前用户已存在，请勿重复提交");
        }
        return new ResponseEntity<>(mwStoreCustomerService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @PutMapping
    @Log("修改customer")
    @ApiOperation("修改customer")
    @PreAuthorize("@el.check('admin','mwStoreCustomer:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwStoreCustomer resources){
        mwStoreCustomerService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除customer")
    @ApiOperation("删除customer")
    @PreAuthorize("@el.check('admin','mwStoreCustomer:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            mwStoreCustomerService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
