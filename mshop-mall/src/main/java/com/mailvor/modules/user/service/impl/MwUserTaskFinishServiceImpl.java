/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;


import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.modules.user.domain.MwUserTaskFinish;
import com.mailvor.modules.user.service.MwUserTaskFinishService;
import com.mailvor.modules.user.service.mapper.MwUserTaskFinishMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 * 用户任务完成记录表 服务实现类
 * </p>
 *
 * @author mazhongjun
 * @since 2019-12-07
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MwUserTaskFinishServiceImpl extends BaseServiceImpl<MwUserTaskFinishMapper, MwUserTaskFinish> implements MwUserTaskFinishService {

    private final MwUserTaskFinishMapper mwUserTaskFinishMapper;


    /**
     * 设置任务完成
     * @param uid uid
     * @param taskId 任务id
     */
    @Override
    public void setFinish(Long uid, int taskId) {
        Long count = this.lambdaQuery()
                .eq(MwUserTaskFinish::getUid,uid)
                .eq(MwUserTaskFinish::getTaskId,taskId)
                .count();
        if(count == 0){
            MwUserTaskFinish userTaskFinish = new MwUserTaskFinish();
            userTaskFinish.setUid(uid);
            userTaskFinish.setTaskId(taskId);
            mwUserTaskFinishMapper.insert(userTaskFinish);
        }
    }



}
