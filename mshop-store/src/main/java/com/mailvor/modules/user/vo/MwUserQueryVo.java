package com.mailvor.modules.user.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "用户表查询参数")
public class MwUserQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "用户账户(跟accout一样)")
    private String username;

    @Schema(description = "用户账号")
    private String account;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "用户余额")
    private BigDecimal nowMoney;

    @Schema(description = "用户积分")
    private BigDecimal integral;

    @Schema(description = "等级")
    private Integer level;
    private Integer levelPdd;
    private Integer levelJd;
    private Integer levelDy;
    private Integer levelVip;

    @Schema(description = "推广员id")
    private Long spreadUid;

    @Schema(description = "下级人数")
    private Long spreadCount;

    @Schema(description = "可提现金额")
    private Double totalCash;
    @Schema(description = "累计提现金额")
    private Double totalExtract;


    @Schema(description = "未解锁金额")
    private Double unlockMoney;
    @Schema(description = "微信信息")
    private String wxProfile;
    @Schema(description = "支付宝信息")
    private String aliProfile;

    @Schema(description = "邀请码")
    private String code;
    @Schema(description = "会员过期时间")
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
