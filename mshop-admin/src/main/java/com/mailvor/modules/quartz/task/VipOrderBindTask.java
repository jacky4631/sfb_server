package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.domain.MailvorVipOrder;
import com.mailvor.modules.tk.service.MailvorVipOrderService;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 唯品会订单绑定用户
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Slf4j
@Component
public class VipOrderBindTask {
    @Resource
    protected MailvorVipOrderService orderService;

    @Resource
    private SuStoreOrderService suStoreOrderService;

    protected void run(String paramStr) {
        OrderBindParam param;
        if(StringUtils.isEmpty(paramStr) || "{}".equals(paramStr)) {
            param = new OrderBindParam();
        } else {
            param = JSON.parseObject(paramStr, OrderBindParam.class);
        }

        //vipOrder.getStatParam即为用户uid
        List<MailvorVipOrder> vipOrders = orderService.getUnbindOrderList(param.getPrior());
        if(vipOrders.isEmpty()) {
            return;
        }

        vipOrders.stream().forEach(vipOrder -> {
            Long uid = OrderUtil.getVipOrderUser(vipOrder.getStatParam());
            if(uid > 0) {
                //绑定vip订单 参照订单找回，
                suStoreOrderService.bindOrder(uid, vipOrder);
            }
        });
    }

    @Data
    public static class OrderBindParam{
        /**
         * 多少秒之前的订单
         * 默认一天
         * */
        private Long prior = 24*60*60L;
    }

}
