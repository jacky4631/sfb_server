/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mailvor.modules.order.service.dto.ChartDataDto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
* @author shenji
* @date 2022-08-29
*/
public interface TkOrderService<T> extends IService<T> {

    /**
     * 计算当前可以提现的金额
     * 积分乘以100
     *
     * @param uid the uid
     * @return the double
     */
    Double totalCash(Long uid, LocalDateTime time);

    /**
     * 更新订单提现状态 cash=1
     *
     * @param uid the uid
     */
    void updateCash(Long uid, LocalDateTime time);

    Double sumTotalPrice();

    Double sumTotalFee();

    List<ChartDataDto> chartList(Date time);

    List<ChartDataDto> chartListT(Date time);

    List<ChartDataDto> chartListFee(Date time);

    boolean hasUnlockOrder(Long uid, Integer innerType);

    Double totalFee(Integer type, List<Long> uid, Integer innerType);
    Double totalFee(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid);
    Long totalCount(Integer type, List<Long> uid, Integer innerType);
    Long totalCount(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid);
    Double totalPrice(Integer type, List<Long> uid, Integer innerType);
    Double totalPrice(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid);

    Double totalHb(Integer type);
}
