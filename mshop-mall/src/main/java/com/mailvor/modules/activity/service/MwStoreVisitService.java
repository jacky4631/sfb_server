/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreVisit;
import com.mailvor.modules.activity.service.dto.MwStoreVisitDto;
import com.mailvor.modules.activity.service.dto.MwStoreVisitQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author mazhongjun
* @date 2020-05-13
*/
public interface MwStoreVisitService extends BaseService<MwStoreVisit>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreVisitQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreVisitDto>
    */
    List<MwStoreVisit> queryAll(MwStoreVisitQueryCriteria criteria);

    /**
     * 添加用户访问拼团记录
     * @param uid 用户id
     * @param productId 产品id
     */
    void addStoreVisit(Long uid,Long productId);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreVisitDto> all, HttpServletResponse response) throws IOException;
}
