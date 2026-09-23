package com.dtk.api.request.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * dtk api: page param，independent of business entity
 *
 * @author 1
 * @date 2020/11/9 17:15
 */
@Getter
@Setter
public class DtkPageParamRequest {
    @ApiModelProperty(value = "每页条数，默认为100，最大值200，若小于10，则按10条处理，每页条数仅支持输入10,50,100,200", required = true, example =
            "10")
    private Integer pageSize;
    @ApiModelProperty(value = "分页id，默认为1，支持传统的页码分页方式和scroll_id分页方式，根据用户自身需求传入值。示例1：商品入库，则首次传入1，后续传入接口返回的pageid" +
            "，接口将持续返回符合条件的完整商品列表，该方式可以避免入口商品重复；示例2：根据pageSize和totalNum计算出总页数，按照需求返回指定页的商品（该方式可能在临近页取到重复商品）", required
            = true, example = "1")
    private String pageId;
}
