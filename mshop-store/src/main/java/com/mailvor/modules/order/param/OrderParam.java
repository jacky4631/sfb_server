package com.mailvor.modules.order.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * @ClassName OrderParam
 * @author huangyu
 * @Date 2019/10/28
 **/
@Data
public class OrderParam implements Serializable {

    @Schema(description = "地址ID")
    private String addressId;

    @Schema(description = "商品ID")
    private Long goodsId;

    @Schema(description = "来源")
    private String from;

    @Size(max = 200,message = "备注长度超过了限制")
    @Schema(description = "备注")
    private String mark;

    @NotBlank(message="请选择支付方式")
    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "使用积分 1-表示使用")
    private String useIntegral;

}
