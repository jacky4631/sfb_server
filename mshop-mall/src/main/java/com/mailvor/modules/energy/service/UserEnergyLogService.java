/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.energy.domain.UserEnergyLog;
import com.mailvor.modules.energy.dto.UserEnergyLogDto;
import com.mailvor.modules.energy.dto.UserEnergyLogQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-02-04
*/
public interface UserEnergyLogService extends BaseService<UserEnergyLog>{

    Map<String, Object> queryAll(UserEnergyLogQueryCriteria criteria, Pageable pageable);
    List<UserEnergyLogDto> queryAll(UserEnergyLogQueryCriteria criteria);

    Map<String, Object> logList(Long uid, int page, int limit);
}
