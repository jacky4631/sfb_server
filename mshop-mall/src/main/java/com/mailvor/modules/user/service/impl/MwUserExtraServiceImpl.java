/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.mailvor.modules.user.service.dto.MwUserQueryCriteria;
import com.mailvor.modules.user.service.mapper.UserExtraMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MwUserExtraServiceImpl extends BaseServiceImpl<UserExtraMapper, MwUserExtra> implements MwUserExtraService {

    @Resource
    private IGenerator generator;

    @Resource
    private UserExtraMapper mapper;

    @Override
    public Map<String, Object> queryAll(MwUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserExtra> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<MwUserExtra> queryAll(MwUserQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwUser.class, criteria));
    }

}
