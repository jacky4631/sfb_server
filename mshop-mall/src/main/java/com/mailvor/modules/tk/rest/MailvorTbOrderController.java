/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.rest;

import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tk.domain.MailvorTbOrder;
import com.mailvor.modules.tk.service.MailvorTbOrderService;
import com.mailvor.modules.tk.service.dto.MailvorTbOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorTbOrderQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
* @date 2022-08-29
*/
@AllArgsConstructor
@Api(tags = "tbOrder管理")
@RestController
@RequestMapping("/api/mailvorTbOrder")
public class MailvorTbOrderController {

    private final MailvorTbOrderService mailvorTbOrderService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mailvorTbOrder:list')")
    public void download(HttpServletResponse response, MailvorTbOrderQueryCriteria criteria) throws IOException {
        mailvorTbOrderService.download(generator.convert(mailvorTbOrderService.queryAll(criteria), MailvorTbOrderDto.class), response);
    }

    @GetMapping
    @Log("查询tbOrder")
    @ApiOperation("查询tbOrder")
    @PreAuthorize("@el.check('admin','mailvorTbOrder:list')")
    public ResponseEntity<PageResult<MailvorTbOrderDto>> getMailvorTbOrders(MailvorTbOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mailvorTbOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增tbOrder")
    @ApiOperation("新增tbOrder")
    @PreAuthorize("@el.check('admin','mailvorTbOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MailvorTbOrder resources){
        return new ResponseEntity<>(mailvorTbOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改tbOrder")
    @ApiOperation("修改tbOrder")
    @PreAuthorize("@el.check('admin','mailvorTbOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MailvorTbOrder resources){
        mailvorTbOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除tbOrder")
    @ApiOperation("删除tbOrder")
    @PreAuthorize("@el.check('admin','mailvorTbOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            mailvorTbOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
