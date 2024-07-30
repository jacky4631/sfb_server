/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.service.MwStoreCouponIssueUserService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueUserQueryCriteria;
import com.mailvor.modules.aop.ForbidSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author huangyu
* @date 2019-11-09
*/
@Api(tags = "商城:优惠券前台用户领取记录管理")
@RestController
@RequestMapping("api")
public class StoreCouponIssueUserController {

    private final MwStoreCouponIssueUserService mwStoreCouponIssueUserService;

    public StoreCouponIssueUserController(MwStoreCouponIssueUserService mwStoreCouponIssueUserService) {
        this.mwStoreCouponIssueUserService = mwStoreCouponIssueUserService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwStoreCouponIssueUser")
    @PreAuthorize("hasAnyRole('admin','MWSTORECOUPONISSUEUSER_ALL','MWSTORECOUPONISSUEUSER_SELECT')")
    public ResponseEntity getMwStoreCouponIssueUsers(MwStoreCouponIssueUserQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreCouponIssueUserService.queryAll(criteria,pageable),HttpStatus.OK);
    }


    @ForbidSubmit
    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwStoreCouponIssueUser/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTORECOUPONISSUEUSER_ALL','MWSTORECOUPONISSUEUSER_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        mwStoreCouponIssueUserService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
