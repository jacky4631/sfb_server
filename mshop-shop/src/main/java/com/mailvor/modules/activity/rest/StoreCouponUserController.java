/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.service.MwStoreCouponUserService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponUserQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author huangyu
* @date 2019-11-10
*/
@Api(tags = "商城:优惠券发放记录管理")
@RestController
@RequestMapping("api")
public class StoreCouponUserController {

    private final MwStoreCouponUserService mwStoreCouponUserService;

    public StoreCouponUserController(MwStoreCouponUserService mwStoreCouponUserService) {
        this.mwStoreCouponUserService = mwStoreCouponUserService;
    }

    @Log("查询Y")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwStoreCouponUser")
    @PreAuthorize("hasAnyRole('admin','MWSTORECOUPONUSER_ALL','MWSTORECOUPONUSER_SELECT')")
    public ResponseEntity getMwStoreCouponUsers(MwStoreCouponUserQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(mwStoreCouponUserService.queryAll(criteria,pageable),HttpStatus.OK);
    }


}
