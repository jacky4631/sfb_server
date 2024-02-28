package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryJdParam;
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
public class JdOrderHistoryCollectionTask extends OrderTask{

    public void run(String paramStr) throws InterruptedException {
        QueryJdParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryJdParam();
        } else {
            param = JSON.parseObject(paramStr, QueryJdParam.class);
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
            log.warn("京东历史订单采集 total {} i:{} start:{} end:{} pageNo:{}", count,  i,
                    param.getStartTime(), param.getEndTime(), param.getPageNo());
            Integer hasMore = saveJd(param);

            //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
            if(hasMore == 1) {
                param.setPageNo(param.getPageNo()+1);
            } else {
                //结束时间变成之前的开始时间
                param.setPageNo(1);
                end = start;
            }
            Thread.sleep(100);
        }
    }

}
