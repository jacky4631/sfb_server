package com.dtk.api.response.special;

import lombok.Data;

import java.util.List;

/**
 * 咚咚抢响应结果实体
 *
 * @author baige
 * @date 2020/11/12 11:21
 */
@Data
public class DtkDdqGoodsListResponse {
    private String ddqTime;
    private Integer status;
    private List<DtkDdqGoodsListItemResponse> goodsList;
    private List<DtkDdqRoundListResponse> roundsList;
}
