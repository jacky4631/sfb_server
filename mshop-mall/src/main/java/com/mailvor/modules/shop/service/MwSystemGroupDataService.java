/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.shop.domain.MwSystemGroupData;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataDto;
import com.mailvor.modules.shop.service.dto.MwSystemGroupDataQueryCriteria;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwSystemGroupDataService extends BaseService<MwSystemGroupData>{

    /**
     * 获取配置数据
     * @param name 配置名称
     * @return List
     */
    List<JSONObject> getDatas(String name);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwSystemGroupDataQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWSystemGroupDataDto>
    */
    List<MwSystemGroupData> queryAll(MwSystemGroupDataQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwSystemGroupDataDto> all, HttpServletResponse response) throws IOException;

    Map<String, Object> list(MwSystemGroupDataQueryCriteria criteria, Pageable pageable);
}
