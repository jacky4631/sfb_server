/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.user.domain.MwUserLevel;
import com.mailvor.modules.user.service.dto.UserLevelInfoDto;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户等级记录表 Mapper 接口
 * </p>
 *
 * @author mazhongjun
 * @since 2019-12-06
 */
@Repository
public interface MwUserLevelMapper extends CoreMapper<MwUserLevel> {

    @Select("SELECT l.id,a.add_time as addTime,l.discount,a.level_id as levelId,l.name," +
            "l.icon,l.grade FROM mw_user_level a INNER JOIN mw_system_user_level l " +
            "ON l.id=a.level_id WHERE a.status = 1 AND a.is_del = 0 AND a.id = #{id} LIMIT 1")
    UserLevelInfoDto getUserLevelInfo(int id);



}
