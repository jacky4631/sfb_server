/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.mp.service.mapper.WechatMenuMapper;
import com.mailvor.modules.mp.domain.MwWechatMenu;
import com.mailvor.modules.mp.service.MwWechatMenuService;
import com.mailvor.modules.mp.service.dto.MwWechatMenuDto;
import com.mailvor.modules.mp.service.dto.MwWechatMenuQueryCriteria;
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
//@CacheConfig(cacheNames = "mwWechatMenu")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwWechatMenuServiceImpl extends BaseServiceImpl<WechatMenuMapper, MwWechatMenu> implements MwWechatMenuService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwWechatMenuQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwWechatMenu> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwWechatMenuDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwWechatMenu> queryAll(MwWechatMenuQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwWechatMenu.class, criteria));
    }


    @Override
    public void download(List<MwWechatMenuDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwWechatMenuDto mwWechatMenu : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("缓存数据", mwWechatMenu.getResult());
            map.put("缓存时间", mwWechatMenu.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Boolean isExist(String wechat_menus) {
        MwWechatMenu mwWechatMenu = this.getOne(new LambdaQueryWrapper<MwWechatMenu>()
                .eq(MwWechatMenu::getKey,wechat_menus));
        if(mwWechatMenu == null){
            return false;
        }
        return true;
    }
}
