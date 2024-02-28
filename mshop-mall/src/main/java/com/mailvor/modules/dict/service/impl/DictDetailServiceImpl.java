/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.dict.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.dict.domain.DictDetail;
import com.mailvor.modules.dict.service.DictDetailService;
import com.mailvor.modules.dict.service.dto.DictDetailDto;
import com.mailvor.modules.dict.service.dto.DictDetailQueryCriteria;
import com.mailvor.modules.dict.service.mapper.DictDetailMapper;
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
import java.util.stream.Collectors;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author huangyu
* @date 2020-05-14
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "dictDetail")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictDetailServiceImpl extends BaseServiceImpl<DictDetailMapper, DictDetail> implements DictDetailService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<DictDetail> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), DictDetailDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<DictDetail> queryAll(DictDetailQueryCriteria criteria){
         return baseMapper.selectDictDetailList(criteria.getLabel(),criteria.getDictName());
    }


    @Override
    public void download(List<DictDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictDetailDto dictDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("字典标签", dictDetail.getLabel());
            map.put("字典值", dictDetail.getValue());
            map.put("排序", dictDetail.getSort());
            map.put("字典id", dictDetail.getDictId());
            map.put("创建日期", dictDetail.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, String> queryDetailsByName(String dictName) {
        List<DictDetailDto> dtoList = getDictDetailDtos(dictName);
        return dtoList.stream().collect(Collectors.toMap(DictDetailDto::getValue, DictDetailDto::getLabel));
    }

    @Override
    public Map<String, String> queryDetailsByKey(String dictName) {
        List<DictDetailDto> dtoList = getDictDetailDtos(dictName);
        return dtoList.stream().collect(Collectors.toMap(DictDetailDto::getLabel, DictDetailDto::getValue));
    }

    /**
     * 获取dictDetailDto列表
     *
     * @param dictName dict类型名称
     * @return {@link List}<{@link DictDetailDto}>
     */
    private List<DictDetailDto> getDictDetailDtos(String dictName) {
        DictDetailQueryCriteria criteria =new DictDetailQueryCriteria();
        criteria.setDictName(dictName);
        List<DictDetail> list =  baseMapper.selectDictDetailList(criteria.getLabel(),criteria.getDictName());
        return generator.convert(list, DictDetailDto.class);
    }

}
