/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.quartz.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class QuartzJobDto implements Serializable {

    /** 定时任务ID */
    private Long id;

    /** Bean名称 */
    private String beanName;

    /** cron 表达式 */
    private String cronExpression;

    /** 状态：1暂停、0启用 */
    private Boolean isPause;

    /** 任务名称 */
    private String jobName;

    /** 方法名称 */
    private String methodName;

    /** 参数 */
    private String params;

    /** 备注 */
    private String remark;

    /** 创建日期 */
    private Timestamp createTime;

}
