/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
* @author wangjun
* @date 2024-05-27
*/
@Data
public class UserFeedbackParam implements Serializable {

    @NotBlank
    /** 反馈内容 */
    private String feedback;
}
