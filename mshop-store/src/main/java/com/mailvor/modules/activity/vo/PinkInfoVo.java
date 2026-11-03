package com.mailvor.modules.activity.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.user.vo.MwUserQueryVo;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PinkInfoVo
 * @author huangyu
 * @Date 2019/11/20
 **/
@Data
@Builder
public class PinkInfoVo implements Serializable {

    @Schema(description = "还差几人成团")
    private Integer count;

    @Schema(description = "当前拼团数据返回订单编号")
    private String currentPinkOrder;

    @Schema(description = "是否完成 0未完成 1完成")
    private Integer isOk = 0;

    @Schema(description = "拼团信息列表")
    private List<MwStorePinkQueryVo> pinkAll;

    @Schema(description = "拼团状态 0未成功，进行中 1已成功 -1拼团失败")
    private Integer pinkBool = 0;

    @Schema(description = "拼团信息")
    private MwStorePinkQueryVo pinkT;

    @Schema(description = "拼团内容信息")
    private MwStoreCombinationQueryVo storeCombination;

    @Schema(description = "拼团内容")
    private String storeCombinationHost;

    @Schema(description = "是否在团内 0不在 1在")
    private Integer userBool;

    @Schema(description = "拼团用户信息")
    private MwUserQueryVo userInfo;

    @Schema(description = "库存唯一值")
    private String uniqueId;
}
