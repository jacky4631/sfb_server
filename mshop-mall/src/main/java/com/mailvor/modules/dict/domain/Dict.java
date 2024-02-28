/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.dict.domain;

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
* @date 2020-05-14
*/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dict")
public class Dict extends BaseDomain {

    /** 字典ID */
    @TableId
    private Long id;


    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    private String name;


    /** 描述 */
    private String remark;




    public void copy(Dict source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
