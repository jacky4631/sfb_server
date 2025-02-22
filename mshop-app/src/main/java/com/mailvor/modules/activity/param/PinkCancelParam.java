package com.mailvor.modules.activity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName PinkCancelParam
 * @author huangyu
 * @Date 2020/6/23
 **/
@Getter
@Setter
public class PinkCancelParam {

    @NotBlank(message = "参数错误")
    @ApiModelProperty(value = "拼团产品ID")
    private String id;

    @NotBlank(message = "参数错误")
    @ApiModelProperty(value = "团购产品id")
    private String cid;
}
