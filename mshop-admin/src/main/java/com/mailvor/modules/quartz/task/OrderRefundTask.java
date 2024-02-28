package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.tk.service.*;
import com.mailvor.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 已绑定订单失效后扣除积分
 * @author Zheng Jie
 * @date 2022-10-04
 */
@Slf4j
@Component
public class OrderRefundTask {
    @Resource
    protected MailvorTbOrderService orderService;
    @Resource
    protected MailvorJdOrderService jdOrderService;
    @Resource
    protected MailvorPddOrderService pddOrderService;
    @Resource
    protected MailvorVipOrderService vipOrderService;
    @Resource
    protected MailvorDyOrderService dyOrderService;

    @Resource
    private SuStoreOrderService suStoreOrderService;

    protected void run(String paramStr) {
        //淘宝 京东 拼多多 抖音 唯品会都需执行
        //淘宝 查找30天内 bind=1 并且tkStatus=13的订单，扣除奖励红包， 积分记录, 订单bind改为2
        //todo 其他平台暂未验证
        OrderRefundParam param;
        if(StringUtils.isEmpty(paramStr) || "{}".equals(paramStr)) {
            param = new OrderRefundParam();
        } else {
            param = JSON.parseObject(paramStr, OrderRefundParam.class);
        }
        List<MailvorTbOrder> tbOrders = orderService.getRefundAndBindOrderList(param.getPrior());

        tbOrders.stream().forEach(tbOrder -> {
            if(tbOrder.getHb() == null) {
                return;
            }
            suStoreOrderService.decHbAndUnbindOrder(tbOrder.getUid(), tbOrder);
        });

        List<MailvorJdOrder> jdOrders = jdOrderService.getRefundAndBindOrderList(param.getPrior());
        jdOrders.stream().forEach(jdOrder -> {
            if(jdOrder.getHb() == null) {
                return;
            }
            suStoreOrderService.decHbAndUnbindOrder(jdOrder.getUid(), jdOrder);
        });

        List<MailvorPddOrder> pddOrders = pddOrderService.getRefundAndBindOrderList(param.getPrior());
        pddOrders.stream().forEach(pddOrder -> {
            if(pddOrder.getHb() == null) {
                return;
            }
            suStoreOrderService.decHbAndUnbindOrder(pddOrder.getUid(), pddOrder);
        });

        List<MailvorDyOrder> dyOrders = dyOrderService.getRefundAndBindOrderList(param.getPrior());
        dyOrders.stream().forEach(dyOrder -> {
            if(dyOrder.getHb() == null) {
                return;
            }
            suStoreOrderService.decHbAndUnbindOrder(dyOrder.getUid(), dyOrder);
        });

        List<MailvorVipOrder> vipOrders = vipOrderService.getRefundAndBindOrderList(param.getPrior());
        vipOrders.stream().forEach(vipOrder -> {
            if(vipOrder.getHb() == null) {
                return;
            }
            suStoreOrderService.decHbAndUnbindOrder(vipOrder.getUid(), vipOrder);
        });
    }

    @Data
    public static class OrderRefundParam {
        /**
         * 多少时间之前的订单 天
         */
        private Integer prior = 30;

    }

}
