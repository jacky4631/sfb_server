package com.mailvor.modules.tk.vo.vip;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一数据结构
 * */
@Schema(description = "淘宝商品vo")
@Data
public class VipWordDataVO {

    @JSONField(name = "urlInfoList")
    @Schema(description = "转链数据")
    private List<VipWordVO> list;
}
