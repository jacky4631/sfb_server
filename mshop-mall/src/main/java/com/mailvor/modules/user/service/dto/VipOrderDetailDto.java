package com.mailvor.modules.user.service.dto;

import lombok.*;

/**
 * @ClassName WechatUserDTO
 * @author huangyu
 * @Date 2020/6/4
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VipOrderDetailDto {
    private String brandStoreName;
    private String brandStoreSn;

    private String commCode;
    private String commName;

    private String commission;
    private String commissionRate;

    private String commissionTotalCost;
    private Integer goodsCount;

    private String goodsFinalPrice;
    private String goodsId;

    private String goodsName;
    private String goodsThumb;
    private Boolean isSubsidyTaskOrder;
    private String orderSource;
    private String sizeId;
    private String spuId;
    private Integer status;
}
