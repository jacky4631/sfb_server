/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.product.vo.MwSystemStoreQueryVo;
import com.mailvor.modules.shop.domain.MwSystemStore;
import com.mailvor.modules.shop.service.dto.MwSystemStoreDto;
import com.mailvor.modules.shop.service.dto.MwSystemStoreQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwSystemStoreService extends BaseService<MwSystemStore>{

    MwSystemStoreQueryVo getMwSystemStoreById(int id);

    /**
     * 获取门店列表
     * @param latitude 纬度
     * @param longitude 经度
     * @param page page
     * @param limit limit
     * @return List
     */
    List<MwSystemStoreQueryVo> getStoreList(String latitude, String longitude, int page, int limit);

    /**
     * 获取最新单个门店
     * @param latitude 纬度
     * @param longitude 经度
     * @return MWSystemStoreQueryVo
     */
    MwSystemStoreQueryVo getStoreInfo(String latitude, String longitude);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwSystemStoreQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWSystemStoreDto>
    */
    List<MwSystemStore> queryAll(MwSystemStoreQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwSystemStoreDto> all, HttpServletResponse response) throws IOException;
}
