/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.mailvor.domain.PageResult;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.service.dto.MailvorMtOrderQueryCriteria;
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
* @date 2022-08-29
*/
public interface MailvorMtOrderService extends TkOrderService<MailvorMtOrder> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MailvorMtOrder> queryAll(MailvorMtOrderQueryCriteria criteria, Pageable pageable);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MailvorMtOrder>
    */
    List<MailvorMtOrder> queryAll(MailvorMtOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MailvorMtOrder> all, HttpServletResponse response) throws IOException;


    List<MailvorMtOrder> getUnbindOrderList(Integer prior);

    List<MailvorMtOrder> getRefundAndBindOrderList(Integer prior);

    Set<MailvorMtOrder> selectCashOrderIds(Long uid, LocalDateTime time);

    MailvorMtOrder mockOrder(Long orderId, Long uid, Date createTime, String shopName,
                             Double rate, Double fee,  Double payPrice,String img,String title,
                             String link, String itemId, Integer innerType);
    void bindUser(Long uid, Long id);
    void refundOrder(Long id);

    void invalidRefundOrders(List<Long> ids);

    List<MailvorMtOrder> getSelfUnspreadHbList(Integer day, Integer limit);

    Long getSpreadCountToday(Long uid);

    List<OrderCheckDTO> checkCount(Integer prior, Double scale);
}
