/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.dto.MwUserBillQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @author huangyu
* @date 2019-11-06
*/
@Api(tags = "商城:用户账单管理")
@RestController
@RequestMapping("api")
public class UserBillController {

    @Resource
    private MwUserBillService mwUserBillService;

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwUserBill")
    @PreAuthorize("hasAnyRole('admin','MWUSERBILL_ALL','MWUSERBILL_SELECT')")
    public ResponseEntity getMwUserBills(MwUserBillQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwUserBillService.queryAll(criteria,pageable),HttpStatus.OK);
    }

}
