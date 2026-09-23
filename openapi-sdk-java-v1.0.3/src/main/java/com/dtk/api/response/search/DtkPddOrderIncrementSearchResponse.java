package com.dtk.api.response.search;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 拼多多订单增量查询-响应实体
 *
 * @author 1
 * @date 2020/11/11 17:33
 */
@Getter
@Setter
public class DtkPddOrderIncrementSearchResponse {
    /**
     * 多多客工具id
     */
    private Long authDuoId;

    /**
     * 结算批次号
     */
    private String batchNo;

    /**
     * 订单关联礼金活动Id
     */
    private Long cashGiftId;

    /**
     * 商品一~四级类目ID列表
     */
    private List<Long> catIds;

    /**
     * 是否是 cpa 新用户，1表示是，0表示否
     */
    private Integer cpaNew;

    /**
     * 自定义参数
     */
    private String customParameters;

    /**
     * 订单审核失败原因
     */
    private String failReason;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品标题
     */
    private String goodsName;

    /**
     * 订单中sku的单件价格，单位为分
     */
    private BigDecimal goodsPrice;

    /**
     * 购买商品的数量
     */
    private Long goodsQuantity;

    /**
     * goodsSign是加密后的goodsId, goodsId已下线，请使用goodsSign来替代。
     * 需要注意的是：推广链接带有goodsSign信息的情况下，产生的订单中才会有相应的goodsSign信息。其他情况下产生的订单，该字段不返回。
     */
    private String goodsSign;

    /**
     * 商品缩略图
     */
    private String goodsThumbnailUrl;

    /**
     * 成团编号
     */
    private Long groupId;

    /**
     * 是否直推 ，1表示是，0表示否
     */
    private Integer isDirect;

    /**
     * 实际支付金额，单位为分
     */
    private BigDecimal orderAmount;

    /**
     * 订单生成时间，UNIX时间戳
     */
    private String orderCreateTime;

    /**
     * 成团时间
     */
    private String orderGroupSuccessTime;

    /**
     * 最后更新时间
     */
    private String orderModifyAt;

    /**
     * 支付时间
     */
    private String orderPayTime;

    /**
     * 确认收货时间
     */
    private String orderReceiveTime;

    /**
     * 结算时间
     */
    private String orderSettleTime;

    /**
     * 推广订单编号
     */
    private String orderSN;

    /**
     * 订单状态： -1 未支付; 0-已支付；1-已成团；2-确认收货；3-审核成功；4-审核失败（不可提现）；5-已经结算；8-非多多进宝商品（无佣金订单）
     */
    private Integer orderStatus;

    /**
     * 订单状态描述
     */
    private String orderStatusDesc;

    /**
     * 审核时间
     */
    private String orderVerifyTime;

    /**
     * 比价状态：0：正常，1：比价
     */
    private Integer priceCompareStatus;

    /**
     * 佣金金额，单位为分
     */
    private BigDecimal promotionAmount;

    /**
     * 佣金比例，千分比
     */
    private BigDecimal promotionRate;

    /**
     * 推广位ID
     */
    private String pid;

    /**
     * 场景工具商佣金
     */
    private Integer sceneAtMarketFee;

    /**
     * 直播间订单推广duoId
     */
    private Long sepDuoId;

    /**
     * 直播间推广佣金
     */
    private String sepMarketFee;

    /**
     * 直播间推广自定义参数
     */
    private String sepParameters;

    /**
     * 直播间订单推广位
     */
    private String sepPid;

    /**
     * 直播间推广佣金比例
     */
    private Integer sepRate;

    /**
     * 招商分成服务费金额，单位为分
     */
    private BigDecimal shareAmount;

    /**
     * 招商分成服务费比例，千分比
     */
    private BigDecimal shareRate;

    /**
     * 订单推广类型
     */
    private Long type;

    /**
     * 招商多多客id
     */
    private Long zsDuoId;
}
