package com.mailvor.modules.energy.dto;

import com.mailvor.modules.energy.config.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnergyConfigDto {
    private EnergyTbConfig tbConfig;

    private EnergyJdConfig jdConfig;

    private EnergyPddConfig pddConfig;

    private EnergyDyConfig dyConfig;

    private EnergyVipConfig vipConfig;

    /**
     * 赠送每天消耗热度
     * */
    private Double dayEnergy;
    /**
     * 推广每天消耗热度
     * */
    private Double dayTuiEnergy;
    /**
     * 推广每天消耗热度最大值
     * */
    private Double dayTuiEnergyMax;

    /**
     * 解锁时间最小值
     * */
    private Integer unlockMin;
    /**
     * 解锁时间最小值
     * */
    private Integer unlockMax;

    /**
     * 赠送配置
     * */
    private List<EnergyOrderConfig> orderConfigs;
    /**
     * 推广配置
     * */
    private List<EnergyOrderConfig> orderTbConfigs;
    private List<EnergyOrderConfig> orderJdConfigs;
    private List<EnergyOrderConfig> orderPddConfigs;
    private List<EnergyOrderConfig> orderDyConfigs;
    private List<EnergyOrderConfig> orderVipConfigs;

    public EnergyShopConfig getShopConfig(String platform) {
        switch (platform) {
            case "tb":
                return tbConfig;
            case "jd":
                return jdConfig;
            case "pdd":
                return pddConfig;
            case "dy":
                return dyConfig;
            case "vip":
                return vipConfig;
        }
        return tbConfig;
    }

    public void init() {
        setUnlockMin(7);
        setUnlockMax(10);
        setDayEnergy(50d);
        setDayTuiEnergy(50d);
        setDayTuiEnergyMax(500d);

        EnergyTbConfig tbConfig1 = new EnergyTbConfig();
        tbConfig1.setValue(2000d);
        tbConfig1.setValueOne(400d);
        tbConfig1.setValueTwo(200d);
        setTbConfig(tbConfig1);

        EnergyJdConfig jdConfig1 = new EnergyJdConfig();
        jdConfig1.setValue(1400d);
        jdConfig1.setValueOne(280d);
        jdConfig1.setValueTwo(140d);
        setJdConfig(jdConfig1);

        EnergyPddConfig pddConfig1 = new EnergyPddConfig();
        pddConfig1.setValue(1700d);
        pddConfig1.setValueOne(340d);
        pddConfig1.setValueTwo(170d);
        setPddConfig(pddConfig1);

        EnergyDyConfig dyConfig1 = new EnergyDyConfig();
        dyConfig1.setValue(1600d);
        dyConfig1.setValueOne(320d);
        dyConfig1.setValueTwo(160d);
        setDyConfig(dyConfig1);

        EnergyVipConfig vipConfig1 = new EnergyVipConfig();
        vipConfig1.setValue(1000d);
        vipConfig1.setValueOne(200d);
        vipConfig1.setValueTwo(100d);
        setVipConfig(vipConfig1);

        List<EnergyOrderConfig> orderConfigs1 = new ArrayList<>(5);
        orderConfigs1.add(EnergyOrderConfig.builder().min(0d).max(1000d).feeMin(15d).feeMax(25d).countMin(20).countMax(30).build());
        orderConfigs1.add(EnergyOrderConfig.builder().min(1000d).max(2000d).feeMin(20d).feeMax(30d).countMin(25).countMax(35).build());
        orderConfigs1.add(EnergyOrderConfig.builder().min(2000d).max(4000d).feeMin(35d).feeMax(45d).countMin(30).countMax(40).build());
        orderConfigs1.add(EnergyOrderConfig.builder().min(4000d).max(10000d).feeMin(40d).feeMax(50d).countMin(35).countMax(45).build());
        orderConfigs1.add(EnergyOrderConfig.builder().min(10000d).max(100000d).feeMin(45d).feeMax(60d).countMin(45).countMax(55).build());
        setOrderConfigs(orderConfigs1);


        List<EnergyOrderConfig> orderConfigsTb = new ArrayList<>(4);
        orderConfigsTb.add(EnergyOrderConfig.builder().min(0d).max(10d).feeMin(30d).feeMax(40d).countMin(8).countMax(10).build());
        orderConfigsTb.add(EnergyOrderConfig.builder().min(10d).max(20d).feeMin(15d).feeMax(25d).countMin(10).countMax(20).build());
        orderConfigsTb.add(EnergyOrderConfig.builder().min(20d).max(30d).feeMin(10d).feeMax(20d).countMin(15).countMax(25).build());
        orderConfigsTb.add(EnergyOrderConfig.builder().min(30d).max(50d).feeMin(5d).feeMax(15d).countMin(20).countMax(30).build());
        setOrderTbConfigs(orderConfigsTb);

        List<EnergyOrderConfig> orderConfigsPdd = new ArrayList<>(4);
        orderConfigsPdd.add(EnergyOrderConfig.builder().min(0d).max(9d).feeMin(25d).feeMax(40d).countMin(7).countMax(10).build());
        orderConfigsPdd.add(EnergyOrderConfig.builder().min(9d).max(18d).feeMin(15d).feeMax(20d).countMin(7).countMax(15).build());
        orderConfigsPdd.add(EnergyOrderConfig.builder().min(18d).max(25d).feeMin(10d).feeMax(20d).countMin(10).countMax(15).build());
        orderConfigsPdd.add(EnergyOrderConfig.builder().min(25d).max(50d).feeMin(5d).feeMax(15d).countMin(15).countMax(20).build());
        setOrderPddConfigs(orderConfigsPdd);

        List<EnergyOrderConfig> orderConfigsJd = new ArrayList<>(4);
        orderConfigsJd.add(EnergyOrderConfig.builder().min(0d).max(7d).feeMin(28d).feeMax(40d).countMin(5).countMax(10).build());
        orderConfigsJd.add(EnergyOrderConfig.builder().min(7d).max(14d).feeMin(15d).feeMax(25d).countMin(8).countMax(15).build());
        orderConfigsJd.add(EnergyOrderConfig.builder().min(14d).max(21d).feeMin(10d).feeMax(20d).countMin(15).countMax(25).build());
        orderConfigsJd.add(EnergyOrderConfig.builder().min(21d).max(50d).feeMin(5d).feeMax(15d).countMin(20).countMax(25).build());
        setOrderJdConfigs(orderConfigsJd);


        List<EnergyOrderConfig> orderConfigsDy = new ArrayList<>(4);
        orderConfigsDy.add(EnergyOrderConfig.builder().min(0d).max(8d).feeMin(26d).feeMax(42d).countMin(6).countMax(10).build());
        orderConfigsDy.add(EnergyOrderConfig.builder().min(8d).max(16d).feeMin(15d).feeMax(20d).countMin(8).countMax(15).build());
        orderConfigsDy.add(EnergyOrderConfig.builder().min(16d).max(24d).feeMin(10d).feeMax(20d).countMin(13).countMax(25).build());
        orderConfigsDy.add(EnergyOrderConfig.builder().min(24d).max(50d).feeMin(5d).feeMax(15d).countMin(17).countMax(30).build());
        setOrderDyConfigs(orderConfigsDy);

        List<EnergyOrderConfig> orderConfigsVip = new ArrayList<>(4);
        orderConfigsVip.add(EnergyOrderConfig.builder().min(0d).max(5d).feeMin(25d).feeMax(40d).countMin(5).countMax(10).build());
        orderConfigsVip.add(EnergyOrderConfig.builder().min(5d).max(10d).feeMin(15d).feeMax(25d).countMin(8).countMax(15).build());
        orderConfigsVip.add(EnergyOrderConfig.builder().min(10d).max(15d).feeMin(10d).feeMax(20d).countMin(10).countMax(18).build());
        orderConfigsVip.add(EnergyOrderConfig.builder().min(15d).max(50d).feeMin(5d).feeMax(15d).countMin(13).countMax(20).build());
        setOrderVipConfigs(orderConfigsVip);
    }
}
