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
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
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

    @Resource
    private MwUserExtraService userExtraService;

    @Resource
    private MwUserService userService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String paramStr) {
        //找到尚未拆红包，并且已经超过解锁时间的订单
        List<UserEnergyOrder> energyOrders = energyOrderService.getUnpackEnergyList();
        if (energyOrders.isEmpty()) {
            return;
        }
        List<Long> logList = energyOrders.stream().map(UserEnergyOrder::getLogId).distinct().collect(toList());
//        //这里查找体验订单和赠送订单
        List<UserEnergyOrderLog> orderLogs = orderLogService.listByIds(logList)
                .stream().filter(log -> log.getType()!=1).collect(toList());
//
        Map<Long, UserEnergyOrderLog> logMap = orderLogs
                .stream().collect(Collectors.toMap(UserEnergyOrderLog::getId, Function.identity()));
//
//        List<Long> uidList = orderLogs.stream().map(log -> log.getUid()).distinct().collect(toList());
//        Map<Long, MwUserExtra> userExtraMap = new HashMap<>();
//        Map<Long, MwUser> userMap = new HashMap<>();
//        if(!uidList.isEmpty()) {
//            userExtraMap = userExtraService.listByIds(uidList)
//                    .stream().collect(Collectors.toMap(MwUserExtra::getUid, Function.identity()));
//            userMap = userService.listByIds(uidList)
//                    .stream().collect(Collectors.toMap(MwUser::getUid, Function.identity()));
//        }

        for (UserEnergyOrder energyOrder : energyOrders) {
            String platform = energyOrder.getPlatform();
            UserEnergyOrderLog log = logMap.get(energyOrder.getLogId());
//            //只要找到log 说明是体验订单
//            if(log != null) {
//                //只校验体验订单 这里校验用户是否是加盟会员或者体验会员 是就拆红包 都不是设置订单锁住状态 今天不拆红包
//                MwUser user = userMap.get(energyOrder.getUid());
//                MwUserExtra userExtra = userExtraMap.get(energyOrder.getUid());
//                if(TkUtil.getLevel(platform, user) != 5 && TkUtil.getLevel(platform, userExtra) != 5) {
//                    //都不是锁住订单，今天不再做拆红包校验，防止一直查找到，后续的订单无法拆开
//                    energyOrder.setIsLock(1);
//                    energyOrderService.updateById(energyOrder);
//                    continue;
//                }
//            }
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
