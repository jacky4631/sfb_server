/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.activity.service.dto.MwExtractConfigDto;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.energy.dto.MonthCardConfigDto;
import com.mailvor.modules.energy.dto.OrderCheckConfigDto;
import com.mailvor.modules.energy.dto.RecoverScaleConfigDto;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.shop.service.dto.MwSystemConfigDto;
import com.mailvor.modules.shop.service.dto.MwSystemConfigQueryCriteria;
import com.mailvor.modules.shop.service.dto.PayConfigDto;
import com.mailvor.modules.shop.service.mapper.SystemConfigMapper;
import com.mailvor.modules.tk.constants.TkConstants;
import com.mailvor.modules.user.config.AppDataConfig;
import com.mailvor.modules.user.config.HbUnlockConfig;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.RedisUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.mailvor.constant.ShopConstants.APP_LOGIN_WHITELIST;
import static com.mailvor.constant.ShopConstants.MSHOP_USER_SHARE;
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
        String key = TkUtil.getMixedPlatformKey(ENERGY_CONFIG);
        Object result = redisUtils.get(key);
        if (ObjectUtil.isNotNull(result)) {
            return (EnergyConfigDto)result;
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        EnergyConfigDto res;
        if(config == null) {
            res = new EnergyConfigDto();
            res.init();
        } else {
            res = JSON.parseObject(config.getValue(), EnergyConfigDto.class);
        }
        redisUtils.set(key,res);
        return res;
    }

    public void setEnergyConfig(EnergyConfigDto param) {
        String key = TkUtil.getMixedPlatformKey(ENERGY_CONFIG);
        MwSystemConfig systemConfig = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));

        MwSystemConfig mwSystemConfigModel = new MwSystemConfig();
        mwSystemConfigModel.setMenuName(key);
        mwSystemConfigModel.setValue(JSON.toJSONString(param));

        redisUtils.set(key,param);
        if(ObjectUtil.isNull(systemConfig)){
            save(mwSystemConfigModel);
        }else{
            mwSystemConfigModel.setId(systemConfig.getId());
            saveOrUpdate(mwSystemConfigModel);
        }

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

    @Override
    public HbUnlockConfig getHbUnlockConfig() {
        String key = TkUtil.getMixedPlatformKey(HB_UNLOCK_CONFIG);
        Object result = redisUtils.get(key);
        if (result != null) {
            return (HbUnlockConfig)result;
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        HbUnlockConfig res;
        if(config == null) {
            res = new HbUnlockConfig();
        } else {
            res = JSON.parseObject(config.getValue(), HbUnlockConfig.class);
        }
        redisUtils.set(key, res);
        return res;
    }

    public void setHbUnlockConfig(HbUnlockConfig param) {
        String key = TkUtil.getMixedPlatformKey(HB_UNLOCK_CONFIG);
        MwSystemConfig systemConfig = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                        .eq(MwSystemConfig::getMenuName, key));
        if(systemConfig == null) {
            systemConfig = new MwSystemConfig();
            systemConfig.setMenuName(key);
        }
        String value = JSON.toJSONString(param);
        systemConfig.setValue(value);
        redisUtils.set(key, param);
        saveOrUpdate(systemConfig);
    }

    @Override
    public PayConfigDto getAppPayConfig() {
        String key = TkUtil.getMixedPlatformKey(PAY_CONFIG);
        Object result = redisUtils.get(key);
        if (result != null) {
            return (PayConfigDto)result;
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        PayConfigDto configDto;
        if(config == null) {
            configDto = new PayConfigDto();
        } else {
            configDto = JSON.parseObject(config.getValue(), PayConfigDto.class);
        }
        redisUtils.set(key, configDto);
        return configDto;
    }

    @Override
    public void setAppPayConfig(PayConfigDto configDTO) {
        String key = TkUtil.getMixedPlatformKey(PAY_CONFIG);

        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        if(config == null) {
            config = new MwSystemConfig();
            config.setMenuName(key);
        }
        config.setValue(JSON.toJSONString(configDTO));
        saveOrUpdate(config);
        redisUtils.set(key, configDTO);
    }

    @Override
    public MwExtractConfigDto getAppExtractConfig() {
        String key = TkUtil.getMixedPlatformKey(EXTRACT_CONFIG);
        Object result = redisUtils.get(key);
        if (result != null) {
            return (MwExtractConfigDto)result;
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        MwExtractConfigDto res;
        if(config == null) {
            res = new MwExtractConfigDto();
        } else {
            res = JSON.parseObject(config.getValue(), MwExtractConfigDto.class);
        }
        redisUtils.set(key, res);
        return res;
    }





    @Override
    public AppDataConfig getAppDataConfig() {
        String key = TkUtil.getMixedPlatformKey(HOME_DATA);
        Object result = redisUtils.get(key);
        if (result != null) {
            return (AppDataConfig)result;
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        AppDataConfig res;
        if(config == null) {
            res = new AppDataConfig();
        } else {
            res = JSON.parseObject(config.getValue(), AppDataConfig.class);
        }
        redisUtils.set(key, res);
        return res;
    }

    public void setAppDataConfig(AppDataConfig param) {
        String key = TkUtil.getMixedPlatformKey(HOME_DATA);
        MwSystemConfig systemConfig = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName, key));
        if(systemConfig == null) {
            systemConfig = new MwSystemConfig();
            systemConfig.setMenuName(key);
        }
        systemConfig.setValue(JSON.toJSONString(param));
        TkConstants.kuCid = null;
        redisUtils.set(key, param);
        saveOrUpdate(systemConfig);
    }


    @Override
    public List<String> getAppShareConfig() {

        String key = TkUtil.getMixedPlatformKey(MSHOP_USER_SHARE);
        List<Object> result = redisUtils.getList(key);
        if (result != null) {
            return result.stream()
                    .map(obj -> (String) obj) // 根据需求进行类型转换
                    .collect(Collectors.toList());
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        List<String> res;
        if(config == null) {
            res = Collections.EMPTY_LIST;
        } else {
            res = JSON.parseObject(config.getValue(), List.class);
        }
        redisUtils.setList(key, res);
        return res;
    }

    public void setAppShareConfig(List<String> images){
        String key = TkUtil.getMixedPlatformKey(MSHOP_USER_SHARE);
        MwSystemConfig systemConfig = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName, key));
        if(systemConfig == null) {
            systemConfig = new MwSystemConfig();
            systemConfig.setMenuName(key);
        }
        systemConfig.setValue(JSON.toJSONString(images));
        redisUtils.setList(key, images);
        saveOrUpdate(systemConfig);
    }




    @Override
    public List<String> getAppLoginWhitelist() {

        String key = TkUtil.getMixedPlatformKey(APP_LOGIN_WHITELIST);
        List<Object> result = redisUtils.getList(key);
        if (result != null) {
            return result.stream()
                    .map(obj -> (String) obj) // 根据需求进行类型转换
                    .collect(Collectors.toList());
        }
        MwSystemConfig config = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,key));
        List<String> res;
        if(config == null) {
            res = Collections.EMPTY_LIST;
        } else {
            res = JSON.parseObject(config.getValue(), List.class);
        }
        redisUtils.setList(key, res);
        return res;
    }

    @Override
    public void setAppLoginWhiteList(List<String> whiteList){
        String key = TkUtil.getMixedPlatformKey(APP_LOGIN_WHITELIST);
        MwSystemConfig systemConfig = getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName, key));
        if(systemConfig == null) {
            systemConfig = new MwSystemConfig();
            systemConfig.setMenuName(key);
        }
        systemConfig.setValue(JSON.toJSONString(whiteList));
        redisUtils.setList(key, whiteList);
        saveOrUpdate(systemConfig);
    }

}
