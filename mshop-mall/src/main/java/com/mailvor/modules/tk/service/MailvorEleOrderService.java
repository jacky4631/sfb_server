/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.mailvor.domain.PageResult;
import com.mailvor.modules.tk.domain.MailvorEleOrder;
import com.mailvor.modules.tk.service.dto.MailvorEleOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorEleOrderQueryCriteria;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
* @author shenji
* @date 2022-09-07
*/
public interface MailvorEleOrderService extends TkOrderService<MailvorEleOrder> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MailvorEleOrderDto> queryAll(MailvorEleOrderQueryCriteria criteria, Pageable pageable);
    PageResult<MailvorEleOrderDto> queryAll(MailvorEleOrderQueryCriteria criteria, Pageable pageable, Integer unlockDay);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MailvorEleOrderDto>
    */
    List<MailvorEleOrder> queryAll(MailvorEleOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MailvorEleOrderDto> all, HttpServletResponse response) throws IOException;

    List<MailvorEleOrder> getUnbindOrderList(Integer prior);
    List<MailvorEleOrder> getRefundAndBindOrderList(Integer prior);
    void bindUser(Long uid, String orderId);
    void unbindUser(String id);


    Set<MailvorEleOrder> selectCashOrderIds(Long uid, LocalDateTime time);


    MailvorEleOrder mockOrder(String orderId, String productId, Long uid, Date createTime, String shopName,
                             Double rate, Double fee, Double payPrice, String img, String title, Integer innerType);

    void invalidRefundOrders(List<String> ids);

    List<MailvorEleOrder> getSelfUnspreadHbList(Integer day, Integer limit);


    Long getSpreadCountToday(Long uid);
    List<OrderCheckDTO> checkCount(Integer prior, Double scale);
}
