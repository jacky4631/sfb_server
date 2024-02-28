package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryMtRefundParam;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Slf4j
@Component
public class OrderMtRefundCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryMtRefundParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryMtRefundParam();
        } else {
            param = JSON.parseObject(paramStr, QueryMtRefundParam.class);
        }
        if(StringUtils.isEmpty(param.getStartTime()) || StringUtils.isEmpty(param.getStartTime())) {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start;
            //默认10分钟之前的订单
            if(param.getMinutes() != null) {
                start = end.minusMinutes(param.getMinutes());
            } else {
                start = end.minusMinutes(10);
            }

            param.setEndTime(end.format(FF));
            param.setStartTime(start.format(FF));
        }
        saveMtOrder(param);

    }

    protected void saveMtOrder(QueryMtRefundParam param) {
        log.warn("美团CPS订单采集 page:{} size: {} start:{} end:{}", param.getPage(), param.getSize(),
                param.getStartTime(), param.getEndTime());

        String lastOrderId = saveMtRefund(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(lastOrderId != null && !"end".equals(lastOrderId)) {
            param.setPage(param.getPage()+1);
            saveMtOrder(param);
        }
    }
}
