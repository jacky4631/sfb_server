/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.energy.domain.UserEnergy;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2023-02-04
*/
@Repository
public interface UserEnergyMapper extends CoreMapper<UserEnergy> {
    @Update("update mw_user_energy set pay_count=pay_count+1" +
            " where uid=#{uid}")
    int incPayCount(@Param("uid") Long uid);
}
