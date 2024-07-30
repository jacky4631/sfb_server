/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;

/**
* @author wangjun
* @date 2022-09-07
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "mw_vip_order", autoResultMap = true)
public class MailvorVipOrder extends TkOrder {
    /** 订单号 */
    @TableId
    private String orderSn;

    /** 订单状态 */
    @NotBlank
    private String orderSubStatusName;

    /** 下单时间 */
    @NotNull
    private Date orderTime;

    /** 自定义统计参数 */
    private String statParam;

    private String openId;

    /** 订单支付金额:单位元 */
    private String totalCost;

    /** appKey */
    private String appKey;

    /** 渠道商模式下表示自定义渠道标识；工具商模式下表示pid */
    private String channelTag;

    /** 商品佣金金额(元,保留两位小数) */
    private String commission;

    /** 佣金比例 从detailList中解析得到 目前在用 */
    private Double commissionRate;

    /** 入账时间，时间戳，单位毫秒 */
    private Date commissionEnterTime;

    /** 是否预付订单:0-否，1-是 */
    private Integer isPrepay;

    /** 订单拆单标识: 0-否，1-是 */
    private Integer isSplit;

    /** 订单上次更新时间 时间戳 单位毫秒 */
    private Date lastUpdateTime;

    /** 新老客：0-待定，1-新客，2-老客 */
    private Integer newCustomer;

    /** 订单来源 */
    private String orderSource;

    /** 订单归因方式：0-常规推广,1-惊喜红包,2-锁粉,3-超级红包 */
    private Integer orderTrackReason;

    /** 推广PID:目前等同于channelTag */
    private String pid;

    /** 是否自推自买 0-否，1-是 */
    private Integer selfBuy;

    /** 订单结算状态 0-未结算,1-已结算 */
    private Integer settled;

    /** 订单状态:0-不合格，1-待定，2-已完结 */
    private Integer status;

    /** 商品json array信息 */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private ArrayList<JSONObject> detailList;


    public void copy(MailvorVipOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
