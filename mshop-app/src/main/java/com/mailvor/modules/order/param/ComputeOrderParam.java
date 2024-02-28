package com.mailvor.modules.order.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName ComputeOrderParam
 * @author huangyu
 * @Date 2020/6/22
 **/
@Getter
@Setter
@ToString
public class ComputeOrderParam {
    //@NotBlank(message = "请选择地址")
    @ApiModelProperty(value = "地址ID")
    private String addressId;

    @ApiModelProperty(value = "使用积分 1-表示使用")
    private String useIntegral;
}
