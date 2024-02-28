/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.user.domain.MwUserPool;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface UserPoolMapper extends CoreMapper<MwUserPool> {

    @Select("SELECT refund FROM mw_user_pool WHERE uid = #{uid}")
    Integer getRefund(Long uid);

    @Update("update mw_user_pool set refund=refund+1 WHERE uid = #{uid}")
    Integer addRefund(Long uid);

    @Update("update mw_user_pool set refund=0 WHERE uid = #{uid}")
    Integer resetRefund(Long uid);
}
