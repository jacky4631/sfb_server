/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.user.service.MwUserCardService;
import com.mailvor.modules.user.service.dto.MwUserCardQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* @author huangyu
* @date 2023-04-06
*/
@Api(tags = "商城:会员开店管理")
@RestController
@RequestMapping("api")
public class MemberCardController {

    @Resource
    private MwUserCardService cardService;

    @Log("查询用户")
    @ApiOperation(value = "查询开店用户")
    @GetMapping(value = "/mwUser/card")
    @PreAuthorize("hasAnyRole('admin','MWUSER_CARD_ALL','MWUSER_CARD_SELECT')")
    public ResponseEntity getMwUsers(MwUserCardQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(cardService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @ApiOperation(value = "删除用户签名")
    @PreAuthorize("hasAnyRole('admin','MWUSER_CARD_ALL','MWUSER_CARD_DELETE')")
    @PostMapping(value = "/mwUser/card/clearSignature/{id}")
    public ResponseEntity onStatus(@PathVariable Long id){
        cardService.clearSignature(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    @Log("删除开店用户")
    @ApiOperation(value = "删除开店用户")
    @DeleteMapping(value = "/mwUser/card/{id}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_CARD_ALL','MWUSER_CARD_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        cardService.cleanCard(id);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @Log("删除开店用户银行卡")
    @ApiOperation(value = "删除开店用户银行卡")
    @DeleteMapping(value = "/mwUser/card/bank/{id}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_CARD_ALL','MWUSER_CARD_DELETE')")
    public ResponseEntity deleteCardBank(@PathVariable Long id){
        cardService.cleanCardBank(id);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }
    @Log("删除开店用户手机号")
    @ApiOperation(value = "删除开店用户手机号")
    @DeleteMapping(value = "/mwUser/card/phone/{id}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_CARD_ALL','MWUSER_CARD_DELETE')")
    public ResponseEntity deleteCardPhone(@PathVariable Long id){
        cardService.cleanCardPhone(id);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @Log("查询加盟用户")
    @ApiOperation(value = "查询加盟用户")
    @GetMapping(value = "/mwUser/card/re")
    @PreAuthorize("hasAnyRole('admin','MWUSER_CARD_ALL','MWUSER_CARD_SELECT')")
    public ResponseEntity re(@RequestParam String id) throws Exception {
        cardService.re(id);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }
}
