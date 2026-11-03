package com.mailvor.modules.user.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "店铺详情")
public class ShopDetailDto {
    @Schema(description = "uid")
    private Long uid;
    @Schema(description = "名称")
    private String nickname;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "描述")
    private String desc;
    @Schema(description = "已关注")
    private Integer fans;
    @Schema(description = "销量")
    private Integer sales;
    //首页按钮
    @Schema(description = "商品列表")
    private List goods;

}
