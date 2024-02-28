package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "MwGoodsQueryVo对象", description = "商品表查询参数")
public class GoodsListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    List<GoodsDetailVo> list;

}
