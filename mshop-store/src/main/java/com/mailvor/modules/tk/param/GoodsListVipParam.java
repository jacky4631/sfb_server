/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *
 * @author shenji
 * @date 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="GoodsListVipParam", description="唯品会商品表查询参数")
public class GoodsListVipParam extends BaseParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "页码,默认为1")
    private Integer page =1;
    @ApiModelProperty(value = "排序字段")
    private String fieldName;

    @ApiModelProperty(value = "排序顺序：0-正序，1-逆序，默认正序")
    private Integer order;
    @ApiModelProperty(value = "chanTag=pid，即推广位标识 (必传)，用来标记推广中的某个资源位，比如APP的banner、icon等（不能含有特殊字符，仅限字母、数字、下划线， 长度最大64）" +
            "2、如果没有广告位 则传默认广告位标识: default_pid")
    private String chanTag = "default_pid";

    private String openId;

    private String realCall = "true";
}
