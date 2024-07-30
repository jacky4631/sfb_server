package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 * @author shenji
 * @date 2019-10-19
 */
@Data
@ApiModel(value = "MtResVo", description = "接口返回参数")
public class MtResVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "status")
    private Integer code;

    private MtDataVo msg;

}
