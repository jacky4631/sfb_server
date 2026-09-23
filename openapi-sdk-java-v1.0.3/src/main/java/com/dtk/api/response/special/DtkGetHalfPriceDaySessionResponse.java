package com.dtk.api.response.special;

import lombok.Data;

/**
 * 每日半价响应结果状态实体
 *
 * @author baige
 * @date 2020/11/12 10:04
 */
@Data
public class DtkGetHalfPriceDaySessionResponse {
    private String hpdTime;
    private String status;
}
