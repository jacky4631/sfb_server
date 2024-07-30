/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.*;
import com.mailvor.domain.BaseDomain;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 用户额外信息表
 *
* @author huangyu
* @date 2020-05-12
*/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@TableName(value = "mw_user_extra",autoResultMap = true)
public class MwUserExtra extends BaseDomain {
    /** 用户id */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /** 银盛个人商户号 */
    private String merchantNo;

    public void copy(MwUserExtra source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
