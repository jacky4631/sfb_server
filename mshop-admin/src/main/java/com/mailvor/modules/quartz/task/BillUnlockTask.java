package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.service.MwUserBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 解锁账单
 * @author Zheng Jie
 * @date 2022-10-04
 */
@Slf4j
@Component
public class BillUnlockTask {
    @Resource
    private MwUserBillService userBillService;

    protected void run(String paramStr) {
        //淘宝 京东 拼多多 抖音 唯品会都需执行
        //淘宝 查找30天内 bind=1 并且tkStatus=13的订单，扣除奖励红包， 积分记录, 订单bind改为2
        //todo 其他平台暂未验证
        JSONObject jsonObject = JSON.parseObject(paramStr);
//        Integer day = jsonObject.getInteger("day");
        Integer limit = jsonObject.getInteger("limit");
        if(limit == null || limit == 0) {
            limit = 20;
        }
        List<MwUserBill> userBills = userBillService.getUnlockList(limit);
        //todo 更新余额 更新热度 更新积分？

    }

}
