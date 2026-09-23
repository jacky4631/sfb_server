package com.dtk.api.response.search;

import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 14:33
 */
@Getter
@Setter
public class DtkHotSearchListResponse {
    private String theme;
    private String words;
    private String pic;
    private String wordDesc;
    private String label;
    private String hotValue;
    private String rankNum;
}
