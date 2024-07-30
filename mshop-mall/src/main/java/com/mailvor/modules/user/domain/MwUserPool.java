/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
* @author huangyu
* @date 2020-05-12
*/

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@TableName(value = "mw_user_pool",autoResultMap = true)
public class MwUserPool extends BaseDomain {

    /** 用户id */
    @TableId(value = "uid", type = IdType.NONE)
    private Long uid;
    /** 退款次数 */
    private Integer refund;
    /** 退款类型设置 0=默认 订单退款 1=系统检测到异常订单 */
    private Integer type;

    public void copy(MwUserPool source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
