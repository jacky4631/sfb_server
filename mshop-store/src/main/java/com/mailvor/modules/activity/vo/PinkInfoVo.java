package com.mailvor.modules.activity.vo;


import com.mailvor.modules.user.vo.MwUserQueryVo;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "还差几人成团")
    private Integer count;

    @ApiModelProperty(value = "当前拼团数据返回订单编号")
    private String currentPinkOrder;

    @ApiModelProperty(value = "是否完成 0未完成 1完成")
    private Integer isOk = 0;

    @ApiModelProperty(value = "拼团信息列表")
    private List<MwStorePinkQueryVo> pinkAll;

    @ApiModelProperty(value = "拼团状态 0未成功，进行中 1已成功 -1拼团失败")
    private Integer pinkBool = 0;

    @ApiModelProperty(value = "拼团信息")
    private MwStorePinkQueryVo pinkT;

    @ApiModelProperty(value = "拼团内容信息")
    private MwStoreCombinationQueryVo storeCombination;

    @ApiModelProperty(value = "拼团内容")
    private String storeCombinationHost;

    @ApiModelProperty(value = "是否在团内 0不在 1在")
    private Integer userBool;

    @ApiModelProperty(value = "拼团用户信息")
    private MwUserQueryVo userInfo;

    @ApiModelProperty(value = "库存唯一值")
    private String uniqueId;
}
