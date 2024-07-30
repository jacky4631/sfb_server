/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author huangyu
* @date 2020-05-14
*/
@Data
public class UserAvatarDto implements Serializable {

    private Long id;

    /** 真实文件名 */
    private String realName;

    /** 路径 */
    private String path;

    /** 大小 */
    private String size;

    /** 创建时间 */
    private Timestamp createTime;
}
