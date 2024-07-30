package com.mailvor.modules.user.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-16
 */
@Data
@ApiModel(value = "MwUserQueryVo对象", description = "用户表查询参数")
public class MwUserQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "用户账户(跟accout一样)")
    private String username;

    @ApiModelProperty(value = "用户账号")
    private String account;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "用户积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "等级")
    private Integer level;
    private Integer levelPdd;
    private Integer levelJd;
    private Integer levelDy;
    private Integer levelVip;

    @ApiModelProperty(value = "推广员id")
    private Long spreadUid;

    @ApiModelProperty(value = "下级人数")
    private Long spreadCount;

    @ApiModelProperty(value = "可提现金额")
    private Double totalCash;
    @ApiModelProperty(value = "累计提现金额")
    private Double totalExtract;


    @ApiModelProperty(value = "未解锁金额")
    private Double unlockMoney;
    @ApiModelProperty(value = "微信信息")
    private String wxProfile;
    @ApiModelProperty(value = "支付宝信息")
    private String aliProfile;

    @ApiModelProperty(value = "邀请码")
    private String code;
    @ApiModelProperty(value = "会员过期时间")
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

    /**1可修改 0不可修改*/
    private Integer canChangeCode;

    private String storeBrokerageRatio;
    private String storeBrokerageTwo;
}
