/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.canvas.service.dto;

import lombok.Data;
import java.io.Serializable;

/**
* @author mshop
* @date 2021-02-01
*/
@Data
public class StoreCanvasDto implements Serializable {

    /** 画布id */
    private Long canvasId;

    /** 终端 1-小程序 2-H5 3-APP 4-PC */
    private Integer terminal;

    /** 画布json数据 */
    private String json;

    /** 类型 1-系统画布 2-自定义页面 3-商家店铺装修 */
    private Integer type;

    /** 名称 */
    private String name;

    /** 店铺id，当type=3的时候，值为具体的店铺id，其它情况为0 */
    private Long shopId;

    /** 创建时间 */
    private String createTime;

    /** 修改时间 */
    private String updateTime;
}
