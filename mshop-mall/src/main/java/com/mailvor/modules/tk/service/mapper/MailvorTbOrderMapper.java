/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.order.service.dto.ChartDataDto;
import com.mailvor.modules.tk.domain.MailvorTbOrder;
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
* @date 2022-08-29
*/
@Repository
public interface MailvorTbOrderMapper extends CoreMapper<MailvorTbOrder> {

    @Override
    @Select("SELECT * FROM mailvor_tb_order ${ew.customSqlSegment}")
    <E extends IPage<MailvorTbOrder>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<MailvorTbOrder> queryWrapper);

    @Update("update mailvor_tb_order set uid = ${uid} where trade_parent_id = #{id}")
    void bindUser(@Param("uid")Long uid, @Param("id") Long id);

    @Update("update mailvor_tb_order set bind = 2 where trade_parent_id = #{id}")
    void unbindUser(@Param("id") Long id);

    @Select("select IFNULL(sum(integral),0) from mailvor_tb_order " +
            "where uid=#{uid} and tk_create_time <= #{time} and bind=1 and cash=0")
    double totalCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select("select trade_parent_id from mailvor_tb_order where uid=#{uid} and tk_create_time <= #{time} and cash=0")
    Set<MailvorTbOrder> selectCashOrderId(@Param("uid") Long uid, @Param("time") Date time);

    @Update("update mailvor_tb_order set cash = 1 where uid=#{uid} and tk_create_time <= #{time} and cash=0")
    void updateCash(@Param("uid") Long uid, @Param("time") Date time);

    @Select( "select IFNULL(sum(alipay_total_price),0)  from mailvor_tb_order " +
            "where is_del=0")
    Double sumTotalPrice();

    @Select( "select IFNULL(sum(pub_share_pre_fee),0)  from mailvor_tb_order " +
            "where is_del=0")
    Double sumTotalFee();

    @Select("SELECT IFNULL(sum(alipay_total_price),0) " +
            " FROM mailvor_tb_order ${ew.customSqlSegment}")
    Double sumPrice(@Param(Constants.WRAPPER) Wrapper<MailvorTbOrder> wrapper);

    @Select("SELECT IFNULL(sum(pub_share_pre_fee),0) " +
            " FROM mailvor_tb_order ${ew.customSqlSegment}")
    Double sumFee(@Param(Constants.WRAPPER) Wrapper<MailvorTbOrder> wrapper);

    @Select("SELECT IFNULL(sum(alipay_total_price),0) as num," +
            "DATE_FORMAT(tk_create_time, '%m-%d') as time " +
            " FROM mailvor_tb_order where is_del=0 and inner_type=0 and tk_create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(tk_create_time,'%Y-%m-%d') " +
            " ORDER BY tk_create_time ASC")
    List<ChartDataDto> chartList(@Param("time") Date time);
    @Select("SELECT count(trade_parent_id) as num," +
            "DATE_FORMAT(tk_create_time, '%m-%d') as time " +
            " FROM mailvor_tb_order where is_del=0 and inner_type=0 and tk_create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(tk_create_time,'%Y-%m-%d') " +
            " ORDER BY tk_create_time ASC")
    List<ChartDataDto> chartListT(@Param("time") Date time);
    @Select("SELECT IFNULL(sum(pub_share_pre_fee),0) as num," +
            "DATE_FORMAT(tk_create_time, '%m-%d') as time " +
            " FROM mailvor_tb_order where is_del=0 and inner_type=0 and tk_create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(tk_create_time,'%Y-%m-%d') " +
            " ORDER BY tk_create_time ASC")
    List<ChartDataDto> chartListFee(@Param("time") Date time);

//    @Update("update mailvor_tb_order set tk_status = 13" +
//            " where trade_parent_id in" +
//            " <foreach collection='ids' index='index' item='id' separator=',' open='(' close=')'>" +
//            " #{id}" +
//            " </foreach>")
//    void invalidRefundOrders(@Param("ids") List<Long> ids);
    @Update("update mailvor_tb_order set tk_status = 13,bind=3,hb=0.0" +
        " where trade_parent_id = ${id}")
    void invalidRefundOrders(@Param("id") Long id);


    @Select("SELECT IFNULL(sum(hb),0) " +
            " FROM mailvor_tb_order ${ew.customSqlSegment}")
    Double sumHb(@Param(Constants.WRAPPER) Wrapper<MailvorTbOrder> wrapper);


    @Select("SELECT uid,count(*) as orderCount" +
            " FROM mailvor_tb_order ${ew.customSqlSegment}")
    List<OrderCheckDTO> checkCount(@Param(Constants.WRAPPER) Wrapper<MailvorTbOrder> wrapper);

}
