package com.mailvor.modules.user.service.dto;

import com.alibaba.fastjson.JSONObject;
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
public class TljDataDto {

    /**
     * 淘礼金ids
     * */
    private List<JSONObject> data;

}
