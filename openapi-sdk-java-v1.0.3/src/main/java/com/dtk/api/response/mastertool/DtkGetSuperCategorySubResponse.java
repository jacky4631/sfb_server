package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

/**
 * 超级分类请求响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:53
 */
@Getter
@Setter
public class DtkGetSuperCategorySubResponse {
    private Integer subcid;
    private String subcname;
    private String scpic;
}
