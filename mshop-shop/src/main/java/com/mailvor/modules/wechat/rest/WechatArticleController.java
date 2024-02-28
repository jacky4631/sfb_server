/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest;

import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.services.WechatArticleService;
import com.mailvor.modules.mp.domain.MwArticle;
import com.mailvor.modules.mp.service.MwArticleService;
import com.mailvor.modules.mp.service.dto.MwArticleDto;
import com.mailvor.modules.mp.service.dto.MwArticleQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
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
* @date 2019-10-07
*/
@Api(tags = "商城:微信图文管理")
@RestController
@RequestMapping("api")
public class WechatArticleController {

    private final MwArticleService mwArticleService;
    private final WechatArticleService wechatArticleService;

    public WechatArticleController(MwArticleService mwArticleService, WechatArticleService wechatArticleService) {
        this.mwArticleService = mwArticleService;
        this.wechatArticleService = wechatArticleService;
    }

    @ApiOperation(value = "查询单条信息")
    @GetMapping(value = "/mwArticle/info/{id}")
    @PreAuthorize("hasAnyRole('admin','MWARTICLE_ALL','MWARTICLE_GET')")
    public ResponseEntity getInfo(@PathVariable Integer id){
        return new ResponseEntity<>(mwArticleService.getById(id),HttpStatus.OK);
    }

    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwArticle")
    @PreAuthorize("hasAnyRole('admin','MWARTICLE_ALL','MWARTICLE_SELECT')")
    public ResponseEntity getMwArticles(MwArticleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwArticleService.queryAll(criteria,pageable),HttpStatus.OK);
    }


    @ApiOperation(value = "新增")
    @PostMapping(value = "/mwArticle")
    @PreAuthorize("hasAnyRole('admin','MWARTICLE_ALL','MWARTICLE_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwArticle resources){
        return new ResponseEntity<>(mwArticleService.save(resources),HttpStatus.CREATED);
    }


    @ApiOperation(value = "修改")
    @PutMapping(value = "/mwArticle")
    @PreAuthorize("hasAnyRole('admin','MWARTICLE_ALL','MWARTICLE_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwArticle resources){
        mwArticleService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwArticle/{id}")
    @PreAuthorize("hasAnyRole('admin','MWARTICLE_ALL','MWARTICLE_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        mwArticleService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ForbidSubmit
    @ApiOperation(value = "发布文章")
    @GetMapping(value = "/mwArticle/publish/{id}")
    @PreAuthorize("hasAnyRole('admin','MWARTICLE_ALL','MWARTICLE_DELETE')")
    public ResponseEntity publish(@PathVariable Integer id)  throws Exception{
        MwArticleDto mwArticleDTO= new MwArticleDto();
        MwArticle mwArticle = mwArticleService.getById(id);
        BeanUtils.copyProperties(mwArticle,mwArticleDTO);
        wechatArticleService.publish(mwArticleDTO);
        return new ResponseEntity(HttpStatus.OK);
    }

}
