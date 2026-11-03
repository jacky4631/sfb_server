package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * @ClassName AddressParam
 * @author huangyu
 * @Date 2019/10/28
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressParam  implements Serializable {

    @Schema(description = "地址ID")
    private String id;
    @NotBlank
    @Size(min = 1, max = 30,message = "名字长度超过了限制")
    @Schema(description = "收货地址真实名字")
    private String real_name;

    @Schema(description = "收货地址邮编")
    private String post_code;

    @Schema(description = "是否默认收货地址 true是 false否")
    private String is_default;

    private String wx_export;

    @NotBlank
    @Size(min = 1, max = 60,message = "地址长度超过了限制")
    @Schema(description = "收货详细地址")
    private String detail;

    @NotBlank
    @Schema(description = "收货手机号码")
    private String phone;

    @Schema(description = "收货地址详情")
    private AddressDetailParam address;
}
