package com.mailvor.modules.push.dto;

import com.mailvor.common.json.JsonObject;
import lombok.Data;

@Data
public class NotificationDto {
    private String alert;
    private JsonObject android = new JsonObject();
    private JsonObject ios = new JsonObject();
}
