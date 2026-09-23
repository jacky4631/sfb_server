package com.dtk.api.response.special;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 京东一元购响应结果实体
 *
 * @author 1
 * @date 2020/11/11 17:51
 */
@Getter
@Setter
public class DtkJdOneDollarPurchaseOrderInquiryResponse {

    /**
     * 标记唯一订单行：订单+sku维度的唯一标识
     */
    private String id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 父单的订单号：如一个订单拆成多个子订单时，原订单号会作为父单号，拆分的订单号为子单号存储在orderid中。若未发生拆单，该字段为0
     */
    private Long parentId;

    /**
     * 下单时间,格式yyyy-MM-dd HH:mm:ss
     */
    private String orderTime;

    /**
     * 完成时间（购买用户确认收货时间）,格式yyyy-MM-dd HH:mm:ss
     */
    private String finishTime;

    /**
     * 更新时间,格式yyyy-MM-dd HH:mm:ss
     */
    private String modifyTime;

    /**
     * 下单设备 1.pc 2.无线
     */
    private String orderEmt;

    /**
     * 下单用户是否为PLUS会员 0：否，1：是
     */
    private Integer plus;

    /**
     * 推客ID
     */
    private Long unionId;

    /**
     * 商品ID
     */
    private Long skuId;

    /**
     * 商品名称
     */
    private String skuName;

    /**
     * 商品数量
     */
    private Integer skuNum;

    /**
     * 商品已退货数量
     */
    private Integer skuReturnNum;

    /**
     * 商品售后中数量
     */
    private Integer skuFrozenNum;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 佣金比例(投放的广告主计划比例)
     */
    private BigDecimal commissionRate;

    /**
     * 分成比例（单位：%）
     */
    private BigDecimal subSideRate;

    /**
     * 补贴比例（单位：%）
     */
    private BigDecimal subsidyRate;

    /**
     * 最终分佣比例（单位：%）=分成比例+补贴比例
     */
    private BigDecimal finalRate;

    /**
     * 预估计佣金额：由订单的实付金额拆分至每个商品的预估计佣金额，不包括运费，以及京券、东券、E卡、余额等虚拟资产支付的金额。该字段仅为预估值，实际佣金以actualCosPrice为准进行计算
     */
    private BigDecimal estimateCosPrice;

    /**
     * 推客的预估佣金（预估计佣金额*佣金比例*最终比例），如订单完成前发生退款，此金额也会更新。
     */
    private BigDecimal estimateFee;

    /**
     * 实际计算佣金的金额。订单完成后，会将误扣除的运费券金额更正。如订单完成后发生退款，此金额会更新。
     */
    private BigDecimal actualCosPrice;

    /**
     * 推客分得的实际佣金（实际计佣金额*佣金比例*最终比例）。如订单完成后发生退款，此金额会更新。
     */
    private BigDecimal actualFee;

    /**
     * sku维度的有效码（
     * -1：未知,
     * 2.无效-拆单,
     * 3.无效-取消,
     * 4.无效-京东帮帮主订单,
     * 5.无效-账号异常,
     * 6.无效-赠品类目不返佣,
     * 7.无效-校园订单,
     * 8.无效-企业订单,
     * 9.无效-团购订单,
     * 11.无效-乡村推广员下单,
     * 13.无效-违规订单,
     * 14.无效-来源与备案网址不符,
     * 15.待付款,
     * 16.已付款,
     * 17.已完成（购买用户确认收货）,
     * 20.无效-此复购订单对应的首购订单无效,
     * 21.无效-云店订单
     */
    private Integer validCode;

    /**
     * 同跨店：2同店 3跨店
     */
    private Integer traceType;

    /**
     * 推广位ID
     */
    private Long positionId;

    /**
     * 应用id（网站id、appid、社交媒体id）
     */
    private Long siteId;

    /**
     * PID所属母账号平台名称（原第三方服务商来源），两方分佣会有该值
     */
    private String unionAlias;

    /**
     * 格式:子推客ID_子站长应用ID_子推客推广位ID
     */
    private String pid;

    /**
     * 一级类目id
     */
    private Long cid1;

    /**
     * 二级类目id
     */
    private Long cid2;

    /**
     * 三级类目id
     */
    private Long cid3;

    /**
     * 子渠道标识，在转链时可自定义传入，格式要求：字母、数字或下划线，最多支持80个字符(需要联系运营开放白名单才能拿到数据)
     */
    private String subUnionId;

    /**
     * 联盟标签数据
     */
    private String unionTag;

    /**
     * 商家ID
     */
    private Long popId;

    /**
     * 推客生成推广链接时传入的扩展字段（需要联系运营开放白名单才能拿到数据）。
     */
    private String ext1;

    /**
     * 预估结算时间，订单完成后才会返回，格式：yyyyMMdd，
     * 默认：0。表示最新的预估结算日期。当payMonth为当前的未来时间时，表示该订单可结算；当payMonth为当前的过去时间时，表示该订单已结算
     */
    private String payMonth;

    /**
     * 招商团活动id：当商品参加了招商团会有该值，为0时表示无活动
     */
    private Long cpActId;

    /**
     * 站长角色：1 推客 2 团长
     */
    private Integer unionRole;

    /**
     * 礼金分摊金额：使用礼金的订单会有该值
     */
    private BigDecimal giftCouponOcsAmount;

    /**
     * 礼金批次ID：使用礼金的订单会有该值
     */
    private String giftCouponKey;

    /**
     * 计佣扩展信息，表示结算月:每月实际佣金变化情况，格式：{20191020:10,20191120:-2}，订单完成后会有该值
     */
    private String balanceExt;

    /**
     * 商品信息，入参传入fields，goodsInfo获取
     */
    private GoodsInfo goodsInfo;

    @Data
    public static class GoodsInfo {

        /**
         * sku主图链接
         */
        private String imageUrl;
    }
}
