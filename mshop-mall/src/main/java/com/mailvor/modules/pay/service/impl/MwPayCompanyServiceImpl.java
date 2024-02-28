/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service.impl;

import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.pay.service.MwPayCompanyService;
import com.mailvor.modules.pay.domain.MwPayChannel;
import com.mailvor.modules.pay.domain.MwPayCompany;
import com.mailvor.modules.pay.dto.PayCompanyDto;
import com.mailvor.modules.pay.dto.PayCompanyQueryCriteria;
import com.mailvor.modules.pay.service.mapper.PayCompanyMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
* @author wangke
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwPayCompanyServiceImpl extends BaseServiceImpl<PayCompanyMapper, MwPayCompany> implements MwPayCompanyService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(PayCompanyQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwPayCompany> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<PayCompanyDto> payChannelDtos = generator.convert(page.getList(), PayCompanyDto.class);

        map.put("content",payChannelDtos);
        map.put("totalElements",page.getTotal());
        return map;
    }

    @Override
    //@Cacheable
    public List<MwPayCompany> queryAll(PayCompanyQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwPayChannel.class, criteria));
    }
}
