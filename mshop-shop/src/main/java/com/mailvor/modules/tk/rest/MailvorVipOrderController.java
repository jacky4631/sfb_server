/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.rest;

import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tk.domain.MailvorVipOrder;
import com.mailvor.modules.tk.service.MailvorVipOrderService;
import com.mailvor.modules.tk.service.dto.MailvorVipOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorVipOrderQueryCriteria;
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
* @author wangjun
* @date 2022-09-07
*/
@AllArgsConstructor
@Api(tags = "商城：唯品会订单管理")
@RestController
@RequestMapping("/api/mailvorVipOrder")
public class MailvorVipOrderController {

    private final MailvorVipOrderService mailvorVipOrderService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mailvorVipOrder:list')")
    public void download(HttpServletResponse response, MailvorVipOrderQueryCriteria criteria) throws IOException {
        mailvorVipOrderService.download(generator.convert(mailvorVipOrderService.queryAll(criteria), MailvorVipOrderDto.class), response);
    }

    @GetMapping
    @Log("查询vipOrder")
    @ApiOperation("查询vipOrder")
    @PreAuthorize("@el.check('admin','mailvorVipOrder:list')")
    public ResponseEntity<PageResult<MailvorVipOrderDto>> getMailvorVipOrders(MailvorVipOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mailvorVipOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增vipOrder")
    @ApiOperation("新增vipOrder")
    @PreAuthorize("@el.check('admin','mailvorVipOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MailvorVipOrder resources){
        return new ResponseEntity<>(mailvorVipOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改vipOrder")
    @ApiOperation("修改vipOrder")
    @PreAuthorize("@el.check('admin','mailvorVipOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MailvorVipOrder resources){
        mailvorVipOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除vipOrder")
    @ApiOperation("删除vipOrder")
    @PreAuthorize("@el.check('admin','mailvorVipOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody String[] ids) {
        Arrays.asList(ids).forEach(id->{
            mailvorVipOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
