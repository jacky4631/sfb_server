/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.user.domain.MwUserRecharge;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
* @author mazhongjun
* @date 2020-05-12
*/
@Repository
public interface UserRechargeMapper extends CoreMapper<MwUserRecharge> {
    @Select("SELECT IFNULL(sum(price),0) " +
            " FROM mw_user_recharge ${ew.customSqlSegment}")
    Double sumPrice(@Param(Constants.WRAPPER) Wrapper<MwUserRecharge> wrapper);

    @Update( "update mw_user_recharge set paid = #{paid},pay_time = #{payTime} where order_id = #{orderId}")
    void updatePaid(@Param("orderId") String orderId, @Param("paid") Integer paid, @Param("payTime") Date payTime);
}
