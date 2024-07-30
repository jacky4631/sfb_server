/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.shop.domain.MwMaterialGroup;
import com.mailvor.modules.shop.service.dto.MwMaterialGroupDto;
import com.mailvor.modules.shop.service.dto.MwMaterialGroupQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwMaterialGroupService extends BaseService<MwMaterialGroup>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwMaterialGroupQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWMaterialGroupDto>
    */
    List<MwMaterialGroup> queryAll(MwMaterialGroupQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwMaterialGroupDto> all, HttpServletResponse response) throws IOException;
}
