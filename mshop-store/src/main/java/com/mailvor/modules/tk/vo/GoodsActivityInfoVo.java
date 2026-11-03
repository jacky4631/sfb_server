package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "大淘客搜索活动信息")
public class GoodsActivityInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "活动ID")
    private Long activityId;
}
