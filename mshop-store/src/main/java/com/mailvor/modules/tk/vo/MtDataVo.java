package com.mailvor.modules.tk.vo;


import com.mailvor.modules.tk.domain.MailvorMtOrder;
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
public class MtDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 如果为end说明没有更多订单了
     * 用于cps和cps订单查询
     * */
    private String positionIndex;

    /**
     * 异常订单使用下面的数据
     * 当recordCount > page*pageSize说明还有更多订单
     * */
    private Integer recordCount;
    private Integer page;
    private Integer pageSize;

    private ArrayList<MailvorMtOrder> records;
}
