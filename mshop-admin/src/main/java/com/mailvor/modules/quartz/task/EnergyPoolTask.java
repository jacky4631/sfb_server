package com.mailvor.modules.quartz.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.modules.energy.config.EnergyShopConfig;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.energy.service.UserEnergyService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.utils.TkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 热度兼容老用户
 *
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class EnergyPoolTask {
    @Resource
    private MwUserRechargeService rechargeService;

    @Resource
    private MwUserService userService;

    @Resource
    private UserEnergyService userEnergyService;


    @Resource
    private MwSystemConfigService systemConfigService;
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String param) {
        //param格式："2022-01-05 11:22:50"
        //该任务只能执行一次，切记
        //找到所有某个时间段前充值成功的订单，然后分配佣金池
        DateTime date = DateUtil.parse(param);
        List<MwUserRecharge> rechargeList = rechargeService.list(new LambdaQueryWrapper<MwUserRecharge>()
                .eq(MwUserRecharge::getPaid, 1)
                .lt(MwUserRecharge::getCreateTime, date));
        EnergyConfigDto energyConfigDto = systemConfigService.getEnergyConfig();
        for(MwUserRecharge recharge: rechargeList) {
            Long uid = recharge.getUid();
            String platform = recharge.getPlatform();
            MwUser user = userService.getById(uid);
            if(user == null) {
                continue;
            }
            //增加自己的红包池
            EnergyShopConfig shopConfig = energyConfigDto.getShopConfig(platform);
            if(TkUtil.getLevel(platform, user) == 5) {
                userEnergyService.addEnergy(uid, uid, platform, BigDecimal.valueOf(shopConfig.getValue()),0);
            }
            //增加上级的红包池
            Long pUid = user.getSpreadUid();
            if(pUid == null || pUid == 0) {
                continue;
            }
            MwUser pUser = userService.getById(pUid);
            if(pUser == null) {
                continue;
            }
            //校验用户校验是否等于5 加热度
            if(TkUtil.getLevel(platform, pUser) == 5) {
                userEnergyService.addEnergy(pUid, uid, platform, BigDecimal.valueOf(shopConfig.getValueOne()),0);
            }

            //增加上上级的红包池
            Long ppUid = pUser.getSpreadUid();
            if(ppUid == null || ppUid == 0) {
                continue;
            }
            MwUser ppUser = userService.getById(ppUid);
            if(ppUser == null) {
                continue;
            }
            //校验用户校验是否等于5 加热度
            if(TkUtil.getLevel(platform, ppUser) == 5) {
                userEnergyService.addEnergy(ppUid, uid, platform, BigDecimal.valueOf(shopConfig.getValueTwo()),0);
            }
        }
    }

    public static void main(String[] args) {
        DateTime date = DateUtil.parse("2022-01-05 11:22:50");
        System.out.println(date);
    }
}
