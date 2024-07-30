/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.mp.service.dto.MwWechatTemplateQueryCriteria;
import com.mailvor.modules.mp.domain.MwWechatTemplate;
import com.mailvor.modules.mp.service.dto.MwWechatTemplateDto;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author mazhongjun
* @date 2020-05-12
*/
public interface MwWechatTemplateService extends BaseService<MwWechatTemplate>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwWechatTemplateQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwWechatTemplateDto>
    */
    List<MwWechatTemplate> queryAll(MwWechatTemplateQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwWechatTemplateDto> all, HttpServletResponse response) throws IOException;

    MwWechatTemplate findByTempkey(String recharge_success_key);
}
