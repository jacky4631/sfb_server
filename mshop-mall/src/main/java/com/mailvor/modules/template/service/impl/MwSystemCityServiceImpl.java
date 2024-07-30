/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.template.domain.MwSystemCity;
import com.mailvor.modules.template.service.MwSystemCityService;
import com.mailvor.modules.template.service.dto.MwSystemCityDto;
import com.mailvor.modules.template.service.dto.MwSystemCityQueryCriteria;
import com.mailvor.modules.template.service.mapper.MwSystemCityMapper;
import com.mailvor.utils.FileUtil;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
* @author mazhongjun
* @date 2020-06-29
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwSystemCityServiceImpl extends BaseServiceImpl<MwSystemCityMapper, MwSystemCity> implements MwSystemCityService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwSystemCityQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemCity> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwSystemCityDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwSystemCity> queryAll(MwSystemCityQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwSystemCity.class, criteria));
    }


    @Override
    public void download(List<MwSystemCityDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwSystemCityDto mwSystemCity : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("城市id", mwSystemCity.getCityId());
            map.put("省市级别", mwSystemCity.getLevel());
            map.put("父级id", mwSystemCity.getParentId());
            map.put("区号", mwSystemCity.getAreaCode());
            map.put("名称", mwSystemCity.getName());
            map.put("合并名称", mwSystemCity.getMergerName());
            map.put("经度", mwSystemCity.getLng());
            map.put("纬度", mwSystemCity.getLat());
            map.put("是否展示", mwSystemCity.getIsShow());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
