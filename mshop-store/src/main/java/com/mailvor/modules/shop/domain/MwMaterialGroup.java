/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_material_group")
public class MwMaterialGroup extends BaseDomain {

    /** PK */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;


    /** 创建者ID */
    private String createId;


    /** 分组名 */
    private String name;


    public void copy(MwMaterialGroup source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
