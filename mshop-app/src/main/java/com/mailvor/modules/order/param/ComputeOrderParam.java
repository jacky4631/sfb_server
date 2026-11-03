package com.mailvor.modules.order.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Schema(description = "地址ID")
    private String addressId;

    @Schema(description = "使用积分 1-表示使用")
    private String useIntegral;
}
