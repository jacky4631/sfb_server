package com.mailvor.modules.quartz.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.modules.energy.dto.RecoverScaleConfigDto;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUserHbScale;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserHbScaleService;
import com.mailvor.modules.user.service.MwUserRechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 每天统计前一天翻倍用户
 *
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class EnergyHbScaleTask {
    @Resource
    private MwUserHbScaleService userHbScaleService;

    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private MwUserRechargeService userRechargeService;

    protected void run(String paramStr) throws InterruptedException {
        //计算月卡翻倍用户
        scale(2);
    }


    protected void scale(Integer type) {

        List<MwUserHbScale> userHbScales = new ArrayList<>();

        RecoverScaleConfigDto scaleConfigDto = systemConfigService.getRecoverScaleConfig();

        Date yesterday = DateUtil.yesterday();
        //找到前一天月卡用户
        List<MwUserRecharge> expRecharges = userRechargeService.list(new LambdaQueryWrapper<MwUserRecharge>()
                .eq(MwUserRecharge::getPaid, 1)
                .in(MwUserRecharge::getType, type)
                .ge(MwUserRecharge::getPayTime, DateUtil.beginOfDay(yesterday))
                .le(MwUserRecharge::getPayTime, DateUtil.endOfDay(yesterday)));
        if(expRecharges.isEmpty()) {
            return;
        }

        List<Long> uidList = expRecharges.stream().map(mwUserRecharge -> mwUserRecharge.getUid()).distinct().collect(Collectors.toList());
        BigDecimal recoverScale = scaleConfigDto.getMonthRecoverScale();
        String desc = "月卡";

        Integer count = NumberUtil.div(NumberUtil.mul(uidList.size(), recoverScale), 100).intValue();
        log.info("执行{}订单翻倍任务 uid长度{} 翻倍比例{} 最终数量{}", desc, uidList.size(), scaleConfigDto.getMonthRecoverScale(), count);
        if(count < 0) {
            return;
        }
        //随机选择用户
        List<Long> scaleUidList = randomUid(uidList, count);
        log.info("执行{}订单翻倍任务 uidList{} scaleUidList{}", desc, JSON.toJSONString(uidList), JSON.toJSONString(scaleUidList));
        for(int i= 0; i < scaleUidList.size();i++) {
            MwUserHbScale mwUserHbScale = MwUserHbScale.builder().uid(scaleUidList.get(i))
                        .monthScale(scaleConfigDto.getMonthScale())
                        .monthInvalidDay(scaleConfigDto.getMonthInvalidDay()).build();


            userHbScales.add(mwUserHbScale);
        }
        userHbScaleService.saveOrUpdateBatch(userHbScales);

    }
    public List<Long> randomUid(List<Long> uidList, Integer count) {
        List<Long> newList = new ArrayList<>(count);
        //打乱顺序
        Collections.shuffle(uidList);
        //取出 count 数量的专家
        for (int i = 0; i < count; i++) {
            newList.add(uidList.get(i));
        }
        return newList;
    }

    public static void main(String[] args) {
        EnergyHbScaleTask scaleTask = new EnergyHbScaleTask();
        List<Long> uidList = Arrays.asList(1L, 3L, 5L, 11L, 15L, 12L, 20L,30L);
        List<Long> newList = scaleTask.randomUid(uidList, 5);
        System.out.println(JSON.toJSONString(newList));
    }
}
