/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.canvas.service.impl;

import com.mailvor.modules.canvas.domain.StoreCanvas;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.modules.canvas.service.StoreCanvasService;
import com.mailvor.modules.canvas.service.dto.StoreCanvasDto;
import com.mailvor.modules.canvas.service.dto.StoreCanvasQueryCriteria;
import com.mailvor.modules.canvas.service.mapper.StoreCanvasMapper;
import lombok.AllArgsConstructor;
import com.mailvor.dozer.service.IGenerator;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.mailvor.domain.PageResult;
/**
* @author mshop
* @date 2021-02-01
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "storeCanvas")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StoreCanvasServiceImpl extends BaseServiceImpl<StoreCanvasMapper, StoreCanvas> implements StoreCanvasService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public PageResult<StoreCanvasDto> queryAll(StoreCanvasQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<StoreCanvas> page = new PageInfo<>(queryAll(criteria));
        return generator.convertPageInfo(page,StoreCanvasDto.class);
    }


    @Override
    //@Cacheable
    public List<StoreCanvas> queryAll(StoreCanvasQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(StoreCanvas.class, criteria));
    }


    @Override
    public void download(List<StoreCanvasDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StoreCanvasDto storeCanvas : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("终端 1-小程序 2-H5 3-APP 4-PC", storeCanvas.getTerminal());
            map.put("画布json数据", storeCanvas.getJson());
            map.put("类型 1-系统画布 2-自定义页面 3-商家店铺装修", storeCanvas.getType());
            map.put("名称", storeCanvas.getName());
            map.put("店铺id，当type=3的时候，值为具体的店铺id，其它情况为0", storeCanvas.getShopId());
            map.put("创建时间", storeCanvas.getCreateTime());
            map.put("修改时间", storeCanvas.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
