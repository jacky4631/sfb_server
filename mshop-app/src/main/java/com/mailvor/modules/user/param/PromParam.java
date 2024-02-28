package com.mailvor.modules.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName PromParam
 * @author huangyu
 * @Date 2019/11/12
 **/
@Data
public class PromParam  implements Serializable {

    @ApiModelProperty(value = "推荐人级别 0一级推荐人 1二级推荐人")
    private Integer grade = 0;

    @ApiModelProperty(value = "关键字搜索")
    private String  keyword;

    @ApiModelProperty(value = "页码")
    private Integer limit = 10;

    @ApiModelProperty(value = "页大小")
    private Integer page = 1;

    @ApiModelProperty(value = "排序")
    private String  sort;

    @ApiModelProperty(value = "1级用户uid")
    private Long uid;
}
