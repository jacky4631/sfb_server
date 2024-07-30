/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.order.domain.MwExpress;
import com.mailvor.modules.order.service.MwExpressService;
import com.mailvor.modules.order.service.dto.MwExpressDto;
import com.mailvor.modules.order.service.dto.MwExpressQueryCriteria;
import com.mailvor.modules.order.service.mapper.ExpressMapper;
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
public class MwExpressServiceImpl extends BaseServiceImpl<ExpressMapper, MwExpress> implements MwExpressService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwExpressQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwExpress> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwExpressDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwExpress> queryAll(MwExpressQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwExpress.class, criteria));
    }


    @Override
    public void download(List<MwExpressDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwExpressDto mwExpress : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("快递公司简称", mwExpress.getCode());
            map.put("快递公司全称", mwExpress.getName());
            map.put("排序", mwExpress.getSort());
            map.put("是否显示", mwExpress.getIsShow());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
