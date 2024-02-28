package com.mailvor.modules.quartz.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserExtraService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 记录体验日志数量的定时任务
 * 0点到2点执行
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class ExpCountTask {
    @Resource
    private UserEnergyOrderLogService logService;

    @Resource
    private MwUserExtraService userExtraService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String param) {
        Date now = new Date();
        Integer expType = 2;
        Integer levelVip = 5;
        Integer maxCount = 7;
        //找到等级为5 并且天数小于今天0点 数量小于7的
        List<MwUserExtra> tbExtras = userExtraService.list(new LambdaQueryWrapper<MwUserExtra>()
                .eq(MwUserExtra::getLevel, levelVip)
                .lt(MwUserExtra::getExpLog, maxCount)
                        .and(wrapper -> wrapper.isNull(MwUserExtra::getExpUpdate)
                                .or()
                                .lt(MwUserExtra::getExpUpdate, DateUtil.beginOfDay(now))));
        if(CollectionUtils.isNotEmpty(tbExtras)) {
            for(MwUserExtra userExtra : tbExtras) {
                long tbCount = logService.count(new LambdaQueryWrapper<UserEnergyOrderLog>()
                        .eq(UserEnergyOrderLog::getType, expType)
                        .eq(UserEnergyOrderLog::getPlatform, PlatformEnum.TB.getValue())
                        .eq(UserEnergyOrderLog::getUid, userExtra.getUid()));
                userExtra.setExpLog((int)tbCount);
                userExtra.setExpUpdate(now);
            }
            userExtraService.updateBatchById(tbExtras);
        }

        //找到等级为5 并且天数小于今天0点 数量小于7的
        List<MwUserExtra> jdExtras = userExtraService.list(new LambdaQueryWrapper<MwUserExtra>()
                .eq(MwUserExtra::getLevelJd, levelVip)
                .lt(MwUserExtra::getExpLogJd, maxCount)
                .and(wrapper -> wrapper.isNull(MwUserExtra::getExpUpdateJd)
                        .or()
                        .lt(MwUserExtra::getExpUpdateJd, DateUtil.beginOfDay(now))));
        if(CollectionUtils.isNotEmpty(jdExtras)) {
            for(MwUserExtra userExtra : jdExtras) {
                long tbCount = logService.count(new LambdaQueryWrapper<UserEnergyOrderLog>()
                        .eq(UserEnergyOrderLog::getType, expType)
                        .eq(UserEnergyOrderLog::getPlatform, PlatformEnum.JD.getValue())
                        .eq(UserEnergyOrderLog::getUid, userExtra.getUid()));
                userExtra.setExpLogJd((int)tbCount);
                userExtra.setExpUpdateJd(now);
            }
            userExtraService.updateBatchById(jdExtras);
        }

        //找到等级为5 并且天数小于今天0点 数量小于7的
        List<MwUserExtra> pddExtras = userExtraService.list(new LambdaQueryWrapper<MwUserExtra>()
                .eq(MwUserExtra::getLevelPdd, levelVip)
                .lt(MwUserExtra::getExpLogPdd, maxCount)
                .and(wrapper -> wrapper.isNull(MwUserExtra::getExpUpdatePdd)
                        .or()
                        .lt(MwUserExtra::getExpUpdatePdd, DateUtil.beginOfDay(now))));
        if(CollectionUtils.isNotEmpty(pddExtras)) {
            for(MwUserExtra userExtra : pddExtras) {
                long tbCount = logService.count(new LambdaQueryWrapper<UserEnergyOrderLog>()
                        .eq(UserEnergyOrderLog::getType, expType)
                        .eq(UserEnergyOrderLog::getPlatform, PlatformEnum.PDD.getValue())
                        .eq(UserEnergyOrderLog::getUid, userExtra.getUid()));
                userExtra.setExpLogPdd((int)tbCount);
                userExtra.setExpUpdatePdd(now);
            }
            userExtraService.updateBatchById(pddExtras);
        }

        //找到等级为5 并且天数小于今天0点 数量小于7的
        List<MwUserExtra> dyExtras = userExtraService.list(new LambdaQueryWrapper<MwUserExtra>()
                .eq(MwUserExtra::getLevelDy, levelVip)
                .lt(MwUserExtra::getExpLogDy, maxCount)
                .and(wrapper -> wrapper.isNull(MwUserExtra::getExpUpdateDy)
                        .or()
                        .lt(MwUserExtra::getExpUpdateDy, DateUtil.beginOfDay(now))));
        if(CollectionUtils.isNotEmpty(dyExtras)) {
            for(MwUserExtra userExtra : dyExtras) {
                long tbCount = logService.count(new LambdaQueryWrapper<UserEnergyOrderLog>()
                        .eq(UserEnergyOrderLog::getType, expType)
                        .eq(UserEnergyOrderLog::getPlatform, PlatformEnum.DY.getValue())
                        .eq(UserEnergyOrderLog::getUid, userExtra.getUid()));
                userExtra.setExpLogDy((int)tbCount);
                userExtra.setExpUpdateDy(now);
            }
            userExtraService.updateBatchById(dyExtras);
        }

        //找到等级为5 并且天数小于今天0点 数量小于7的
        List<MwUserExtra> vipExtras = userExtraService.list(new LambdaQueryWrapper<MwUserExtra>()
                .eq(MwUserExtra::getLevelVip, levelVip)
                .lt(MwUserExtra::getExpLogVip, maxCount)
                .and(wrapper -> wrapper.isNull(MwUserExtra::getExpUpdateVip)
                        .or()
                        .lt(MwUserExtra::getExpUpdateVip, DateUtil.beginOfDay(now))));
        if(CollectionUtils.isNotEmpty(vipExtras)) {
            for(MwUserExtra userExtra : vipExtras) {
                long tbCount = logService.count(new LambdaQueryWrapper<UserEnergyOrderLog>()
                        .eq(UserEnergyOrderLog::getType, expType)
                        .eq(UserEnergyOrderLog::getPlatform, PlatformEnum.VIP.getValue())
                        .eq(UserEnergyOrderLog::getUid, userExtra.getUid()));
                userExtra.setExpLogVip((int)tbCount);
                userExtra.setExpUpdateVip(now);
            }
            userExtraService.updateBatchById(vipExtras);
        }
    }

}
