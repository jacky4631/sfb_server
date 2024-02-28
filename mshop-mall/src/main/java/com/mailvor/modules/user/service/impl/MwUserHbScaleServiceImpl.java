/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.energy.dto.UserEnergyOrderQueryCriteria;
import com.mailvor.modules.energy.dto.UserEnergyOrderScaleQueryCriteria;
import com.mailvor.modules.user.domain.MwUserHbScale;
import com.mailvor.modules.user.service.MwUserHbScaleService;
import com.mailvor.modules.user.service.mapper.UserHbScaleMapper;
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
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
public class MwUserHbScaleServiceImpl extends BaseServiceImpl<UserHbScaleMapper, MwUserHbScale> implements MwUserHbScaleService {

    private final IGenerator generator;
    private final UserHbScaleMapper mapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(UserEnergyOrderScaleQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserHbScale> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<MwUserHbScale> orders = page.getList();

        map.put("content", orders);
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<MwUserHbScale> queryAll(UserEnergyOrderScaleQueryCriteria criteria){
        return mapper.selectList(QueryHelpPlus.getPredicate(UserEnergyOrderQueryCriteria.class, criteria));
    }

}
