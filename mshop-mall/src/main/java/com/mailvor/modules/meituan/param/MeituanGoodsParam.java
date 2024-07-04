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
public class MeituanGoodsParam extends MeituanBaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "定位经纬度的经度，请传递经度*100万倍的整形数字，如经度116.404*100万倍为116404000； 针对到店、" +
            "到家医药商品业务类型，若未输入经纬度，则默认北京；针对到家外卖商品券业务类型，若未输入经纬度，则默认全国")
    private Long longitude;
    @ApiModelProperty(value = "定位经纬度的纬度，请传递纬度*100万倍的整形数字，如纬度39.928*100万倍为39928000; 针对到店、" +
            "到家医药商品业务类型，若未输入经纬度，则默认北京；针对到家外卖商品券业务类型，若未输入经纬度，则默认全国")
    private Long latitude;

    @ApiModelProperty(value = "筛选商品售卖价格上限【单位元】")
    private Integer priceCap;
    @ApiModelProperty(value = "筛选商品价格下限【单位元】")
    private Integer priceFloor;

    @ApiModelProperty(value = "筛选商品佣金值上限【单位元】，若商品按照佣金值进行范围筛选，则排序只能按照佣金降序，本字段只支持到店业务类型、到家医药业务类型")
    private Integer commissionCap;
    @ApiModelProperty(value = "筛选商品佣金值下限【单位元】，若商品按照佣金值进行范围筛选，则排序只能按照佣金降序，本字段只支持到店业务类型、到家医药业务类型")
    private List commissionFloor;

    @ApiModelProperty(value = "商品ID集合，非必填，若填写该字段则不支持其他筛选条件，集合里ID用英文“,”隔开。一次最多支持查询20个售卖券ID")
    private List vpSkuViewIds;

    @ApiModelProperty(value = "选品池榜单主题ID，支持查询1:精选，2:今日必推、3:同城热销、4:跟推爆款的商品售卖券；到店、到家医药业务类型，本项为必填，且只支持传枚举3")
    private Integer listTopiId;
    @ApiModelProperty(value = "分页大小，不填返回默认分页20")
    private Integer pageSize;

    @ApiModelProperty(value = "页数，不填返回默认页数1")
    private Integer pageNo;

    @ApiModelProperty(value = "1）未入参榜单listTopicId时：支持1 售价排序、2 销量排序；2）入参榜单listTopicId时：" +
            "当platform为1，选择到家业务类型：外卖商品券类型，支持1 售价排序、 2 销量降序、 3佣金降序，不填则默认为1；到家医药类型，支持2 销量降序、 " +
            "3 佣金降序，不填则默认为2； 当platform为2，选择到店业务类型：支持2 销量降序、 3佣金降序，不填则默认为2")
    private Integer sortField;

    @ApiModelProperty(value = "仅对到家业务类型生效，未入参榜单listTopicId时：1 升序，2 降序； 入参榜单listTopicId时：" +
            "1 升序，2 降序，并且仅对sortField为1售价排序的时候生效，其他筛选值不生效； 其他说明：不填则默认为1升序")
    private Integer ascDescOrder;

}
