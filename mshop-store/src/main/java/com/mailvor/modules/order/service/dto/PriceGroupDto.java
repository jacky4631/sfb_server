package com.mailvor.modules.order.service.dto;

import com.mailvor.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName PriceGroup
 * @author huangyu
 * @Date 2019/10/27
 **/
@Data
public class PriceGroupDto {

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal costPrice;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal storeFreePostage;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal storePostage;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal totalPrice;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal vipPrice;

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal payIntegral;
}
