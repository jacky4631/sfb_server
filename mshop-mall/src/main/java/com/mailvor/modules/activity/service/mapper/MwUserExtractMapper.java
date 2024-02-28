/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.activity.domain.MwUserExtract;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2020-05-13
*/
@Repository
public interface MwUserExtractMapper extends CoreMapper<MwUserExtract> {
    @Select("select IFNULL(sum(extract_price),0) from mw_user_extract " +
            "where status=1 " +
            "and uid=#{uid}")
    double sumPrice(@Param("uid") Long uid);

    @Select("SELECT IFNULL(sum(extract_price),0) " +
            " FROM mw_user_extract ${ew.customSqlSegment}")
    Double sumExtractPrice(@Param(Constants.WRAPPER) Wrapper<MwUserExtract> wrapper);
    @Update( "update mw_user_extract set alipay_code = #{userId} where uid = #{uid} AND extract_type = 'alipay' AND status = 0")
    void updateUnpaidAliCode(@Param("uid") Long uid, @Param("userId") String userId);
}
