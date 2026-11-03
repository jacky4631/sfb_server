package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * @ClassName UserEditParam
 * @author huangyu
 * @Date 2020/02/07
 **/
@Data
public class UserEditParam implements Serializable {

    @Schema(description = "用户头像")
    private String avatar;

    @Size(min = 1, max = 8,message = "用户昵称长度超过了限制")
    @Schema(description = "用户昵称")
    private String nickname;

    @Size(min = 11, max = 11,message = "手机号长度超过了限制")
    @Schema(description = "手机号")
    private String phone;

    @Size(min = 1, max = 20,message = "姓名长度超过了限制")
    @Schema(description = "姓名")
    private String realName;

    @Size(min = 4, max = 20,message = "邀请码长度超过了限制")
    @Schema(description = "父类邀请码或者手机号")
    private String code;

    @Size(min = 4, max = 8,message = "邀请码长度超过了限制")
    @Schema(description = "修改的邀请码")
    private String changedCode;


}
