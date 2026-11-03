/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tk.domain.MailvorDyOrder;
import com.mailvor.modules.tk.service.MailvorDyOrderService;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderQueryCriteria;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
/**
* @author shenji
* @date 2022-09-07
*/
@AllArgsConstructor
@Tag(name = "商城：抖音订单管理")
@RestController
@RequestMapping("/api/mailvorDyOrder")
public class MailvorDyOrderController {

    private final MailvorDyOrderService mailvorDyOrderService;
    private final IGenerator generator;


    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:list')")
    public void download(HttpServletResponse response, MailvorDyOrderQueryCriteria criteria) throws IOException {
        mailvorDyOrderService.download(generator.convert(mailvorDyOrderService.queryAll(criteria), MailvorDyOrderDto.class), response);
    }

    @GetMapping
    @Log("查询dyOrder")
    @Operation(summary = "查询dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:list')")
    public ResponseEntity<PageResult<MailvorDyOrderDto>> getMailvorDyOrders(MailvorDyOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mailvorDyOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增dyOrder")
    @Operation(summary = "新增dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MailvorDyOrder resources){
        return new ResponseEntity<>(mailvorDyOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改dyOrder")
    @Operation(summary = "修改dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MailvorDyOrder resources){
        mailvorDyOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除dyOrder")
    @Operation(summary = "删除dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            mailvorDyOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
