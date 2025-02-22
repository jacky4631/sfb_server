package com.mailvor.modules.cart.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName CartIds
 * @author huangyu
 * @Date 2019/11/15
 **/
@Data
public class CartIdsParm {

    @NotNull(message = "参数有误")
    @ApiModelProperty(value = "购物车ID，多个用,分隔开")
    List<String> ids;
}
