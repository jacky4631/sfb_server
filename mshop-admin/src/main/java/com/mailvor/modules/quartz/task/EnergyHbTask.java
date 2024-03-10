package com.mailvor.modules.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.modules.energy.domain.UserEnergyOrder;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.energy.service.UserEnergyOrderService;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import com.mailvor.modules.tk.domain.TkOrder;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 热度订单拆红包任务
 *
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class EnergyHbTask {

    @Resource
    protected MailvorTbOrderService tbOrderService;
    @Resource
    protected MailvorJdOrderService jdOrderService;
    @Resource
    protected MailvorPddOrderService pddOrderService;
    @Resource
    protected MailvorDyOrderService dyOrderService;
    @Resource
    protected MailvorVipOrderService vipOrderService;

    @Resource
    private UserEnergyOrderService energyOrderService;

    @Resource
    private SuStoreOrderService suStoreOrderService;

    @Resource
    private UserEnergyOrderLogService orderLogService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String paramStr) {
        //找到尚未拆红包，并且已经超过解锁时间的订单
        List<UserEnergyOrder> energyOrders = energyOrderService.getUnpackEnergyList();
        if (energyOrders.isEmpty()) {
            return;
        }
        List<Long> logList = energyOrders.stream().map(UserEnergyOrder::getLogId).distinct().collect(toList());
//        //这里查找赠送订单
        List<UserEnergyOrderLog> orderLogs = orderLogService.listByIds(logList)
                .stream().filter(log -> log.getType()==0).collect(toList());

        Map<Long, UserEnergyOrderLog> logMap = orderLogs
                .stream().collect(Collectors.toMap(UserEnergyOrderLog::getId, Function.identity()));

        for (UserEnergyOrder energyOrder : energyOrders) {
            String platform = energyOrder.getPlatform();
            UserEnergyOrderLog log = logMap.get(energyOrder.getLogId());
            String orderId = energyOrder.getOrderId();
            //设置已拆红包
            energyOrder.setHb(1);
            energyOrderService.updateById(energyOrder);
            if (StringUtils.isBlank(orderId)) {
                continue;
            }
            Long uid = energyOrder.getUid();

            TkOrder tkOrder = null;
            //订单校验
            switch (platform) {
                case "tb":
                    tkOrder = tbOrderService.getById(orderId);
                    break;
                case "jd":
                    LambdaQueryWrapper<MailvorJdOrder> wrapperO = new LambdaQueryWrapper<>();
                    wrapperO.eq(MailvorJdOrder::getOrderId, orderId).last("limit 1");
                    tkOrder = jdOrderService.getOne(wrapperO);
                    break;
                case "pdd":
                    tkOrder = pddOrderService.getById(orderId);
                    break;
                case "vip":
                    tkOrder = vipOrderService.getById(orderId);
                    break;
                case "dy":
                    tkOrder = dyOrderService.getById(orderId);
                    break;

            }
            if (tkOrder == null || tkOrder.getBind() != 0) {
                continue;
            }
            suStoreOrderService.incEnergyMoneyAndBindOrder(uid, tkOrder, energyOrder, log);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
