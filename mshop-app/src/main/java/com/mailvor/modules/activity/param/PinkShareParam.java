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
public class PinkShareParam {
    @NotBlank(message = "参数有误")
    private String id;

    @Schema(description = "来源")
    private String from;
}
