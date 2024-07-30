/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2023-02-04
*/
@Repository
public interface UserEnergyOrderLogMapper extends CoreMapper<UserEnergyOrderLog> {

    @Select("select count(*) from mw_user_energy_order_log e where e.uid = #{uid} and e.platform = #{platform} and e.type = #{type}")
    Long countLog(@Param("uid") Long uid, @Param("platform") String platform, @Param("type") Integer type);
}
