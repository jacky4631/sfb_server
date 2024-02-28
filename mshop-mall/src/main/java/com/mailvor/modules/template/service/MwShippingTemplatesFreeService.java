/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.template.domain.MwShippingTemplatesFree;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesFreeDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesFreeQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author mazhongjun
* @date 2020-06-29
*/
public interface MwShippingTemplatesFreeService extends BaseService<MwShippingTemplatesFree>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwShippingTemplatesFreeQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwShippingTemplatesFreeDto>
    */
    List<MwShippingTemplatesFree> queryAll(MwShippingTemplatesFreeQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwShippingTemplatesFreeDto> all, HttpServletResponse response) throws IOException;
}
