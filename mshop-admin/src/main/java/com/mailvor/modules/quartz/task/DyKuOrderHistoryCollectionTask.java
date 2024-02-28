package com.mailvor.modules.quartz.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryDyKuParam;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Zheng Jie
 * @date 2022-09-08
 */
@Slf4j
@Component
public class DyKuOrderHistoryCollectionTask extends OrderTask{

    public void run(String paramStr) throws InterruptedException {
        QueryDyKuParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryDyKuParam();
        } else {
            param = JSON.parseObject(paramStr, QueryDyKuParam.class);
        }
        Integer day = param.getDay();
        Integer minutes = param.getMinutes();
        //结束时间
        Date end = new Date();
        Integer totalMinutes  = day*24*60;
        int count = totalMinutes/minutes;
        for(int i = 0; i < count; i++) {
            //开始时间为结束时间加上间隔

            Date start = DateUtil.offsetMinute(end, -minutes);
            param.setStart(start);
            param.setEnd(end);
            log.warn("抖音库历史订单采集 total {} i:{} start:{} end:{} page:{}", count,  i, param.getStart(), param.getEnd(),
                    param.getPage());
            Integer minId = saveDyKu(param);

            //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
            if(minId > param.getPage()) {
                param.setPage(minId);
            } else {
                //结束时间变成之前的开始时间
                param.setPage(1);
                end = start;
            }

            Thread.sleep(100);
        }
    }

}
