/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.feedback.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

/**
* @author wangjun
* @date 2024-05-27
*/
@Data
public class MwUserFeedbackQueryCriteria{

    /** 精确 */
    @Query
    private Long uid;
}
