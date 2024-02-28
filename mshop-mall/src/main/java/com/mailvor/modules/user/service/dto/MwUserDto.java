/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwUserDto implements Serializable {

    /** 用户id */
    private Long uid;

    /** 用户账户(跟accout一样) */
    private String username;

    /** 用户密码（跟pwd） */
    private String password;


    /** 真实姓名 */
    private String realName;

    /** 生日 */
    private Integer birthday;

    /** 身份证号码 */
    private String cardId;

    /** 用户备注 */
    private String mark;

    /** 用户分组id */
    private Integer groupId;

    /** 用户昵称 */
    private String nickname = "";

    /** 用户头像 */
    private String avatar;

    /** 手机号码 */
    private String phone;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    /** 添加ip */
    private String addIp;


    /** 用户余额 */
    private BigDecimal nowMoney;

    /** 佣金金额 */
    private BigDecimal brokeragePrice;

    /** 用户剩余积分 */
    private BigDecimal integral;

    /** 连续签到天数 */
    private Integer signNum;

    /** 1为正常，0为禁止 */
    private Integer status;

    /** 等级 */
    private Integer level;
    private Integer levelPdd;
    private Integer levelJd;
    private Integer levelDy;
    private Integer levelVip;

    /** 推广元id */
    private Long spreadUid;

    /** 推广员关联时间 */
    private Date spreadTime;

    /** 用户类型 */
    private String userType;

    /** 用户购买次数 */
    private Integer payCount;

    /** 下级人数 */
    private Integer spreadCount;

    /** 用户登陆类型，h5,wechat,routine */
    private String loginType;

    private String additionalNo;

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

    private String tbPid;
    private String jdPid;
    private String pddPid;

    /**
     * 邀请码
     * */
    private String code;

    private WechatUserDto wxProfile;
    private AliUserDto aliProfile;

}
