/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_express")
public class MwExpress extends BaseDomain {

    /** 快递公司id */
    @TableId
    private Integer id;


    /** 快递公司简称 */
    @NotBlank(message = "请输入快递公司编号")
    private String code;


    /** 快递公司全称 */
    @NotBlank(message = "请输入快递公司名称")
    private String name;


    /** 排序 */
    private Integer sort;


    /** 是否显示 */
    private Integer isShow;


    public void copy(MwExpress source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
