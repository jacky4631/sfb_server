/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.rest.param;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Data
public class PayCompanyParam implements Serializable {

    @NotBlank(message = "主体名称不能为空")
    /** 公司名称 */
    private String company;
    /** 营业执照地址 */
    private String licenseUrl;
    /** 公章地址 */
    private String sealUrl;
    /** 手机号码 */
    private String phone;

    public void copy(PayCompanyParam source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
