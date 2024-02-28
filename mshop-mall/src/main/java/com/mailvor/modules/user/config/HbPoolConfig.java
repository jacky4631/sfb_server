package com.mailvor.modules.user.config;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HbPoolConfig {
    //todo 现在写死 以后放到配置中
    public static final BigDecimal HB_POOL_SELF = BigDecimal.valueOf(0.25);

    public static final BigDecimal HB_POOL_ONE = BigDecimal.valueOf(0.05);
    public static final BigDecimal HB_POOL_TWO = BigDecimal.valueOf(0.03);
    //红包支出需要计算上级和上上级 20%+10% 支出1.3倍
    public static final BigDecimal HB_POOL_SCALE = BigDecimal.valueOf(1.3);

}
