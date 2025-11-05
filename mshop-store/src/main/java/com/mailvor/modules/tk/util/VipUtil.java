package com.mailvor.modules.tk.util;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VipUtil {

    public static String getVipShopOpenId(Long uid) {
        if(uid != null) {
            return uid.toString();
        }
        return "0";
    }

}
