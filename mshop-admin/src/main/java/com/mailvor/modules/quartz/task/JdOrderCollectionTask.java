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
public class JdOrderCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryJdParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryJdParam();
        } else {
            param = JSON.parseObject(paramStr, QueryJdParam.class);
        }
        if(StringUtils.isEmpty(param.getStartTime()) || StringUtils.isEmpty(param.getEndTime())) {
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
        if(param.getType() == null) {
            param.setType(1);
        }
        saveJdOrder(param);
    }

    protected void saveJdOrder(QueryJdParam param) {
        log.debug("京东订单采集 start:{} end:{} page:{}", param.getStartTime(), param.getEndTime(), param.getPageNo());
        Integer hasMore = saveJd(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(hasMore == 1) {
            param.setPageNo(param.getPageNo()+1);
            saveJdOrder(param);
        }
    }
}
