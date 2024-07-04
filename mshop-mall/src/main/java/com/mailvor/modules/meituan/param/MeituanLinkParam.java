/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.meituan.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value="MeituanLinkParam", description="美团转链参数")
public class MeituanLinkParam extends MeituanBaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动物料ID，我要推广-活动推广中第一列的id信息（和商品id、活动链接三选一填写，不能全填)")
    private String actId;

    @ApiModelProperty(value = "商品id，对商品查询接口返回的skuViewid（和活动物料ID、活动链接三选一，不能全填）")
    private String skuViewId;

    @ApiModelProperty(value = "二级媒体身份标识，用于渠道效果追踪，限制64个字符，仅支持英文、数字和下划线")
    private String sid;
    @ApiModelProperty(value = "链接类型，枚举值：1 H5长链接；2 H5短链接；3 deeplink(唤起)链接；4 微信小程序唤起路径")
    private Integer linkType;
    @ApiModelProperty(value = "只支持到家外卖商品券、买菜业务类型链接和活动物料链接。活动链接，即想要推广的目标链接，出参会返回成自己可推的链接，" +
            "限定为当前可推广的活动链接或者商品券链接，请求内容尽量保持在200字以内，文本中仅存在一个http协议头的链接")
    private String text;

}
