package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryMtParam;
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
public class OrderMtCPSHistoryCollectionTask extends OrderTask{

    public void run(String paramStr) throws InterruptedException {
        QueryMtParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryMtParam();
        } else {
            param = JSON.parseObject(paramStr, QueryMtParam.class);
        }
        Integer day = param.getDay();
        Integer minutes = param.getMinutes();
        //结束时间
        LocalDateTime end = LocalDateTime.now();
        Integer totalMinutes  = day*24*60;
        int count = totalMinutes/minutes;
        for(int i = 0; i < count; i++) {
            //开始时间为结束时间加上间隔
            LocalDateTime start = end.minusMinutes(minutes);
            log.debug("美团CPS历史订单采集 total {} i:{} start:{} end:{}", count,  i,
                    start.format(FF), end.format(FF));
            param.setStartTime(start);
            param.setEndTime(end);
            //保存订单
            saveMtOrder(param);

//            param.setScrollId(1);
            //结束时间变成之前的开始时间
            end = start;

            Thread.sleep(1000);
        }
    }
    protected void saveMtOrder(QueryMtParam param) {
        log.debug("美团CPS订单采集 page:{} size: {} start:{} end:{}", param.getScrollId(), param.getSize(),
                param.getStartTime(), param.getEndTime());

        String lastOrderId = saveMtCPS(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(StringUtils.isNotBlank(lastOrderId)) {
            param.setScrollId(lastOrderId);
            saveMtOrder(param);
        }
    }
}
