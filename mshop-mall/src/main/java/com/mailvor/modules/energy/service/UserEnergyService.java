/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.energy.domain.UserEnergy;
import com.mailvor.modules.energy.dto.UserEnergyDto;
import com.mailvor.modules.energy.dto.UserEnergyQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-02-04
*/
public interface UserEnergyService extends BaseService<UserEnergy>{

    Map<String, Object> queryAll(UserEnergyQueryCriteria criteria, Pageable pageable);

    List<UserEnergy> queryAll(UserEnergyQueryCriteria criteria);
    boolean addEnergy(Long uid, Long oid, String platform, Integer level, Integer energyType);
    boolean addMonthEnergy(Long uid, Long oid, String platform, Integer level, Integer energyType);
    boolean addEnergy(Long uid, Long oid, String platform, BigDecimal addEnergy, Integer energyType);

    boolean decEnergy(Long uid, String platform, BigDecimal decEnergy, Integer energyType);

    List<UserEnergy> getEnergyList(BigDecimal dayEnergy);
    List<UserEnergy> getEnergyTuiList(BigDecimal dayEnergy);
    UserEnergyDto getEnergy(Long uid, boolean detailInfo);

    UserEnergyDto setEnergy(Long uid, String platform, BigDecimal dayEnergy);
}
