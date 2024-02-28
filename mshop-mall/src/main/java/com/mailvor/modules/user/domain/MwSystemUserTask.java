/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@TableName("mw_system_user_task")
public class MwSystemUserTask extends BaseDomain {

    @TableId
    private Integer id;


    /** 任务名称 */
    private String name;


    /** 配置原名 */
    private String realName;


    /** 任务类型 */
    private String taskType;


    /** 限定数 */
    private Integer number;


    /** 等级id */
    private Integer levelId;


    /** 排序 */
    private Integer sort;


    /** 是否显示 */
    private Integer isShow;


    /** 是否务必达成任务,1务必达成,0=满足其一 */
    private Integer isMust;


    /** 任务说明 */
    private String illustrate;


    public void copy(MwSystemUserTask source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
