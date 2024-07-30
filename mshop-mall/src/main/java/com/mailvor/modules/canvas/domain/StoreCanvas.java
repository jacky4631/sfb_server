/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.canvas.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import com.mailvor.domain.BaseDomain;

/**
* @author mshop
* @date 2021-02-01
*/
@Data
@TableName("mw_store_canvas")
public class StoreCanvas extends BaseDomain {
    /** 画布id */
    @TableId
    private Long canvasId;

    /** 终端 1-小程序 2-H5 3-APP 4-PC */
    @NotNull
    private Integer terminal;

    /** 画布json数据 */
    private String json;

    /** 类型 1-系统画布 2-自定义页面 3-商家店铺装修 */
    private Integer type;

    /** 名称 */
    @NotBlank
    private String name;

    /** 店铺id，当type=3的时候，值为具体的店铺id，其它情况为0 */
    private Long shopId;




    public void copy(StoreCanvas source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
