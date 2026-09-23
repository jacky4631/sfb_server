package com.dtk.api.request.special.subranking;

import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.utils.RequiredCheck;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 各大榜单请求参数
 *
 * @author baige
 * @date 2020/11/25 17:01
 */
@Getter
@Setter
public class DtkGetRankingListBaseRequest extends DtkPageParamRequest {
    @ApiModelProperty(value = "版本号", example = "v1.3.0")
    private String version = "v1.3.0";
    @RequiredCheck
    @ApiModelProperty(value = "榜单类型，1.实时榜 2.全天榜 3.热推榜 4.复购榜 5.热词飙升榜 6.热词排行榜 7.综合热搜榜", required = true)
    private Integer rankType;
    @ApiModelProperty(value = "大淘客一级类目id，仅对实时榜单、全天榜单有效")
    private Integer cid;
}
