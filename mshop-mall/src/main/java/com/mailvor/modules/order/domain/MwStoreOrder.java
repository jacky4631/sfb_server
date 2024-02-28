/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("mw_store_order")
public class MwStoreOrder extends BaseDomain {

    /** 订单ID */
    @TableId
    private Long id;

    /** 订单号 */
    private String orderId;

    /** 用户id */
    private Long uid;


    /** 用户姓名 */
    private String realName;


    /** 用户电话 */
    private String userPhone;


    /** 详细地址 */
    private String userAddress;

    /** 订单状态（-1 : 申请退款 -2 : 退货成功 0：待发货；1：待收货；2：已收货；3：待评价；-1：已退款） */
    private Integer status;

    /** 快递公司编号 */
    private String deliverySn;


    /** 快递名称/送货人姓名 */
    private String deliveryName;

    /** 快递单号/手机号 */
    private String deliveryId;

    /** 使用积分 */
    private BigDecimal useIntegral;

    /** 备注 */
    private String mark;


    /** 唯一id(md5加密)类似id */
     @TableField(value = "`unique`")
    private String unique;

    /** 管理员备注 */
    private String remark;

    private Integer isSystemDel;

    @ApiModelProperty(value = "实际支付积分")
    private BigDecimal payIntegral;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;
    private String goodsImg;
    private String goodsName;


    public void copy(MwStoreOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
