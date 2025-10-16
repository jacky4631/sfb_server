package com.mailvor.modules.tk.vo.vip;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一数据结构
 * */
@Schema(description = "唯品会搜索商品vo")
@Data
public class VipSearchDataVO {

    @Schema(description = "总数")
    private Integer total;
    @JSONField(name = "goodsInfoList")
    @Schema(description = "商品列表")
    private List<VipSearchVO> list;
}
