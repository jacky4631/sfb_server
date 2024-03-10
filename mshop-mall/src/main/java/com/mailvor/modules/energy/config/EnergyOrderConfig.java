package com.mailvor.modules.energy.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EnergyOrderConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    /*
    * 赠送热度：热度值范围最小值
    * 推广热度：天数范围最小值
    * */
    private Double min;
    /*
     * 赠送热度：热度值范围最大值
     * 推广热度：天数范围最小值
     * */
    private Double max;
    /*
     * 订单金额最小值
     * */
    private Double feeMin;
    /*
     * 订单金额最大值
     * */
    private Double feeMax;

    /*
     * 订单数量最小值
     * */
    private Integer countMin;
    /*
     * 订单数量最大值
     * */
    private Integer countMax;
}
