/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.order.service.dto.UserRefundDto;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.dto.PromUserDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface UserMapper extends CoreMapper<MwUser> {


    @Select("<script>SELECT u.uid,u.nickname,u.avatar,u.phone,u.integral, DATE_FORMAT(u.create_time,'%Y/%m/%d') as time," +
            "u.spread_count as childCount,COUNT(o.id) as orderCount," +
            "IFNULL(SUM(o.pay_integral),0) as numberCount FROM mw_user u " +
            "LEFT JOIN mw_store_order o ON u.uid=o.uid " +
            "WHERE u.uid in <foreach item='id' index='index' collection='uids' " +
            " open='(' separator=',' close=')'>" +
            "   #{id}" +
            " </foreach> <if test='keyword != null'>" +
            " AND ( u.nickname LIKE CONCAT(CONCAT('%',#{keyword}),'%') OR u.phone LIKE CONCAT(CONCAT('%',#{keyword}),'%'))</if>" +
            " GROUP BY u.uid ORDER BY #{orderByStr} " +
            "</script>")
    List<PromUserDto> getUserSpreadCountList(Page page,
                                             @Param("uids") List uids,
                                             @Param("keyword") String keyword,
                                             @Param("orderByStr") String orderBy);

    @Update("update mw_user set now_money=now_money-#{payPrice}" +
            " where uid=#{uid}")
    int decPrice(@Param("payPrice") BigDecimal payPrice, @Param("uid") Long uid);
//
//    @Update("update mw_user set brokerage_price=brokerage_price+#{brokeragePrice}" +
//            " where uid=#{uid}")
//    int incBrokeragePrice(@Param("brokeragePrice") double brokeragePrice,@Param("uid") int uid);

    @Update("update mw_user set pay_count=pay_count+1" +
            " where uid=#{uid}")
    int incPayCount(@Param("uid") Long uid);

    @Update("update mw_user set now_money=now_money+#{price}" +
            " where uid=#{uid}")
    int incMoney(@Param("uid") Long uid,BigDecimal price);

    @Update("update mw_user set integral=integral-#{integral}" +
            " where uid=#{uid}")
    int decIntegral(@Param("integral") double integral,@Param("uid") Long uid);

    @Update("update mw_user set integral=integral+#{integral}" +
            " where uid=#{uid}")
    int incIntegral(@Param("integral") double integral,@Param("uid") Long uid);

    @Update( "update mw_user set status = #{status} where uid = #{id}")
    void updateOnstatus(@Param("status") Integer status, @Param("id") Long id);

    @Update( "update mw_user set now_money = now_money + ${money} where uid = #{id}")
    void updateMoney(@Param("money") double money, @Param("id") long id);

    @Update("update mw_user set brokerage_price = brokerage_price+ ${price} where uid = #{id}")
    void incBrokeragePrice(@Param("price") BigDecimal price,@Param("id") Long id);

    @Update("update mw_user set additional_no = ${no} where uid = #{id}")
    void updateAdditionalNo(@Param("no") String no,@Param("id") Long id);
    @Select("<script>SELECT u.uid,u.nickname,u.avatar,u.phone,u.level,u.level_jd,u.level_pdd,u.level_dy,u.level_vip,DATE_FORMAT(u.create_time,'%Y.%m.%d') as time," +
            "DATE_FORMAT(u.update_time,'%Y.%m.%d') as updateTime," +
            "u.spread_count as childCount" +
            " FROM mw_user u " +
//            "LEFT JOIN mw_user_fee_log o ON u.uid=o.uid " +
            "WHERE u.uid in <foreach item='id' index='index' collection='uids' " +
            " open='(' separator=',' close=')'>" +
            "   #{id}" +
            " </foreach>" +
            " GROUP BY u.uid ORDER BY #{orderByStr} " +
            "</script>")
    @Results(value = {
            @Result(property="uid", column = "uid"),
            @Result(property="nickname", column = "nickname"),
            @Result(property="fee", column="uid", javaType= Double.class, one=@One(select="thirtyFee"))
    })
    List<PromUserDto> getAppUserSpreadCountList(Page page,
                                             @Param("uids") List uids,
                                             @Param("orderByStr") String orderBy);

    @Select("SELECT GROUP_CONCAT(fee SEPARATOR '#')" +
            " FROM mw_user_fee_log WHERE uid = #{uid} AND type=3 AND cid=1")
    String thirtyFee(String uid);
    @Select("SELECT uid, DATE_FORMAT(update_time,'%Y/%m/%d') as updateTime" +
            " FROM mw_user ${ew.customSqlSegment}")
    List<PromUserDto> selectIds(@Param(Constants.WRAPPER) Wrapper<MwUser> wrapper);

    @Select("SELECT u.uid,u.`level`,u.level_jd,u.level_pdd,u.level_dy,u.level_vip,p.refund FROM `mw_user` u " +
            "LEFT JOIN `mw_user_pool` p ON u.uid=p.uid WHERE u.uid = #{uid}")
    UserRefundDto getUserRefund(@Param("uid") Long uid);

    @Select("<script>SELECT u.uid,u.`level`,u.level_jd,u.level_pdd,u.level_dy,u.level_vip,p.refund FROM `mw_user` u " +
            "LEFT JOIN `mw_user_pool` p ON u.uid=p.uid " +
            "WHERE u.uid in <foreach item='id' index='index' collection='uids' " +
            " open='(' separator=',' close=')'>" +
            "   #{id}" +
            " </foreach> " +
            "</script>")
    List<UserRefundDto> getUserRefunds(@Param("uids") List uids);
}
