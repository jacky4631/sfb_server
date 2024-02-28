/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.mailvor.domain.PageResult;
import com.mailvor.modules.tk.domain.MailvorDyOrder;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderQueryCriteria;
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
public interface MailvorDyOrderService  extends TkOrderService<MailvorDyOrder> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MailvorDyOrderDto> queryAll(MailvorDyOrderQueryCriteria criteria, Pageable pageable);
    PageResult<MailvorDyOrderDto> queryAll(MailvorDyOrderQueryCriteria criteria, Pageable pageable, Integer unlockDay);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MailvorDyOrderDto>
    */
    List<MailvorDyOrder> queryAll(MailvorDyOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MailvorDyOrderDto> all, HttpServletResponse response) throws IOException;

    List<MailvorDyOrder> getUnbindOrderList(Integer prior);
    List<MailvorDyOrder> getRefundAndBindOrderList(Integer prior);
    void bindUser(Long uid, String orderId);
    void unbindUser(String id);


    Set<MailvorDyOrder> selectCashOrderIds(Long uid, LocalDateTime time);


    MailvorDyOrder mockOrder(String orderId, String productId, Long uid, Date createTime, String shopName,
                             Double rate, Double fee, Double payPrice, String img, String title, Integer innerType);

    void invalidRefundOrders(List<String> ids);

    List<MailvorDyOrder> getSelfUnspreadHbList(Integer day, Integer limit);


    Long getSpreadCountToday(Long uid);
    List<OrderCheckDTO> checkCount(Integer prior, Double scale);
}
