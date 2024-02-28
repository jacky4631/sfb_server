/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.energy.dto.*;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.dto.MwSystemConfigDto;
import com.mailvor.modules.shop.service.dto.MwSystemConfigQueryCriteria;
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

    ExpCardConfigDto getExpCardConfig();

    MonthCardConfigDto getMonthCardConfig();

    RecoverScaleConfigDto getRecoverScaleConfig();

    Long getSpreadHbCount();

    OrderCheckConfigDto getOrderCheckConfig();
}
