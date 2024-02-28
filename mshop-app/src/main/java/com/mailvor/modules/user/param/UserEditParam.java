package com.mailvor.modules.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @ClassName UserEditParam
 * @author huangyu
 * @Date 2020/02/07
 **/
@Data
public class UserEditParam implements Serializable {

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @Size(min = 1, max = 8,message = "用户昵称长度超过了限制")
    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @Size(min = 11, max = 11,message = "手机号长度超过了限制")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @Size(min = 1, max = 20,message = "姓名长度超过了限制")
    @ApiModelProperty(value = "姓名")
    private String realName;

    @Size(min = 4, max = 20,message = "邀请码长度超过了限制")
    @ApiModelProperty(value = "父类邀请码或者手机号")
    private String code;

    @Size(min = 4, max = 8,message = "邀请码长度超过了限制")
    @ApiModelProperty(value = "修改的邀请码")
    private String changedCode;


}
