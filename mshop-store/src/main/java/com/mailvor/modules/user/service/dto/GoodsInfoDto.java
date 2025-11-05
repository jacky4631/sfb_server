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
public class GoodsInfoDto {
    private String imageUrl;
    private String owner;
    private Long mainSkuId;
    private Long productId;
    private String shopName;
    private Long shopId;
    private String goodsId;
}
