/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.rest;

import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.service.MailvorMtOrderService;
import com.mailvor.modules.tk.service.dto.MailvorMtOrderQueryCriteria;
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
* @date 2022-08-29
*/
@AllArgsConstructor
@Api(tags = "商城:美团订单管理")
@RestController
@RequestMapping("/api/mailvorMtOrder")
public class MailvorMtOrderController {

    private final MailvorMtOrderService mtOrderService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mailvorTbOrder:list')")
    public void download(HttpServletResponse response, MailvorMtOrderQueryCriteria criteria) throws IOException {
        mtOrderService.download(mtOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询mtOrder")
    @ApiOperation("查询mtOrder")
    @PreAuthorize("@el.check('admin','mailvorMtOrder:list')")
    public ResponseEntity<PageResult<MailvorMtOrder>> getMailvorTbOrders(MailvorMtOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mtOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增mtOrder")
    @ApiOperation("新增mtOrder")
    @PreAuthorize("@el.check('admin','mailvorMtOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MailvorMtOrder resources){
        return new ResponseEntity<>(mtOrderService.save(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改mtOrder")
    @ApiOperation("修改mtOrder")
    @PreAuthorize("@el.check('admin','mailvorMtOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MailvorMtOrder resources){
        mtOrderService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除mtOrder")
    @ApiOperation("删除mtOrder")
    @PreAuthorize("@el.check('admin','mailvorMtOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            mtOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
