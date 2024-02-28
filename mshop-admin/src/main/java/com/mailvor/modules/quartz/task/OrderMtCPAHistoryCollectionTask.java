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
public class OrderMtCPAHistoryCollectionTask extends OrderTask{

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
        LocalDateTime end = LocalDateTime.parse("2024-01-10 15:19:30", FF);
//        LocalDateTime.now();
        Integer totalMinutes  = day*24*60;
        int count = totalMinutes/minutes;
        for(int i = 0; i < count; i++) {
            //开始时间为结束时间加上间隔
            LocalDateTime start = end.minusMinutes(minutes);
            log.warn("美团CPA历史订单采集 total {} i:{} start:{} end:{}", count,  i,
                    start.format(FF), end.format(FF));
            param.setStartTime(start.format(FF));
            param.setEndTime(end.format(FF));
            //保存订单
            saveMtOrder(param);

            param.setPage(1);
            //结束时间变成之前的开始时间
            end = start;

            Thread.sleep(1000);
        }
    }

    protected void saveMtOrder(QueryMtParam param) {
        log.warn("美团CPA订单采集 page:{} size: {} start:{} end:{}", param.getPage(), param.getSize(),
                param.getStartTime(), param.getEndTime());

        String lastOrderId = saveMtCPA(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(lastOrderId != null && !"end".equals(lastOrderId)) {
            param.setPage(param.getPage()+1);
            saveMtOrder(param);
        }
    }
}
