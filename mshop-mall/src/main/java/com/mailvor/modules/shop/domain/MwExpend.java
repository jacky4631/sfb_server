/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDeleteDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @author mazhongjun
* @date 2020-05-12
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("mw_expend")
public class MwExpend extends BaseDeleteDomain {

    @TableId(value = "id",type= IdType.AUTO)
    private Long id;


    private BigDecimal money;

    private String mark;

    /**
     * 支出日期
     * */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date addTime;

    public void copy(MwExpend source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
