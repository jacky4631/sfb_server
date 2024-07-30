/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.order.domain.MwStoreOrderStatus;
import com.mailvor.modules.order.service.dto.MwStoreOrderStatusDto;
import com.mailvor.modules.order.service.dto.MwStoreOrderStatusQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwStoreOrderStatusService extends BaseService<MwStoreOrderStatus>{

    /**
     * 添加订单操作记录
     * @param oid 订单id
     * @param changetype 操作状态
     * @param changeMessage 操作内容
     */
    void create(Long oid,String changetype,String changeMessage);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreOrderStatusQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreOrderStatusDto>
    */
    List<MwStoreOrderStatus> queryAll(MwStoreOrderStatusQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreOrderStatusDto> all, HttpServletResponse response) throws IOException;
}
