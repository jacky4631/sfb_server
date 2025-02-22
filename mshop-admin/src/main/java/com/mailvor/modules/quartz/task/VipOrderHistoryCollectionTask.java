package com.mailvor.modules.quartz.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryVipParam;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Slf4j
@Component
public class VipOrderHistoryCollectionTask extends OrderTask{

    public void run(String paramStr) throws InterruptedException {
        QueryVipParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryVipParam();
        } else {
            param = JSON.parseObject(paramStr, QueryVipParam.class);
        }
        Integer day = param.getDay();
        Integer minutes = param.getMinutes();
        //结束时间
        long end = System.currentTimeMillis();
        Integer totalMinutes  = day*24*60;
        int count = totalMinutes/minutes;
        for(int i = 0; i < count; i++) {
            //开始时间为结束时间加上间隔
            long start = end - minutes*60*1000;
            param.setOrderTimeStart(start+"");
            param.setOrderTimeEnd(end+"");
            log.warn("唯品会历史订单采集 total {} i:{} start:{} end:{} page:{}", count,  i,
                    DateUtil.date(start).toString("yyyy-MM-dd HH:mm:ss"),
                    DateUtil.date(end).toString("yyyy-MM-dd HH:mm:ss"), param.getPage());
            boolean hasMore = saveVip(param);

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
