/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDomain;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

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

    /**
     * 体验卡级别 level=99代表开启过 不允许再次开启
     * */
    private Integer level;
    private Integer levelPdd;
    private Integer levelJd;
    private Integer levelDy;
    private Integer levelVip;

    /** 体验卡到期时间 */

    @TableField(updateStrategy = FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expired;
    @TableField(updateStrategy = FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredJd;
    @TableField(updateStrategy = FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredPdd;
    @TableField(updateStrategy = FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredDy;
    @TableField(updateStrategy = FieldStrategy.IGNORED,insertStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredVip;

    /**
     * 体验记录数量
     * */
    private Integer expLog;
    private Integer expLogJd;
    private Integer expLogPdd;
    private Integer expLogDy;
    private Integer expLogVip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expUpdate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expUpdateJd;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expUpdatePdd;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expUpdateDy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expUpdateVip;
    public void copy(MwUserExtra source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
