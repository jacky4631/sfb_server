package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.tk.param.QueryDyParam;
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
public class DyOrderCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryDyParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryDyParam();
        } else {
            param = JSON.parseObject(paramStr, QueryDyParam.class);
        }
        if(StringUtils.isEmpty(param.getStart_time()) || StringUtils.isEmpty(param.getEnd_time())) {
            //2023-04-06 18:36:01
//            LocalDateTime end = LocalDateTime.of(2023, 4, 6, 18, 50, 0);
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start;
            //默认10分钟之前的订单
            if(param.getMinutes() != null) {
                start = end.minusMinutes(param.getMinutes());
            } else {
                start = end.minusMinutes(10);
            }

            param.setEnd_time(end.format(FF));
            param.setStart_time(start.format(FF));
        }

        saveDyOrder(param);
    }

    protected void saveDyOrder(QueryDyParam param) {
        log.debug("抖音订单采集 start:{} end:{} page:{}", param.getStart_time(), param.getEnd_time(), param.getPage());
        boolean hasMore = saveDy(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(hasMore) {
            param.setPage(param.getPage()+1);
            saveDyOrder(param);
        }
    }
}
