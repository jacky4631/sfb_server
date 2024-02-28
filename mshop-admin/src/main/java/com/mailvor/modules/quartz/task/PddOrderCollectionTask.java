package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryPddParam;
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
public class PddOrderCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryPddParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryPddParam();
        } else {
            param = JSON.parseObject(paramStr, QueryPddParam.class);
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
        savePddOrder(param);

    }

    protected void savePddOrder(QueryPddParam param) {
        log.warn("拼多多订单采集 start:{} end:{} lastOrderId:{}", param.getStartTime(), param.getEndTime(), param.getLastOrderId());

        String lastOrderId = savePdd(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(lastOrderId != null) {
            param.setLastOrderId(lastOrderId);
            savePddOrder(param);
        }
    }
}
