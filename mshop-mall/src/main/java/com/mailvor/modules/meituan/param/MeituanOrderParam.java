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

import java.util.List;

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
public class MeituanOrderParam extends MeituanBaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务线标识；1）当platform为1，选择到家及其他业务类型时，业务线枚举为1：外卖订单 WAIMAI 2：闪购红包 3：酒旅 " +
            "4：美团电商订单（团好货） 5：医药 6：拼好饭 7：商品超值券包 COUPON 8：买菜 MAICAI 11：闪购商品；" +
            "不传则默认传空表示非售卖券包订单类型的全部查询。若输入参数含7 商品超值券包，则只返回商品超值券包订单；" +
            "2）当platform为2，选择到店业务类型 时，业务线枚举1：到餐 2：到综 3：酒店 4：门票，不填则默认传1")
    private List<Integer> businessLine;

    @ApiModelProperty(value = "活动物料id，我要推广-活动推广中第一列的id信息，不传则返回所有actId的数据，建议查询订单时传入actId")
    private String actId;
    @ApiModelProperty(value = "二级推广位id，最长64位，不传则返回所有sid的数据")
    private String sid;
    @ApiModelProperty(value = "订单id，入参后可与业务线标识businessLine配合使用，输入的orderId需要与businessLine能对应上。" +
            "举例：如查询商品超值券包订单时orderId传券包订单号，businessLine传7；" +
            "除此以外其他查询筛选条件不生效，不传业务线标识businessLine则默认仅查非券包订单")
    private String orderId;



    @ApiModelProperty(value = "查询时间类型对应的查询开始时间，10位时间戳表示，单位秒")
    private Long startTime;
    @ApiModelProperty(value = "查询时间类型对应的查询结束时间，10位时间戳表示，单位秒")
    private Long endTime;


    @ApiModelProperty(value = "每页限制条数，默认100，最大支持100")
    private Integer limit;

    @ApiModelProperty(value = "查询时间类型，枚举值， 1 按订单支付时间查询， 2 按照更新时间查询， 默认为1")
    private Integer queryTimeType;

    @ApiModelProperty(value = "交易类型，1表示CPS，2表示CPA")
    private Integer tradeType;
    @ApiModelProperty(value = "分页id，当searchType选择2逐页查询时，本字段为必填。若不填写，默认查询首页。取值为上一页查询时出参的scrollId字段")
    private String scrollId;
    @ApiModelProperty(value = "订单分页查询方案选择，不填则默认为1。1 分页查询（最多能查询到1万条订单），当选择本查询方案，page参数不能为空。" +
            "此查询方式后续不再维护，建议使用2逐页查询。2 逐页查询（不限制查询订单数，只能逐页查询，不能指定页数），当选择本查询方案，需配合scrollId参数使用")
    private Integer searchType = 2;
    @ApiModelProperty(value = "可输入城市名称圈定特定城市的订单，单次最多查询10个城市（英文逗号分隔）。不传则默认全部城市订单。" +
            " 注：如需确认城市具体名称，可参考后台订单明细页的城市筛选项，或参考具体活动的城市命名。目前只支持到家业务类型-商品超值券包业务线。")
    private List<String> cityNames;


}
