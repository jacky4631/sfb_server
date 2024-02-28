/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.shop.domain.MwMaterial;
import com.mailvor.modules.shop.service.MwMaterialService;
import com.mailvor.modules.shop.service.dto.MwMaterialDto;
import com.mailvor.modules.shop.service.dto.MwMaterialQueryCriteria;
import com.mailvor.modules.shop.service.mapper.MaterialMapper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwMaterialServiceImpl extends BaseServiceImpl<MaterialMapper, MwMaterial> implements MwMaterialService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwMaterialQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwMaterial> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwMaterialDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwMaterial> queryAll(MwMaterialQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwMaterial.class, criteria));
    }



}
