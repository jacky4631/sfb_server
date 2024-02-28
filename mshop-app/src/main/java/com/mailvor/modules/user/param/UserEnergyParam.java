package com.mailvor.modules.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName UserEditParam
 * @author huangyu
 * @Date 2020/02/07
 **/
@Data
public class UserEnergyParam implements Serializable {

    @NotNull(message = "每天消耗热度不能为空")
    @ApiModelProperty(value = "每天消耗热度")
    private Double dayEnergy;

    @NotBlank(message = "平台不能为空")
    @ApiModelProperty(value = "平台")
    private String platform;


}
