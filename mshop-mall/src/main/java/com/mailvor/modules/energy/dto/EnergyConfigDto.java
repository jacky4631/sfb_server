package com.mailvor.modules.energy.dto;

import com.mailvor.modules.energy.config.*;
import lombok.Data;

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
}
