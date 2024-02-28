package com.mailvor.modules.user.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "地址ID")
    private String id;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "银行卡号")
    private String bankNo;

    @ApiModelProperty(value = "签约成功的协议号")
    private String protocolNo;

    @ApiModelProperty(value = "是否默认")
    private Integer isDefault;

    @ApiModelProperty(value = "是否默认")
    private Integer sign;

}
