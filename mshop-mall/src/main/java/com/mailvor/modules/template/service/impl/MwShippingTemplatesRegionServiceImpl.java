/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.service.impl;


import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.template.domain.MwShippingTemplatesRegion;
import com.mailvor.modules.template.service.MwShippingTemplatesRegionService;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesRegionDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesRegionQueryCriteria;
import com.mailvor.modules.template.service.mapper.MwShippingTemplatesRegionMapper;
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

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author mazhongjun
* @date 2020-06-29
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mwShippingTemplatesRegion")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwShippingTemplatesRegionServiceImpl extends BaseServiceImpl<MwShippingTemplatesRegionMapper, MwShippingTemplatesRegion> implements MwShippingTemplatesRegionService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwShippingTemplatesRegionQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwShippingTemplatesRegion> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwShippingTemplatesRegionDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwShippingTemplatesRegion> queryAll(MwShippingTemplatesRegionQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwShippingTemplatesRegion.class, criteria));
    }


    @Override
    public void download(List<MwShippingTemplatesRegionDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwShippingTemplatesRegionDto mwShippingTemplatesRegion : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("省ID", mwShippingTemplatesRegion.getProvinceId());
            map.put("模板ID", mwShippingTemplatesRegion.getTempId());
            map.put("城市ID", mwShippingTemplatesRegion.getCityId());
            map.put("首件", mwShippingTemplatesRegion.getFirst());
            map.put("首件运费", mwShippingTemplatesRegion.getFirstPrice());
            map.put("续件", mwShippingTemplatesRegion.getContinues());
            map.put("续件运费", mwShippingTemplatesRegion.getContinuePrice());
            map.put("计费方式", mwShippingTemplatesRegion.getType());
            map.put("分组唯一值", mwShippingTemplatesRegion.getUniqid());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
