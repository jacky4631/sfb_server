package com.mailvor.modules.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserEditParam
 * @author huangyu
 * @Date 2020/02/07
 **/
@Data
public class UserBindingParam implements Serializable {

    @ApiModelProperty(value = "session")
    private String session;

}
