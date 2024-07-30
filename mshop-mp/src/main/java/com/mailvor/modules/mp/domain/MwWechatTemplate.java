/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_wechat_template")
public class MwWechatTemplate extends BaseDomain {

    /** 模板id */
    @TableId
    private Integer id;


    /** 模板编号 */
    private String tempkey;


    /** 模板名 */
    private String name;


    /** 回复内容 */
    private String content;


    /** 模板ID */
    private String tempid;



    /** 状态 */
    private Integer status;

    /** 类型：template:模板消息 subscribe:订阅消息 */
    private String type;

    public void copy(MwWechatTemplate source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
