package com.mailvor.modules.push.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class NotificationDto {
    private String alert;
    private JSONObject android = new JSONObject();
    private JSONObject ios = new JSONObject();
}
