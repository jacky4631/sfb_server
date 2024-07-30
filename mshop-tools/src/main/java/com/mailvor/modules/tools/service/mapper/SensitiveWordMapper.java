/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service.mapper;


import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.tools.domain.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SensitiveWordMapper extends CoreMapper<SensitiveWord> {

}
