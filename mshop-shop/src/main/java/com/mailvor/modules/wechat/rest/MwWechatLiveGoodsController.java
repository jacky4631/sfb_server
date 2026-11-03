/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.mp.domain.MwWechatLiveGoods;
import com.mailvor.modules.mp.service.MwWechatLiveGoodsService;
import com.mailvor.modules.mp.service.dto.MwWechatLiveGoodsDto;
import com.mailvor.modules.mp.service.dto.MwWechatLiveGoodsQueryCriteria;
import lombok.AllArgsConstructor;
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

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
* @author huangyu
* @date 2020-08-11
*/
@AllArgsConstructor
@Tag(name = "微信：直播间商品管理")
@RestController
@RequestMapping("/api/mwWechatLiveGoods")
public class MwWechatLiveGoodsController {

    private final MwWechatLiveGoodsService mwWechatLiveGoodsService;
    private final IGenerator generator;

    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwWechatLiveGoods:list')")
    public void download(HttpServletResponse response, MwWechatLiveGoodsQueryCriteria criteria) throws IOException {
        mwWechatLiveGoodsService.download(generator.convert(mwWechatLiveGoodsService.queryAll(criteria), MwWechatLiveGoodsDto.class), response);
    }

    @GetMapping
    @Log("查询mwWechatLiveGoods")
    @Operation(summary = "查询mwWechatLiveGoods")
    @PreAuthorize("@el.check('admin','mwWechatLiveGoods:list')")
    public ResponseEntity<Object> getMwWechatLiveGoodss(MwWechatLiveGoodsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwWechatLiveGoodsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @PostMapping
    @Log("新增mwWechatLiveGoods")
    @Operation(summary = "新增mwWechatLiveGoods")
    @PreAuthorize("@el.check('admin','mwWechatLiveGoods:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MwWechatLiveGoods resources){
        return new ResponseEntity<>(mwWechatLiveGoodsService.saveGoods(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @PutMapping
    @Log("修改mwWechatLiveGoods")
    @Operation(summary = "修改mwWechatLiveGoods")
    @PreAuthorize("@el.check('admin','mwWechatLiveGoods:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MwWechatLiveGoods resources){
        mwWechatLiveGoodsService.updateGoods(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除mwWechatLiveGoods")
    @Operation(summary = "删除mwWechatLiveGoods")
    @PreAuthorize("@el.check('admin','mwWechatLiveGoods:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
                mwWechatLiveGoodsService.removeGoods(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "同步数据")
    @PostMapping("/synchro")
    public ResponseEntity<Object> synchroWxOlLiveGoods(@RequestBody Integer[] ids) {
        mwWechatLiveGoodsService.synchroWxOlLive(Arrays.asList(ids));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
