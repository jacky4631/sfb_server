/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_user_recharge")
public class MwUserRecharge extends BaseDomain {

    @TableId
    private Long id;


    /** 充值用户UID */
    private Long uid;

    /** 付款用户UID */
    private Long oid;

    /** 订单号 */
    private String orderId;


    /** 充值金额 */
    private BigDecimal price;

    private BigDecimal givePrice;


    /** 充值类型 alipay bank等*/
    private String rechargeType;

    private String rechargeId;

    /** 是否充值 */
    private Integer paid;

    /** 通道id */
    private Long channelId;

    /** 充值支付时间 */
    private Date payTime;



    /** 退款金额 */
    private BigDecimal refundPrice;


    /** 昵称 */
    private String nickname;

    private Integer grade;

    private String platform;

    private String result;

    /**
     * 加盟类型 0=年卡 2=月卡
     * */
    private Integer type;

    public void copy(MwUserRecharge source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
