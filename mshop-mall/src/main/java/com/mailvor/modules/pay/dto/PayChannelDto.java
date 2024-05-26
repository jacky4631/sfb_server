/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
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
public class PayChannelDto implements Serializable {
    /**通道id*/
    private Long id;

    /** 通道名称,同主体设置为一样 */
    private String channelName;
    /** 通道类型 参考 {@link com.mailvor.modules.pay.enums.PayChannelEnum} */
    private String channelKey;

    private String name;

    /** 证书信息 */
    private String certProfile;

    /** 异步回调 */
    private String notifyUrl;
    /** 最大额度 */
    private Double maxAmount;
    /** 剩余额度 */
    private Double amount;


    /** 通道开关 8=正常，0=永久关闭 1=投诉关闭 2=临时关闭 0和1需要手动开启 2在刷新后重置 */
    private Integer status;

    private Integer extract;

    /** 通道最近刷新时间，待定 */
    @JsonFormat(pattern = "HH:mm:ss",timezone="GMT+8")
    private Date cycleTime;
    /** 公司名称 */
    private String companyId;

    private Integer type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    public void copy(PayChannelDto source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
