package com.mailvor.modules.energy.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class EnergyShopConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    /*
    * 开通店主自己可以获取的热度值
    * */
    private Double value;
    /*
     * 开通店主上级可以获取的热度值
     * */
    private Double valueOne;
    /*
     * 开通店主上上级可以获取的热度值
     * */
    private Double valueTwo;
}
