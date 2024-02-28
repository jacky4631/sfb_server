/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
@TableName("mw_wechat_reply")
public class MwWechatReply implements Serializable {

    /** 微信关键字回复id */
    @TableId
    private Integer id;


    /** 关键字 */
    @TableField(value = "`key`")
    private String key;


    /** 回复类型 */
    private String type;


    /** 回复数据 */
    private String data;


    /** 0=不可用  1 =可用 */
    private Integer status;


    /** 是否隐藏 */
    private Integer hide;


    public void copy(MwWechatReply source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
