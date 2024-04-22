/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDomain;
import lombok.*;

import java.util.Date;

/**
* @author shenji
* @date 2022-08-29
*/
@Data
public class TkOrder extends BaseDomain {

    /** 是否拆过红包，0=未拆红包 1=已拆红包 2=已退款 3=已失效*/
    private Integer bind;

    /** 用户ID */
    private Long uid;

    /** 积分 */
    private Long integral;

    /** 未用 */
    private Integer cash;
    /** 拆红包奖励金额 */
    private Double hb;

    /** 内部类型 0=默认 2=热度*/
    private Integer innerType;

    /** 拆红包时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date spreadHbTime;

}
