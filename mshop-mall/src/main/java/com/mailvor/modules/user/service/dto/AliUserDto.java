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
public class AliUserDto {

    private String userId;

    private String avatar;

    private String city;

    private String nickname;

    private String province;

    private String gender;


}
