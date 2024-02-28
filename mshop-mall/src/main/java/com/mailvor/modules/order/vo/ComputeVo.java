package com.mailvor.modules.order.vo;

import com.mailvor.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName ComputeVo
 * @author huangyu
 * @Date 2019/10/27
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComputeVo implements Serializable {

    private Double usedIntegral;
    //使用了多少积分
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal payIntegral;
}
