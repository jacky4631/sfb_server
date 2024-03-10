/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.dto;

import lombok.Data;
import java.util.Date;
import java.io.Serializable;

/**
* @author lioncity
* @date 2020-12-09
*/
@Data
public class MwAppVersionDto implements Serializable {

    private Integer id;

    private Integer isDel;

    /** 更新时间 */
    private Date createTime;

    private Date updateTime;

    /** ios版本号 */
    private String iosVersion;

    /** 安卓版本号 */
    private String androidVersion;

    /** 版本名称 */
    private String versionName;

    /** 版本描述 */
    private String versionInfo;

    /** 安卓下载链接 */
    private String androidUrl;

    /** 是否强制升级 */
    private Integer forceUpdate;

    /** ios store应用商店链接 */
    private String iosUrl;


    /** 是否启用 */
    private Integer enable;
}
