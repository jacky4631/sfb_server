/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.user.service.dto.MwUserRechargeDto;
import com.mailvor.modules.user.service.dto.MwUserRechargeQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
* @author huangyu
* @date 2020-03-02
*/
@Api(tags = "充值管理管理")
@RestController
@RequestMapping("/api/mwUserRecharge")
public class UserRechargeController {

    private final MwUserRechargeService mwUserRechargeService;
    private final IGenerator generator;
    public UserRechargeController(MwUserRechargeService mwUserRechargeService, IGenerator generator) {
        this.mwUserRechargeService = mwUserRechargeService;
        this.generator = generator;
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('mwUserRecharge:list')")
    public void download(HttpServletResponse response, MwUserRechargeQueryCriteria criteria) throws IOException {
        mwUserRechargeService.download(generator.convert(mwUserRechargeService.queryAll(criteria), MwUserRechargeDto.class), response);
    }

    @GetMapping
    @Log("查询充值管理")
    @ApiOperation("查询充值管理")
    @PreAuthorize("@el.check('mwUserRecharge:list')")
    public ResponseEntity<Object> getMwUserRecharges(MwUserRechargeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwUserRechargeService.queryAll(criteria,pageable),HttpStatus.OK);
    }




    @ForbidSubmit
    @Log("删除充值管理")
    @ApiOperation("删除充值管理")
    @PreAuthorize("@el.check('mwUserRecharge:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        mwUserRechargeService.removeByIds(new ArrayList<>(Arrays.asList(ids)));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
