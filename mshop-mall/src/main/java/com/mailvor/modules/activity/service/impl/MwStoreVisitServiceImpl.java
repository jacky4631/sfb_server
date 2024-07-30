/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ProductTypeEnum;
import com.mailvor.modules.activity.domain.MwStoreVisit;
import com.mailvor.modules.activity.service.MwStoreVisitService;
import com.mailvor.modules.activity.service.dto.MwStoreVisitDto;
import com.mailvor.modules.activity.service.dto.MwStoreVisitQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreVisitMapper;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.service.MwStoreProductService;
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



/**
* @author huangyu
* @date 2020-05-13
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreVisitServiceImpl extends BaseServiceImpl<MwStoreVisitMapper, MwStoreVisit> implements MwStoreVisitService {

    private final IGenerator generator;
    private final MwStoreProductService mwStoreProductService;
    private final MwStoreVisitMapper mwStoreVisitMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreVisitQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreVisit> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwStoreVisitDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreVisit> queryAll(MwStoreVisitQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreVisit.class, criteria));
    }

    @Override
    public void download(List<MwStoreVisitDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreVisitDto mwStoreVisit : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("产品ID", mwStoreVisit.getProductId());
            map.put("产品类型", mwStoreVisit.getProductType());
            map.put("产品分类ID", mwStoreVisit.getCateId());
            map.put("产品类型", mwStoreVisit.getType());
            map.put("用户ID", mwStoreVisit.getUid());
            map.put("访问次数", mwStoreVisit.getCount());
            map.put("备注描述", mwStoreVisit.getContent());
            map.put("添加时间", mwStoreVisit.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 添加用户访问拼团记录
     * @param uid 用户id
     * @param productId 产品id
     */
    @Override
    public void addStoreVisit(Long uid, Long productId) {

        LambdaQueryWrapper<MwStoreVisit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreVisit::getUid, uid).eq(MwStoreVisit::getProductId, productId);
        MwStoreVisit storeVisit = this.baseMapper.selectOne(wrapper);

        if (ObjectUtil.isNull(storeVisit)) {
            //查询产品分类
            MwStoreProduct mwStoreProduct = mwStoreProductService.getProductInfo(productId);

            MwStoreVisit mwStoreVisit = MwStoreVisit.builder()
                    .productId(productId)
                    .productType(ProductTypeEnum.COMBINATION.getValue())
                    .cateId(Integer.valueOf(mwStoreProduct.getCateId()))
                    .type(ProductTypeEnum.COMBINATION.getValue())
                    .uid(uid)
                    .count(1)
                    .build();
            this.save(mwStoreVisit);
        }

    }
}
