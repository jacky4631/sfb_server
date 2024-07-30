/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
public class TBOrderDTO extends BaseDomain {

    /** 订单ID */
    @TableId
    private Long id;


    /** 订单号 */
    private String tb_paid_time;
    /** 订单号 */
    private String tk_paid_time;
    /** 订单号 */
    private String pay_price;
    /** 订单号 */
    private String pub_share_fee;
    /** 订单号 */
    private String trade_id;
    /** 订单号 */
    private Integer tk_order_role;
    /** 订单号 */
    private String tk_earning_time;
    /** 订单号 */
    private BigDecimal adzone_id;
    /** 订单号 */
    private String pub_share_rate;
    /** 订单号 */
    private BigDecimal refund_tag;
    /** 订单号 */
    private String subsidy_rate;
    /** 订单号 */
    private Double tk_total_rate;

    /** 订单号 */
    private String item_category_name;
    /** 订单号 */
    private String seller_nick;
    /** 订单号 */
    private BigDecimal pub_id;
    /** 订单号 */
    private String alimama_rate;
    /** 订单号 */
    private String subsidy_type;
    /** 订单号 */
    private String item_img;
    /** 订单号 */
    private Double pub_share_pre_fee;
    /** 订单号 */
    private String alipay_total_price;

    /** 订单号 */
    private String item_title;
    /** 订单号 */
    private String site_name;
    /** 订单号 */
    private Integer item_num;
    /** 订单号 */
    private String subsidy_fee;
    /** 订单号 */
    private String alimama_share_fee;
    /** 订单号 */
    private String trade_parent_id;
    /** 订单号 */
    private String order_type;
    /** 订单号 */
    private String tk_create_time;
    /** 订单号 */
    private String flow_source;
    /** 订单号 */
    private String terminal_type;
    /** 订单号 */
    private String click_time;
    /** 订单号 */
    private Integer tk_status;
    private Double item_price;
    private BigDecimal item_id;
    private String adzone_name;
    private Double total_commission_rate;
    private String item_link;
    private BigDecimal site_id;
    private String seller_shop_title;
    private String income_rate;
    private String total_commission_fee;
    private BigDecimal special_id;
    private BigDecimal relation_id;
    private String tk_deposit_time;
    private String tb_deposit_time;
    private String deposit_price;
    private String app_key;
    private String share_relative_rate;
    private String share_fee;
    private String share_pre_fee;
    private Integer tk_share_role_type;
    private Boolean has_pre;
    private String position_index;
    private Boolean has_next;
    private Integer page_no;
    private Integer page_size;
    private String modified_time;

    public void copy(TBOrderDTO source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
