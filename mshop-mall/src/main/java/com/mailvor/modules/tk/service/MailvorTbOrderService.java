/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.mailvor.domain.PageResult;
import com.mailvor.modules.tk.domain.MailvorTbOrder;
import com.mailvor.modules.tk.service.dto.MailvorTbOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorTbOrderQueryCriteria;
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
public interface MailvorTbOrderService  extends TkOrderService<MailvorTbOrder> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MailvorTbOrderDto> queryAll(MailvorTbOrderQueryCriteria criteria, Pageable pageable);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MailvorTbOrderDto>
    */
    List<MailvorTbOrder> queryAll(MailvorTbOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MailvorTbOrderDto> all, HttpServletResponse response) throws IOException;


    List<MailvorTbOrder> getUnbindOrderList(Integer prior);

    List<MailvorTbOrder> getRefundAndBindOrderList(Integer prior);

    Set<MailvorTbOrder> selectCashOrderIds(Long uid, LocalDateTime time);

    MailvorTbOrder mockOrder(Long orderId, Long uid, Date createTime, String shopName,
                             Double rate, Double fee,  Double payPrice,String img,String title,
                             String link, String itemId, Integer innerType);
    void bindUser(Long uid, Long id);
    void refundOrder(Long id);

    void invalidRefundOrders(List<Long> ids);

    List<MailvorTbOrder> getSelfUnspreadHbList(Integer limit);

    Long getSpreadCountToday(Long uid);

    List<OrderCheckDTO> checkCount(Integer prior, Double scale);
}
