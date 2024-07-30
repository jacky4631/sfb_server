/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.shop.domain.MwExpend;
import com.mailvor.modules.shop.service.ExpendService;
import com.mailvor.modules.shop.service.dto.MwExpendDto;
import com.mailvor.modules.shop.service.dto.MwExpendQueryCriteria;
import com.mailvor.modules.shop.service.mapper.ExpendMapper;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


/**
* @author wangke
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExpendServiceImpl extends BaseServiceImpl<ExpendMapper, MwExpend> implements ExpendService {

    private final IGenerator generator;

    private final ExpendMapper mapper;


    //===============管理后台==============

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwExpendQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwExpend> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content",generator.convert(page.getList(), MwExpendDto.class));
        map.put("totalElements",page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwExpend> queryAll(MwExpendQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwExpend.class, criteria));
    }


    @Override
    public void download(List<MwExpend> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwExpend expend : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("对应的数据名称", expend.getMoney());
            map.put("添加数据时间", expend.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public Double totalExpend(Integer type){
        switch (type){
            case 1:
                return mapper.sumPrice(getWrapper(DateUtils.getToday()));
            case 2:
                return mapper.sumPrice(getWrapperPro(DateUtils.getProDay(), DateUtils.getToday()));
            case 3:
                return mapper.sumPrice(getWrapper(DateUtils.getMonth()));
            case 4:
                return mapper.sumPrice(getWrapperPro(DateUtils.getProMonth(), DateUtils.getMonth()));
            case 5:
                return mapper.sumPrice(getWrapper(DateUtils.get30Day()));
            case 6:
                return mapper.sumPrice(getWrapper(DateUtils.get7Day()));
        }
        return 0.0;
    }


    private LambdaQueryWrapper<MwExpend> getWrapper(DateTime start) {
        return getWrapperPro(start, null);
    }

    private LambdaQueryWrapper<MwExpend> getWrapperPro(Date start, Date end) {
        LambdaQueryWrapper<MwExpend> wrapperTwo = new LambdaQueryWrapper<>();
        wrapperTwo.eq(MwExpend::getIsDel, 0);

        if(start != null) {
            wrapperTwo.ge(MwExpend::getAddTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MwExpend::getAddTime, end);
        }
        return wrapperTwo;
    }

}
