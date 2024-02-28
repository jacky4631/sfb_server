/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.customer.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;

import com.mailvor.domain.BaseDomain;

/**
* @author Bug
* @date 2020-12-10
*/
@Data
@TableName("mw_store_customer")
public class MwStoreCustomer extends BaseDomain {
    /** id */
    @TableId
    private Long id;

    /** 用户昵称 */
    private String nickName;

    /** openId */
    @NotBlank(message = "请用户扫码后提交")
    private String openId;

    /** 备注 */
    private String remark;




    /** 是否启用 */
    private Integer isEnable;


    public void copy(MwStoreCustomer source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
