/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest;

import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.mp.domain.MwWechatLive;
import com.mailvor.modules.mp.service.MwWechatLiveService;
import com.mailvor.modules.mp.service.dto.UpdateGoodsDto;
import com.mailvor.modules.mp.service.dto.MwWechatLiveDto;
import com.mailvor.modules.mp.service.dto.MwWechatLiveQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
* @author huangyu
* @date 2020-08-10
*/
@AllArgsConstructor
@Api(tags = "wxlive管理")
@RestController
@RequestMapping("/api/mwWechatLive")
public class MwWechatLiveController {

    private final MwWechatLiveService mwWechatLiveService;
    private final IGenerator generator;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwWechatLive:list')")
    public void download(HttpServletResponse response, MwWechatLiveQueryCriteria criteria) throws IOException {
        mwWechatLiveService.download(generator.convert(mwWechatLiveService.queryAll(criteria), MwWechatLiveDto.class), response);
    }

    @GetMapping
    @Log("查询wxlive")
    @ApiOperation("查询wxlive")
    @PreAuthorize("@el.check('admin','mwWechatLive:list')")
    public ResponseEntity<Object> getMwWechatLives(MwWechatLiveQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwWechatLiveService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @ForbidSubmit
    @PostMapping
    @Log("新增wxlive")
    @ApiOperation("新增wxlive")
    @PreAuthorize("@el.check('admin','mwWechatLive:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwWechatLive resources){
        return new ResponseEntity<>(mwWechatLiveService.saveLive(resources),HttpStatus.CREATED);
    }


    @ForbidSubmit
    @PostMapping("/addGoods")
    @Log("添加商品")
    @ApiOperation("添加商品")
    @PreAuthorize("@el.check('admin','mwWechatLive:add')")
    public ResponseEntity<Object> addGoods(@Validated @RequestBody UpdateGoodsDto resources){
        return new ResponseEntity<>(mwWechatLiveService.addGoods(resources),HttpStatus.CREATED);
    }

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @ForbidSubmit
    @PutMapping
    @Log("修改wxlive")
    @ApiOperation("修改wxlive")
    @PreAuthorize("@el.check('admin','mwWechatLive:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwWechatLive resources){
        mwWechatLiveService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @ForbidSubmit
    @Log("删除wxlive")
    @ApiOperation("删除wxlive")
    @PreAuthorize("@el.check('admin','mwWechatLive:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            mwWechatLiveService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @ApiOperation("同步数据")
    @GetMapping("/synchro")
    public ResponseEntity<Object> synchroWxOlLive() {
        mwWechatLiveService.synchroWxOlLive();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
