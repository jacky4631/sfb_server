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
public class TbOrderCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryTBParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryTBParam();
        } else {
            param = JSON.parseObject(paramStr, QueryTBParam.class);
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
        if(param.getQueryType() == null) {
            param.setQueryType(1);
        }
        saveTbOrder(param);

//        //再次采集一遍渠道订单
//        param.setOrderScene(2);
//        saveTbOrder(param);
    }

    protected void saveTbOrder(QueryTBParam param) {
        log.debug("淘宝订单采集 start:{} end:{} positionIndex:{}", param.getStartTime(), param.getEndTime(), param.getPositionIndex());
        String position = saveTb(param);
        if(StringUtils.isNotBlank(position)) {
            param.setPositionIndex(position);
            saveTbOrder(param);
        }
    }

}
