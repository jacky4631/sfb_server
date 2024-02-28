/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.customer.service.dto;

import lombok.Data;
import java.util.Date;
import java.io.Serializable;

/**
* @author Bug
* @date 2020-12-10
*/
@Data
public class MwStoreCustomerDto implements Serializable {

    /** id */
    private Long id;

    /** 用户昵称 */
    private String nickName;

    /** openId */
    private String openId;

    /** 备注 */
    private String remark;

    /** 添加时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

    private Integer isDel;

    /** 是否启用 */
    private Integer isEnable;
}
