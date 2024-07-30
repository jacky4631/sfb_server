package com.mailvor.modules.utils;

import com.mailvor.enums.PlatformEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TkOrderFee {
    double commission;
    String orderId;
    Date createTime;
    PlatformEnum platform;

    double rate;
}
