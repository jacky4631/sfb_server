package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AddressDetailParam
 * @author huangyu
 * @Date 2019/10/28
 **/
@Data
public class AddressDetailParam  implements Serializable {

    @JsonProperty(value = "city_id")
    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "地区")
    private String district;

    @Schema(description = "省份")
    private String province;
}
