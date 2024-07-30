/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.rest;

import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import com.mailvor.modules.tk.service.MailvorJdOrderService;
import com.mailvor.modules.tk.service.dto.MailvorJdOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorJdOrderQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
/**
* @author shenji
* @date 2022-09-05
*/
@AllArgsConstructor
@Api(tags = "商城:京东订单管理")
@RestController
@RequestMapping("/api/mailvorJdOrder")
public class MailvorJdOrderController {

    private final MailvorJdOrderService mailvorJdOrderService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mailvorJdOrder:list')")
    public void download(HttpServletResponse response, MailvorJdOrderQueryCriteria criteria) throws IOException {
        mailvorJdOrderService.download(generator.convert(mailvorJdOrderService.queryAll(criteria), MailvorJdOrderDto.class), response);
    }

    @GetMapping
    @Log("查询jdOrder")
    @ApiOperation("查询jdOrder")
    @PreAuthorize("@el.check('admin','mailvorJdOrder:list')")
    public ResponseEntity<PageResult<MailvorJdOrderDto>> getMailvorJdOrders(MailvorJdOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mailvorJdOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增jdOrder")
    @ApiOperation("新增jdOrder")
    @PreAuthorize("@el.check('admin','mailvorJdOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MailvorJdOrder resources){
        return new ResponseEntity<>(mailvorJdOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改jdOrder")
    @ApiOperation("修改jdOrder")
    @PreAuthorize("@el.check('admin','mailvorJdOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MailvorJdOrder resources){
        mailvorJdOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除jdOrder")
    @ApiOperation("删除jdOrder")
    @PreAuthorize("@el.check('admin','mailvorJdOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            mailvorJdOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
