/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.tk.domain.TkOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @author shenji
* @date 2022-08-29
*/
@Repository
public interface TkOrderMapper extends CoreMapper<TkOrder> {
}
