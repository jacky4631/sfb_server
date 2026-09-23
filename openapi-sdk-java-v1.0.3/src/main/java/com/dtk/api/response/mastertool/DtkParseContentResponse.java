package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 淘系万能解析响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:02
 */
@Getter
@Setter
public class DtkParseContentResponse {
    private String goodsId;
    private String originUrl;
    private String originType;
    private String commissionType;
    private BigDecimal commissionRate;
    private DtkParseContentOriginInfoResponse originInfo;
    private String itemId;
    private String itemName;
    private String mainPic;
    private String dataType;
}
