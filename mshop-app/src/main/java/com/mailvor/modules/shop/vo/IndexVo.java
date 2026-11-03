package com.mailvor.modules.shop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.activity.vo.MwStoreCombinationQueryVo;
import com.mailvor.modules.activity.vo.MwStoreSeckillQueryVo;
import com.mailvor.modules.mp.service.dto.MwWechatLiveDto;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
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
@Schema(description = "首页数据")
public class IndexVo {

    @Schema(description = "banner")
    private List<JsonObject> banner;
    //首页按钮
    @Schema(description = "首页按钮")
    private List<JsonObject> menus;
    //精品推荐->拼团
    @Schema(description = "精品推荐")
    private List<MwStoreProductQueryVo> bastList;
    //首发新品->秒杀
    @Schema(description = "首发新品")
    private List<MwStoreProductQueryVo> firstList;
    //猜你喜欢
    @Schema(description = "猜你喜欢")
    private List<MwStoreProductQueryVo> benefit;
    //热门榜单
    @Schema(description = "热门榜单")
    private List<MwStoreProductQueryVo> likeInfo;
    //滚动
    @Schema(description = "滚动")
    private List<JsonObject> roll;
    //地图key
    @Schema(description = "地图key")
    private String mapKey;
    //精品推荐->拼团
    @Schema(description = "精品推荐->拼团")
    private List<MwStoreCombinationQueryVo> combinationList;
    //首发新品->秒杀
    @Schema(description = "首发新品->秒杀")
    private List<MwStoreSeckillQueryVo> seckillList;
    //直播间信息
    @Schema(description = "直播间")
    private List<MwWechatLiveDto> liveList;

}
