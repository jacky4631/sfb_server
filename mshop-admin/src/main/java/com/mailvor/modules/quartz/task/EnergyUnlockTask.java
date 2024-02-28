package com.mailvor.modules.quartz.task;

import com.mailvor.modules.energy.service.UserEnergyOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 热度订单解锁超红包任务 每天凌晨执行一次
 *
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class EnergyUnlockTask {

    @Resource
    private UserEnergyOrderService energyOrderService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String paramStr) {
        energyOrderService.unlockOrder();

    }

}
