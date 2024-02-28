/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.CommonEnum;
import com.mailvor.modules.shop.domain.MwSystemGroupData;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataDto;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataQueryCriteria;
import com.mailvor.modules.shop.service.mapper.SystemGroupDataMapper;
import com.mailvor.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;



/**
* @author wangke
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwSystemGroupDataServiceImpl extends BaseServiceImpl<SystemGroupDataMapper, MwSystemGroupData> implements MwSystemGroupDataService {

    private final IGenerator generator;


    /**
     * 获取配置数据
     * @param name 配置名称
     * @return List
     */
    @Override
    //@Cacheable(value = "mshop:configDatas",key = "#name")
    public List<JSONObject> getDatas(String name) {
        List<MwSystemGroupData> systemGroupDatas = this.baseMapper
                .selectList(Wrappers.<MwSystemGroupData>lambdaQuery()
                        .eq(MwSystemGroupData::getGroupName,name)
                        .eq(MwSystemGroupData::getStatus,CommonEnum.SHOW_STATUS_1.getValue())
                        .orderByDesc(MwSystemGroupData::getSort));

        List<JSONObject> list = systemGroupDatas
                .stream()
                .map(MwSystemGroupData::getValue)
                .map(JSONObject::parseObject)
                .collect(Collectors.toList());

        return list;
    }


    //===============管理后台==============

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwSystemGroupDataQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemGroupData> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<MwSystemGroupDataDto> systemGroupDataDTOS = new ArrayList<>();
        for (MwSystemGroupData systemGroupData : page.getList()) {

            MwSystemGroupDataDto systemGroupDataDTO = generator.convert(systemGroupData, MwSystemGroupDataDto.class);
            systemGroupDataDTO.setMap(JSON.parseObject(systemGroupData.getValue()));
            systemGroupDataDTOS.add(systemGroupDataDTO);
        }
        map.put("content",systemGroupDataDTOS);
        map.put("totalElements",page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwSystemGroupData> queryAll(MwSystemGroupDataQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwSystemGroupData.class, criteria));
    }


    @Override
    public void download(List<MwSystemGroupDataDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwSystemGroupDataDto MWSystemGroupData : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("对应的数据名称", MWSystemGroupData.getGroupName());
            map.put("数据组对应的数据值（json数据）", MWSystemGroupData.getValue());
            map.put("添加数据时间", MWSystemGroupData.getAddTime());
            map.put("数据排序", MWSystemGroupData.getSort());
            map.put("状态（1：开启；2：关闭；）", MWSystemGroupData.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    //@Cacheable
    public Map<String, Object> list(MwSystemGroupDataQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemGroupData> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<JSONObject> systemGroupDataDTOS = new ArrayList<>();
        Date now = new Date();
        for (MwSystemGroupData systemGroupData : page.getList()) {
            JSONObject data = JSON.parseObject(systemGroupData.getValue());
            Date endTime = data.getDate("endTime");
            //如果当前时间大于等于结束时间 排除
            if(endTime != null && DateUtil.compare(now, endTime)>=0) {
                continue;
            }

            systemGroupDataDTOS.add(data);
        }
        map.put("content",systemGroupDataDTOS);
        map.put("totalElements",page.getTotal());
        return map;
    }

}
