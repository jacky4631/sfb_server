/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.feedback.service.dto;

import lombok.Data;
import java.util.Date;
import java.io.Serializable;

/**
* @author wangjun
* @date 2024-05-27
*/
@Data
public class MwUserFeedbackDto implements Serializable {

    /** id */
    private Long id;

    /** 用户id */
    private Long uid;

    /** 反馈内容 */
    private String feedback;

    /** 添加时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    private Integer isDel;
}
