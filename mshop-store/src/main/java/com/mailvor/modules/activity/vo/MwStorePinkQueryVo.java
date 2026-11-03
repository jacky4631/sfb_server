package com.mailvor.modules.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 拼团表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-11-19
 */
@Data
@Schema(description = "拼团表查询参数")
public class MwStorePinkQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "订单id 生成")
    private String orderId;

    @Schema(description = "订单id  数据库")
    private Integer orderIdKey;

    @Schema(description = "购买商品个数")
    private Integer totalNum;

    @Schema(description = "购买总金额")
    private BigDecimal totalPrice;

    @Schema(description = "拼团产品id")
    private Long cid;

    @Schema(description = "产品id")
    private Long pid;

    @Schema(description = "拼团总人数")
    private Integer people;

    @Schema(description = "拼团产品单价")
    private BigDecimal price;

    @Schema(description = "开始时间")
    private Date createTime;

    @Schema(description = "结束时间")
    private Date stopTime;

    @Schema(description = "团长id 0为团长")
    private Long kId;


    @Schema(description = "状态1进行中2已完成3未完成")
    private Integer status;

}
