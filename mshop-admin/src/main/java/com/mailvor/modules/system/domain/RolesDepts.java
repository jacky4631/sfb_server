/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
* @author mazhongjun
* @date 2020-05-16
*/
@Data
@TableName("roles_depts")
public class RolesDepts implements Serializable {

    /** 角色ID */
    private Long roleId;

    /** 部门ID */
    private Long deptId;


    public void copy(RolesDepts source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
