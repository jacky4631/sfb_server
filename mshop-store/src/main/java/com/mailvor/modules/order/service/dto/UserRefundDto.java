package com.mailvor.modules.order.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName StatusDto
 * @author huangyu
 * @Date 2019/10/30
 **/
@Data
public class UserRefundDto implements Serializable {
    private Long uid;
    private Integer level;
    private Integer levelPdd;
    private Integer levelJd;
    private Integer levelDy;
    private Integer levelVip;

    private Integer refund;
}
