package com.mailvor.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName OrderExtendDto
 * @Date 2022/12/20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTabDto implements Serializable {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "是否含有红包")
    private Boolean hasHb;

}
