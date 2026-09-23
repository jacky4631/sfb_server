package com.dtk.api.response.special;

import lombok.Data;

/**
 * 咚咚抢响应结果场次实体
 *
 * @author baige
 * @date 2020/11/12 11:23
 */
@Data
public class DtkDdqRoundListResponse {
    private String ddqTime;
    private Integer status;
}
