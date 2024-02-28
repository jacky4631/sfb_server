package com.mailvor.modules.shop.vo;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.activity.vo.MwStoreCombinationQueryVo;
import com.mailvor.modules.activity.vo.MwStoreSeckillQueryVo;
import com.mailvor.modules.mp.service.dto.MwWechatLiveDto;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
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
public class ShopDetailVo {
    @ApiModelProperty("名称")
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
    private List<JSONObject> goods;

}
