/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("mw_pay_company")
public class MwPayCompany extends BaseDomain {
    /**通道id*/
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /** 公司名称 */
    private String company;
    /** 营业执照地址 */
    private String licenseUrl;
    /** 公章地址 */
    private String sealUrl;
    /** 手机号码 */
    private String phone;

    public void copy(MwPayCompany source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
