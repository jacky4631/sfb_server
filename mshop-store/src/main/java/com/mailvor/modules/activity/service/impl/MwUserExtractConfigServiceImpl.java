/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.activity.domain.MwUserExtract;
import com.mailvor.modules.activity.domain.MwUserExtractConfig;
import com.mailvor.modules.activity.service.MwUserExtractConfigService;
import com.mailvor.modules.activity.service.dto.MwUserExtractQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwUserExtractConfigMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
* @author huangyu
* @date 2020-05-13
*/
@Slf4j
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserExtractConfigServiceImpl extends BaseServiceImpl<MwUserExtractConfigMapper, MwUserExtractConfig> implements MwUserExtractConfigService {

    private final IGenerator generator;
    private final MwUserExtractConfigMapper mapper;

    @Override
    public boolean canExtract(Long uid) {
        MwUserExtractConfig extractConfig = mapper.selectById(uid);
        return extractConfig == null || extractConfig.getAutoExtract() == 0;
    }

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwUserExtractQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserExtractConfig> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwUserExtractConfig> queryAll(MwUserExtractQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwUserExtract.class, criteria));
    }

}
