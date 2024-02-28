/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.service.dto.BillOrderRecordDto;
import com.mailvor.modules.user.service.dto.MUserBillDto;
import com.mailvor.modules.user.service.dto.MwUserBillDto;
import com.mailvor.modules.user.vo.BillVo;
import com.mailvor.modules.user.vo.SignVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author mazhongjun
* @date 2020-05-12
*/
@Repository
public interface UserBillMapper extends CoreMapper<MwUserBill> {

    @Select("select IFNULL(sum(number),0) from mw_user_bill " +
            "where status=1 and type='sign' and pm=1 and category='integral' " +
            "and uid=#{uid}")
    double sumIntegral(@Param("uid") Long uid);

    @Select("SELECT DATE_FORMAT(a.create_time,'%Y-%m-%d') as addTime,a.title,a.number " +
            "FROM mw_user_bill a INNER JOIN mw_user u ON u.uid=a.uid WHERE a.category = 'integral'" +
            " AND a.type = 'sign' AND a.status = 1 AND a.uid = #{uid} " +
            "ORDER BY a.create_time DESC")
    List<SignVo> getSignList(@Param("uid") Long uid, Page page);

    @Select("SELECT o.order_id as orderId,DATE_FORMAT(b.create_time, '%Y-%m-%d %H:%i') as time," +
            "b.number,u.avatar,u.nickname FROM mw_user_bill b " +
            "INNER JOIN mw_store_order o ON o.id=b.link_id " +
            "RIGHT JOIN mw_user u ON u.uid=o.uid" +
            " WHERE b.uid = #{uid} AND ( DATE_FORMAT(b.create_time, '%Y-%m')= #{time} ) AND " +
            "b.category = 'now_money' AND b.type = 'brokerage' ORDER BY time DESC")
    List<BillOrderRecordDto> getBillOrderRList(@Param("time") String time, @Param("uid") Long uid);

    @Select("SELECT DATE_FORMAT(create_time,'%Y-%m') as time " +
            " FROM mw_user_bill ${ew.customSqlSegment}")
    List<String> getBillOrderList(@Param(Constants.WRAPPER) Wrapper<MwUserBill> userWrapper, Page page);

    @Select("SELECT DATE_FORMAT(create_time,'%Y-%m') as time,group_concat(id SEPARATOR ',') ids " +
            " FROM mw_user_bill ${ew.customSqlSegment}")
    List<BillVo> getBillList(@Param(Constants.WRAPPER) Wrapper<MwUserBill> userWrapper, Page page);

    @Select("SELECT DATE_FORMAT(create_time,'%Y-%m-%d %H:%i') as add_time,title,number,pm " +
            " FROM mw_user_bill ${ew.customSqlSegment}")
    List<MUserBillDto> getUserBillList(@Param(Constants.WRAPPER) Wrapper<MwUserBill> userWrapper);

    @Select("select IFNULL(sum(number),0) from mw_user_bill " +
            "where status=1 and type='brokerage' and pm=1 and category='now_money' " +
            "and uid=#{uid}")
    double sumPrice(@Param("uid") int uid);

    @Select("select IFNULL(sum(number),0) from mw_user_bill " +
            "where status=1 and type='recharge' and pm=1 and category='now_money' " +
            "and uid=#{uid}")
    double sumRechargePrice(@Param("uid") Long uid);


    @Select("select IFNULL(sum(number),0) from mw_user_bill " +
            "where status=1 and type='brokerage' and pm=1 and category='now_money' " +
            "and uid=#{uid} and TO_DAYS(NOW()) - TO_DAYS(create_time) <= 1")
    double sumYesterdayPrice(@Param("uid") Long uid);

    @Select("<script> select b.title,b.pm,b.mark,b.uid,b.category,b.type,b.number,b.create_time ,u.nickname " +
            "from mw_user_bill b left join mw_user u on u.uid=b.uid  where 1=1  "  +
            "<if test =\"category !=''\">and b.category=#{category}</if> " +
            "<if test =\"type !=''\">and b.type=#{type}</if> " +
            "<if test =\"title !=''\">and b.title=#{title}</if> " +
            "<if test =\"pm !=null\">and b.pm=#{pm}</if> " +
            "<if test =\"date !=null\">and b.create_time &gt;= STR_TO_DATE(#{date},'%Y-%m-%d %H:%i:%s')</if> " +
            "<if test =\"date1 !=null\">and b.create_time &lt;=STR_TO_DATE(#{date1},'%Y-%m-%d %H:%i:%s')</if> " +
            "<if test =\"search !=''\">and (b.uid LIKE CONCAT('%',#{search},'%') or b.mark LIKE CONCAT('%',#{search},'%') or u.nickname LIKE CONCAT('%',#{search},'%'))</if>" +
            "order by b.create_time desc </script> ")
    List<MwUserBillDto> findAllByQueryCriteria(@Param("category") String category, @Param("type") String type, @Param("search") String search, @Param("pm") Integer pm, @Param("date")String date, @Param("date1")String date1, @Param("title")String title);
}
