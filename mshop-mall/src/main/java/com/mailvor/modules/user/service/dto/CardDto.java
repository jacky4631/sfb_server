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
public class CardDto {

    private String no;

    private String name;

    private String type;

}
