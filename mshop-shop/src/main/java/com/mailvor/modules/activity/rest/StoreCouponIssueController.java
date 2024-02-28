/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.domain.MwStoreCouponIssue;
import com.mailvor.modules.activity.service.MwStoreCouponIssueService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueQueryCriteria;
import com.mailvor.modules.aop.ForbidSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author huangyu
* @date 2019-11-09
*/
@Api(tags = "商城:优惠券发布管理")
@RestController
@RequestMapping("api")
public class StoreCouponIssueController {

    private final MwStoreCouponIssueService mwStoreCouponIssueService;

    public StoreCouponIssueController(MwStoreCouponIssueService mwStoreCouponIssueService) {
        this.mwStoreCouponIssueService = mwStoreCouponIssueService;
    }

    @Log("查询已发布")
    @ApiOperation(value = "查询已发布")
    @GetMapping(value = "/mwStoreCouponIssue")
    @PreAuthorize("@el.check('admin','MWSTORECOUPONISSUE_ALL','MWSTORECOUPONISSUE_SELECT')")
    public ResponseEntity getMwStoreCouponIssues(MwStoreCouponIssueQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreCouponIssueService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("发布")
    @ApiOperation(value = "发布")
    @PostMapping(value = "/mwStoreCouponIssue")
    @PreAuthorize("@el.check('admin','MWSTORECOUPONISSUE_ALL','MWSTORECOUPONISSUE_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwStoreCouponIssue resources){
        if(resources.getTotalCount() > 0) {
            resources.setRemainCount(resources.getTotalCount());
        }
        return new ResponseEntity<>(mwStoreCouponIssueService.save(resources),HttpStatus.CREATED);
    }

    @Log("修改状态")
    @ApiOperation(value = "修改状态")
    @PutMapping(value = "/mwStoreCouponIssue")
    @PreAuthorize("@el.check('admin','MWSTORECOUPONISSUE_ALL','MWSTORECOUPONISSUE_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreCouponIssue resources){
        mwStoreCouponIssueService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwStoreCouponIssue/{id}")
    @PreAuthorize("@el.check('admin','MWSTORECOUPONISSUE_ALL','MWSTORECOUPONISSUE_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        mwStoreCouponIssueService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
