/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author mazhongjun
* @date 2020-06-29
*/
@Data
public class MwShippingTemplatesDto implements Serializable {

    /** 模板ID */
    private Integer id;

    /** 模板名称 */
    private String name;

    /** 计费方式 */
    private Integer type;

    /** 地域以及费用 */
    private String regionInfo;

    /** 指定包邮开关 */
    private Integer appoint;

    /** 指定包邮内容 */
    private String appointInfo;

    /** 添加时间 */
    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer isDel;

    /** 排序 */
    private Integer sort;
}
