/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.dto.UserEnergyOrderLogDto;
import com.mailvor.modules.energy.dto.UserEnergyOrderLogQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-02-04
*/
public interface UserEnergyOrderLogService extends BaseService<UserEnergyOrderLog>{

    Map<String, Object> queryAll(UserEnergyOrderLogQueryCriteria criteria, Pageable pageable);
    List<UserEnergyOrderLogDto> queryAll(UserEnergyOrderLogQueryCriteria criteria);
}
