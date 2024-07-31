/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.quartz.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author huangyu
* @date 2020-05-13
*/

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_quartz_job")
public class QuartzJob extends BaseDomain {

    public static final String JOB_KEY = "JOB_KEY";

    /** 定时任务ID */
    @TableId
    private Long id;


    /** Spring Bean名称 */
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



    public void copy(QuartzJob source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
