/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import com.mailvor.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
* @author shenji
* @date 2022-12-17
*/
@Data
public class BaseOrderDto extends BaseDomain implements Serializable {
    /** 是否拆过红包，0=表示未拆过红包 1=已拆过红包绑定 2=已退款 */
    private Integer bind;

    /** 用户ID */
    private Long uid;

    /** 积分 */
    private Long integral;

    //是否提现
    private Integer cash;

    /** 红包解锁剩余时间 ok-已解锁 err-红包失效 其他-1天2小时，5小时*/
    private String remain;

    //拆红包奖励金额
    private Double hb;
    /** 基础奖励金额 */
    private Double baseHb;

    /** 店主奖励金额 */
    private Double shopHb;

    private Integer innerType;
}
