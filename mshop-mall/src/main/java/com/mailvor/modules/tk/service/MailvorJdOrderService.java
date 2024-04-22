/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.mailvor.domain.PageResult;
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import com.mailvor.modules.tk.service.dto.MailvorJdOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorJdOrderQueryCriteria;
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
* @date 2022-09-05
*/
public interface MailvorJdOrderService  extends TkOrderService<MailvorJdOrder> {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MailvorJdOrderDto> queryAll(MailvorJdOrderQueryCriteria criteria, Pageable pageable);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MailvorJdOrderDto>
    */
    List<MailvorJdOrder> queryAll(MailvorJdOrderQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MailvorJdOrderDto> all, HttpServletResponse response) throws IOException;

    List<MailvorJdOrder> getUnbindOrderList(Integer prior);
    List<MailvorJdOrder> getRefundAndBindOrderList(Integer prior);

    void bindUser(Long uid, Long id);
    void refundOrder(Long id);


    Set<MailvorJdOrder> selectCashOrderIds(Long uid, LocalDateTime time);


    MailvorJdOrder mockOrder(Long orderId,  Long skuId, Long uid, Date createTime, String shopName,
                             Double rate,  Double fee, Double payPrice, String img, String title, Integer innerType);

    void invalidRefundOrders(List<Long> ids);

    List<MailvorJdOrder> getSelfUnspreadHbList(Integer limit);


    Long getSpreadCountToday(Long uid);
    List<OrderCheckDTO> checkCount(Integer prior, Double scale);

}
