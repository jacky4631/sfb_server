/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码的 Vo 类
 * @author huangyu
 * @date 2019年7月11日13:59:49
 */
@Data
public class UserPassVo {

    private String oldPass;

    private String newPass;

    @NotBlank
    private String opePwd;
}
