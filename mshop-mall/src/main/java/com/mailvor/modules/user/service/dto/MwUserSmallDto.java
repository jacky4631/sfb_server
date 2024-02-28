/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.dto;

import lombok.Data;

import java.io.Serializable;


/**
* @author huangyu
* @date 2019-10-06
*/
@Data
public class MwUserSmallDto implements Serializable {

    // 用户id
    private Integer uid;

    // 用户昵称
    private String nickname;

    // 用户头像
    private String avatar;

    // 手机号码
    private String phone;


}
