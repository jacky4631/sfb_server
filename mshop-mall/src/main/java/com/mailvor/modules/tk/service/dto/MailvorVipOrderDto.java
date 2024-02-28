/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.modules.user.service.dto.VipOrderDetailDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

/**
* @author wangjun
* @date 2022-09-07
*/
@Data
public class MailvorVipOrderDto extends BaseOrderDto{

    /** 订单号 */
    private String orderSn;

    /** 订单状态 */
    private String orderSubStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    /** 下单时间 */
    private Date orderTime;

    /** 自定义统计参数 */
    private String statParam;

    /** 订单支付金额:单位元 */
    private String totalCost;

    /** appKey */
    private String appKey;

    /** 渠道商模式下表示自定义渠道标识；工具商模式下表示pid */
    private String channelTag;

    /** 商品佣金金额(元,保留两位小数) */
    private String commission;

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
    private ArrayList<VipOrderDetailDto> detailList;
}
