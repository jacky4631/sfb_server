package com.mailvor.modules.tk.vo;


import com.alibaba.fastjson.annotation.JSONField;
import com.mailvor.modules.tk.domain.MailvorDyKuOrder;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 * @author shenji
 * @date 2019-10-19
 */
@Data
@ApiModel(value = "DyKuResVo", description = "抖音库订单接口返回参数")
public class DyKuResVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    @JSONField(name="min_id")
    private Integer minId;

    private ArrayList<MailvorDyKuOrder> data;

}
