/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class MwStoreCouponIssueUserDto implements Serializable {

    private Integer id;

    /** 领取优惠券用户ID */
    private Integer uid;

    /** 优惠券前台领取ID */
    private Integer issueCouponId;

    /** 领取时间 */
    private Integer addTime;
}
