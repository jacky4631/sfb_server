/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@TableName("mw_store_order_cart_info")
public class MwStoreOrderCartInfo implements Serializable {

    @TableId
    private Long id;


    /** 订单id */
    private Long oid;

    /**
     * 订单号
     */
    private String orderId;


    /** 购物车id */
    private Long cartId;


    /** 商品ID */
    private Long productId;


    /** 购买东西的详细信息 */
    private String cartInfo;


    /** 唯一id */
    @TableField(value = "`unique`")
    private String unique;

    /** 是否能售后0不能1能 */
    private Integer isAfterSales;


    public void copy(MwStoreOrderCartInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
