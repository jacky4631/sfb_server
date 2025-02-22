/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("mw_store_visit")
public class MwStoreVisit implements Serializable {

    @TableId
    private Long id;


    /** 产品ID */
    private Long productId;


    /** 产品类型 */
    private String productType;


    /** 产品分类ID */
    private Integer cateId;


    /** 产品类型 */
    private String type;


    /** 用户ID */
    private Long uid;


    /** 访问次数 */
    private Integer count;


    /** 备注描述 */
    private String content;


    /** 添加时间 */
    @TableField(fill= FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Integer addTime;


    public void copy(MwStoreVisit source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
