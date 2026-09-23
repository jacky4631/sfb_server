package com.dtk.api.response.special;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品历史券后价响应结果价格趋势实体
 *
 * @author baige
 * @date 2020/11/12 10:24
 */
@Data
public class DtkPriceTrendHistoricalPriceResponse {
    private BigDecimal actualPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;
}
