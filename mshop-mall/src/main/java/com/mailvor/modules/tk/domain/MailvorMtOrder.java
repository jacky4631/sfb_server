/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

/**
* https://pub.meituan.com/v2/csr#/apidoc
 * @author shenji
* @date 2022-08-29
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "mailvor_mt_order", autoResultMap = true)
public class MailvorMtOrder extends TkOrder {
    @TableId
    /** 订单编号 */
    private Long uniqueItemId;
    /**
     * js处理超过17位的Long会丢失精度
     * */
    @TableField(exist = false)
    private String uniqueItemIdStr;
    @TableField(exist = false)
    private Long itemId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date verifyTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date orderPayTime;
    private Long orderId;
    /**
     * js处理超过17位的Long会丢失精度
     * */
    @TableField(exist = false)
    private String orderIdStr;

    private Double actualItemAmount;
    private Double actualOrderAmount;
    private String shopUuid;
    private String shopName;
    private String brandName;
    private String cityName;
    private String cat0Name;
    private String cat1Name;
    private String orderType;
    private String couponId;
    private Integer couponGroupId;

    private Double couponDiscountAmount;

    private Double couponPriceLimit;

    /**
     * 佣金金额,单位元
     * */
    private Double balanceAmount;
    /**
     * 佣金比例
     * */
    private Double balanceCommissionRatio;
    private String orderUserId;
    /**
     * 0	未使用：已经购买，但是并未到店核验
     * 1	已使用：已经购买，且已到店核验
     * 2	退款中：该子订单正在发生退款行为
     * 3	已退款：该子订单退款已完成
     * 5	已消费退款：到店核销后，发生退款行为
     * */
    private Integer itemStatus;


    /**
     * 订单结算状态(聚合)
     * 1	支付成功,表示订单已经完成支付
     * 2	核销成功，表示订单已经到店核验
     * 3	结算成功，标识订单已经完成结算，会有佣金
     * 99	无效订单：标识订单无需结算，会有结算失败理由
     * 999	未知状态：异常订单，正常情况下不会出现该状态
     *
     * */
    private Integer itemBizStatus;
    private String settlementType;
    private String couponSource;
    private String orderPlatform;
    private Long utmSource;

    private String utmMedium;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date modifyTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date settleTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date billingDate;
    private String promotionId;

    private Long dealId;

    private Long launchTag;

    //美团异常订单信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date usedDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date refundDate;
    private String comment;
    private String bizType;
    /**
     * 异常事件类型，1-退款,2-风控,4-负项调整
     * */
    private Integer eventType;

    @TableField(exist = false)
    /** cpa订单信息，1-外卖首购 2-外卖复购*/
    private Integer cpaType;
    @TableField(exist = false)
    /** 红包解锁剩余时间 ok-已解锁 err-红包失效 其他-1天2小时，5小时*/
    private String remain;
    public void copy(MailvorMtOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
