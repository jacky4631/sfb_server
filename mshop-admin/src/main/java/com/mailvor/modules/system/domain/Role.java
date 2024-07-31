/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
* @author mazhongjun
* @date 2020-05-14
*/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_role")
public class Role extends BaseDomain {

    /** ID */
    @TableId
    private Long id;


    /** 名称 */
    @NotBlank(message = "请填写角色名称")
    private String name;


    /** 备注 */
    private String remark;


    /** 数据权限 */
    private String dataScope;


    /** 角色级别 */
    private Integer level;

    @TableField(exist = false)
    private Set<Menu> menus;

    @TableField(exist = false)
    private Set<Dept> depts;


    /** 功能权限 */
    private String permission;


    public void copy(Role source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
