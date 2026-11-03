package com.mailvor.modules.product.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName AttrValueDTO
 * @author huangyu
 * @Date 2019/10/23
 **/
@Data
public class AttrValueDto {

    @Schema(description = "属性")
    private String attr;

    @Schema(description = "是否选择")
    private Boolean check = false;
}
