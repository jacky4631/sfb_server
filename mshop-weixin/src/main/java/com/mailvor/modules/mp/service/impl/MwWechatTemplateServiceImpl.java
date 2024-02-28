/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.mp.domain.MwWechatTemplate;
import com.mailvor.modules.mp.service.MwWechatTemplateService;
import com.mailvor.modules.mp.service.dto.MwWechatTemplateQueryCriteria;
import com.mailvor.modules.mp.service.dto.MwWechatTemplateDto;
import com.mailvor.modules.mp.service.mapper.WechatTemplateMapper;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mwWechatTemplate")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwWechatTemplateServiceImpl extends BaseServiceImpl<WechatTemplateMapper, MwWechatTemplate> implements MwWechatTemplateService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwWechatTemplateQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwWechatTemplate> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwWechatTemplateDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwWechatTemplate> queryAll(MwWechatTemplateQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwWechatTemplate.class, criteria));
    }


    @Override
    public void download(List<MwWechatTemplateDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwWechatTemplateDto mwWechatTemplate : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("模板编号", mwWechatTemplate.getTempkey());
            map.put("模板名", mwWechatTemplate.getName());
            map.put("回复内容", mwWechatTemplate.getContent());
            map.put("模板ID", mwWechatTemplate.getTempid());
            map.put("添加时间", mwWechatTemplate.getAddTime());
            map.put("状态", mwWechatTemplate.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public MwWechatTemplate findByTempkey(String recharge_success_key) {
        return this.getOne(new LambdaQueryWrapper<MwWechatTemplate>()
                .eq(MwWechatTemplate::getTempkey,recharge_success_key));
    }
}
