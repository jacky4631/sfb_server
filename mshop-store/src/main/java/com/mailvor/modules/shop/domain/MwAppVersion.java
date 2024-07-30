/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;

import javax.validation.constraints.NotNull;

/**
* @author lioncity
* @date 2020-12-09
*/
@Data
@TableName("mw_app_version")
public class MwAppVersion extends BaseDomain {
    @TableId
    private Integer id;

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

    @NotNull(message = "是否启用不能为空")
    /** 是否启用 */
    private Integer enable;

    private String platformName;

    public void copy(MwAppVersion source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
