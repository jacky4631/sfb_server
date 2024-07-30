/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_user_extract_config")
public class MwUserExtractConfig extends BaseDomain {

    @TableId
    private Long uid;

    /** 0=可以提现 1=禁止提现 */
    private Integer autoExtract;


}
