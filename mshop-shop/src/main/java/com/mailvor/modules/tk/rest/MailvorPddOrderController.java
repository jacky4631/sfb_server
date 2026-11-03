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
import com.mailvor.modules.tk.domain.MailvorPddOrder;
import com.mailvor.modules.tk.service.MailvorPddOrderService;
import com.mailvor.modules.tk.service.dto.MailvorPddOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorPddOrderQueryCriteria;
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
* @date 2022-09-06
*/
@AllArgsConstructor
@Tag(name = "商城：拼多多订单管理")
@RestController
@RequestMapping("/api/mailvorPddOrder")
public class MailvorPddOrderController {

    private final MailvorPddOrderService mailvorPddOrderService;
    private final IGenerator generator;


    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mailvorPddOrder:list')")
    public void download(HttpServletResponse response, MailvorPddOrderQueryCriteria criteria) throws IOException {
        mailvorPddOrderService.download(generator.convert(mailvorPddOrderService.queryAll(criteria), MailvorPddOrderDto.class), response);
    }

    @GetMapping
    @Log("查询pddOrder")
    @Operation(summary = "查询pddOrder")
    @PreAuthorize("@el.check('admin','mailvorPddOrder:list')")
    public ResponseEntity<PageResult<MailvorPddOrderDto>> getMailvorPddOrders(MailvorPddOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mailvorPddOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增pddOrder")
    @Operation(summary = "新增pddOrder")
    @PreAuthorize("@el.check('admin','mailvorPddOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MailvorPddOrder resources){
        return new ResponseEntity<>(mailvorPddOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改pddOrder")
    @Operation(summary = "修改pddOrder")
    @PreAuthorize("@el.check('admin','mailvorPddOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MailvorPddOrder resources){
        mailvorPddOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除pddOrder")
    @Operation(summary = "删除pddOrder")
    @PreAuthorize("@el.check('admin','mailvorPddOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            mailvorPddOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
