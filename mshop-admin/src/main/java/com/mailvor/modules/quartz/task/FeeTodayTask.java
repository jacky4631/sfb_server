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
 * 统计今日预估
 * //30秒执行一次
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class FeeTodayTask {
    @Resource
    private MwUserService userService;

    protected void run(String sizeStr) {
        Integer size = Integer.parseInt(sizeStr);
        //只要用户提交或者自动绑定订单，就会增加积分，就会更新用户的更新时间
        //获取今天有更新的用户，uid从大到小排列,一次100条 执行
        //读取今天有更新的用户
        List<PromUserDto> users = userService.selectIds(Wrappers.<MwUser>lambdaQuery()
                .gt(MwUser::getUpdateTime, DateUtils.getToday()));
        if (users.isEmpty()) {
            return;
        }
        List<Long> uidList = users.stream().map(PromUserDto::getUid).collect(Collectors.toList());
        Collections.reverse(uidList);
        List<List<Long>> partition = Lists.partition(uidList, size);
        if (partition.size() > 0) {
            for (List<Long> list : partition) {
                //获取当日预估
                userService.initFeeLogs(1, list);
            }
        }

    }
}
