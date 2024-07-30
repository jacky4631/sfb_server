/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.shop.domain.MwMaterialGroup;
import com.mailvor.modules.shop.service.MwMaterialGroupService;
import com.mailvor.modules.shop.service.dto.MwMaterialGroupDto;
import com.mailvor.modules.shop.service.dto.MwMaterialGroupQueryCriteria;
import com.mailvor.modules.shop.service.mapper.MaterialGroupMapper;
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
* @author huangyu
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwMaterialGroupServiceImpl extends BaseServiceImpl<MaterialGroupMapper, MwMaterialGroup> implements MwMaterialGroupService {

    private final IGenerator generator;

    @Override
    public Map<String, Object> queryAll(MwMaterialGroupQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwMaterialGroup> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwMaterialGroupDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    public List<MwMaterialGroup> queryAll(MwMaterialGroupQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwMaterialGroup.class, criteria));
    }


    @Override
    public void download(List<MwMaterialGroupDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwMaterialGroupDto mwMaterialGroup : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("创建时间", mwMaterialGroup.getCreateTime());
            map.put("创建者ID", mwMaterialGroup.getCreateId());
            map.put("分组名", mwMaterialGroup.getName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
