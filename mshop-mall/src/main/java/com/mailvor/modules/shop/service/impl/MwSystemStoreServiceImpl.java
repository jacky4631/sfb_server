/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.product.vo.MwSystemStoreQueryVo;
import com.mailvor.modules.shop.domain.MwSystemStore;
import com.mailvor.modules.shop.service.MwSystemStoreService;
import com.mailvor.modules.shop.service.dto.MwSystemStoreDto;
import com.mailvor.modules.shop.service.dto.MwSystemStoreQueryCriteria;
import com.mailvor.modules.shop.service.mapper.SystemStoreMapper;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.ShopKeyUtils;
import com.mailvor.utils.location.LocationUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class MwSystemStoreServiceImpl extends BaseServiceImpl<SystemStoreMapper, MwSystemStore> implements MwSystemStoreService {

    private final IGenerator generator;
    private final SystemStoreMapper systemStoreMapper;

    @Override
    public MwSystemStoreQueryVo getMwSystemStoreById(int id){
        return generator.convert(this.getById(id), MwSystemStoreQueryVo.class);
    }

    /**
     * 获取门店列表
     * @param latitude 纬度
     * @param longitude 经度
     * @param page page
     * @param limit limit
     * @return List
     */
    @Override
    public List<MwSystemStoreQueryVo> getStoreList(String latitude, String longitude, int page, int limit) {
        Page<MwSystemStore> pageModel = new Page<>(page, limit);
        if(StrUtil.isBlank(latitude) || StrUtil.isBlank(longitude)){
            return generator.convert(this.page(pageModel).getRecords(), MwSystemStoreQueryVo.class);
        }
        List<MwSystemStoreQueryVo> list = systemStoreMapper.getStoreList(pageModel,Double.valueOf(longitude),Double.valueOf(latitude));
        list.forEach(item->{
            String newDis = NumberUtil.round(Double.valueOf(item.getDistance()) / 1000,2).toString();
            item.setDistance(newDis);
        });
        return list;
    }

    /**
     * 获取最新单个门店
     * @param latitude 纬度
     * @param longitude 经度
     * @return MwSystemStoreQueryVo
     */
    @Override
    public MwSystemStoreQueryVo getStoreInfo(String latitude, String longitude) {
        MwSystemStore MWSystemStore = systemStoreMapper.selectOne(
                Wrappers.<MwSystemStore>lambdaQuery()
                        .eq(MwSystemStore::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                        .orderByDesc(MwSystemStore::getId)
                        .last("limit 1"));
        if(MWSystemStore == null) {
            return null;
        }
        String mention = RedisUtil.get(ShopKeyUtils.getStoreSelfMention());
        if(StrUtil.isBlank(mention) || ShopCommonEnum.ENABLE_2.getValue().toString().equals(mention)) {
            return null;
        }
        MwSystemStoreQueryVo systemStoreQueryVo = generator.convert(MWSystemStore, MwSystemStoreQueryVo.class);
        if(StrUtil.isNotEmpty(latitude) && StrUtil.isNotEmpty(longitude)){
            double distance = LocationUtils.getDistance(Double.valueOf(latitude),Double.valueOf(longitude),
                    Double.valueOf(MWSystemStore.getLatitude()),Double.valueOf(MWSystemStore.getLongitude()));
            systemStoreQueryVo.setDistance(String.valueOf(distance));
        }
        return systemStoreQueryVo;
    }



    //===================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwSystemStoreQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemStore> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwSystemStoreDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwSystemStore> queryAll(MwSystemStoreQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwSystemStore.class, criteria));
    }


    @Override
    public void download(List<MwSystemStoreDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwSystemStoreDto mwSystemStore : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("门店名称", mwSystemStore.getName());
            map.put("简介", mwSystemStore.getIntroduction());
            map.put("手机号码", mwSystemStore.getPhone());
            map.put("省市区", mwSystemStore.getAddress());
            map.put("详细地址", mwSystemStore.getDetailedAddress());
            map.put("门店logo", mwSystemStore.getImage());
            map.put("纬度", mwSystemStore.getLatitude());
            map.put("经度", mwSystemStore.getLongitude());
            map.put("核销有效日期", mwSystemStore.getValidTime());
            map.put("每日营业开关时间", mwSystemStore.getDayTime());
            map.put("是否显示", mwSystemStore.getIsShow());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
