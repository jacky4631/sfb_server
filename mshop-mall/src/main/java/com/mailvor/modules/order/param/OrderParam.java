package com.mailvor.modules.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @ClassName OrderParam
 * @author huangyu
 * @Date 2019/10/28
 **/
@Data
public class OrderParam implements Serializable {

    @ApiModelProperty(value = "地址ID")
    private String addressId;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @ApiModelProperty(value = "来源")
    private String from;

    @Size(max = 200,message = "备注长度超过了限制")
    @ApiModelProperty(value = "备注")
    private String mark;

    @NotBlank(message="请选择支付方式")
    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "使用积分 1-表示使用")
    private String useIntegral;

}
