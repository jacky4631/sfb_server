package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName FaceParam
 * @author huangyu
 * @Date 2020/02/07
 **/
@Data
public class FaceParam implements Serializable {

    @NotBlank
    @Schema(description = "姓名")
    private String certName;

    @NotBlank
    @Schema(description = "身份证")
    private String certNo;

    @NotBlank
    @Schema(description = "手机号")
    private String phone;
}
