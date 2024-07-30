package com.mailvor.modules.user.service.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("店铺详情")
public class ShopDetailDto {
    @ApiModelProperty("uid")
    private Long uid;
    @ApiModelProperty("名称")
    private String nickname;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("描述")
    private String desc;
    @ApiModelProperty("已关注")
    private Integer fans;
    @ApiModelProperty("销量")
    private Integer sales;
    //首页按钮
    @ApiModelProperty("商品列表")
    private List goods;

}
