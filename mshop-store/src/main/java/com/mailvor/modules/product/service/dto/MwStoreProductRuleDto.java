/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.dto;

import com.mailvor.common.json.JsonArray;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author huangyu
* @date 2020-06-28
*/
@Data
public class MwStoreProductRuleDto implements Serializable {

    private Integer id;

    /** 规格名称 */
    private String ruleName;

    /** 规格值 */
    private JsonArray ruleValue;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer isDel;
}
