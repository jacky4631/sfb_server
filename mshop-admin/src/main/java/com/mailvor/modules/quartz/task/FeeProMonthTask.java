package com.mailvor.modules.quartz.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.dto.PromUserDto;
import com.mailvor.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计上月预估
 * 每个月1号执行一次
 * 0 5 0 1 * ? 每月的1日的凌晨0点05分执行任务
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class FeeProMonthTask {
    @Resource
    private MwUserService userService;

    protected void run(String sizeStr) {

        Integer size = Integer.parseInt(sizeStr);
        List<PromUserDto> users = userService.selectIds(Wrappers.<MwUser>lambdaQuery()
                .gt(MwUser::getUpdateTime, DateUtils.getProMonth()));
        if (users.isEmpty()) {
            return;
        }
        List<Long> uidList = users.stream().map(PromUserDto::getUid).collect(Collectors.toList());
        Collections.reverse(uidList);
        List<List<Long>> partition = Lists.partition(uidList, size);
        if (partition.size() > 0) {
            for (List<Long> list : partition) {
                //获取当日预估
                userService.initFeeLogs(4, list);
            }
        }
    }
}
