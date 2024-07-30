/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.template.domain.MwShippingTemplates;
import com.mailvor.modules.template.service.dto.ShippingTemplatesDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author mazhongjun
* @date 2020-06-29
*/
public interface MwShippingTemplatesService extends BaseService<MwShippingTemplates>{

    /**
     * 新增与更新模板
     * @param id 模板id
     * @param shippingTemplatesDto ShippingTemplatesDto
     */
    void addAndUpdate(Integer id,ShippingTemplatesDto shippingTemplatesDto);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwShippingTemplatesQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwShippingTemplatesDto>
    */
    List<MwShippingTemplates> queryAll(MwShippingTemplatesQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwShippingTemplatesDto> all, HttpServletResponse response) throws IOException;
}
