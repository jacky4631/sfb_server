package com.mailvor.modules.quartz.task;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.modules.energy.domain.UserEnergy;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.energy.service.UserEnergyOrderService;
import com.mailvor.modules.energy.service.UserEnergyService;
import com.mailvor.modules.quartz.task.param.EnergyLogParam;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 热度日志定时任务，根据热度生成热度日志
 * 生成订单记录 非生成订单
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class EnergyLogTask {

    @Resource
    private UserEnergyService energyService;

    @Resource
    private UserEnergyOrderService energyOrderService;
    @Resource
    private UserEnergyOrderLogService energyOrderLogService;

    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private MwUserService userService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String param) {
        //todo 目前是查询所有热度大于100的用户，和当天的热度日志，用户量多了以后可能有性能问题
        //todo 后期使用mysql关联查询，查找热度大于等于100并且当天UserEnergyOrderLog为空的用户

        EnergyLogParam logParam = JSON.parseObject(param, EnergyLogParam.class);

        List<Long> uids = logParam.getUids();
        List<String> platforms = logParam.getPlatforms();

        //找到当天没有热度日志同时热度大于0的用户，生成热度日志UserEnergyOrderLog，同时产生热度订单UserEnergyOrder
        //找到当天所有热度日志
        List<UserEnergyOrderLog> orderLogs = energyOrderLogService.list(new LambdaQueryWrapper<UserEnergyOrderLog>()
                .ge(UserEnergyOrderLog::getCreateTime, DateUtils.getToday()));
        //转成map
        Map<String, UserEnergyOrderLog> logMap = new HashMap<>();
        orderLogs.stream().forEach(log -> {
            logMap.put(getKey(log.getUid(), log.getPlatform(), log.getType()), log);
        });

        //获取热度配置
        EnergyConfigDto configDto = systemConfigService.getEnergyConfig();

        //计算赠送热度
        //赠送订单根据之前的记录数量生成订单 同时当天没有生成
        //找到所有热度值大于等于配置的用户
        BigDecimal oneB = BigDecimal.valueOf(configDto.getDayEnergy());
        List<UserEnergy> userEnergyList = energyService.getEnergyList(oneB);
        //如果没有用户热度大于配置 返回
        if(!userEnergyList.isEmpty()) {
            List<Long> uidList = userEnergyList.stream().map(userEnergy -> userEnergy.getUid()).collect(Collectors.toList());
            Map<Long, MwUser> userMap = userService.listByIds(uidList).stream().collect(Collectors.toMap(MwUser::getUid, Function.identity()));
            //生成订单扣除热度
            Integer energyType = 0;
            for(UserEnergy userEnergy : userEnergyList) {

                Long uid = userEnergy.getUid();
                //如果用户不在列表里 不生成数据，正式上线后这里置空
                if(!uids.isEmpty() && !uids.contains(uid)) {
                    continue;
                }
                MwUser user = userMap.get(uid);
                if(user == null) {
                    continue;
                }
                //生成淘宝
                String tb = PlatformEnum.TB.getValue();
                UserEnergyOrderLog tbLog = logMap.get(getKey(uid, tb, energyType));

                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(tb) && user.getLevel()==5 && tbLog == null && userEnergy.getTbEnergy().compareTo(oneB) !=-1) {
                    //减少1淘宝热度
                    energyService.decEnergy(uid, tb, oneB, energyType);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getTbEnergy(), tb, configDto, energyType);
                }

                //生成京东
                String jd = PlatformEnum.JD.getValue();
                UserEnergyOrderLog jdLog = logMap.get(getKey(uid, jd, energyType));
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(jd) && user.getLevelJd()==5 && jdLog == null && userEnergy.getJdEnergy().compareTo(oneB) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, jd, oneB, energyType);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getJdEnergy(), jd, configDto, energyType);
                }

                //生成拼多多
                String pdd = PlatformEnum.PDD.getValue();
                UserEnergyOrderLog pddLog = logMap.get(getKey(uid, pdd, energyType));
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(pdd) && user.getLevelPdd()==5 && pddLog == null && userEnergy.getPddEnergy().compareTo(oneB) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, pdd, oneB, energyType);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getPddEnergy(), pdd, configDto, energyType);
                }

                //生成抖音
                String dy = PlatformEnum.DY.getValue();
                UserEnergyOrderLog dyLog = logMap.get(getKey(uid, dy, energyType));
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(dy) && user.getLevelDy() ==5 && dyLog == null && userEnergy.getDyEnergy().compareTo(oneB) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, dy, oneB, energyType);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getDyEnergy(), dy, configDto, energyType);
                }

                //生成唯品会
                String vip = PlatformEnum.VIP.getValue();
                UserEnergyOrderLog vipLog = logMap.get(getKey(uid, vip, energyType));
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(vip) && user.getLevelVip()==5 && vipLog == null && userEnergy.getVipEnergy().compareTo(oneB) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, vip, oneB, energyType);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getVipEnergy(), vip, configDto, energyType);
                }
            }
        }



        //计算推广热度订单 需要计算今天是否已经生成记录
        BigDecimal dayTuiEnergy = BigDecimal.valueOf(configDto.getDayTuiEnergy());
        List<UserEnergy> userEnergyTuiList = energyService.getEnergyTuiList(dayTuiEnergy);
        if(!userEnergyTuiList.isEmpty()) {
            List<Long> uidTuiList = userEnergyTuiList.stream().map(userEnergy -> userEnergy.getUid()).collect(Collectors.toList());
            Map<Long, MwUser> userTuiMap = userService.listByIds(uidTuiList).stream().collect(Collectors.toMap(MwUser::getUid, Function.identity()));
            //生成订单扣除热度
            Integer energyTuiType = 1;
            for(UserEnergy userEnergy : userEnergyTuiList) {

                Long uid = userEnergy.getUid();
                //如果用户不在列表里 不生成数据，正式上线后这里置空
                if(!uids.isEmpty() && !uids.contains(uid)) {
                    continue;
                }
                MwUser user = userTuiMap.get(uid);
                if(user == null) {
                    continue;
                }
                //生成淘宝
                String tb = PlatformEnum.TB.getValue();
                UserEnergyOrderLog tbLog = logMap.get(getKey(uid, tb, energyTuiType));


                //获取该用户淘宝每天消耗热度
                BigDecimal tbDay = userEnergy.getTbDay().compareTo(BigDecimal.ZERO) == 1 ? userEnergy.getTbDay() : dayTuiEnergy;
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(tb) && user.getLevel()==5 && tbLog == null && userEnergy.getTbTuiEnergy().compareTo(tbDay) !=-1) {
                    //减少1淘宝热度
                    energyService.decEnergy(uid, tb, tbDay, energyTuiType);

                    //得到淘宝日耗热度是默认热度的倍数
                    BigDecimal times = NumberUtil.div(tbDay, dayTuiEnergy);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getTbTuiEnergy(), tb, configDto, energyTuiType, times);
                }

                //生成京东
                String jd = PlatformEnum.JD.getValue();
                UserEnergyOrderLog jdLog = logMap.get(getKey(uid, jd, energyTuiType));
                //获取该用户京东每天消耗热度
                BigDecimal jdDay = userEnergy.getJdDay().compareTo(BigDecimal.ZERO) == 1 ? userEnergy.getJdDay() : dayTuiEnergy;
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(jd) && user.getLevelJd()==5 && jdLog == null && userEnergy.getJdTuiEnergy().compareTo(jdDay) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, jd, jdDay, energyTuiType);

                    BigDecimal times = NumberUtil.div(jdDay, dayTuiEnergy);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getJdTuiEnergy(), jd, configDto, energyTuiType, times);
                }

                //生成拼多多
                String pdd = PlatformEnum.PDD.getValue();
                UserEnergyOrderLog pddLog = logMap.get(getKey(uid, pdd, energyTuiType));
                //获取该用户拼多多每天消耗热度
                BigDecimal pddDay = userEnergy.getPddDay().compareTo(BigDecimal.ZERO) == 1 ? userEnergy.getPddDay() : dayTuiEnergy;
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(pdd) && user.getLevelPdd()==5 && pddLog == null && userEnergy.getPddTuiEnergy().compareTo(pddDay) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, pdd, pddDay, energyTuiType);
                    BigDecimal times = NumberUtil.div(pddDay, dayTuiEnergy);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getPddTuiEnergy(), pdd, configDto, energyTuiType, times);
                }

                //生成抖音
                String dy = PlatformEnum.DY.getValue();
                UserEnergyOrderLog dyLog = logMap.get(getKey(uid, dy, energyTuiType));
                //获取该用户抖音每天消耗热度
                BigDecimal dyDay = userEnergy.getDyDay().compareTo(BigDecimal.ZERO) == 1 ? userEnergy.getDyDay() : dayTuiEnergy;
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(dy) && user.getLevelDy() ==5 && dyLog == null && userEnergy.getDyTuiEnergy().compareTo(dyDay) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, dy, dyDay, energyTuiType);
                    BigDecimal times = NumberUtil.div(dyDay, dayTuiEnergy);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getDyTuiEnergy(), dy, configDto, energyTuiType, times);
                }

                //生成唯品会
                String vip = PlatformEnum.VIP.getValue();
                UserEnergyOrderLog vipLog = logMap.get(getKey(uid, vip, energyTuiType));
                //获取该用户唯品会每天消耗热度
                BigDecimal vipDay = userEnergy.getVipDay().compareTo(BigDecimal.ZERO) == 1 ? userEnergy.getVipDay() : dayTuiEnergy;
                //日志不存在同时热度大于等于1就生成
                if(platforms.contains(vip) && user.getLevelVip()==5 && vipLog == null && userEnergy.getVipTuiEnergy().compareTo(vipDay) !=-1) {
                    //减少1热度
                    energyService.decEnergy(uid, vip, vipDay, energyTuiType);
                    BigDecimal times = NumberUtil.div(vipDay, dayTuiEnergy);
                    //生成日志和订单记录
                    energyOrderService.createEnergyOrders(uid, userEnergy.getVipTuiEnergy(), vip, configDto, energyTuiType, times);
                }
            }
        }


    }

    protected String getKey(Long uid, String platform, Integer type) {
        return uid+"_"+platform+"_"+type;
    }

    public static void main(String[] args) {
        System.out.println(BigDecimal.valueOf(5).compareTo(BigDecimal.valueOf(1)));
        System.out.println(BigDecimal.valueOf(1).compareTo(BigDecimal.valueOf(1)));
        System.out.println(BigDecimal.valueOf(0).compareTo(BigDecimal.valueOf(1)));
        System.out.println(BigDecimal.valueOf(-1).compareTo(BigDecimal.valueOf(1)));
    }
}
