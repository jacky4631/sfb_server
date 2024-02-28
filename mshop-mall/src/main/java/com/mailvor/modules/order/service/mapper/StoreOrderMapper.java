/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.service.dto.ChartDataDto;
import com.mailvor.modules.order.vo.OrderDataVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface StoreOrderMapper extends CoreMapper<MwStoreOrder> {



    @Select("SELECT sum(pay_integral) as price,count(id) as count," +
            "DATE_FORMAT(create_time, '%m-%d') as time FROM mw_store_order" +
            " WHERE is_del = 0 " +
            "GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d') ORDER BY create_time DESC")
    List<OrderDataVo> getOrderDataPriceList(Page page);


    @Select("SELECT IFNULL(sum(pay_integral),0) " +
            " FROM mw_store_order ${ew.customSqlSegment}")
    Double todayPrice(@Param(Constants.WRAPPER) Wrapper<MwStoreOrder> wrapper);



    @Select("select IFNULL(sum(pay_integral),0) from mw_store_order " +
            "where is_del=0 and uid=#{uid}")
    double sumPrice(@Param("uid") Long uid);


    @Select("SELECT COUNT(*) FROM mw_store_order WHERE pay_time >= ${today}")
    Integer countByPayTimeGreaterThanEqual(@Param("today")int today);

    @Select("SELECT COUNT(*) FROM mw_store_order WHERE pay_time < ${today}  and pay_time >= ${yesterday}")
    Integer countByPayTimeLessThanAndPayTimeGreaterThanEqual(@Param("today")int today, @Param("yesterday")int yesterday);

    @Select( "select IFNULL(sum(pay_integral),0)  from mw_store_order " +
            "where is_del=0")
    Double sumTotalPrice();

    @Select("SELECT IFNULL(sum(pay_integral),0) as num," +
            "DATE_FORMAT(create_time, '%m-%d') as time " +
            " FROM mw_store_order where is_del=0 and create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d') " +
            " ORDER BY create_time ASC")
    List<ChartDataDto> chartList(@Param("time") Date time);
    @Select("SELECT count(id) as num," +
            "DATE_FORMAT(create_time, '%m-%d') as time " +
            " FROM mw_store_order where is_del=0 and create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d') " +
            " ORDER BY create_time ASC")
    List<ChartDataDto> chartListT(@Param("time") Date time);
}
