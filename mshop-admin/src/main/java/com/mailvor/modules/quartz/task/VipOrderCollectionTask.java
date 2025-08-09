package com.mailvor.modules.quartz.task;

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
public class VipOrderCollectionTask extends OrderTask{

    public void run(String paramStr){
        QueryVipParam param;
        if(StringUtils.isEmpty(paramStr)) {
            param = new QueryVipParam();
        } else {
            param = JSON.parseObject(paramStr, QueryVipParam.class);
        }
        if(StringUtils.isEmpty(param.getOrderTimeStart()) || StringUtils.isEmpty(param.getOrderTimeEnd())) {
            long end = System.currentTimeMillis();
            long start;
            //默认10分钟之前的订单
            if(param.getMinutes() != null) {
                start = end - param.getMinutes()*60*1000;
            } else {
                start = end - 10*60*1000;
            }

            param.setOrderTimeEnd(end + "");
            param.setOrderTimeStart(start + "");
        }
        saveVipOrder(param);
    }

    protected void saveVipOrder(QueryVipParam param) {
        log.debug("唯品会订单采集 start:{} end:{} page:{}", param.getOrderTimeStart(), param.getOrderTimeEnd(), param.getPage());
        boolean hasMore = saveVip(param);

        //如果还有更多，不做时间更新，继续查询下一页，订单少时无须测试
        if(hasMore) {
            param.setPage(param.getPage()+1);
            saveVipOrder(param);
        }
    }
}
