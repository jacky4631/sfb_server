package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import com.mailvor.modules.tk.service.MailvorJdOrderService;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 京东订单绑定用户
 * @author Zheng Jie
 * @date 2022-10-25
 */
@Slf4j
@Component
public class JdOrderBindTask {
    @Resource
    protected MailvorJdOrderService orderService;

    @Resource
    private SuStoreOrderService suStoreOrderService;

    protected void run(String paramStr) {
        //pid绑定 优先查找订单pid绑定用户
        OrderBindParam param;
        if(StringUtils.isEmpty(paramStr) || "{}".equals(paramStr)) {
            param = new OrderBindParam();
        } else {
            param = JSON.parseObject(paramStr, OrderBindParam.class);
        }
        List<MailvorJdOrder> jdOrders = orderService.getUnbindOrderList(param.getPrior());
        if(jdOrders.isEmpty()) {
            return;
        }

        jdOrders.stream().forEach(jdOrder -> {
            //pid绑定 getPositionId即为用户id
            Long uid = OrderUtil.getJdOrderUser(jdOrder.getSubUnionId());
            if(uid > 0) {
                suStoreOrderService.bindOrder(uid, jdOrder);
            }
        });
    }

    @Data
    public static class OrderBindParam{
        /**
         * 多少时间之前的订单 分钟
         * */
        private Integer prior = 600;
    }

}
