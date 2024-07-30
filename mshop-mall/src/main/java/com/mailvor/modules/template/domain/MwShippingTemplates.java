/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author huangyu
* @date 2020-06-29
*/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_shipping_templates")
public class MwShippingTemplates extends BaseDomain {

    /** 模板ID */
    @TableId
    private Integer id;


    /** 模板名称 */
    private String name;


    /** 计费方式 */
    private Integer type;


    /** 地域以及费用 */
    private String regionInfo;


    /** 指定包邮开关 */
    private Integer appoint;


    /** 指定包邮内容 */
    private String appointInfo;




    /** 排序 */
    private Integer sort;


    public void copy(MwShippingTemplates source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
