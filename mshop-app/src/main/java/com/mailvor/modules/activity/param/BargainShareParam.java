package com.mailvor.modules.activity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName BargainShareParam
 * @author huangyu
 * @Date 2020/6/20
 **/
@Getter
@Setter
public class BargainShareParam {

    @NotBlank(message = "参数有误")
    @ApiModelProperty(value = "砍价产品ID")
    private String bargainId;

    @ApiModelProperty(value = "来源")
    private String from;
}
