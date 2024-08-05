/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.order.service.dto.ChartDataDto;
import com.mailvor.modules.tk.domain.MailvorPddOrder;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
* @author shenji
* @date 2022-09-06
*/
@Repository
public interface MailvorPddOrderMapper extends CoreMapper<MailvorPddOrder> {

    @Override
    @Select("SELECT * FROM mw_order_pdd ${ew.customSqlSegment}")
    <E extends IPage<MailvorPddOrder>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<MailvorPddOrder> queryWrapper);

    @Update("update mw_order_pdd set uid = ${uid} where order_sn = #{id}")
    void bindUser(@Param("uid")Long uid, @Param("id") String orderSn);
    @Update("update mw_order_pdd set bind = 2 where order_sn = #{id}")
    void refundOrder(@Param("id") String orderSn);


    @Select("select IFNULL(sum(integral),0) from mw_order_pdd " +
            "where uid=#{uid} and order_create_time <= #{time} and bind=1 and cash=0")
    double totalCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select("select order_sn from mw_order_pdd where uid=#{uid} and order_create_time <= #{time} and cash=0")
    Set<MailvorPddOrder> selectCashOrderId(@Param("uid") Long uid, @Param("time") Date time);


    @Update("update mw_order_pdd set cash = 1 where uid=#{uid} and order_create_time <= #{time} and cash=0")
    void updateCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select( "select IFNULL(sum(order_amount),0)  from mw_order_pdd " +
            "where is_del=0")
    Double sumTotalPrice();

    @Select( "select IFNULL(sum(promotion_amount),0)  from mw_order_pdd " +
            "where is_del=0")
    Double sumTotalFee();

    @Select("SELECT IFNULL(sum(order_amount),0) " +
            " FROM mw_order_pdd ${ew.customSqlSegment}")
    Double sumPrice(@Param(Constants.WRAPPER) Wrapper<MailvorPddOrder> wrapper);

    @Select("SELECT IFNULL(sum(promotion_amount),0) " +
            " FROM mw_order_pdd ${ew.customSqlSegment}")
    Double sumFee(@Param(Constants.WRAPPER) Wrapper<MailvorPddOrder> wrapper);


    @Select("SELECT IFNULL(sum(order_amount),0) as num," +
            "DATE_FORMAT(order_create_time, '%m-%d') as time " +
            " FROM mw_order_pdd where is_del=0 and inner_type=0 and order_create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(order_create_time,'%Y-%m-%d') " +
            " ORDER BY order_create_time ASC")
    List<ChartDataDto> chartList(@Param("time") Date time);
    @Select("SELECT count(order_sn) as num," +
            "DATE_FORMAT(order_create_time, '%m-%d') as time " +
            " FROM mw_order_pdd where is_del=0 and inner_type=0 and order_create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(order_create_time,'%Y-%m-%d') " +
            " ORDER BY order_create_time ASC")
    List<ChartDataDto> chartListT(@Param("time") Date time);
    @Select("SELECT IFNULL(sum(promotion_amount),0) as num," +
            "DATE_FORMAT(order_create_time, '%m-%d') as time " +
            " FROM mw_order_pdd where is_del=0 and inner_type=0 and order_create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(order_create_time,'%Y-%m-%d') " +
            " ORDER BY order_create_time ASC")
    List<ChartDataDto> chartListFee(@Param("time") Date time);

//    @Update("update mw_order_pdd set order_status = 4" +
//            " where order_sn in" +
//            " <foreach item='id' index='index' collection='ids' " +
//            " open='(' separator=',' close=')'>" +
//            " #{id}" +
//            " </foreach>")
//    void invalidRefundOrders(@Param("ids") String[] ids);
    @Update("update mw_order_pdd set order_status = 4,bind=3,hb=0.0" +
            " where order_sn = #{id}")
    void invalidRefundOrders(@Param("id") String id);

    @Select("SELECT IFNULL(sum(hb),0) " +
            " FROM mw_order_pdd ${ew.customSqlSegment}")
    Double sumHb(@Param(Constants.WRAPPER) Wrapper<MailvorPddOrder> wrapper);

    @Select("SELECT uid,count(*) as orderCount " +
            " FROM mw_order_pdd ${ew.customSqlSegment}")
    List<OrderCheckDTO> checkCount(@Param(Constants.WRAPPER) Wrapper<MailvorPddOrder> wrapper);
}
