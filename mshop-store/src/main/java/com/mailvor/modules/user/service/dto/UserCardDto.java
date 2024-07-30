package com.mailvor.modules.user.service.dto;

import lombok.*;

import java.util.List;

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
public class UserCardDto {

    private List<CardDto> cards;

}
