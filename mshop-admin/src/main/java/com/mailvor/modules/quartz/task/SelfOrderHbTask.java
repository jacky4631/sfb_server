package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.order.service.dto.UserRefundDto;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.user.config.HbUnlockConfig;
import com.mailvor.modules.user.service.MwUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 自购拆红包任务
 *
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class SelfOrderHbTask {
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
    private SuStoreOrderService suStoreOrderService;

    @Resource
    private MwUserService userService;

    @Resource
    private MwSystemConfigService systemConfigService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String paramStr) throws InterruptedException {

        JSONObject jsonObject = JSON.parseObject(paramStr);
        Integer day = jsonObject.getInteger("day");
        Integer limit = jsonObject.getInteger("limit");
        //找到尚未拆红包，并且已经超过解锁时间的订单
        //todo 每次找到10条 已经绑定用户 未拆红包的 bind=0  并且已经超过默认解锁天数3天的订单

        HbUnlockConfig unlockConfig = systemConfigService.getHbUnlockConfig();
        //普通用户解锁+3天
        Integer unlockDay = unlockConfig.getUnlock() + day;
        List<Long> uidList = new ArrayList<>();

        //获取所有订单
        List<MailvorTbOrder> tbOrders = tbOrderService.getSelfUnspreadHbList(unlockDay,limit);
        if(!tbOrders.isEmpty()) {
            uidList.addAll(tbOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorJdOrder> jdOrders = jdOrderService.getSelfUnspreadHbList(unlockDay,limit);
        if(!jdOrders.isEmpty()) {
            uidList.addAll(jdOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorPddOrder> pddOrders = pddOrderService.getSelfUnspreadHbList(unlockDay,limit);
        if(!pddOrders.isEmpty()) {
            uidList.addAll(pddOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorDyOrder> dyOrders = dyOrderService.getSelfUnspreadHbList(unlockDay,limit);
        if(!dyOrders.isEmpty()) {
            uidList.addAll(dyOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorVipOrder> vipOrders = vipOrderService.getSelfUnspreadHbList(unlockDay,limit);
        if(!vipOrders.isEmpty()) {
            uidList.addAll(vipOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        if(uidList.isEmpty()) {
            return;
        }
        uidList = uidList.stream().distinct().filter(Objects::nonNull).collect(toList());

        List<UserRefundDto> userDTOS = userService.getUserRefunds(uidList);
        Map<Long, UserRefundDto> userMap = userDTOS.stream().collect(Collectors.toMap(UserRefundDto::getUid, Function.identity()));
        if(!tbOrders.isEmpty()) {
            for(MailvorTbOrder tbOrder : tbOrders) {
                UserRefundDto userRefundDto = userMap.get(tbOrder.getUid());
                boolean canUnlock = suStoreOrderService.checkSelfOrder(tbOrder, userRefundDto, unlockConfig);
                if(canUnlock) {
                    suStoreOrderService.incMoneyAndBindOrder(tbOrder.getUid(), tbOrder);
                }

            }
        }
        if(!jdOrders.isEmpty()) {
            for(MailvorJdOrder jdOrder : jdOrders) {
                UserRefundDto userRefundDto = userMap.get(jdOrder.getUid());
                boolean canUnlock = suStoreOrderService.checkSelfOrder(jdOrder, userRefundDto, unlockConfig);
                if(canUnlock) {
                    suStoreOrderService.incMoneyAndBindOrder(jdOrder.getUid(), jdOrder);
                }

            }
        }
        if(!pddOrders.isEmpty()) {
            for(MailvorPddOrder pddOrder : pddOrders) {
                UserRefundDto userRefundDto = userMap.get(pddOrder.getUid());
                boolean canUnlock = suStoreOrderService.checkSelfOrder(pddOrder, userRefundDto, unlockConfig);
                if(canUnlock) {
                    suStoreOrderService.incMoneyAndBindOrder(pddOrder.getUid(), pddOrder);
                }

            }
        }

        if(!dyOrders.isEmpty()) {
            for(MailvorDyOrder dyOrder : dyOrders) {
                UserRefundDto userRefundDto = userMap.get(dyOrder.getUid());
                boolean canUnlock = suStoreOrderService.checkSelfOrder(dyOrder, userRefundDto, unlockConfig);
                if(canUnlock) {
                    suStoreOrderService.incMoneyAndBindOrder(dyOrder.getUid(), dyOrder);
                }

            }
        }

        if(!vipOrders.isEmpty()) {
            for(MailvorVipOrder vipOrder : vipOrders) {
                UserRefundDto userRefundDto = userMap.get(vipOrder.getUid());
                boolean canUnlock = suStoreOrderService.checkSelfOrder(vipOrder, userRefundDto, unlockConfig);
                if(canUnlock) {
                    suStoreOrderService.incMoneyAndBindOrder(vipOrder.getUid(), vipOrder);
                }

            }
        }
    }



}
