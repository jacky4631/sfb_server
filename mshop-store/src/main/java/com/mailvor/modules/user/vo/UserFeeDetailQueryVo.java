/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UserMoneyDTO
 * @author huangyu
 * @Date 2023/2/4
 **/
@Data
@Builder
public class UserFeeDetailQueryVo implements Serializable {
    private Long uid;
    private Long orderTotal;
    private String feeTotal;
    private List<String> channels;

    private List<String> counts;
    private List<String> fees;


}
