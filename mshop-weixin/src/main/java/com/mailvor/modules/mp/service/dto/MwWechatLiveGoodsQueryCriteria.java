/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

/**
* @author huangyu
* @date 2020-08-11
*/
@Data
public class MwWechatLiveGoodsQueryCriteria {

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String name;
}
