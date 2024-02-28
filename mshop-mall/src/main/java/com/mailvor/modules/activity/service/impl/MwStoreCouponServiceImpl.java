/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.activity.domain.MwStoreCoupon;
import com.mailvor.modules.activity.service.MwStoreCouponService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponDto;
import com.mailvor.modules.activity.service.dto.MwStoreCouponQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreCouponMapper;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.service.MwStoreProductService;
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
import java.util.Arrays;
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
public class MwStoreCouponServiceImpl extends BaseServiceImpl<MwStoreCouponMapper, MwStoreCoupon> implements MwStoreCouponService {

    private final IGenerator generator;
    private final MwStoreProductService storeProductService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreCouponQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCoupon> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<MwStoreCouponDto> storeCouponDtos = generator.convert(page.getList(), MwStoreCouponDto.class);
        for (MwStoreCouponDto storeCouponDto : storeCouponDtos) {
            if(StrUtil.isNotBlank(storeCouponDto.getProductId())){
                List<MwStoreProduct> storeProducts = storeProductService.lambdaQuery()
                        .in(MwStoreProduct::getId, Arrays.asList(storeCouponDto.getProductId().split(",")))
                        .list();
                storeCouponDto.setProduct(storeProducts);
            }
        }
        map.put("content", storeCouponDtos);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreCoupon> queryAll(MwStoreCouponQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreCoupon.class, criteria));
    }


    @Override
    public void download(List<MwStoreCouponDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCouponDto MWStoreCoupon : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("优惠券名称", MWStoreCoupon.getTitle());
            map.put("兑换消耗积分值", MWStoreCoupon.getIntegral());
            map.put("兑换的优惠券面值", MWStoreCoupon.getCouponPrice());
            map.put("最低消费多少金额可用优惠券", MWStoreCoupon.getUseMinPrice());
            map.put("优惠券有效期限（单位：天）", MWStoreCoupon.getCouponTime());
            map.put("排序", MWStoreCoupon.getSort());
            map.put("状态（0：关闭，1：开启）", MWStoreCoupon.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
