/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* @author shenji
* @date 2022-09-06
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "mailvor_pdd_order", autoResultMap = true)
public class MailvorPddOrder extends TkOrder {
    /** 订单编号 */
    @TableId
    private String orderSn;

    /** 订单状态：0-已支付；1-已成团；2-确认收货；3-审核成功；4-审核失败（不可提现）；5-已经结算 ;10-已处罚 */
    private Integer orderStatus;

    /** 订单状态描述 */
    private String orderStatusDesc;

    /** 比价状态：0：正常，1：比价 */
    private Integer priceCompareStatus;

    /** 佣金金额，单位为分 */
    private Long promotionAmount;

    /** 自定义参数，转链时传入 */
    private String customParameters;

    /** 商品ID */
    @NotNull
    private Long goodsId;

    /** 商品名称 */
    private String goodsName;

    /** 订单中sku的单件价格，单位为分 */
    private Long goodsPrice;

    /** 商品数量 */
    private Long goodsQuantity;

    /** goodsSign是加密后的goodsId，goodsId已下线，请使用goodsSign来替代 */
    private String goodsSign;

    /** 商品缩略图 */
    private String goodsThumbnailUrl;

    /** 成团编号 */
    private Long groupId;

    /** 店铺id */
    private Long mallId;

    /** 店铺名称 */
    private String mallName;

    /** 是否直推 ，1表示是，0表示否 */
    private Integer isDirect;

    /** 实际支付金额，单位为分 */
    private Long orderAmount;

    /** 多多客工具id */
    private Long authDuoId;

    /** 预判断是否为代购订单，-1（默认）表示未出结果，0表示预判不是代购订单，1表示代购订单，具体请以最后审核状态为准 */
    private Integer bandanRiskConsult;

    /** 结算批次号 */
    private String batchNo;

    /** 订单关联礼金活动Id */
    private Long cashGiftId;

    /** 是否是 cpa 新用户，1表示是，0表示否 */
    private Integer cpaNew;

    /** 订单审核失败/惩罚原因 */
    private String failReason;

    /** 商品一级类目名称 */
    private String goodsCategoryName;

    /** 非补贴订单原因，例如："商品补贴达上限"，"达到单个用户下单上限"，"非指定落地页直推订单"，"订单超过2个月未审核成功"等 */
    private String noSubsidyReason;

    /** 订单生成时间，UNIX时间戳 */
    private Date orderCreateTime;

    /** 成团时间 */
    private Date orderGroupSuccessTime;

    /** 最后更新时间 */
    private Date orderModifyAt;

    /** 支付时间 */
    private Date orderPayTime;

    /** 确认收货时间 */
    private Date orderReceiveTime;

    /** 结算时间 */
    private Date orderSettleTime;

    /** 审核时间 */
    private Date orderVerifyTime;

    /** 推广位ID */
    private String pId;

    /** 平台券金额，表示该订单使用的平台券金额，单位分 */
    private Long platformDiscount;

    /** 佣金比例，千分比 */
    private Long promotionRate;

    /** 超级红包补贴类型：0-非红包补贴订单，1-季度新用户补贴 */
    private Integer redPacketType;

    /** 直播间订单推广duoId */
    private Long sepDuoId;

    /** 直播间推广佣金 */
    private Integer sepMarketFee;

    /** 直播间推广自定义参数 */
    private String sepParameters;

    /** 直播间订单推广位 */
    private String sepPid;

    /** 直播间推广佣金比例 */
    private Integer sepRate;

    /** 招商分成服务费金额，单位为分 */
    private Integer shareAmount;

    /** 招商分成服务费比例，千分比 */
    private Integer shareRate;

    /** 优势渠道专属商品补贴金额，单位为分 */
    private Integer subsidyAmount;

    /** 等级补贴给渠道的收入补贴，不允许直接给下级代理展示，单位为分 */
    private Integer subsidyDuoAmountLevel;

    /** 官方活动给渠道的收入补贴金额，不允许直接给下级代理展示，单位为分 */
    private Integer subsidyDuoAmountTenMillion;

    /** 补贴订单备注 */
    private String subsidyOrderRemark;

    /** 订单补贴类型：0-非补贴订单，1-千万补贴，2-社群补贴，3-多多星选，4-品牌优选，5-千万神券 */
    private Integer subsidyType;

    /** 下单场景类型：0-单品推广，1-红包活动推广，4-多多进宝商城推广，7-今日爆款，8-品牌清仓，9-1.9包邮，77-刮刮卡活动推广，94-充值中心，101-品牌黑卡，103-百亿补贴频道，104-内购清单频道，105-超级红包 */
    private Integer type;

    /** 招商多多客id */
    private Long zsDuoId;


    public void copy(MailvorPddOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
