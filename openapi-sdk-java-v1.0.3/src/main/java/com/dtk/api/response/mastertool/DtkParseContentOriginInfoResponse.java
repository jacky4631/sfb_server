package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 淘系万能解析响应链接中的信息实体
 *
 * @author 1
 * @date 2020/11/11 16:06
 */
@Getter
@Setter
public class DtkParseContentOriginInfoResponse {
    private String title;
    private String shopName;
    private String shopLogo;
    private String image;
    private String startTime;
    private String endTime;
    private BigDecimal amount;
    private BigDecimal startFee;
    private BigDecimal price;
    private BigDecimal actualPrice;
    private String activityId;
    private String pid;
    private Integer status;
}
