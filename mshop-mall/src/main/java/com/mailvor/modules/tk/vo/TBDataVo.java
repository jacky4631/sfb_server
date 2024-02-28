package com.mailvor.modules.tk.vo;


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
public class TBDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean has_pre;
    private boolean has_next;
    private Integer page_no;
    private Integer page_size;
    private String position_index;

    private TBResultsVo results;
}
