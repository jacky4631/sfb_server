/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwUserExtractConfig;
import com.mailvor.modules.activity.service.dto.MwUserExtractQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-13
*/
public interface MwUserExtractConfigService extends BaseService<MwUserExtractConfig>{
    boolean canExtract(Long uid);

    Map<String,Object> queryAll(MwUserExtractQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有数据不分页
     * @param criteria 条件参数
     * @return List<MwUserExtractDto>
     */
    List<MwUserExtractConfig> queryAll(MwUserExtractQueryCriteria criteria);

}
