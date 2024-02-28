/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.rest;

import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tk.domain.MailvorDyOrder;
import com.mailvor.modules.tk.service.MailvorDyOrderService;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderQueryCriteria;
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
* @date 2022-09-07
*/
@AllArgsConstructor
@Api(tags = "dyOrder管理")
@RestController
@RequestMapping("/api/mailvorDyOrder")
public class MailvorDyOrderController {

    private final MailvorDyOrderService mailvorDyOrderService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:list')")
    public void download(HttpServletResponse response, MailvorDyOrderQueryCriteria criteria) throws IOException {
        mailvorDyOrderService.download(generator.convert(mailvorDyOrderService.queryAll(criteria), MailvorDyOrderDto.class), response);
    }

    @GetMapping
    @Log("查询dyOrder")
    @ApiOperation("查询dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:list')")
    public ResponseEntity<PageResult<MailvorDyOrderDto>> getMailvorDyOrders(MailvorDyOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mailvorDyOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增dyOrder")
    @ApiOperation("新增dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MailvorDyOrder resources){
        return new ResponseEntity<>(mailvorDyOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改dyOrder")
    @ApiOperation("修改dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MailvorDyOrder resources){
        mailvorDyOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除dyOrder")
    @ApiOperation("删除dyOrder")
    @PreAuthorize("@el.check('admin','mailvorDyOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            mailvorDyOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
