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
public class OrderExtractDto {
    private String type;
    private String orderId;
}
