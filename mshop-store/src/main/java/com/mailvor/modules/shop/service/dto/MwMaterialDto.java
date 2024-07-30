/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwMaterialDto implements Serializable {

    /** PK */
    private String id;


    /** 创建时间 */
    private Date createTime;


    /** 创建者ID */
    private String createId;

    /** 类型1、图片；2、视频 */
    private String type;

    /** 分组ID */
    private String groupId;

    /** 素材名 */
    private String name;

    /** 素材链接 */
    private String url;
}
