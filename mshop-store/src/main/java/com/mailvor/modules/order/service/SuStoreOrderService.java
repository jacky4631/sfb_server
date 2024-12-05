/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.service.dto.UserRefundDto;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.user.config.HbUnlockConfig;
import com.mailvor.modules.user.domain.MwUser;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface SuStoreOrderService extends BaseService<MwStoreOrder>{

    /**
     * 返回订单确认数据
     * @param mwUser mwUser
     * @param goodsId id
     * @return ConfirmOrderVO
     */
    boolean confirmIntegral(MwUser mwUser, Long goodsId);

//    void gainUserIntegral(Long uid, String orderId, Date orderCreateTime, BigDecimal gainIntegral, PlatformEnum platformEnum);

    BigDecimal calIntegral(MwSystemUserLevel userLevel, double preFee);

    void bindOrder(Long uid, MailvorTbOrder order);
    void decHbAndRefundOrder(Long uid, MailvorTbOrder order);

    void bindOrder(Long uid, MailvorJdOrder order);
    void decHbAndRefundOrder(Long uid, MailvorJdOrder order);

    void bindOrder(Long uid, MailvorPddOrder order);
    void decHbAndRefundOrder(Long uid, MailvorPddOrder order);

    void bindOrder(Long uid, MailvorVipOrder order);
    void decHbAndRefundOrder(Long uid, MailvorVipOrder order);

    void bindOrder(Long uid, MailvorDyOrder order);
    void decHbAndRefundOrder(Long uid, MailvorDyOrder order);
    void decHbAndRefundOrder(Long uid, MailvorMtOrder order);
    Map<String, Double> incMoneyAndBindOrder(Long uid, TkOrder order, Date unlockTime);

    Date checkOrder(TkOrder tkOrder, Long uid);

    Date checkSelfOrder(TkOrder tkOrder, UserRefundDto user, HbUnlockConfig unlockConfig);

    void bindOrder(Long uid, MailvorMtOrder order);
}
