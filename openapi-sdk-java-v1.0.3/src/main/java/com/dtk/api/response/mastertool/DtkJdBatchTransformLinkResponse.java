package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 16:28
 */
@Getter
@Setter
public class DtkJdBatchTransformLinkResponse {

    @ApiModelProperty("批量转链后的内容")
    private String content;
}
