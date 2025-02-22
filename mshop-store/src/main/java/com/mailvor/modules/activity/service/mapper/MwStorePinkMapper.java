/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.activity.domain.MwStorePink;
import com.mailvor.modules.activity.service.dto.PinkDto;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface MwStorePinkMapper extends CoreMapper<MwStorePink> {
    @Select("SELECT p.id,p.uid,p.people,p.price,p.stop_time as stopTime,u.nickname,u.avatar" +
            " FROM mw_store_pink p INNER JOIN mw_user u ON u.uid=p.uid" +
            " WHERE stop_time > now() AND p.cid = #{cid} AND p.k_id = 0 " +
            "AND p.is_refund = 0 ORDER BY p.create_time DESC")
    List<PinkDto> getPinks(Long cid);

    //<![CDATA[ >= ]]>
    @Select("SELECT p.id,u.nickname,u.avatar" +
            " FROM mw_store_pink p RIGHT  JOIN mw_user u ON u.uid=p.uid" +
            " where p.status= 2 AND p.uid <> ${uid} " +
            "AND p.is_refund = 0")
    List<PinkDto> getPinkOkList(Long uid);

    @Select("SELECT p.id,p.uid,p.people,p.price,p.stop_time as stopTime,u.nickname,u.avatar" +
            " FROM mw_store_pink p LEFT JOIN mw_user u ON u.uid=p.uid" +
            " where p.k_id= ${kid} " +
            "AND p.is_refund = 0")
    List<PinkDto> getPinkMember(int kid);

    @Select("SELECT p.id,p.uid,p.people,p.price,p.stop_time as stopTime,u.nickname,u.avatar" +
            " FROM mw_store_pink p LEFT JOIN mw_user u ON u.uid=p.uid" +
            " where p.id= ${id} ")
    PinkDto getPinkUserOne(int id);

    @Select("select IFNULL(sum(total_num),0) from mw_store_pink " +
            "where status=2 and is_refund=0")
    int sumNum();
}
