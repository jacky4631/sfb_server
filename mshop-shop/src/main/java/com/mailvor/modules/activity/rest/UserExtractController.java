/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mailvor.api.MshopException;
import com.mailvor.modules.activity.domain.MwUserExtract;
import com.mailvor.modules.activity.domain.MwUserExtractConfig;
import com.mailvor.modules.activity.domain.MwUserExtracts;
import com.mailvor.modules.activity.service.MwUserExtractConfigService;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.activity.service.dto.MwExtractConfigParam;
import com.mailvor.modules.activity.service.dto.MwUserExtractQueryCriteria;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
* @author huangyu
* @date 2019-11-14
*/
@Tag(name = "商城：提现管理")
@RestController
@RequestMapping("api")
public class UserExtractController {

    @Resource
    private MwUserExtractService mwUserExtractService;

    @Resource
    private MwUserExtractConfigService extractConfigService;

    @Resource
    private MwUserService userService;

    @Log("查询提现列表")
    @Operation(summary = "查询提现列表")
    @GetMapping(value = "/mwUserExtract")
    @PreAuthorize("hasAnyRole('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_SELECT')")
    public ResponseEntity getMwUserExtracts(MwUserExtractQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwUserExtractService.queryAll(criteria,pageable),HttpStatus.OK);
    }



    @Log("修改审核")
    @Operation(summary = "修改审核")
    @PutMapping(value = "/mwUserExtract")
    @PreAuthorize("hasAnyRole('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwUserExtract resources){
        mwUserExtractService.doExtract(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @Log("修改审核批量")
    @Operation(summary = "修改审核批量")
    @PutMapping(value = "/mwUserExtracts")
    @PreAuthorize("hasAnyRole('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_EDIT')")
    public ResponseEntity extracts(@Validated @RequestBody MwUserExtracts body){
        mwUserExtractService.doExtracts(body.getExtracts());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("查询禁止提现用户")
    @Operation(summary = "查询禁止提现用户")
    @GetMapping(value = "/extract/ban/list")
    @PreAuthorize("hasAnyRole('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_SELECT')")
    public ResponseEntity getInvalidExtractList(MwUserExtractQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(extractConfigService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping(value = "/extract/ban")
    @Log("新增禁止提现用户")
    @Operation(summary = "新增禁止提现用户")
    @PreAuthorize("@el.check('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_EDIT')")
    public ResponseEntity<Object> invalidExtract(@Validated @RequestBody MwUserExtractConfig param){

        MwUser user = userService.getById(param.getUid());
        if(user == null) {
            throw new MshopException("用户不存在");
        }
        MwUserExtractConfig config = extractConfigService.getById(param.getUid());
        if(config != null) {
            throw new MshopException("记录已存在");
        }

        config = new MwUserExtractConfig();
        config.setUid(param.getUid());
        config.setAutoExtract(1);

        extractConfigService.save(config);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/extract/ban/{uid}")
    @Log("删除禁止提现用户")
    @Operation(summary = "删除禁止提现用户")
    @PreAuthorize("@el.check('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_DELETE')")
    public ResponseEntity<Object> delInvalidExtract(@PathVariable Long uid){

        MwUser user = userService.getById(uid);
        if(user == null) {
            throw new MshopException("用户不存在");
        }
        extractConfigService.removeById(uid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(value = "/extract/config")
    @Log("修改提现配置")
    @Operation(summary = "修改提现配置")
    @PreAuthorize("@el.check('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_EDIT')")
    public ResponseEntity<Object> editConfig(@Validated @RequestBody MwExtractConfigParam param){
        mwUserExtractService.setExtractConfig(param);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/extract/config")
    @Log("获取提现配置")
    @Operation(summary = "获取提现配置")
    @PreAuthorize("@el.check('admin','MWUSEREXTRACT_ALL','MWUSEREXTRACT_EDIT')")
    public ResponseEntity<Object> getConfig(){
        return new ResponseEntity<>(mwUserExtractService.getExtractConfig(),HttpStatus.OK);
    }
}
