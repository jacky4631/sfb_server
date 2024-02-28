package com.mailvor.modules.order.vo;


import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.order.service.dto.StatusDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-27
 */
@Data
@ApiModel(value = "MwStoreOrderQueryVo对象", description = "订单表查询参数")
public class MwStoreOrderQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "用户电话")
    private String userPhone;

    @ApiModelProperty(value = "详细地址")
    private String userAddress;

    private List<MwStoreCartQueryVo> cartInfo;

    private StatusDto _status;

    @ApiModelProperty(value = "实际支付积分")
    private BigDecimal payIntegral;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "订单状态（-1 : 申请退款 -2 : 退货成功 0：待发货；1：待收货；2：已收货；3：待评价；-1：已退款）")
    private Integer status;

    @ApiModelProperty(value = "快递名称/送货人姓名")
    private String deliveryName;

    private String deliverySn;

    @ApiModelProperty(value = "快递单号/手机号")
    private String deliveryId;

    @ApiModelProperty(value = "使用积分")
    private BigDecimal useIntegral;


    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "唯一id(md5加密)类似id")
    private String unique;

    @ApiModelProperty(value = "管理员备注")
    private String remark;

    private Integer isSystemDel;

    @ApiModelProperty(value = "腾讯地图key")
    private String mapKey;

    private Long goodsId;
    private String goodsImg;
    private String goodsName;

}
