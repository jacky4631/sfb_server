package com.dtk.api.response.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 京东商品类目响应结果实体
 *
 * @author QC
 * @date 2021-9-24
 */
@Setter
@Getter
public class DtkJdGoodsTypeResponse {
    private Integer id;
    private String name;
    @ApiModelProperty(value = "类目级别")
    private Integer level;
    @ApiModelProperty(value = "值=0时为顶点cat_id,通过树顶级节点获取cat树")
    private Integer parentId;
}
