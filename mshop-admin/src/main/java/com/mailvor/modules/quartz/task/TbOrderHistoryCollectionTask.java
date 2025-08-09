package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryTBParam;
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
public class TbOrderHistoryCollectionTask extends OrderTask{

    public void run(String paramStr) throws InterruptedException {
        QueryTBParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryTBParam();
        } else {
            param = JSON.parseObject(paramStr, QueryTBParam.class);
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
            param.setStartTime(start.format(FF));
            param.setEndTime(end.format(FF));
            log.debug("淘宝历史订单采集 total {} i:{} start:{} end:{} positionIndex:{}", count,  i, start, end, param.getPositionIndex());
            String position = saveTb(param);
            if(position != null) {
                param.setPositionIndex(position);
            } else {
                param.setPositionIndex("");
                //结束时间变成之前的开始时间
                end = start;
            }

            Thread.sleep(100);
        }
    }

}
