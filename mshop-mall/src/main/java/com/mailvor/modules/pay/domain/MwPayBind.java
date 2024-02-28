/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import lombok.*;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("mw_pay_bind")
public class MwPayBind extends BaseDomain {
    /**用户id_payType
     * payType 1=支付宝 2=微信 3=银行卡 其他未知
     * **/
    @TableId
    private String id;

    /** 通道id */
    private Long channelId;

    /** 标识用户是否更换过绑定 1=更换过 */
    private Integer changeBind;

    public void copy(MwPayBind source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
