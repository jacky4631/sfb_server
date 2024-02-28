package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.domain.MailvorPddOrder;
import com.mailvor.modules.tk.service.MailvorPddOrderService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 淘宝订单绑定用户
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Slf4j
@Component
public class PddOrderBindTask {
    @Resource
    protected MailvorPddOrderService orderService;

    @Resource
    protected MwUserService mwUserService;

    @Resource
    private SuStoreOrderService suStoreOrderService;

    protected void run(String paramStr) {
        //根据customParam绑定
        OrderBindParam param;
        if(StringUtils.isEmpty(paramStr) || "{}".equals(paramStr)) {
            param = new OrderBindParam();
        } else {
            param = JSON.parseObject(paramStr, OrderBindParam.class);
        }
        List<MailvorPddOrder> pddOrders = orderService.getUnbindOrderList(param.getPrior());
        if(pddOrders.isEmpty()) {
            return;
        }

        pddOrders.stream().forEach(pddOrder -> {
            //拼多多授权后customParams格式为{"uid":"55"}，未授权为pid，解析报错
            Long uid = OrderUtil.getPddOrderUser(pddOrder.getCustomParameters());
            if(uid > 0) {
                suStoreOrderService.bindOrder(uid, pddOrder);
            }
        });
    }

    @Data
    public static class OrderBindParam{
        /**
         * 多少时间之前的订单 分钟
         * */
        private Integer prior = 20;

    }

}
