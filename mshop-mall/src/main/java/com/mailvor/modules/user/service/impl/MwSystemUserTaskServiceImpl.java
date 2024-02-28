/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.order.service.mapper.StoreOrderMapper;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.user.domain.MwSystemUserTask;
import com.mailvor.modules.user.domain.MwUserTaskFinish;
import com.mailvor.modules.user.service.MwSystemUserLevelService;
import com.mailvor.modules.user.service.MwSystemUserTaskService;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.MwUserTaskFinishService;
import com.mailvor.modules.user.service.dto.TaskDto;
import com.mailvor.modules.user.service.dto.MwSystemUserTaskDto;
import com.mailvor.modules.user.service.dto.MwSystemUserTaskQueryCriteria;
import com.mailvor.modules.user.service.mapper.SystemUserTaskMapper;
import com.mailvor.modules.user.service.mapper.UserBillMapper;
import com.mailvor.modules.user.service.mapper.MwUserTaskFinishMapper;
import com.mailvor.modules.user.vo.MwSystemUserTaskQueryVo;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwSystemUserTaskServiceImpl extends BaseServiceImpl<SystemUserTaskMapper, MwSystemUserTask> implements MwSystemUserTaskService {

    @Autowired
    private IGenerator generator;
    @Autowired
    private MwSystemUserLevelService systemUserLevelService;

    @Autowired
    private SystemUserTaskMapper mwSystemUserTaskMapper;
    @Autowired
    private MwUserTaskFinishMapper mwUserTaskFinishMapper;

    @Autowired
    private UserBillMapper userBillMapper;
    @Autowired
    private StoreOrderMapper storeOrderMapper;

    @Autowired
    private MwUserTaskFinishService userTaskFinishService;
    @Autowired
    private MwUserBillService userBillService;





    /**
     * 获取已经完成的任务数量
     *
     * @param levelId 等级id
     * @param uid     uid
     * @return int
     */
    @Override
    public Long getTaskComplete(int levelId, Long uid) {
        List<MwSystemUserTask> list = this.lambdaQuery()
                .eq(MwSystemUserTask::getLevelId,levelId)
                .eq(MwSystemUserTask::getIsShow,ShopCommonEnum.SHOW_1.getValue())
                .list();
        List<Integer> taskIds = list.stream().map(MwSystemUserTask::getId)
                .collect(Collectors.toList());
        if(taskIds.isEmpty()) {
            return 0L;
        }

        Long count = mwUserTaskFinishMapper.selectCount(Wrappers.<MwUserTaskFinish>lambdaQuery()
                .in(MwUserTaskFinish::getTaskId,taskIds)
                .eq(MwUserTaskFinish::getUid,uid));
        return count;
    }

    /**
     * 获取等级会员任务列表
     * @param levelId 等级id
     * @param uid uid
     * @return TaskDto
     */
    @Override
    public TaskDto getTaskList(int levelId, Long uid) {
       LambdaQueryWrapper<MwSystemUserTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwSystemUserTask::getLevelId,levelId)
                .eq(MwSystemUserTask::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .orderByDesc(MwSystemUserTask::getSort);
        List<MwSystemUserTaskQueryVo> list = generator.convert(mwSystemUserTaskMapper
                .selectList(wrapper), MwSystemUserTaskQueryVo.class);

        TaskDto taskDTO = new TaskDto();
        taskDTO.setList(list);
        taskDTO.setReachCount(this.getTaskComplete(levelId,uid));
        taskDTO.setTask(this.tidyTask(list,uid));

        return taskDTO;
    }


    /**
     * 设置任务内容完成情况
     * @param task 任务列表
     * @param uid uid
     * @return list
     */
    private List<MwSystemUserTaskQueryVo> tidyTask(List<MwSystemUserTaskQueryVo> task, Long uid) {
        for (MwSystemUserTaskQueryVo taskQueryVo : task) {
            Long count = userTaskFinishService.lambdaQuery()
                    .eq(MwUserTaskFinish::getTaskId,taskQueryVo.getId())
                    .eq(MwUserTaskFinish::getUid,uid)
                    .count();
            if(count > 0){
                taskQueryVo.setNewNumber(taskQueryVo.getNumber());
                taskQueryVo.setSpeed(100); //完成比例
                taskQueryVo.setFinish(ShopCommonEnum.IS_FINISH_1.getValue());
                taskQueryVo.setTaskTypeTitle("");
            }else{
                double sumNumber = 0d;
                String title = "";
                switch (taskQueryVo.getTaskType()){
                    case "SatisfactionIntegral":
                        sumNumber = userBillMapper.sumIntegral(uid);
                        title = "还需要{0}经验";
                        break;
                    case "ConsumptionAmount":
                        sumNumber = storeOrderMapper.sumPrice(uid);
                        title = "还需消费{0}元";
                        break;
                    case "CumulativeAttendance":
                        sumNumber = userBillService.cumulativeAttendance(uid);
                        title = "还需签到{0}天";
                        break;
                    default:
                }

                if(sumNumber >= taskQueryVo.getNumber()){
                    userTaskFinishService.setFinish(uid,taskQueryVo.getId());
                    taskQueryVo.setFinish(ShopCommonEnum.IS_FINISH_1.getValue());
                    taskQueryVo.setSpeed(100);
                    taskQueryVo.setTaskTypeTitle("");
                    taskQueryVo.setNewNumber(taskQueryVo.getNumber());
                }else{
                    double numdata = NumberUtil.sub(taskQueryVo.getNumber().doubleValue(),sumNumber);
                    taskQueryVo.setTaskTypeTitle(MessageFormat.format(title,numdata));
                    double speed = NumberUtil.div(sumNumber,taskQueryVo.getNumber().doubleValue());
                    taskQueryVo.setSpeed(Double.valueOf(NumberUtil.mul(speed,100)).intValue());
                    taskQueryVo.setFinish(ShopCommonEnum.IS_FINISH_0.getValue());
                    taskQueryVo.setNewNumber(Double.valueOf(sumNumber).intValue());
                }
            }
        }

        return task;
    }


    //==========================================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwSystemUserTaskQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemUserTask> page = new PageInfo<>(queryAll(criteria));
        List<MwSystemUserTaskDto> systemUserTaskDTOS = generator.convert(page.getList(), MwSystemUserTaskDto.class);
        for (MwSystemUserTaskDto systemUserTaskDTO : systemUserTaskDTOS) {
            MwSystemUserLevel userLevel=systemUserLevelService.getById(systemUserTaskDTO.getLevelId());
            if(userLevel == null) {
                continue;
            }
            systemUserTaskDTO.setLevalName(userLevel.getName());
        }
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", systemUserTaskDTOS);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwSystemUserTask> queryAll(MwSystemUserTaskQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwSystemUserTask.class, criteria));
    }


    @Override
    public void download(List<MwSystemUserTaskDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwSystemUserTaskDto mwSystemUserTask : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("任务名称", mwSystemUserTask.getName());
            map.put("配置原名", mwSystemUserTask.getRealName());
            map.put("任务类型", mwSystemUserTask.getTaskType());
            map.put("限定数", mwSystemUserTask.getNumber());
            map.put("等级id", mwSystemUserTask.getLevelId());
            map.put("排序", mwSystemUserTask.getSort());
            map.put("是否显示", mwSystemUserTask.getIsShow());
            map.put("是否务必达成任务,1务必达成,0=满足其一", mwSystemUserTask.getIsMust());
            map.put("任务说明", mwSystemUserTask.getIllustrate());
            map.put("新增时间", mwSystemUserTask.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
