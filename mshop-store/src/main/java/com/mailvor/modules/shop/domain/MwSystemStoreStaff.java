/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_system_store_staff")
public class MwSystemStoreStaff extends BaseDomain {

    @TableId
    private Integer id;


    /** 微信用户id */
    private Long uid;


    /** 店员头像 */
    @NotBlank(message = "请选择用户")
    private String avatar;


    /** 门店id */
    @NotNull(message = "请选择门店")
    private Integer storeId;


    /** 店员名称 */
    @NotBlank(message = "请输入店员名称")
    private String staffName;


    /** 手机号码 */
    @NotBlank(message = "请输入手机号码")
    private String phone;


    /** 核销开关 */
    private Integer verifyStatus;


    /** 状态 */
    private Integer status;



    /** 微信昵称 */
    private String nickname;


    /** 所属门店 */
    private String storeName;


    public void copy(MwSystemStoreStaff source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
