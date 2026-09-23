package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 超级分类请求响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:48
 */
@Getter
@Setter
public class DtkGetSuperCategoryResponse {
    private Integer cid;
    private String cname;
    private String cpic;
    private List<DtkGetSuperCategorySubResponse> subcategories;
}
