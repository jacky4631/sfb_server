/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class PayChannelQueryCriteria {

    // 精确
    @Query(blurry = "channelName")
    private String channelName;

    @Query
    private Integer status;
    @Query
    private Integer type;

    @Query
    private Long companyId;
}
