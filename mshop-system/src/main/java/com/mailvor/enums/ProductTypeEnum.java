package com.mailvor.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mazhongjun
 * 产品类型枚举
 */

@Getter
@AllArgsConstructor
public enum ProductTypeEnum {

    PINK("pink","拼团"),

    SECKILL("seckill","秒杀"),

    COMBINATION("combination","拼团产品");

    private String value;
    private String desc;
}
