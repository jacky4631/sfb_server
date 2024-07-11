/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@Data
@ApiModel(value="QueryMtParam", description="")
public class QueryMtParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "商品所属业务一级分类类型：1 到家及其他业务类型，2 到店业务类型（包含到店美食、休闲生活、酒店、门票）；不填则默认1")
    private Integer platform;
    @ApiModelProperty(value = "业务线标识；1）当platform为1，选择到家及其他业务类型时，业务线枚举为" +
            "1：外卖订单 WAIMAI 2：闪购红包 3：酒旅 4：美团电商订单（团好货） 5：医药 6：拼好饭 7：商品超值券包 COUPON " +
            "8：买菜 MAICAI 11：闪购商品；不传则默认传空表示非售卖券包订单类型的全部查询。若输入参数含7 商品超值券包，则只返回商品超值券包订单；" +
            "2）当platform为2，选择到店业务类型 时，业务线枚举1：到餐 2：到综 3：酒店 4：门票，不填则默认传1")
    private List<Integer> businessLine;

    @ApiModelProperty(value = "分页id，当searchType选择2逐页查询时，本字段为必填。若不填写，默认查询首页。取值为上一页查询时出参的scrollId字段 没有更多订单返回空")
    private String scrollId;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer size = 100;

    @ApiModelProperty(value = "开始时间 格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间 格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    @ApiModelProperty(value = "取最近多少天的订单")
    private Integer day = 93;
    @ApiModelProperty(value = "一次取多少分钟的订单")
    private Integer minutes = 60*24;
}
