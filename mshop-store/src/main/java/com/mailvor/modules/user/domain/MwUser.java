/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDomain;
import com.mailvor.modules.user.service.dto.AliUserDto;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

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
@TableName(value = "mw_user",autoResultMap = true)
public class MwUser extends BaseDomain {

    /** 用户id */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /** 用户账户(跟accout一样) */
    private String username;

    /** 用户密码（跟pwd） */
    private String password;

    /** 真实姓名 */
    private String realName;


    /** 生日 */
    private Integer birthday;

    /** 用户备注 */
    private String mark;

    /** 用户昵称 */
    private String nickname;

    /** 用户头像 */
    private String avatar;

    /** 手机号码 */
    private String phone;

    /** 添加ip */
    private String addIp;

    /** 最后一次登录ip */
    private String lastIp;


    /** 用户余额 */
    private BigDecimal nowMoney;

    /** 累积的佣金 加盟后转到用户余额 */
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


    /** 推广员id */
    private Long spreadUid;


    /** 推广员关联时间 */
    private Date spreadTime;

    /** 被用于开通店铺 1已使用 0未使用 */
    private Integer spreadUpdate;

    /** 用户类型 */
    private String userType;

    /** 下级人数 */
    private Long spreadCount;

    @TableField(typeHandler = FastjsonTypeHandler.class,updateStrategy = FieldStrategy.IGNORED)
    private AliUserDto aliProfile;

    /**
     * 追单号
     * */
    private String additionalNo;

    /**
     * 邀请码
     * */
    private String code;

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

    /** 0-默认 1-第一次修改 2-第二次修改 3-第三次修改 */
    private Integer changeWord;

    /**公众号用户openId*/
    private String wechatOpenId;

    public void copy(MwUser source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
