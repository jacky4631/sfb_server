/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.dto.MwUserQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwUserExtraService extends BaseService<MwUserExtra>{

    Map<String,Object> queryAll(MwUserQueryCriteria criteria, Pageable pageable);

    List<MwUserExtra> queryAll(MwUserQueryCriteria criteria);

}
