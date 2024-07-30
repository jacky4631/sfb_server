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
public class PayCompanyEditParam extends PayCompanyParam {
    /**通道id*/
    @NotNull(message = "主体id不能为空")
    private Long id;

    public void copy(PayCompanyEditParam source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
