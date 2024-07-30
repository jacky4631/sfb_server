/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.product.domain.MwStoreProductRule;
import com.mailvor.modules.product.service.MwStoreProductRuleService;
import com.mailvor.modules.product.service.dto.MwStoreProductRuleQueryCriteria;
import com.mailvor.modules.product.service.mapper.MwStoreProductRuleMapper;
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
* @date 2020-06-28
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreProductRuleServiceImpl extends BaseServiceImpl<MwStoreProductRuleMapper, MwStoreProductRule> implements MwStoreProductRuleService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreProductRuleQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreProductRule> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreProductRule> queryAll(MwStoreProductRuleQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreProductRule.class, criteria));
    }


    @Override
    public void download(List<MwStoreProductRule> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreProductRule mwStoreProductRule : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("规格名称", mwStoreProductRule.getRuleName());
            map.put("规格值", mwStoreProductRule.getRuleValue());
            map.put(" createTime",  mwStoreProductRule.getCreateTime());
            map.put(" updateTime",  mwStoreProductRule.getUpdateTime());
            map.put(" isDel",  mwStoreProductRule.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
