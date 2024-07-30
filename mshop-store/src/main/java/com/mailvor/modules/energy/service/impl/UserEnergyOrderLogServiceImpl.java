/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service.impl;

import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.dto.UserEnergyOrderLogDto;
import com.mailvor.modules.energy.dto.UserEnergyOrderLogQueryCriteria;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.energy.service.mapper.UserEnergyOrderLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-02-04
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserEnergyOrderLogServiceImpl extends BaseServiceImpl<UserEnergyOrderLogMapper, UserEnergyOrderLog> implements UserEnergyOrderLogService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private UserEnergyOrderLogMapper mapper;

    @Override
    public Map<String, Object> queryAll(UserEnergyOrderLogQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<UserEnergyOrderLogDto> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<UserEnergyOrderLogDto> queryAll(UserEnergyOrderLogQueryCriteria criteria){
        return mapper.selectList(QueryHelpPlus.getPredicate(UserEnergyOrderLogQueryCriteria.class, criteria));
    }


}
