/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwSystemUserTask;
import com.mailvor.modules.user.service.dto.TaskDto;
import com.mailvor.modules.user.service.dto.MwSystemUserTaskDto;
import com.mailvor.modules.user.service.dto.MwSystemUserTaskQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwSystemUserTaskService extends BaseService<MwSystemUserTask>{


    /**
     * 获取已经完成的任务数量
     *
     * @param levelId 等级id
     * @param uid     uid
     * @return int
     */
    Long getTaskComplete(int levelId,Long uid);

    /**
     * 获取等级会员任务列表
     * @param levelId 等级id
     * @param uid uid
     * @return TaskDto
     */
    TaskDto getTaskList(int levelId, Long uid);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwSystemUserTaskQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwSystemUserTaskDto>
    */
    List<MwSystemUserTask> queryAll(MwSystemUserTaskQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwSystemUserTaskDto> all, HttpServletResponse response) throws IOException;
}
