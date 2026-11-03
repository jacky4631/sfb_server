package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AddressParam
 * @author huangyu
 * @Date 2019/10/28
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankParam implements Serializable {

    @Schema(description = "地址ID")
    private String id;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "银行卡号")
    private String bankNo;

    @Schema(description = "签约成功的协议号")
    private String protocolNo;

    @Schema(description = "是否默认")
    private Integer isDefault;

    @Schema(description = "是否默认")
    private Integer sign;

}
