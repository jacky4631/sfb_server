package com.mailvor.modules.quartz.task;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.modules.energy.domain.UserEnergy;
import com.mailvor.modules.energy.domain.UserEnergyLog;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.energy.service.UserEnergyLogService;
import com.mailvor.modules.energy.service.UserEnergyService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 热度兼容老用户第二版 区分赠送热度和推广热度
 *
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class EnergySupportTask {
    @Resource
    private UserEnergyService userEnergyService;

    @Resource
    private UserEnergyLogService userEnergyLogService;

    @Resource
    private MwSystemConfigService systemConfigService;
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String param) {
        //todo 原热度都为赠送热度
        //todo 每个平台找到之前的每日记录的数量，该数量为1天扣100热度生成1条，乘以100/2得到返还的热度 加到用户对应平台的赠送热度得到总剩余热度
        //todo 剩余赠送热度=（对应平台赠送热度-返还热度）或者总剩余热度，取其小，如果有剩余放到推广热度上面
        //todo 比如情况1：淘宝，用户剩余热度为1500，之前记录有15条，得到返还热度为15*100/2=750，加到1500上，总剩余2250，
        //todo      赠送热度为2000-750=1250 推广热度为2250-1250=1000
        //todo 比如情况2：淘宝，用户剩余热度为400，之前记录有15条，得到返还热度为15*100/2=750，加到400上，总剩余1150，
        //todo      赠送热度为2000-500=1500，大于总剩余的1150，所以赠送热度为1150 推广热度0
        //todo 比如情况3：淘宝，用户剩余热度为0，之前记录有20条，得到返还热度为20*100/2=1000，总剩余1000，
        //todo      赠送热度为2000-500=1500，大于总剩余的1000，所以赠送热度为1000 推广热度0
        //todo 其他平台以此类推

        List<UserEnergyLog> energyLogs = userEnergyLogService.list(new LambdaQueryWrapper<UserEnergyLog>()
                .eq(UserEnergyLog::getType, 2));
        //todo转成map计算数量 key uid+platform
        Map<String, BigDecimal> logMap = new HashMap<>();
        for(UserEnergyLog userEnergyLog : energyLogs) {
            String key = getKey(userEnergyLog.getUid(), userEnergyLog.getPlatform());
            BigDecimal energy = logMap.get(key);
            if(energy == null) {
                energy = userEnergyLog.getEnergy();
            } else {
                energy = NumberUtil.add(energy, userEnergyLog.getEnergy());
            }
            logMap.put(key, energy);
        }


        List<UserEnergy> userEnergyList = userEnergyService.list();
        EnergyConfigDto energyConfigDto = systemConfigService.getEnergyConfig();

        for(UserEnergy userEnergy : userEnergyList) {
            Long uid = userEnergy.getUid();
            //tb
            BigDecimal tbUsedEnergy = logMap.get(getKey(uid, PlatformEnum.TB.getValue()));
            if(tbUsedEnergy != null) {
                BigDecimal rebateEnergy = NumberUtil.div(tbUsedEnergy, 2);
                BigDecimal totalEnergy = NumberUtil.add(userEnergy.getTbEnergy(), rebateEnergy);
                BigDecimal zengEnergy = NumberUtil.sub(energyConfigDto.getTbConfig().getValue(), rebateEnergy);
                if(totalEnergy.compareTo(zengEnergy) == 1) {
                    userEnergy.setTbEnergy(zengEnergy);
                    userEnergy.setTbTuiEnergy(NumberUtil.sub(totalEnergy, zengEnergy));
                } else {
                    userEnergy.setTbEnergy(totalEnergy);
                }
                userEnergyLogService.save(UserEnergyLog.builder().uid(uid).oid(uid)
                        .platform(PlatformEnum.TB.getValue()).energy(rebateEnergy).totalEnergy(totalEnergy).type(1).build());
            }
            //jd
            BigDecimal jdUsedEnergy = logMap.get(getKey(userEnergy.getUid(), PlatformEnum.JD.getValue()));
            if(jdUsedEnergy != null) {
                BigDecimal rebateEnergy = NumberUtil.div(jdUsedEnergy, 2);
                BigDecimal totalEnergy = NumberUtil.add(userEnergy.getJdEnergy(), rebateEnergy);
                BigDecimal zengEnergy = NumberUtil.sub(energyConfigDto.getJdConfig().getValue(), rebateEnergy);
                if(totalEnergy.compareTo(zengEnergy) == 1) {
                    userEnergy.setJdEnergy(zengEnergy);
                    userEnergy.setJdTuiEnergy(NumberUtil.sub(totalEnergy, zengEnergy));
                } else {
                    userEnergy.setJdEnergy(totalEnergy);
                }
                userEnergyLogService.save(UserEnergyLog.builder().uid(uid).oid(uid)
                        .platform(PlatformEnum.JD.getValue()).energy(rebateEnergy).totalEnergy(totalEnergy).type(1).build());
            }
            //pdd
            BigDecimal pddUsedEnergy = logMap.get(getKey(userEnergy.getUid(), PlatformEnum.PDD.getValue()));
            if(pddUsedEnergy != null) {
                BigDecimal rebateEnergy = NumberUtil.div(pddUsedEnergy, 2);
                BigDecimal totalEnergy = NumberUtil.add(userEnergy.getPddEnergy(), rebateEnergy);
                BigDecimal zengEnergy = NumberUtil.sub(energyConfigDto.getPddConfig().getValue(), rebateEnergy);
                if(totalEnergy.compareTo(zengEnergy) == 1) {
                    userEnergy.setPddEnergy(zengEnergy);
                    userEnergy.setPddTuiEnergy(NumberUtil.sub(totalEnergy, zengEnergy));
                } else {
                    userEnergy.setPddEnergy(totalEnergy);
                }

                userEnergyLogService.save(UserEnergyLog.builder().uid(uid).oid(uid)
                        .platform(PlatformEnum.PDD.getValue()).energy(rebateEnergy).totalEnergy(totalEnergy).type(1).build());
            }
            //dy
            BigDecimal dyUsedEnergy = logMap.get(getKey(userEnergy.getUid(), PlatformEnum.DY.getValue()));
            if(dyUsedEnergy != null) {
                BigDecimal rebateEnergy = NumberUtil.div(dyUsedEnergy, 2);
                BigDecimal totalEnergy = NumberUtil.add(userEnergy.getDyEnergy(), rebateEnergy);
                BigDecimal zengEnergy = NumberUtil.sub(energyConfigDto.getDyConfig().getValue(), rebateEnergy);
                if(totalEnergy.compareTo(zengEnergy) == 1) {
                    userEnergy.setDyEnergy(zengEnergy);
                    userEnergy.setDyTuiEnergy(NumberUtil.sub(totalEnergy, zengEnergy));
                } else {
                    userEnergy.setDyEnergy(totalEnergy);
                }

                userEnergyLogService.save(UserEnergyLog.builder().uid(uid).oid(uid)
                        .platform(PlatformEnum.DY.getValue()).energy(rebateEnergy).totalEnergy(totalEnergy).type(1).build());
            }
            //vip
            BigDecimal vipUsedEnergy = logMap.get(getKey(userEnergy.getUid(), PlatformEnum.VIP.getValue()));
            if(vipUsedEnergy != null) {
                BigDecimal rebateEnergy = NumberUtil.div(vipUsedEnergy, 2);
                BigDecimal totalEnergy = NumberUtil.add(userEnergy.getVipEnergy(), rebateEnergy);
                BigDecimal zengEnergy = NumberUtil.sub(energyConfigDto.getVipConfig().getValue(), rebateEnergy);
                if(totalEnergy.compareTo(zengEnergy) == 1) {
                    userEnergy.setVipEnergy(zengEnergy);
                    userEnergy.setVipTuiEnergy(NumberUtil.sub(totalEnergy, zengEnergy));
                } else {
                    userEnergy.setVipEnergy(totalEnergy);
                }
                userEnergyLogService.save(UserEnergyLog.builder().uid(uid).oid(uid)
                        .platform(PlatformEnum.VIP.getValue()).energy(rebateEnergy).totalEnergy(totalEnergy).type(1).build());
            }
            userEnergyService.saveOrUpdate(userEnergy);
        }

    }

    protected String getKey(Long uid, String platform) {
        return uid + "_" + platform;
    }

}
