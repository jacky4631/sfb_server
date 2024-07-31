/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest;

import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.mp.domain.MwWechatTemplate;
import com.mailvor.modules.mp.service.MwWechatTemplateService;
import com.mailvor.modules.mp.service.dto.MwWechatTemplateDto;
import com.mailvor.modules.mp.service.dto.MwWechatTemplateQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author huangyu
* @date 2019-12-10
*/
@Api(tags = "微信：微信模板管理")
@RestController
@RequestMapping("/api/mwWechatTemplate")
@AllArgsConstructor
public class WechatTemplateController {


    private final MwWechatTemplateService mwWechatTemplateService;
    private final IGenerator generator;


    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwWechatTemplate:list')")
    public void download(HttpServletResponse response, MwWechatTemplateQueryCriteria criteria) throws IOException {
        mwWechatTemplateService.download(generator.convert(mwWechatTemplateService.queryAll(criteria), MwWechatTemplateDto.class), response);
    }

    @GetMapping
    @ApiOperation("查询微信模板消息")
    @PreAuthorize("@el.check('admin','mwWechatTemplate:list')")
    public ResponseEntity<Object> getmwWechatTemplates(MwWechatTemplateQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwWechatTemplateService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @PostMapping
    @ApiOperation("新增微信模板消息")
    @PreAuthorize("@el.check('admin','mwWechatTemplate:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwWechatTemplate resources){
        return new ResponseEntity<>(mwWechatTemplateService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @PutMapping
    @ApiOperation("修改微信模板消息")
    @PreAuthorize("@el.check('admin','mwWechatTemplate:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwWechatTemplate resources){
        mwWechatTemplateService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @ApiOperation("删除微信模板消息")
    @PreAuthorize("@el.check('admin','mwWechatTemplate:del')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAll(@PathVariable Integer id) {
        mwWechatTemplateService.removeById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
