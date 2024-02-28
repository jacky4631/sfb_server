package com.mailvor.modules.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName OrderExtendDto
 * @Date 2022/12/20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTabFirstDto implements Serializable {
    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "是否含有红包")
    private Boolean hasHb;

    @ApiModelProperty(value = "等级 0=自己 1=金客 2=银客")
    private Integer level;

    @ApiModelProperty(value = "订单类型")
    private Integer innerType;

}
