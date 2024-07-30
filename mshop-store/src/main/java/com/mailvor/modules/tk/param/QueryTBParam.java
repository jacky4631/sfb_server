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
@ApiModel(value="QueryTBParam", description="剪切板识别")
public class QueryTBParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "查询时间类型，1：按照订单淘客创建时间查询，2:按照订单淘客付款时间查询，3:按照订单淘客结算时间查询，4：按照订单更新时间")
    private Integer queryType = 1;

    @ApiModelProperty(value = "订单查询开始时间。时间格式：YYYY-MM-DD HH:MM:SS")
    private String startTime;
    @ApiModelProperty(value = "订单查询结束时间，订单开始时间至订单结束时间，中间时间段日常要求不超过3个小时，但如618、双11、年货节等大促期间预估时间段不可超过20分钟，超过会提示错误，调用时请务必注意时间段的选择，以保证亲能正常调用！ 时间格式：YYYY-MM-DD HH:MM:SS")
    private String endTime;

    @ApiModelProperty(value = "位点，第一页数据返回里面有个这个字段，查第二页的数据的时候就传过去")
    private String positionIndex = "";
    @ApiModelProperty(value = "淘客订单状态，12-付款，13-关闭，14-确认收货，3-结算成功;不传，表示所有状态")
    private String tkStatus;

    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer pageSize = 100;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer pageNo;
    @ApiModelProperty(value = "取最近多少天的订单")
    private Integer day = 60;
    @ApiModelProperty(value = "一次取多少分钟的订单")
    private Integer minutes = 10;
    @ApiModelProperty(value = "1:常规订单，2:渠道订单，3:会员运营订单")
    private Integer orderScene = 1;
}
