/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.pay.domain.MwPayChannel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface PayChannelMapper extends CoreMapper<MwPayChannel> {
    @Update("update mw_pay_channel set amount=amount-#{orderPrice}" +
            " where id=#{channelId}")
    int decPrice(@Param("orderPrice") BigDecimal orderPrice, @Param("channelId") Long channelId);


    @Update("update mw_pay_channel c set c.status=8,c.amount=c.max_amount where status in (2,8)")
    int reset();

    @Update("update mw_pay_channel c set c.amount=c.max_amount where status in (0,1)")
    int resetClosed();
}
