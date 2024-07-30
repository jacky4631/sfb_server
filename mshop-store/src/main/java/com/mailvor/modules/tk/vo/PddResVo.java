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
@ApiModel(value = "DataokeResVo", description = "接口返回参数")
public class PddResVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "code")
    private Integer code;

    @ApiModelProperty(value = "msg")
    private String msg;

    private PddDataVo data;

}
