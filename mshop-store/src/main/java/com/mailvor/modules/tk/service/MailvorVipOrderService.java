/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.mailvor.domain.PageResult;
import com.mailvor.modules.tk.domain.MailvorVipOrder;
import com.mailvor.modules.tk.service.dto.MailvorVipOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorVipOrderQueryCriteria;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
* @author wangjun
* @date 2022-09-07
*/
public interface MailvorVipOrderService  extends TkOrderService<MailvorVipOrder> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MailvorVipOrderDto> queryAll(MailvorVipOrderQueryCriteria criteria, Pageable pageable);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MailvorVipOrderDto>
    */
    List<MailvorVipOrder> queryAll(MailvorVipOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MailvorVipOrderDto> all, HttpServletResponse response) throws IOException;

    List<MailvorVipOrder> getUnbindOrderList(Long prior);
    List<MailvorVipOrder> getRefundAndBindOrderList(Integer prior);
    void bindUser(Long uid, String orderSn);
    void refundOrder(String id);

    Set<MailvorVipOrder> selectCashOrderIds(Long uid, LocalDateTime time);

    MailvorVipOrder mockOrder(String orderId, Long uid, Date createTime, String shopName,
                              Double rate, Double fee, Double payPrice, String img, String title,
                              String itemId, Integer innerType);

    void invalidRefundOrders(List<String> ids);

    List<MailvorVipOrder> getSelfUnspreadHbList(Integer limit);

    Long getSpreadCountToday(Long uid);
    List<OrderCheckDTO> checkCount(Integer prior, Double scale);

}
