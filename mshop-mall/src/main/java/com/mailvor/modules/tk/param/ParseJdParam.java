/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@Data
@ApiModel(value="ParseJdParam", description="")
public class ParseJdParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "推广物料url，例如活动链接、商品链接等；不支持仅传入skuid")
    private String materialId;

    private String couponLink;

    @ApiModelProperty(value = "网站ID/APP ID")
    private String siteId;

    @ApiModelProperty(value = "推广位id")
    private String positionId;

    /**
     * 暂未获得权限
     * */
    @ApiModelProperty(value = "渠道关系ID")
    private String channelId;
}
