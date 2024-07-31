/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.energy.config.EnergyOrderConfig;
import com.mailvor.modules.energy.domain.UserEnergyOrder;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.energy.dto.UserEnergyOrderDto;
import com.mailvor.modules.energy.dto.UserEnergyOrderQueryCriteria;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.energy.service.UserEnergyOrderService;
import com.mailvor.modules.energy.service.mapper.UserEnergyOrderLogMapper;
import com.mailvor.modules.energy.service.mapper.UserEnergyOrderMapper;
import com.mailvor.modules.tools.utils.CashUtils;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
* @author huangyu
* @date 2023-02-04
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserEnergyOrderServiceImpl extends BaseServiceImpl<UserEnergyOrderMapper, UserEnergyOrder> implements UserEnergyOrderService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private UserEnergyOrderMapper mapper;

    @Resource
    private UserEnergyOrderLogMapper energyOrderLogMapper;

    @Resource
    private UserEnergyOrderLogService orderLogService;
    /**
     * 找到未拆红包的热度订单
     * */
    @Override
    public List<UserEnergyOrder> getUnpackEnergyList() {
        return list(Wrappers.<UserEnergyOrder>lambdaQuery()
                .eq(UserEnergyOrder::getPaid, 1)
                .eq(UserEnergyOrder::getHb, 0)
                .eq(UserEnergyOrder::getRefund, 0)
//                .lt(UserEnergyOrder::getUnlockTime, new Date())
                .last("limit 100"));
    }

    @Override
    public void unlockOrder() {
        mapper.unlockOrder();
    }
    /**
     * 找到未分配给用户的热度订单
     * */
    @Override
    public List<UserEnergyOrder> getUnpaidList(List<String> platforms) {
        return mapper.selectList(Wrappers.<UserEnergyOrder>lambdaQuery()
                .eq(UserEnergyOrder::getPaid, 0)
                .in(UserEnergyOrder::getPlatform, platforms)
                .lt(UserEnergyOrder::getReleaseTime, new Date())
                .last("limit 100"));
    }

    /**
     * 找到需要退款的热度订单
     * */
    @Override
    public List<UserEnergyOrder> getRefundList() {
        return mapper.selectList(Wrappers.<UserEnergyOrder>lambdaQuery()
                .eq(UserEnergyOrder::getRefund, 1)
                .eq(UserEnergyOrder::getPaid, 1));
    }

    @Override
    public UserEnergyOrder getByOrderId(String orderId, Long uid) {
        return mapper.selectOne(Wrappers.<UserEnergyOrder>lambdaQuery()
                .eq(UserEnergyOrder::getUid, uid)
                .eq(UserEnergyOrder::getOrderId, orderId));
    }


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(UserEnergyOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<UserEnergyOrder> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<UserEnergyOrder> orders = page.getList();
        if(orders.isEmpty()) {
            map.put("content", new ArrayList<>(0));
            map.put("totalElements", page.getTotal());
            return map;
        }
        List<Long> logIds = orders.stream().map(energyOrder -> energyOrder.getLogId()).distinct().collect(toList());
        Map<Long, UserEnergyOrderLog> logMap = orderLogService.listByIds(logIds)
                .stream().collect(Collectors.toMap(UserEnergyOrderLog::getId, Function.identity()));
        List<UserEnergyOrderDto> dtos = orders.stream().map(user->{
            UserEnergyOrderDto orderDto = generator.convert(user, UserEnergyOrderDto.class);
            UserEnergyOrderLog orderLog = logMap.get(user.getLogId());
            if(orderLog != null) {
                orderDto.setLogType(orderLog.getType());
            }
            return orderDto;
        }).collect(toList());
        map.put("content", dtos);
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<UserEnergyOrder> queryAll(UserEnergyOrderQueryCriteria criteria){
        return mapper.selectList(QueryHelpPlus.getPredicate(UserEnergyOrderQueryCriteria.class, criteria));
    }

    @Override
    public int mysqlInsertOrUpdateBath(List list) {
        return mapper.mysqlInsertOrUpdateBath(list);
    }

    @Override
    public void createEnergyOrders(Long uid, BigDecimal energy, String platform, EnergyConfigDto configDto, Integer type) {
        createEnergyOrders(uid, energy, platform, configDto, type, BigDecimal.valueOf(1));
    }
    @Override
    public void createEnergyOrders(Long uid, BigDecimal energy, String platform, EnergyConfigDto configDto, Integer type, BigDecimal times) {
        EnergyOrderConfig orderConfig;
        if(type == 0) {
            //赠送配置
            //计算用户当前记录数量
            Long count = energyOrderLogMapper.countLog(uid, platform, type);

            orderConfig = TkUtil.getOrderTuiConfig(count, platform, configDto);
        } else {
            //推广配置
            orderConfig = TkUtil.getOrderConfig(energy, configDto.getOrderConfigs());
        }
        //生成总佣金
        BigDecimal money = CashUtils.randomBD(orderConfig.getFeeMin(), orderConfig.getFeeMax());
        //生成订单数量
        Integer orderCount = CashUtils.randomInt(orderConfig.getCountMin(), orderConfig.getCountMax());

        //需要乘以日耗热度值的倍数，不相等才需要乘以
        if(times.compareTo(BigDecimal.valueOf(1)) != 0) {
            money = NumberUtil.mul(money, times);
            orderCount = NumberUtil.mul(orderCount, times).intValue();
        }

        //生成热度日志
        UserEnergyOrderLog energyOrderLog = new UserEnergyOrderLog();
        energyOrderLog.setMoney(money);
        energyOrderLog.setCount(orderCount);
        energyOrderLog.setUid(uid);
        energyOrderLog.setPlatform(platform);
        energyOrderLog.setEnergy(energy);
        energyOrderLog.setType(type);
        energyOrderLogMapper.insert(energyOrderLog);

        List<UserEnergyOrder> energyOrders = new ArrayList<>();
        //创建热度扶持订单
        createSelfEnergyOrders(uid, money, orderCount, platform, energyOrders, configDto.getUnlockMin(), configDto.getUnlockMax());

        energyOrders.stream().forEach(energyOrder -> energyOrder.setLogId(energyOrderLog.getId()));
        mapper.insertBatch(energyOrders);

    }

    protected void createSelfEnergyOrders(Long uid, BigDecimal money, Integer orderCount,
                                     String platform,List<UserEnergyOrder> energyOrders, Integer unlockMin, Integer unlockMax) {
        //生成时间数据
        //时间主要分布在5:23:23点-23:59:59点
        //小部分在00:00:00-02:00:00
        List<Date> firstDateList = DateUtils.getDateList(orderCount);

        //转换成分计算，生成随机分红
        List<Integer> priceSepList = TkUtil.getPriceList(NumberUtil.mul(money, 100), 100, orderCount);
        initEnergyOrders(uid, platform, firstDateList, priceSepList, energyOrders, unlockMin, unlockMax);

    }

    protected void initEnergyOrders(Long uid, String platform, List<Date> dateList, List<Integer> priceSepList,
                                    List<UserEnergyOrder> energyOrders, Integer unlockMin, Integer unlockMax) {
        int count = dateList.size();
        for(int i = 0; i < count; i++) {
            int sepPrice = priceSepList.get(i);
            BigDecimal priceD = NumberUtil.div(BigDecimal.valueOf(sepPrice), 100);
            //当红包太小时，默认给0.01
            if(priceD.compareTo(BigDecimal.valueOf(0.01)) !=1) {
                priceD = BigDecimal.valueOf(0.01);
            }
            Date releaseTime = dateList.get(i);
            int unlockDay = DateUtils.randomInt(unlockMin, unlockMax);
            Date unlockTime = DateUtil.offsetDay(releaseTime, unlockDay);
            unlockTime = DateUtil.offsetHour(unlockTime, DateUtils.randomInt(5, 23));
            unlockTime = DateUtil.offsetMinute(unlockTime, DateUtils.randomInt(1, 59));
            unlockTime = DateUtil.offsetSecond(unlockTime, DateUtils.randomInt(1, 59));
            UserEnergyOrder energyOrder = UserEnergyOrder.builder()
                    .uid(uid)
                    .releaseMoney(priceD)
                    .releaseTime(releaseTime)
                    .unlockTime(unlockTime)
                    .platform(platform)
                    .paid(0)
                    .hb(0)
                    .refund(0)
                    .build();
            energyOrders.add(energyOrder);
        }

    }

}
