/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.energy.dto.UserEnergyOrderQueryCriteria;
import com.mailvor.modules.energy.dto.UserEnergyOrderScaleQueryCriteria;
import com.mailvor.modules.user.domain.MwUserHbScale;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwUserHbScaleService extends BaseService<MwUserHbScale>{

    Map<String, Object> queryAll(UserEnergyOrderScaleQueryCriteria criteria, Pageable pageable);
    List<MwUserHbScale> queryAll(UserEnergyOrderScaleQueryCriteria criteria);
}
