/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @author huangyu
* @date 2020-06-28
*/
@Data
@TableName(value = "mw_store_product_rule",autoResultMap = true)
public class MwStoreProductRule extends BaseDomain {

    @TableId
    private Integer id;


    /** 规格名称 */
    @NotBlank(message = "请输入规则名称")
    private String ruleName;


    /** 规格值 */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    @NotNull(message = "规格名称/值必填")
    private JSONArray ruleValue;



    public void copy(MwStoreProductRule source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
