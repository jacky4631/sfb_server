/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import com.mailvor.api.MshopException;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.shop.domain.MwExpend;
import com.mailvor.modules.shop.rest.param.MwExpendEditParam;
import com.mailvor.modules.shop.rest.param.MwExpendParam;
import com.mailvor.modules.shop.service.ExpendService;
import com.mailvor.modules.shop.service.dto.MwExpendQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author huangyu
 * @date 2022-07-06
 */
@Api(tags = "商城:额外支出管理")
@RestController
@RequestMapping("api")
public class ExpendController {

    @Resource
    private ExpendService expendService;

    @Log("查询额外支出")
    @ApiOperation(value = "查询额外支出")
    @GetMapping(value = "/mwExpend")
    @PreAuthorize("hasAnyRole('admin','EXPEND_ALL','EXPEND_SELECT')")
    public ResponseEntity getList(MwExpendQueryCriteria criteria,
                                                Pageable pageable) {
        return new ResponseEntity<>(expendService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增额外支出")
    @ApiOperation(value = "新增额外支出")
    @PostMapping(value = "/mwExpend")
    @PreAuthorize("hasAnyRole('admin','EXPEND_ALL','EXPEND_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwExpendParam body) {

        MwExpend expend = new MwExpend();
        expend.setMoney(body.getMoney());
        expend.setMark(body.getMark());
        expend.setAddTime(body.getAddTime());

        return new ResponseEntity<>(expendService.save(expend), HttpStatus.CREATED);
    }

    @ForbidSubmit
    @Log("修改额外支出")
    @ApiOperation(value = "修改额外支出")
    @PutMapping(value = "/mwExpend")
    @PreAuthorize("hasAnyRole('admin','EXPEND_ALL','EXPEND_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwExpendEditParam body) {

        MwExpend expend = expendService.getById(body.getId());

        if(expend == null) {
            throw new MshopException("支出不存在");
        }
        expend.setMoney(body.getMoney());
        expend.setMark(body.getMark());
        expend.setAddTime(body.getAddTime());

        expendService.updateById(expend);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除额外支出")
    @ApiOperation(value = "删除额外支出")
    @DeleteMapping(value = "/mwExpend/{id}")
    @PreAuthorize("hasAnyRole('admin','EXPEND_ALL','EXPEND_DELETE')")
    public ResponseEntity delete(@PathVariable Long id) {
        expendService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
