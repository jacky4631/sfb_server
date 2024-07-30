/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.print;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ：LionCity
 * @date ：Created in 2020-05-11 11:01
 * @description：门店销售数据
 * @modified By：
 * @version: V1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrintStoreOrderVO implements Serializable {
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "打印机编号")
    private String driverNo;
    @ApiModelProperty(value = "门店列表")
    private Integer storeId;
    @ApiModelProperty(hidden = true)
    Long startSecond;
    @ApiModelProperty(hidden = true)
    Long endSecond;
}
