package com.mailvor.modules.activity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * @ClassName BargainShareParam
 * @author huangyu
 * @Date 2020/6/20
 **/
@Getter
@Setter
public class BargainShareParam {

    @NotBlank(message = "参数有误")
    @Schema(description = "砍价产品ID")
    private String bargainId;

    @Schema(description = "来源")
    private String from;
}
