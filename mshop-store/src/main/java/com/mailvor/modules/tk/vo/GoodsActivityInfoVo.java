package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 大淘客搜索商品 活动信息对象
 * </p>
 *
 * @author shenji
 */
@Data
@ApiModel(value = "GoodsActivityInfoVo对象", description = "大淘客搜索活动信息")
public class GoodsActivityInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动ID")
    private Long activityId;
}
