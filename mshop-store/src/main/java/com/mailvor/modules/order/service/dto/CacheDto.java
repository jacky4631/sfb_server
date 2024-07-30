package com.mailvor.modules.order.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName CacheDto
 * @author huangyu
 * @Date 2019/10/27
 **/
@Data
public class CacheDto implements Serializable {
    private BigDecimal price;
}
