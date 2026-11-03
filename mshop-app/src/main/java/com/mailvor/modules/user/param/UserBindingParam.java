package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserEditParam
 * @author huangyu
 * @Date 2020/02/07
 **/
@Data
public class UserBindingParam implements Serializable {

    @Schema(description = "session")
    private String session;

}
