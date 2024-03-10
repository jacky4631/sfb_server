package com.mailvor.modules.user.config;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class HbUnlockConfig {
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlockVip = 5;
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlock = 7;
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlockVip1 = 9;
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlockVip2 = 16;
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlockVip3 = 26;
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlock1 = 15;
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlock2 = 20;
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于0")
    private Integer unlock3 = 40;
}
