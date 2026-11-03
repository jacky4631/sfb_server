package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName PromParam
 * @author huangyu
 * @Date 2019/11/12
 **/
@Data
public class SpreadUpParam implements Serializable {


    @Schema(description = "平台")
    private String  platform;

}
