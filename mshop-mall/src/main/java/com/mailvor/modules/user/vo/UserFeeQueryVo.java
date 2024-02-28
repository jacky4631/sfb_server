/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserMoneyDTO
 * @author huangyu
 * @Date 2023/12/4
 **/
@Data
@Builder
public class UserFeeQueryVo implements Serializable {
    private Long uid;
    private Double minToday;

    private Double maxToday;

    private Double minMonth;

    private Double maxMonth;
}
