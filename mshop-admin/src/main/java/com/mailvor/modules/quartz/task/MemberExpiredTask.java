package com.mailvor.modules.quartz.task;

import com.mailvor.modules.user.service.MwUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 会员过期任务
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class MemberExpiredTask {

    @Resource
    private MwUserService userService;


    protected void run(String param) {
        userService.expiredUser();
    }

}
