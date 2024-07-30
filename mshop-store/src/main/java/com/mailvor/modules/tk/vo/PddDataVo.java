package com.mailvor.modules.tk.vo;


import com.mailvor.modules.tk.domain.MailvorPddOrder;
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
public class PddDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<MailvorPddOrder> orderList;
}
