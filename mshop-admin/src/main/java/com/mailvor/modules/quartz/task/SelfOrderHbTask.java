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
import java.util.*;
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
//        Integer day = jsonObject.getInteger("day");
        Integer limit = jsonObject.getInteger("limit");
        //找到尚未拆红包，并且已经超过解锁时间的订单
        //todo 每次找到10条 已经绑定用户 未拆红包的 bind=0  并且已经超过默认解锁天数3天的订单

        HbUnlockConfig unlockConfig = systemConfigService.getHbUnlockConfig();
        List<Long> uidList = new ArrayList<>();

        //获取所有订单
        List<MailvorTbOrder> tbOrders = tbOrderService.getSelfUnspreadHbList(limit);
        if(!tbOrders.isEmpty()) {
            uidList.addAll(tbOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorJdOrder> jdOrders = jdOrderService.getSelfUnspreadHbList(limit);
        if(!jdOrders.isEmpty()) {
            uidList.addAll(jdOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorPddOrder> pddOrders = pddOrderService.getSelfUnspreadHbList(limit);
        if(!pddOrders.isEmpty()) {
            uidList.addAll(pddOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorDyOrder> dyOrders = dyOrderService.getSelfUnspreadHbList(limit);
        if(!dyOrders.isEmpty()) {
            uidList.addAll(dyOrders.stream().map(order -> order.getUid()).collect(toList()));
        }

        List<MailvorVipOrder> vipOrders = vipOrderService.getSelfUnspreadHbList(limit);
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
                Date unlockTime = suStoreOrderService.checkSelfOrder(tbOrder, userRefundDto, unlockConfig);
                if(unlockTime != null) {
                    suStoreOrderService.incMoneyAndBindOrder(tbOrder.getUid(), tbOrder, unlockTime);
                }

            }
        }
        if(!jdOrders.isEmpty()) {
            for(MailvorJdOrder jdOrder : jdOrders) {
                UserRefundDto userRefundDto = userMap.get(jdOrder.getUid());
                Date unlockTime = suStoreOrderService.checkSelfOrder(jdOrder, userRefundDto, unlockConfig);
                if(unlockTime != null) {
                    suStoreOrderService.incMoneyAndBindOrder(jdOrder.getUid(), jdOrder, unlockTime);
                }

            }
        }
        if(!pddOrders.isEmpty()) {
            for(MailvorPddOrder pddOrder : pddOrders) {
                UserRefundDto userRefundDto = userMap.get(pddOrder.getUid());
                Date unlockTime = suStoreOrderService.checkSelfOrder(pddOrder, userRefundDto, unlockConfig);
                if(unlockTime != null) {
                    suStoreOrderService.incMoneyAndBindOrder(pddOrder.getUid(), pddOrder, unlockTime);
                }

            }
        }

        if(!dyOrders.isEmpty()) {
            for(MailvorDyOrder dyOrder : dyOrders) {
                UserRefundDto userRefundDto = userMap.get(dyOrder.getUid());
                Date unlockTime = suStoreOrderService.checkSelfOrder(dyOrder, userRefundDto, unlockConfig);
                if(unlockTime != null) {
                    suStoreOrderService.incMoneyAndBindOrder(dyOrder.getUid(), dyOrder, unlockTime);
                }

            }
        }

        if(!vipOrders.isEmpty()) {
            for(MailvorVipOrder vipOrder : vipOrders) {
                UserRefundDto userRefundDto = userMap.get(vipOrder.getUid());
                Date unlockTime = suStoreOrderService.checkSelfOrder(vipOrder, userRefundDto, unlockConfig);
                if(unlockTime != null) {
                    suStoreOrderService.incMoneyAndBindOrder(vipOrder.getUid(), vipOrder, unlockTime);
                }

            }
        }
    }



}
