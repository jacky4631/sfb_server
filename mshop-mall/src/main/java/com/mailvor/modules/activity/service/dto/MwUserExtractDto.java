/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class MwUserExtractDto implements Serializable {


    private Long id;

    private Long uid;

    private String name;

    // 名称
    private String realName;

    // bank = 银行卡 alipay = 支付宝wx=微信
    private String extractType;

    // 银行卡
    private String bankCode;

    // 开户地址
    private String bankAddress;

    // 支付宝账号
    private String alipayCode;

    // 提现金额
    private BigDecimal extractPrice;

    private String mark;

    private BigDecimal balance;

    // 无效原因
    private String failMsg;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date failTime;

    // 添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    // -1 未通过 0 审核中 1 已提现
    private Integer status;

    // 微信号
    private String wechat;
}
