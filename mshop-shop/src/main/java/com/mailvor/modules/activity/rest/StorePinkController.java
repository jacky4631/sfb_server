/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.service.MwStorePinkService;
import com.mailvor.modules.activity.service.dto.MwStorePinkQueryCriteria;
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
* @date 2019-11-18
*/
@Api(tags = "商城:拼团记录管理")
@RestController
@RequestMapping("api")
public class StorePinkController {

    private final MwStorePinkService mwStorePinkService;

    public StorePinkController(MwStorePinkService mwStorePinkService) {
        this.mwStorePinkService = mwStorePinkService;
    }

    @Log("查询记录")
    @ApiOperation(value = "查询记录")
    @GetMapping(value = "/mwStorePink")
    @PreAuthorize("@el.check('admin','MWSTOREPINK_ALL','MWSTOREPINK_SELECT')")
    public ResponseEntity getMwStorePinks(MwStorePinkQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStorePinkService.queryAll(criteria,pageable),HttpStatus.OK);
    }


}
