/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwSystemStoreStaffDto implements Serializable {

    private Integer id;

    /** 微信用户id */
    private Long uid;

    /** 店员头像 */
    private String avatar;

    /** 门店id */
    private Integer storeId;

    /** 店员名称 */
    private String staffName;

    /** 手机号码 */
    private String phone;

    /** 核销开关 */
    private Integer verifyStatus;

    /** 状态 */
    private Integer status;

    /** 添加时间 */
    private Date createTime;

    /** 微信昵称 */
    private String nickname;

    /** 所属门店 */
    private String storeName;
}
