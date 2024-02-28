/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDomain;
import com.mailvor.modules.user.service.dto.OrderExtractDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_user_extract")
public class MwUserExtract extends BaseDomain {

    @TableId
    private Long id;


    private Long uid;

    /**
     * 平台简称
     * */
    private String name;

    /** 名称 */
    private String realName;


    /** bank = 银行卡 alipay = 支付宝wx=微信 */
    private String extractType;


    /** 银行卡 不起作用*/
    private String bankCode;


    /** 开户地址 */
    private String bankAddress;


    /** 支付宝账号 */
    private String alipayCode;


    /** 扣除手续费的提现金额 */
    private BigDecimal extractPrice;


    private String mark;

    private String ip;


    /** 提现金额 */
    private BigDecimal balance;


    /** 无效原因 */
    private String failMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date failTime;


    /** -1 未通过 0 审核中 1 已提现 */
    private Integer status;


    /** 微信号 */
    private String wechat;


    @TableField(exist = false)
    @NotBlank
    private String opePwd;
    /** 订单json array信息 */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private ArrayList<OrderExtractDto> orderList;


    public void copy(MwUserExtract source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
