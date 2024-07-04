/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.meituan.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(value="GetCommentParam", description="商品表查询参数")
public class MeituanBaseParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品所属业务一级分类类型；请求的商品推广链接所属的业务类型信息，即只有输入skuViewId时才需要传本字段：" +
            "1 到家及其他业务类型，2 到店业务类型；不填则默认1")
    private Integer platform;

    @ApiModelProperty(value = "商品所属业务二级分类类型；请求的商品推广链接所属的业务类型信息，即只有输入skuViewId时才需要传本字段；" +
            "当字段platform为1，选择到家及其他业务类型时：5 医药，不填则默认null，表示外卖商品券；当字段platform为2，" +
            "选择到店业务类型时：1 到餐，2 到综 3：酒店 4：门票 不填则默认1")
    private Integer bizLine;
}
