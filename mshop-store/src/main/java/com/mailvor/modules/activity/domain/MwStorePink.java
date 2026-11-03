/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("mw_store_pink")
public class MwStorePink extends BaseDomain {

    @TableId
    @Schema(description = "拼团产品ID")
    private Long id;


    /** 用户id */
    @Schema(description = "拼团产品用户ID")
    private Long uid;


    /** 订单id 生成 */
    @Schema(description = "拼团产品订单ID 生成")
    private String orderId;


    /** 订单id  数据库 */
    @Schema(description = "拼团产品订单id  数据库")
    private Long orderIdKey;


    /** 购买商品个数 */
    @Schema(description = "购买商品个数")
    private Integer totalNum;


    /** 购买总金额 */
    @Schema(description = "购买总金额")
    private BigDecimal totalPrice;


    /** 拼团产品id */
    @Schema(description = "拼团产品id")
    private Long cid;


    /** 产品id */
    @Schema(description = "产品id")
    private Long pid;


    /** 拼团总人数 */
    @Schema(description = "拼团总人数")
    private Integer people;


    /** 拼团产品单价 */
    @Schema(description = "拼团产品单价")
    private BigDecimal price;

    /** 拼团产品停止时间 */
    @Schema(description = "拼团产品停止时间")
    private Date stopTime;


    /** 团长id 0为团长 */
    @Schema(description = "拼团产品团长id 0为团长")
    private Long kId;


    /** 是否发送模板消息0未发送1已发送 */
    @Schema(description = "是否发送模板消息0未发送1已发送")
    private Integer isTpl;


    /** 是否退款 0未退款 1已退款 */
    @Schema(description = "是否退款 0未退款 1已退款")
    private Integer isRefund;


    /** 状态1进行中2已完成3未完成 */
    @Schema(description = "状态1进行中2已完成3未完成")
    private Integer status;

    @Schema(description = "库存唯一值")
    private String uniqueId;

    public void copy(MwStorePink source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
