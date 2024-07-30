/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_system_user_level")
public class MwSystemUserLevel extends BaseDomain {

    @TableId
    private Integer id;


    /** 商户id */
    private Integer merId;


    /** 会员名称 */
    @NotBlank(message = "名称必填")
    private String name;


    /** 购买金额 未使用，重要！！！！，请不要使用该字段 */
    private BigDecimal money;


    /** 有效时间 */
    private Integer validDate;


    /** 是否为永久会员 */
    private Integer isForever;


    /** 是否购买,1=购买,0=不购买 */
    private Integer isPay;


    /** 是否显示 1=显示,0=隐藏 */
    private Integer isShow;


    /** 会员等级 */
    @NotNull(message = "请输入会员等级")
    private Integer grade;


    /** 返利比例 */
    @NotNull(message = "返利比例")
    private BigDecimal discount;

    @NotNull(message = "下级返利比例")
    private BigDecimal discountOne;

    @NotNull(message = "下下级返利比例")
    private BigDecimal discountTwo;

    /** 说明 */
    @TableField(value = "`explain`")
    private String explain;

    @NotBlank(message = "充值id必填,建议3位数字，不能重复")
    private String rechargeId;

    /**
     *  用户类型 tb、pdd、jd、vip、dy
     *  默认tb，通用类型，其他平台不配置时默认使用tb
     * */
    private String type;

    public void copy(MwSystemUserLevel source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
