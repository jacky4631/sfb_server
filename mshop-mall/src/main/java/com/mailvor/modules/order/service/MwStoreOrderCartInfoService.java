/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.order.domain.MwStoreOrderCartInfo;
import com.mailvor.modules.order.service.dto.MwStoreOrderCartInfoDto;
import com.mailvor.modules.order.service.dto.MwStoreOrderCartInfoQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwStoreOrderCartInfoService extends BaseService<MwStoreOrderCartInfo>{

    /**
     * 添加购物车商品信息
     * @param oid 订单id
     * @param orderId 订单号
     * @param cartInfo 购物车信息
     */
    void saveCartInfo(Long oid, String orderId,List<MwStoreCartQueryVo> cartInfo);

    MwStoreOrderCartInfo findByUni(String unique);


    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreOrderCartInfoQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreOrderCartInfoDto>
    */
    List<MwStoreOrderCartInfo> queryAll(MwStoreOrderCartInfoQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreOrderCartInfoDto> all, HttpServletResponse response) throws IOException;
}
