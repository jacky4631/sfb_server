package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName PromParam
 * @author huangyu
 * @Date 2019/11/12
 **/
@Data
public class PromParam  implements Serializable {

    @Schema(description = "推荐人级别 0一级推荐人 1二级推荐人")
    private Integer grade = 0;

    @Schema(description = "关键字搜索")
    private String  keyword;

    @Schema(description = "页码")
    private Integer limit = 10;

    @Schema(description = "页大小")
    private Integer page = 1;

    @Schema(description = "排序")
    private String  sort;

    @Schema(description = "1级用户uid")
    private Long uid;
}
