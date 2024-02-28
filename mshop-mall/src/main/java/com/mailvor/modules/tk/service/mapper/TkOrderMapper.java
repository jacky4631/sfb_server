/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.tk.domain.TkOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @author shenji
* @date 2022-08-29
*/
@Repository
public interface TkOrderMapper extends CoreMapper<TkOrder> {

    /**
     * 计算某个用户所有订单表的订单数量
     * */
    @Select("SELECT SUM(orderCount) FROM (" +
            "SELECT COUNT(1) AS orderCount FROM mailvor_tb_order WHERE uid = ${uid} AND inner_type=${innerType} AND bind=1 " +
            " UNION ALL " +
            "SELECT COUNT(1) AS orderCount FROM mailvor_jd_order WHERE uid = ${uid} AND inner_type=${innerType} AND bind=1 " +
            "UNION ALL " +
            "SELECT COUNT(1) AS orderCount FROM mailvor_pdd_order WHERE uid = ${uid} AND inner_type=${innerType} AND bind=1 " +
            "UNION ALL " +
            "SELECT COUNT(1) AS orderCount FROM mailvor_dy_order WHERE uid = ${uid} AND inner_type=${innerType} AND bind=1 " +
            "UNION ALL " +
            "SELECT COUNT(1) AS orderCount FROM mailvor_vip_order WHERE uid = ${uid} AND inner_type=${innerType} AND bind=1" +
            ") AS tk_order")
    Long orderCount(@Param("uid") Long uid,@Param("innerType") Integer innerType);

}
