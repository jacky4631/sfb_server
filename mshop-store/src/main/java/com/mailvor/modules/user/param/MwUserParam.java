/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.param;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MwUserParam {

    @NotNull
    /** 用户id */
    private Long uid;

    /** 用户昵称 */
    private String nickname;

    /** 真实姓名 */
    private String realName;

    /** 推广员id */
    private Long spreadUid;

    /** 手机号码 */
    private String phone;

    /**
     * 邀请码
     * */
    private String code;

    /**
     * 追单号
     * */
    private String additionalNo;

    /**淘宝渠道id， 非pid*/
    private String tbPid;

    /** 用户备注 */
    private String mark;


    /** 等级 */
    private Integer level;
    private Integer levelPdd;
    private Integer levelJd;
    private Integer levelDy;
    private Integer levelVip;

    /** 会员到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expired;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredJd;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredPdd;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredDy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiredVip;

    public void copy(MwUserParam source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
