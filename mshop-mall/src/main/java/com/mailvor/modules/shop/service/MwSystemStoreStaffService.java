/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.shop.domain.MwSystemStoreStaff;
import com.mailvor.modules.shop.service.dto.MwSystemStoreStaffDto;
import com.mailvor.modules.shop.service.dto.MwSystemStoreStaffQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwSystemStoreStaffService extends BaseService<MwSystemStoreStaff>{

    /**
     * 接测店员客服状态
     * @param uid 用户id
     * @param storeId 门店id
     * @return boolean true=可核销
     */
    boolean checkStatus(Long uid,Integer storeId);


    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwSystemStoreStaffQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWSystemStoreStaffDto>
    */
    List<MwSystemStoreStaff> queryAll(MwSystemStoreStaffQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwSystemStoreStaffDto> all, HttpServletResponse response) throws IOException;
}
