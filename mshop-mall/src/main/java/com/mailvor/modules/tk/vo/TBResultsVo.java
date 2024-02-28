package com.mailvor.modules.tk.vo;


import com.mailvor.modules.tk.domain.MailvorTbOrder;
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
public class TBResultsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<MailvorTbOrder> publisher_order_dto;

}
