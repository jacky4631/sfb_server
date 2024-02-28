/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.energy.dto.*;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.shop.service.dto.MwSystemConfigDto;
import com.mailvor.modules.shop.service.dto.MwSystemConfigQueryCriteria;
import com.mailvor.modules.shop.service.mapper.SystemConfigMapper;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.RedisUtils;
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

import static com.mailvor.constant.SystemConfigConstants.*;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwSystemConfigServiceImpl extends BaseServiceImpl<SystemConfigMapper, MwSystemConfig> implements MwSystemConfigService {

    private final IGenerator generator;
    private final RedisUtils redisUtils;

    /**
     * 获取配置值
     * @param name 配置名
     * @return string
     */
    @Override
    public String getData(String name) {
        String result = redisUtils.getY(name);
        if (StrUtil.isNotBlank(result)) {
            return result;
        }

        LambdaQueryWrapper<MwSystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwSystemConfig::getMenuName, name);
        MwSystemConfig systemConfig = this.baseMapper.selectOne(wrapper);
        if (systemConfig == null) {
            return "";
        }
        return systemConfig.getValue();
    }

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwSystemConfigQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemConfig> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwSystemConfigDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwSystemConfig> queryAll(MwSystemConfigQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwSystemConfig.class, criteria));
    }


    @Override
    public void download(List<MwSystemConfigDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwSystemConfigDto mwSystemConfig : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("字段名称", mwSystemConfig.getMenuName());
            map.put("默认值", mwSystemConfig.getValue());
            map.put("排序", mwSystemConfig.getSort());
            map.put("是否隐藏", mwSystemConfig.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public MwSystemConfig findByKey(String key) {
        return this.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
    }

    @Override
    public EnergyConfigDto getEnergyConfig() {
        String result = redisUtils.getY(ENERGY_CONFIG);
        if (StrUtil.isNotBlank(result)) {
            return JSON.parseObject(result, EnergyConfigDto.class);
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,ENERGY_CONFIG));
        if(config == null) {
            return new EnergyConfigDto();
        }
        return JSON.parseObject(config.getValue(), EnergyConfigDto.class);
    }

    @Override
    public ExpCardConfigDto getExpCardConfig() {
        String result = redisUtils.getY(EXP_CARD_CONFIG);
        if (StrUtil.isNotBlank(result)) {
            return JSON.parseObject(result, ExpCardConfigDto.class);
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,EXP_CARD_CONFIG));
        if(config == null) {
            return new ExpCardConfigDto();
        }
        redisUtils.set(EXP_CARD_CONFIG, config.getValue());
        return JSON.parseObject(config.getValue(), ExpCardConfigDto.class);
    }

    @Override
    public MonthCardConfigDto getMonthCardConfig() {
        String result = redisUtils.getY(MONTH_CARD_CONFIG);
        if (StrUtil.isNotBlank(result)) {
            return JSON.parseObject(result, MonthCardConfigDto.class);
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,MONTH_CARD_CONFIG));
        if(config == null) {
            return new MonthCardConfigDto();
        }
        redisUtils.set(MONTH_CARD_CONFIG, config.getValue());
        return JSON.parseObject(config.getValue(), MonthCardConfigDto.class);
    }


    @Override
    public RecoverScaleConfigDto getRecoverScaleConfig() {
        String result = redisUtils.getY(RECOVER_SCALE_CONFIG);
        if (StrUtil.isNotBlank(result)) {
            return JSON.parseObject(result, RecoverScaleConfigDto.class);
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,RECOVER_SCALE_CONFIG));
        if(config == null) {
            return new RecoverScaleConfigDto();
        }
        redisUtils.set(RECOVER_SCALE_CONFIG, config.getValue());
        return JSON.parseObject(config.getValue(), RecoverScaleConfigDto.class);
    }

    @Override
    public Long getSpreadHbCount() {
        String result = redisUtils.getY(SPREAD_HB_COUNT_CONFIG);
        if (StrUtil.isNotBlank(result)) {
            return Long.parseLong(result);
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,SPREAD_HB_COUNT_CONFIG));
        if(config == null) {
            return 2L;
        }
        redisUtils.set(SPREAD_HB_COUNT_CONFIG, config.getValue());
        return Long.parseLong(config.getValue());
    }

    @Override
    public OrderCheckConfigDto getOrderCheckConfig() {
        String result = redisUtils.getY(ORDER_CHECK_CONFIG);
        if (StrUtil.isNotBlank(result)) {
            return JSON.parseObject(result, OrderCheckConfigDto.class);
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,ORDER_CHECK_CONFIG));
        if(config == null) {
            return new OrderCheckConfigDto();
        }
        redisUtils.set(ORDER_CHECK_CONFIG, config.getValue());
        return JSON.parseObject(config.getValue(), OrderCheckConfigDto.class);
    }
}
