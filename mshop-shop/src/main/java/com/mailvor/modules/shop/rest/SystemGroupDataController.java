/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.shop.domain.MwSystemGroupData;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataQueryCriteria;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mailvor.constant.ShopConstants.MSHOP_SECKILL_TIME;

/**
 * @author huangyu
 * @date 2019-10-18
 */
@Api(tags = "商城：数据配置管理")
@RestController
@RequestMapping("api")
public class SystemGroupDataController {

    private final MwSystemGroupDataService mwSystemGroupDataService;

    public SystemGroupDataController(MwSystemGroupDataService mwSystemGroupDataService) {
        this.mwSystemGroupDataService = mwSystemGroupDataService;
    }

    @Log("查询数据配置")
    @ApiOperation(value = "查询数据配置")
    @GetMapping(value = "/mwSystemGroupData")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMGROUPDATA_ALL','MWSYSTEMGROUPDATA_SELECT')")
    public ResponseEntity getMwSystemGroupDatas(MwSystemGroupDataQueryCriteria criteria,
                                                Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "sort");
        Pageable pageableT = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                sort);
        return new ResponseEntity<>(mwSystemGroupDataService.queryAll(criteria, pageableT), HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增数据配置")
    @ApiOperation(value = "新增数据配置")
    @PostMapping(value = "/mwSystemGroupData")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMGROUPDATA_ALL','MWSYSTEMGROUPDATA_CREATE')")
    public ResponseEntity create(@RequestBody String jsonStr) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        this.checkParam(jsonObject);

        MwSystemGroupData mwSystemGroupData = new MwSystemGroupData();
        mwSystemGroupData.setGroupName(jsonObject.get("groupName").toString());
        jsonObject.remove("groupName");
        mwSystemGroupData.setValue(jsonObject.toJSONString());
        mwSystemGroupData.setStatus(jsonObject.getInteger("status"));
        mwSystemGroupData.setSort(jsonObject.getInteger("sort"));

        List<MwSystemGroupData> mwSeckillTime = mwSystemGroupDataService.list(Wrappers.<MwSystemGroupData>lambdaQuery()
                .eq(MwSystemGroupData::getGroupName, MSHOP_SECKILL_TIME));
        if (mwSystemGroupData.getStatus() == 1) {
            mwSeckillTime.forEach(item -> {
                Map map = JSONUtil.toBean(item.getValue(), Map.class);
                if (Objects.nonNull(jsonObject.getInteger("time")) && jsonObject.getInteger("time").equals(map.get("time"))) {
                    throw new BadRequestException("不能同时开启同一时间点");
                }
            });
        }

        return new ResponseEntity<>(mwSystemGroupDataService.save(mwSystemGroupData), HttpStatus.CREATED);
    }

    @ForbidSubmit
    @Log("修改数据配置")
    @ApiOperation(value = "修改数据配置")
    @PutMapping(value = "/mwSystemGroupData")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMGROUPDATA_ALL','MWSYSTEMGROUPDATA_EDIT')")
    public ResponseEntity update(@RequestBody String jsonStr) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        this.checkParam(jsonObject);

        MwSystemGroupData mwSystemGroupData = new MwSystemGroupData();

        mwSystemGroupData.setGroupName(jsonObject.get("groupName").toString());
        jsonObject.remove("groupName");
        mwSystemGroupData.setValue(jsonObject.toJSONString());
        mwSystemGroupData.setStatus(jsonObject.getInteger("status"));

        List<MwSystemGroupData> mshop_seckill_time = mwSystemGroupDataService.list(Wrappers.<MwSystemGroupData>lambdaQuery()
                .eq(MwSystemGroupData::getGroupName, "mshop_seckill_time"));
        if (mwSystemGroupData.getStatus() == 1 && ObjectUtil.isNotEmpty(jsonObject.getInteger("time"))) {
            mshop_seckill_time.forEach(item -> {
                Map map = JSONUtil.toBean(item.getValue(), Map.class);
                if (jsonObject.getInteger("time").equals(map.get("time"))) {
                    throw new BadRequestException("不能同时开启同一时间点");
                }
            });
        }

        if (jsonObject.getInteger("status") == null) {
            mwSystemGroupData.setStatus(1);
        } else {
            mwSystemGroupData.setStatus(jsonObject.getInteger("status"));
        }

        if (jsonObject.getInteger("sort") == null) {
            mwSystemGroupData.setSort(0);
        } else {
            mwSystemGroupData.setSort(jsonObject.getInteger("sort"));
        }


        mwSystemGroupData.setId(Integer.valueOf(jsonObject.get("id").toString()));
        mwSystemGroupDataService.saveOrUpdate(mwSystemGroupData);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除数据配置")
    @ApiOperation(value = "删除数据配置")
    @DeleteMapping(value = "/mwSystemGroupData/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSYSTEMGROUPDATA_ALL','MWSYSTEMGROUPDATA_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id) {
        mwSystemGroupDataService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 检测参数
     *
     * @param jsonObject
     */
    private void checkParam(JSONObject jsonObject) {
        if (ObjectUtil.isNotNull(jsonObject.get("name"))) {
            if (StrUtil.isEmpty(jsonObject.getString("name"))) {
                throw new BadRequestException("名称必须填写");
            }
        }

        if (ObjectUtil.isNotNull(jsonObject.get("title"))) {
            if (StrUtil.isEmpty(jsonObject.getString("title"))) {
                throw new BadRequestException("标题必须填写");
            }
        }

        if (ObjectUtil.isNotNull(jsonObject.get("pic"))) {
            if (StrUtil.isEmpty(jsonObject.getString("pic"))) {
                throw new BadRequestException("图片必须上传");
            }
        }


        if (ObjectUtil.isNotNull(jsonObject.get("info"))) {
            if (StrUtil.isEmpty(jsonObject.getString("info"))) {
                throw new BadRequestException("简介必须填写");
            }
        }

    }
}
