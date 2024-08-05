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
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.mailvor.modules.user.service.dto.GoodsInfoDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* @author shenji
* @date 2022-09-05
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "mw_order_jd", autoResultMap = true)
public class MailvorJdOrder extends TkOrder {
    /** 标记唯一订单行 */
    @TableId
    private String id;

    /** 订单号 */
    @NotNull
    private Long orderId;

    /** 父单的订单号 */
    private Long parentId;

    /** 下单时间 */
    @NotNull
    private Date orderTime;

    /** 完成时间 */
    private Date finishTime;

    /** 更新时间 */
    private Date modifyTime;

    /** 下单设备 1.pc 2.无线 */
    private Integer orderEmt;

    /** 下单用户是否为PLUS会员 0：否，1：是 */
    private Integer plus;

    /** 推客ID */
    private Long unionId;

    /** 商品ID */
    @NotNull
    private Long skuId;

    /** 商品名称 */
    private String skuName;

    /** 商品数量 */
    private Long skuNum;

    /** 商品已退货数量 */
    private Long skuReturnNum;

    /** 商品售后中数量 */
    private Long skuFrozenNum;

    /** 商品单价 */
    private Double price;

    /** 佣金比例 */
    private Double commissionRate;

    /** 分成比例（单位：%） */
    private Double subsideRate;

    /** 补贴比例（单位：%） */
    private Double subsidyRate;

    /** 最终分佣比例 */
    private Double finalRate;

    /** 预估计订单金额 */
    private Double estimateCosPrice;

    /** 预估佣金，以这个为准 */
    private Double estimateFee;

    /** 实际订单金额 */
    private Double actualCosPrice;

    /** 实际佣金，未结算时为0 */
    private Double actualFee;

    /** sku维度的有效码（-1：未知,2.无效-拆单,3.无效-取消,4.无效-京东帮帮主订单,5.无效-账号异常,6.无效-赠品类目不返佣,7.无效-校园订单,8.无效-企业订单,9.无效-团购订单,11.无效-乡村推广员下单,13.无效-违规订单,14.无效-来源与备案网址不符,15.待付款,16.已付款,17.已完成（购买用户确认收货）,20.无效-此复购订单对应的首购订单无效,21.无效-云店订单 */
    private Long validCode;

    /** 同跨店：2同店 3跨店 */
    private Integer traceType;

    /** 推广位ID */
    private Long positionId;

    /** 应用id（网站id、appid、社交媒体id） */
    private Long siteId;

    /** ID所属母账号平台名称 */
    private String unionAlias;

    /** 格式:子推客ID_子站长应用ID_子推客推广位ID */
    private String pid;

    /** 一级类目id */
    private Long cid1;

    /** 二级类目id */
    private Long cid2;

    /** 三级类目id */
    private Long cid3;

    /** 子渠道标识，在转链时可自定义传入，格式要求：字母、数字或下划线，最多支持80个字符 */
    private String subUnionId;

    /** 联盟标签数据 */
    private String unionTag;

    /** 商家ID */
    private Long popId;

    /** 推客生成推广链接时传入的扩展字段 */
    private String ext1;

    /** 预估结算时间 yyyyMMdd */
    private String payMonth;

    /** 团长渠道ID */
    private Long rid;

    /** 微信用户json信息 */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private GoodsInfoDto GoodsInfo;





    public void copy(MailvorJdOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
