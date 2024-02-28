/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.push.dto;

import lombok.Data;

import java.io.Serializable;

/**
* @author shenji
* @date 2022-09-07
*/
@Data
public class JPushDto implements Serializable {

    private String platform = "all";

    private AudienceDto audience = new AudienceDto();

    private NotificationDto notification = new NotificationDto();


    public static JPushDto build(String content, Long uid) {
        JPushDto jPushDto = new JPushDto();
        jPushDto.notification.setAlert(content);
        jPushDto.audience.getAlias().add("uid" + uid);
        return jPushDto;
    }
}
