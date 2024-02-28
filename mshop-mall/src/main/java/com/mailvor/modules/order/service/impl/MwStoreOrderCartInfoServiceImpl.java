/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.order.domain.MwStoreOrderCartInfo;
import com.mailvor.modules.order.service.MwStoreOrderCartInfoService;
import com.mailvor.modules.order.service.dto.MwStoreOrderCartInfoDto;
import com.mailvor.modules.order.service.dto.MwStoreOrderCartInfoQueryCriteria;
import com.mailvor.modules.order.service.mapper.StoreOrderCartInfoMapper;
import com.mailvor.utils.FileUtil;
import com.alibaba.fastjson.JSONObject;
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
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreOrderCartInfoServiceImpl extends BaseServiceImpl<StoreOrderCartInfoMapper, MwStoreOrderCartInfo> implements MwStoreOrderCartInfoService {

    private final IGenerator generator;

    @Override
    public MwStoreOrderCartInfo findByUni(String unique) {
       LambdaQueryWrapper<MwStoreOrderCartInfo> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreOrderCartInfo::getUnique,unique);
        return this.baseMapper.selectOne(wrapper);
    }

    /**
     * 添加购物车商品信息
     * @param oid 订单id
     * @param orderId
     * @param cartInfo 购物车信息
     */
    @Override
    public void saveCartInfo(Long oid, String orderId, List<MwStoreCartQueryVo> cartInfo) {

        List<MwStoreOrderCartInfo> list = new ArrayList<>();
        for (MwStoreCartQueryVo cart : cartInfo) {
            MwStoreOrderCartInfo info = new MwStoreOrderCartInfo();
            info.setOid(oid);
            info.setOrderId(orderId);
            info.setCartId(cart.getId());
            info.setProductId(cart.getProductId());
            info.setCartInfo(JSONObject.toJSON(cart).toString());
            info.setUnique(IdUtil.simpleUUID());
            info.setIsAfterSales(1);
            list.add(info);
        }

        this.saveBatch(list);
    }


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreOrderCartInfoQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreOrderCartInfo> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwStoreOrderCartInfoDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreOrderCartInfo> queryAll(MwStoreOrderCartInfoQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreOrderCartInfo.class, criteria));
    }


    @Override
    public void download(List<MwStoreOrderCartInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreOrderCartInfoDto mwStoreOrderCartInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单id", mwStoreOrderCartInfo.getOid());
            map.put("购物车id", mwStoreOrderCartInfo.getCartId());
            map.put("商品ID", mwStoreOrderCartInfo.getProductId());
            map.put("购买东西的详细信息", mwStoreOrderCartInfo.getCartInfo());
            map.put("唯一id", mwStoreOrderCartInfo.getUnique());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
