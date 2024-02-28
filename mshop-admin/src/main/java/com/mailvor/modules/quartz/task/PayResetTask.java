package com.mailvor.modules.quartz.task;

import com.mailvor.modules.pay.service.MwPayChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付通道额度重置任务
 *
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class PayResetTask {
    @Resource
    private MwPayChannelService payChannelService;
    protected void run(String paramStr) {
        payChannelService.reset();
    }

}
