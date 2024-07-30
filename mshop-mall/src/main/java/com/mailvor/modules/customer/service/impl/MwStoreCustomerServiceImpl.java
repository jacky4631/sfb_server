/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.customer.service.impl;

import com.mailvor.modules.customer.domain.MwStoreCustomer;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.modules.customer.service.dto.MwStoreCustomerDto;
import com.mailvor.modules.customer.service.dto.MwStoreCustomerQueryCriteria;
import com.mailvor.modules.customer.service.mapper.MwStoreCustomerMapper;
import lombok.AllArgsConstructor;
import com.mailvor.dozer.service.IGenerator;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.utils.FileUtil;
import com.mailvor.modules.customer.service.MwStoreCustomerService;
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
* @author Bug
* @date 2020-12-10
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mwStoreCustomer")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreCustomerServiceImpl extends BaseServiceImpl<MwStoreCustomerMapper, MwStoreCustomer> implements MwStoreCustomerService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public PageResult<MwStoreCustomerDto> queryAll(MwStoreCustomerQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCustomer> page = new PageInfo<>(queryAll(criteria));
        return generator.convertPageInfo(page, MwStoreCustomerDto.class);
    }


    @Override
    //@Cacheable
    public List<MwStoreCustomer> queryAll(MwStoreCustomerQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreCustomer.class, criteria));
    }


    @Override
    public void download(List<MwStoreCustomerDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCustomerDto mwStoreCustomer : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户昵称", mwStoreCustomer.getNickName());
            map.put("openId", mwStoreCustomer.getOpenId());
            map.put("备注", mwStoreCustomer.getRemark());
            map.put("添加时间", mwStoreCustomer.getCreateTime());
            map.put("修改时间", mwStoreCustomer.getUpdateTime());
            map.put(" isDel",  mwStoreCustomer.getIsDel());
            map.put("是否启用", mwStoreCustomer.getIsEnable());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
