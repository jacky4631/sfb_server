package com.mailvor.modules.tk.vo.vip;


import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 * @author shenji
 * @date 2019-10-19
 */
@Data
@Schema(description = "商品表查询参数")
public class VipGoodsDetailDataVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "描述")
    private String msg;
    @Schema(description = "商品码")
    private Integer code;
    @JSONField(serialize = false)
    @Schema(description = "商品")
    private VipGoodsDetailVO goods;
    @Schema(description = "商品")
    private List<VipGoodsDetailVO> data;


}
