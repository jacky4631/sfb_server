package com.dtk.api.response.search;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 热搜记录响应结果实体
 *
 * @author 1
 * @date 2020/11/11 17:31
 */
@Getter
@Setter
public class DtkGetTop100Response {
    private List<String> hotWords;
}
