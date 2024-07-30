/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.cart.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* @author huangyu
* @date 2020-05-12
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("mw_store_cart")
public class MwStoreCart extends BaseDomain {

    /** 购物车表ID */
    @TableId
    private Long id;


    /** 用户ID */
    private Long uid;


    /** 类型 */
    private String type;


    /** 商品ID */
    private Long productId;


    /** 商品属性 */
    private String productAttrUnique;


    /** 商品数量 */
    private Integer cartNum;




    /** 0 = 未购买 1 = 已购买 */
    private Integer isPay;



    /** 是否为立即购买 */
    private Integer isNew;


    /** 拼团id */
    //@Column(name = "combination_id")
    private Long combinationId;


    /** 秒杀产品ID */
    private Long seckillId;


    /** 砍价id */
    private Long bargainId;


    public void copy(MwStoreCart source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
