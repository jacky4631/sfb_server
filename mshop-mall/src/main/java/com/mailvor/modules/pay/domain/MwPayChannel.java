/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDomain;
import lombok.*;

import java.util.Comparator;
import java.util.Date;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("mw_pay_channel")
public class MwPayChannel extends BaseDomain implements Comparable {
    /**通道id*/
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 通道名称,同主体设置为一样 */
    private String channelName;
    /** 通道类型 参考 {PayPlatformEnum} */
    private String channelKey;

    /**
     * 名称 对应yml中pay.name
     * */
    private String name;

    private byte[] certProfileEnc;

    /** 异步回调 */
    private String notifyUrl;
    /** 最大额度 */
    private Double maxAmount;
    /** 剩余额度 */
    private Double amount;

    /** 通道开关 8=正常，0=永久关闭 1=投诉关闭 2=临时关闭 0和1需要手动开启 2在刷新后重置 */
    private Integer status;

    /**
     * 是否用于提现 1=是 0=否默认
     * */
    private Integer extract;

    /** 通道刷新时间*/
    @JsonFormat(pattern = "HH:mm:ss",timezone="GMT+8")
    private Date cycleTime;
    /** 主体id 对应MwPayCompany的id */
    private Long companyId;

    /**
     *'通道类型 1=支付宝原生 2=支付宝三方 6=微信原生 7=微信三方 15=云闪付 0=未知，不启用',
     * */
    private Integer type;

    public void copy(MwPayChannel source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public int compareTo(Object o) {
        MwPayChannel c1 = (MwPayChannel) o;

        return getAmount()/c1.getMaxAmount() > c1.getAmount()/c1.getMaxAmount() ? -1 : 1;
    }
}
