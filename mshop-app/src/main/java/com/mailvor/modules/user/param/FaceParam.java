package com.mailvor.modules.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName FaceParam
 * @author huangyu
 * @Date 2020/02/07
 **/
@Data
public class FaceParam implements Serializable {

    @NotBlank
    @ApiModelProperty(value = "姓名")
    private String certName;

    @NotBlank
    @ApiModelProperty(value = "身份证")
    private String certNo;

    @NotBlank
    @ApiModelProperty(value = "手机号")
    private String phone;
}
