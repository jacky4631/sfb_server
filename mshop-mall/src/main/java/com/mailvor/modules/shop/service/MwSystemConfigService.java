/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.service.dto.MwExtractConfigDto;
import com.mailvor.modules.energy.dto.*;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.dto.MwSystemConfigDto;
import com.mailvor.modules.shop.service.dto.MwSystemConfigQueryCriteria;
import com.mailvor.modules.shop.service.dto.PayConfigDto;
import com.mailvor.modules.user.config.AppDataConfig;
import com.mailvor.modules.user.config.HbUnlockConfig;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwSystemConfigService extends BaseService<MwSystemConfig>{

    /**
     * 获取配置值
     * @param name 配置名
     * @return string
     */
    String getData(String name);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwSystemConfigQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWSystemConfigDto>
    */
    List<MwSystemConfig> queryAll(MwSystemConfigQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwSystemConfigDto> all, HttpServletResponse response) throws IOException;

    MwSystemConfig findByKey(String store_brokerage_statu);

    EnergyConfigDto getEnergyConfig();
    void setEnergyConfig(EnergyConfigDto param);

    MonthCardConfigDto getMonthCardConfig();

    RecoverScaleConfigDto getRecoverScaleConfig();

    Long getSpreadHbCount();

    OrderCheckConfigDto getOrderCheckConfig();

    HbUnlockConfig getHbUnlockConfig();
    void setHbUnlockConfig(HbUnlockConfig param);
    PayConfigDto getAppPayConfig();
    void setAppPayConfig(PayConfigDto configDTO);

    MwExtractConfigDto getAppExtractConfig();


    AppDataConfig getAppDataConfig();
    void setAppDataConfig(AppDataConfig param);



    List<String> getAppShareConfig();
    void setAppShareConfig(List<String> images);

    List<String> getAppLoginWhitelist();

    void setAppLoginWhiteList(List<String> whiteList);
}
