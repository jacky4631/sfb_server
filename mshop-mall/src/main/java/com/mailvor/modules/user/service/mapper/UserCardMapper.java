/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.user.domain.MwUserCard;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface UserCardMapper extends CoreMapper<MwUserCard> {

    @Update( "update mw_user_card set contract_path = null,signature_path=null where uid = #{id}")
    void clearSignature(@Param("id") Long id);

}
