package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryDyParam;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Zheng Jie
 * @date 2022-09-08
 */
@Slf4j
@Component
public class DyOrderHistoryCollectionTask extends OrderTask{

    public void run(String paramStr) throws InterruptedException {
        QueryDyParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryDyParam();
        } else {
            param = JSON.parseObject(paramStr, QueryDyParam.class);
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
            param.setStart_time(start.format(FF));
            param.setEnd_time(end.format(FF));
            log.debug("抖音历史订单采集 total {} i:{} start:{} end:{} page:{}", count,  i, param.getStart_time(), param.getEnd_time(),
                    param.getPage());
            boolean hasMore = saveDy(param);

            //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
            if(hasMore) {
                param.setPage(param.getPage()+1);
            } else {
                //结束时间变成之前的开始时间
                param.setPage(1);
                end = start;
            }

            Thread.sleep(100);
        }
    }

}
