package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.dto.ExpCardConfigDto;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.energy.service.UserEnergyOrderService;
import com.mailvor.modules.quartz.task.param.ExpLogParam;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.mailvor.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 体验日志定时任务，根据体验日期生成日志
 * 生成订单记录 非生成订单
 * 排除0点-2点执行
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class ExpLogTask {
    @Resource
    private UserEnergyOrderService energyOrderService;
    @Resource
    private UserEnergyOrderLogService energyOrderLogService;

    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private MwUserExtraService userExtraService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String param) {
        ExpLogParam logParam = JSON.parseObject(param, ExpLogParam.class);

        List<Long> uids = logParam.getUids();
        List<String> platforms = logParam.getPlatforms();

        //体验类型 energyOrderLog = 2;
        Integer expType = 2;
        //找到当天没有热度日志同时体验未到期的用户，生成热度日志UserEnergyOrderLog，同时产生热度订单UserEnergyOrder
        //找到当天所有体验日志
        List<UserEnergyOrderLog> orderLogs = energyOrderLogService.list(new LambdaQueryWrapper<UserEnergyOrderLog>()
                        .eq(UserEnergyOrderLog::getType, expType)
                .ge(UserEnergyOrderLog::getCreateTime, DateUtils.getToday()));
        //转成map
        Map<String, UserEnergyOrderLog> logMap = new HashMap<>();
        orderLogs.stream().forEach(log -> {
            logMap.put(getKey(log.getUid(), log.getPlatform(), log.getType()), log);
        });

        //获取体验配置
        ExpCardConfigDto cardConfigDto = systemConfigService.getExpCardConfig();

        //计算赠送热度
        //赠送订单根据之前的记录数量生成订单 同时当天没有生成
        //找到所有热度大于等于配置的用户
        List<MwUserExtra> userExtras = userExtraService.getVipList();
        //如果没有用户热度大于配置 返回
        if(CollectionUtils.isNotEmpty(userExtras)) {
            //生成订单
            for(MwUserExtra userExtra : userExtras) {

                Long uid = userExtra.getUid();
                //如果用户不在列表里 不生成数据，正式上线后这里置空
                if(!uids.isEmpty() && !uids.contains(uid)) {
                    continue;
                }
                //生成淘宝
                String tb = PlatformEnum.TB.getValue();
                UserEnergyOrderLog tbLog = logMap.get(getKey(uid, tb, expType));

                //日志不存在同时淘宝体验等级等于5就生成
                if(platforms.contains(tb) && userExtra.getLevel()==5 && tbLog == null && userExtra.getExpLog() < 7) {
                    //生成日志和订单记录
                    energyOrderService.createExpOrders(uid, tb, cardConfigDto, expType);
                }

                //生成京东
                String jd = PlatformEnum.JD.getValue();
                UserEnergyOrderLog jdLog = logMap.get(getKey(uid, jd, expType));
                //日志不存在同时京东体验等级等于5就生成
                if(platforms.contains(jd) && userExtra.getLevelJd()==5 && jdLog == null && userExtra.getExpLogJd() < 7) {
                    //生成日志和订单记录
                    energyOrderService.createExpOrders(uid, jd, cardConfigDto, expType);
                }

                //生成拼多多
                String pdd = PlatformEnum.PDD.getValue();
                UserEnergyOrderLog pddLog = logMap.get(getKey(uid, pdd, expType));
                //日志不存在同时拼多多体验等级等于5就生成
                if(platforms.contains(pdd) && userExtra.getLevelPdd()==5 && pddLog == null && userExtra.getExpLogPdd() < 7) {
                    //生成日志和订单记录
                    energyOrderService.createExpOrders(uid, pdd, cardConfigDto, expType);
                }

                //生成抖音
                String dy = PlatformEnum.DY.getValue();
                UserEnergyOrderLog dyLog = logMap.get(getKey(uid, dy, expType));
                //日志不存在同时抖音体验等级等于5就生成
                if(platforms.contains(dy) && userExtra.getLevelDy() ==5 && dyLog == null && userExtra.getExpLogDy() < 7) {
                    //生成日志和订单记录
                    energyOrderService.createExpOrders(uid, dy, cardConfigDto, expType);
                }

                //生成唯品会
                String vip = PlatformEnum.VIP.getValue();
                UserEnergyOrderLog vipLog = logMap.get(getKey(uid, vip, expType));
                //日志不存在同时唯品会体验等级等于5就生成
                if(platforms.contains(vip) && userExtra.getLevelVip()==5 && vipLog == null && userExtra.getExpLogVip() < 7) {
                    //生成日志和订单记录
                    energyOrderService.createExpOrders(uid, vip, cardConfigDto, expType);
                }
            }
        }
    }

    protected String getKey(Long uid, String platform, Integer type) {
        return uid+"_"+platform+"_"+type;
    }

}
