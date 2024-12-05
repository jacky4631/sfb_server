package com.mailvor.modules.order.service.dto;

import lombok.Data;

@Data
public class OrderCheckConfigDto {
    /**
     * 往回推几天
     * */
    private Integer prior = 1;

    /**
     * 订单佣金比例
     * */
    private Double scale = 30d;
    /**
     * 订单数量
     * */
    private Integer count = 5;

    /**
     * 设置的退款数量
     * */
    private Integer refund = 10;
}
