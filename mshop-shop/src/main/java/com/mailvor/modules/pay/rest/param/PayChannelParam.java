/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.rest.param;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Data
public class PayChannelParam implements Serializable {

    /** 通道名称,同主体设置为一样 */
    @NotBlank(message = "通道名称不能为空")
    private String channelName;
    @NotBlank(message = "通道商不能为空")
    private String channelKey;

    private String name;

    /** 证书信息 */
    private String certProfileE;

    /** 异步回调 */
    private String notifyUrl;
    /** 最大额度 */
    @DecimalMin(value = "1", message = "最大额度最小为1元")
    @DecimalMax(value = "10000000", message = "最大额度最大为10000000元")
    @NotNull(message = "最大额度不能为空")
    private Double maxAmount;
    /** 剩余额度 */
    @DecimalMin(value = "1", message = "剩余额度最小为1元")
    @DecimalMax(value = "10000000", message = "剩余额度最大为10000000元")
    @NotNull(message = "剩余额度不能为空")
    private Double amount;

    /** 通道开关 8=正常，0=永久关闭 1=投诉关闭 2=临时关闭 0和1需要手动开启 2在刷新后重置 */
    private Integer status;

    private Integer extract;

    /** 通道刷新时间 */
    @JsonFormat(pattern = "HH:mm:ss",timezone="GMT+8")
    private Date cycleTime;
    /** 主体id */
    @Min(value = 1, message = "主体id不能为空")
    @NotNull(message = "主体id不能为空")
    private Long companyId;

    /**
     *'通道类型 1=支付宝原生 2=支付宝三方 6=微信原生 7=微信三方 0=未知，不启用',
     * */
    @NotNull(message = "通道类型不能为空")
    private Integer type;
    public void copy(PayChannelParam source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
