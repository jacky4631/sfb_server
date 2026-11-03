package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "接口返回参数")
public class VipResVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "code")
    private Integer code;

    @Schema(description = "msg")
    private String msg;

    private VipDataVo data;

}
