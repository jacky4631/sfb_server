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
@ApiModel(value="TbActivityParseParam", description="官方活动会场转链")
public class TbActivityParseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "联盟官方活动ID，从联盟官方活动页获取或从大淘客官方活动推广接口获取")
    private String promotionSceneId;
    @ApiModelProperty(value = "推广pid，默认为在”我的应用“添加的pid")
    private String pid;
    @ApiModelProperty(value = "渠道id将会和传入的pid进行验证")
    private String relationId;
    @ApiModelProperty(value = "平台的淘宝授权id")
    private String authId;
}
