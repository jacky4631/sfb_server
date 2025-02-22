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
public class OrderMtCPSCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryMtParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryMtParam();
        } else {
            param = JSON.parseObject(paramStr, QueryMtParam.class);
        }
        if(param.getStartTime()==null || param.getEndTime()==null) {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start;
            //默认10分钟之前的订单
            if(param.getMinutes() != null) {
                start = end.minusMinutes(param.getMinutes());
            } else {
                start = end.minusMinutes(10);
            }

            param.setEndTime(end);
            param.setStartTime(start);
        }
        saveMtOrder(param);

    }

    protected void saveMtOrder(QueryMtParam param) {
        log.warn("美团CPS订单采集 page:{} size: {} start:{} end:{}", param.getScrollId(), param.getSize(),
                param.getStartTime(), param.getEndTime());

        String lastOrderId = saveMtCPS(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(StringUtils.isNotBlank(lastOrderId)) {
            param.setScrollId(lastOrderId);
            saveMtOrder(param);
        }
    }
}
