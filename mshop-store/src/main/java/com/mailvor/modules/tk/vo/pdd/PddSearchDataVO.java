package com.mailvor.modules.tk.vo.pdd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多搜索商品vo")
@Data
public class PddSearchDataVO {

    @Schema(description = "总数")
    @JSONField(name = "totalCount")
    private Integer total;
    @Schema(description = "翻页时建议填写前页返回的list_id值")
    private String listId;
    @Schema(description = "搜索id，建议生成推广链接时候填写，提高收益")
    private String searchId;
    @JSONField(name = "goodsList")
    @Schema(description = "商品列表")
    private List<PddSearchVO> list;
}
