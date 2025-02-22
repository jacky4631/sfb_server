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
 * @date 2018-12-25
 */
@Slf4j
@Component
public class DyKuOrderCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryDyKuParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryDyKuParam();
        } else {
            param = JSON.parseObject(paramStr, QueryDyKuParam.class);
        }
        if(param.getStart() == null || param.getEnd() == null) {
            //2023-04-06 18:36:01
//            LocalDateTime end = LocalDateTime.of(2023, 4, 6, 18, 50, 0);
            Date end = new Date();
            Date start;
            //默认10分钟之前的订单
            if(param.getMinutes() != null) {
                start = DateUtil.offsetMinute(end, -param.getMinutes());
            } else {
                start = DateUtil.offsetMinute(end, -10);
            }

            param.setEnd(end);
            param.setStart(start);
        }

        saveDyKuOrder(param);
    }

    protected void saveDyKuOrder(QueryDyKuParam param) {
        log.warn("抖音库订单采集 start:{} end:{} page:{}", param.getStart(), param.getEnd(), param.getPage());
        Integer minId = saveDyKu(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(minId > param.getPage()) {
            param.setPage(minId);
            saveDyKuOrder(param);
        }
    }
}
