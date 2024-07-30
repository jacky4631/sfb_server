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
import com.mailvor.modules.tk.domain.MailvorJdOrder;
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
* @date 2022-09-05
*/
@Repository
public interface MailvorJdOrderMapper extends CoreMapper<MailvorJdOrder> {

    @Override
    @Select("SELECT * FROM mailvor_jd_order ${ew.customSqlSegment}")
    <E extends IPage<MailvorJdOrder>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<MailvorJdOrder> queryWrapper);

    @Update("update mailvor_jd_order set uid = ${uid} where order_id = #{id}")
    void bindUser(@Param("uid")Long uid, @Param("id") Long id);
    @Update("update mailvor_jd_order set bind = 2 where order_id = #{id}")
    void refundOrder(@Param("id") Long id);


    @Select("select IFNULL(sum(integral),0) from mailvor_jd_order " +
            "where uid=#{uid} and order_time <= #{time} and bind=1 and cash=0")
    double totalCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select("select order_id from mailvor_jd_order where uid=#{uid} and order_time <= #{time} and cash=0")
    Set<MailvorJdOrder> selectCashOrderId(@Param("uid") Long uid, @Param("time") Date time);

    @Update("update mailvor_jd_order set cash = 1 where uid=#{uid} and order_time <= #{time} and cash=0")
    void updateCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select( "select IFNULL(sum(estimate_cos_price),0)  from mailvor_jd_order " +
            "where is_del=0")
    Double sumTotalPrice();

    @Select( "select IFNULL(sum(estimate_fee),0)  from mailvor_jd_order " +
            "where is_del=0")
    Double sumTotalFee();

    @Select("SELECT IFNULL(sum(estimate_cos_price),0) " +
            " FROM mailvor_jd_order ${ew.customSqlSegment}")
    Double sumPrice(@Param(Constants.WRAPPER) Wrapper<MailvorJdOrder> wrapper);

    @Select("SELECT IFNULL(sum(estimate_fee),0) " +
            " FROM mailvor_jd_order ${ew.customSqlSegment}")
    Double sumFee(@Param(Constants.WRAPPER) Wrapper<MailvorJdOrder> wrapper);

    @Select("SELECT IFNULL(sum(estimate_cos_price),0) as num," +
            "DATE_FORMAT(order_time, '%m-%d') as time " +
            " FROM mailvor_jd_order where is_del=0 and inner_type=0 and order_time >= #{time}" +
            " GROUP BY DATE_FORMAT(order_time,'%Y-%m-%d') " +
            " ORDER BY order_time ASC")
    List<ChartDataDto> chartList(@Param("time") Date time);
    @Select("SELECT count(id) as num," +
            "DATE_FORMAT(order_time, '%m-%d') as time " +
            " FROM mailvor_jd_order where is_del=0 and inner_type=0 and order_time >= #{time}" +
            " GROUP BY DATE_FORMAT(order_time,'%Y-%m-%d') " +
            " ORDER BY order_time ASC")
    List<ChartDataDto> chartListT(@Param("time") Date time);
    @Select("SELECT IFNULL(sum(estimate_fee),0) as num," +
            "DATE_FORMAT(order_time, '%m-%d') as time " +
            " FROM mailvor_jd_order where is_del=0 and inner_type=0 and order_time >= #{time}" +
            " GROUP BY DATE_FORMAT(order_time,'%Y-%m-%d') " +
            " ORDER BY order_time ASC")
    List<ChartDataDto> chartListFee(@Param("time") Date time);

//    @Update("update mailvor_jd_order set valid_code = 3" +
//            " where order_id in" +
//            " <foreach item='id' index='index' collection='ids' " +
//            " open='(' separator=',' close=')'>" +
//            " #{id}" +
//            " </foreach>")
//    void invalidRefundOrders(@Param("ids") List<Long> ids);
    @Update("update mailvor_jd_order set valid_code = 3,bind=3,hb=0.0" +
            " where order_id = #{id}")
    void invalidRefundOrders(@Param("id") Long id);

    @Select("SELECT IFNULL(sum(hb),0) " +
            " FROM mailvor_jd_order ${ew.customSqlSegment}")
    Double sumHb(@Param(Constants.WRAPPER) Wrapper<MailvorJdOrder> wrapper);

    @Select("SELECT uid,count(*) as orderCount " +
            " FROM mailvor_jd_order ${ew.customSqlSegment}")
    List<OrderCheckDTO> checkCount(@Param(Constants.WRAPPER) Wrapper<MailvorJdOrder> wrapper);
}
