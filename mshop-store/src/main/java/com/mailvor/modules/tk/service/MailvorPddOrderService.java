/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.mailvor.domain.PageResult;
import com.mailvor.modules.tk.domain.MailvorPddOrder;
import com.mailvor.modules.tk.service.dto.MailvorPddOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorPddOrderQueryCriteria;
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
* @date 2022-09-06
*/
public interface MailvorPddOrderService  extends TkOrderService<MailvorPddOrder> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MailvorPddOrderDto> queryAll(MailvorPddOrderQueryCriteria criteria, Pageable pageable);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MailvorPddOrderDto>
    */
    List<MailvorPddOrder> queryAll(MailvorPddOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MailvorPddOrderDto> all, HttpServletResponse response) throws IOException;

    List<MailvorPddOrder> getUnbindOrderList(Integer prior);
    List<MailvorPddOrder> getRefundAndBindOrderList(Integer prior);
    void bindUser(Long uid, String orderSn);
    void refundOrder(String orderSn);



    Set<MailvorPddOrder> selectCashOrderIds(Long uid, LocalDateTime time);


    MailvorPddOrder mockOrder(String orderId, String goodsSign, Long uid, Date createTime, String shopName,
                              Double rate, Double fee, Double payPrice, String img, String title, Integer innerType);

    void invalidRefundOrders(List<String> ids);

    List<MailvorPddOrder> getSelfUnspreadHbList(Integer limit);


    Long getSpreadCountToday(Long uid);
    List<OrderCheckDTO> checkCount(Integer prior, Double scale);
}
