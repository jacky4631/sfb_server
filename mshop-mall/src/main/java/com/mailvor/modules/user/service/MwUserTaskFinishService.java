/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;


import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserTaskFinish;

/**
 * <p>
 * 用户任务完成记录表 服务类
 * </p>
 *
 * @author huangyu
 * @since 2019-12-07
 */
public interface MwUserTaskFinishService extends BaseService<MwUserTaskFinish> {

    /**
     * 设置任务完成
     * @param uid uid
     * @param taskId 任务id
     */
    void setFinish(Long uid,int taskId);


}
