/**
 * Copyright (C) 2018-2022
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
@TableName(value = "mw_user_hb_scale",autoResultMap = true)
public class MwUserHbScale extends BaseDomain {

    /** 用户id */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /** 月卡翻倍比例 */
    private BigDecimal monthScale;
    /** 月卡翻倍失效天数，相比create_time */
    private Integer monthInvalidDay;

    public void copy(MwUserHbScale source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
