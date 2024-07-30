/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service.mapper;

import com.mailvor.common.mapper.RootMapper;
import com.mailvor.modules.energy.domain.UserEnergyOrder;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2023-02-04
*/
@Repository
public interface UserEnergyOrderMapper extends RootMapper<UserEnergyOrder> {
    @Update("update mw_user_energy_order set is_lock=0" +
            " where is_lock=1")
    void unlockOrder();
}
