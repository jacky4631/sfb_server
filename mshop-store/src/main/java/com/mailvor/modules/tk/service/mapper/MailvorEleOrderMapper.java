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
import com.mailvor.modules.tk.domain.MailvorEleOrder;
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
* @date 2022-09-07
*/
@Repository
public interface MailvorEleOrderMapper extends CoreMapper<MailvorEleOrder> {

    @Override
    @Select("SELECT * FROM mw_order_ele ${ew.customSqlSegment}")
    <E extends IPage<MailvorEleOrder>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<MailvorEleOrder> queryWrapper);

    @Update("update mw_order_ele set uid = ${uid} where trade_id = #{id}")
    void bindUser(@Param("uid")Long uid, @Param("id") String orderId);
    @Update("update mw_order_ele set bind = 2 where trade_id = #{id}")
    void unbindUser(@Param("id") String orderId);


    @Select("select IFNULL(sum(integral),0) from mw_order_ele " +
            "where uid=#{uid} and pay_time <= #{time} and bind=1 and cash=0")
    double totalCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select("select trade_id from mw_order_ele where uid=#{uid} and pay_time <= #{time} and cash=0")
    Set<MailvorEleOrder> selectCashOrderId(@Param("uid") Long uid, @Param("time") Date time);


    @Update("update mw_order_ele set cash = 1 where uid=#{uid} and pay_time <= #{time} and cash=0")
    void updateCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select( "select IFNULL(sum(pay_price),0)  from mw_order_ele " +
            "where is_del=0")
    Double sumTotalPrice();

    @Select( "select IFNULL(sum(predict_money),0)  from mw_order_ele " +
            "where is_del=0")
    Double sumTotalFee();

    @Select("SELECT IFNULL(sum(pay_price),0) " +
            " FROM mw_order_ele ${ew.customSqlSegment}")
    Double sumPrice(@Param(Constants.WRAPPER) Wrapper<MailvorEleOrder> wrapper);

    @Select("SELECT IFNULL(sum(predict_money),0) " +
            " FROM mw_order_ele ${ew.customSqlSegment}")
    Double sumFee(@Param(Constants.WRAPPER) Wrapper<MailvorEleOrder> wrapper);

    @Select("SELECT IFNULL(sum(pay_price),0) as num," +
            "DATE_FORMAT(pay_time, '%m-%d') as time " +
            " FROM mw_order_ele where is_del=0 and inner_type=0 and pay_time >= #{time}" +
            " GROUP BY DATE_FORMAT(pay_time,'%Y-%m-%d') " +
            " ORDER BY pay_time ASC")
    List<ChartDataDto> chartList(@Param("time") Date time);
    @Select("SELECT count(trade_id) as num," +
            "DATE_FORMAT(pay_time, '%m-%d') as time " +
            " FROM mw_order_ele where is_del=0 and inner_type=0 and pay_time >= #{time}" +
            " GROUP BY DATE_FORMAT(pay_time,'%Y-%m-%d') " +
            " ORDER BY pay_time ASC")
    List<ChartDataDto> chartListT(@Param("time") Date time);
    @Select("SELECT IFNULL(sum(predict_money),0) as num," +
            "DATE_FORMAT(pay_time, '%m-%d') as time " +
            " FROM mw_order_ele where is_del=0 and inner_type=0 and pay_time >= #{time}" +
            " GROUP BY DATE_FORMAT(pay_time,'%Y-%m-%d') " +
            " ORDER BY pay_time ASC")
    List<ChartDataDto> chartListFee(@Param("time") Date time);

//    @Update("update mw_order_ele set flow_point = 'REFUND'" +
//            " where trade_id in" +
//            " <foreach item='id' index='index' collection='ids' " +
//            " open='(' separator=',' close=')'>" +
//            " #{id}" +
//            " </foreach>")
//    void invalidRefundOrders(@Param("ids") List<String> ids);
    @Update("update mw_order_ele set order_status = 3,bind=3,hb=0.0" +
            " where trade_id = #{id}")
    void invalidRefundOrders(@Param("id") String id);

    @Select("SELECT IFNULL(sum(hb),0) " +
            " FROM mw_order_ele ${ew.customSqlSegment}")
    Double sumHb(@Param(Constants.WRAPPER) Wrapper<MailvorEleOrder> wrapper);

    @Select("SELECT uid,count(*) as orderCount " +
            " FROM mw_order_ele ${ew.customSqlSegment}")
    List<OrderCheckDTO> checkCount(@Param(Constants.WRAPPER) Wrapper<MailvorEleOrder> wrapper);

}
