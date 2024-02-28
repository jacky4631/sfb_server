/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.energy.domain.UserEnergyLog;
import com.mailvor.modules.energy.dto.UserEnergyLogDto;
import com.mailvor.modules.energy.dto.UserEnergyLogQueryCriteria;
import com.mailvor.modules.energy.service.UserEnergyLogService;
import com.mailvor.modules.energy.service.mapper.UserEnergyLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-02-04
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserEnergyLogServiceImpl extends BaseServiceImpl<UserEnergyLogMapper, UserEnergyLog> implements UserEnergyLogService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private UserEnergyLogMapper mapper;

    @Override
    public Map<String, Object> queryAll(UserEnergyLogQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<UserEnergyLogDto> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<UserEnergyLogDto> queryAll(UserEnergyLogQueryCriteria criteria){
        return mapper.selectList(QueryHelpPlus.getPredicate(UserEnergyLogQueryCriteria.class, criteria));
    }

    @Override
    public Map<String, Object> logList(Long uid, int page, int limit) {
        LambdaQueryWrapper<UserEnergyLog> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(UserEnergyLog::getUid,uid)
                .orderByDesc(UserEnergyLog::getCreateTime);
        Page<UserEnergyLog> pageModel = new Page<>(page, limit);
        IPage<UserEnergyLog> pageList = mapper.selectPage(pageModel,wrapper);
        ;
        Map<String, Object> map = new HashMap<>();
        map.put("list", generator.convert(pageList.getRecords(), UserEnergyLogDto.class));
        map.put("total", pageList.getTotal());
        map.put("totalPage", pageList.getPages());
        return map;
    }

}
