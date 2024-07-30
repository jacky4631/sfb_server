/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.rest.param;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Data
public class PayChannelEditParam extends PayChannelParam {
    /**通道id*/
    @NotNull(message = "通道id不能为空")
    private Long id;

    public void copy(PayChannelEditParam source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
