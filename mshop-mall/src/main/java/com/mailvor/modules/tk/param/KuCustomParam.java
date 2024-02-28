/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import static com.mailvor.modules.tk.constants.TkConstants.KU_CID;

/**
 *
 *
 * @author shenji
 * @date 2021-02-23
 */
@Data
@ApiModel(value="MwGoodQueryParam对象", description="商品表查询参数")
public class KuCustomParam{
    private static final long serialVersionUID = 1L;

    private String keyword="";

    private String category_id="";

    private String sort="";

    private String min_price="";

    private String max_price;

    private String min_sale="";

    private String min_rate="";

    private String min_avg="";

    private String min_coupon="";

    private String filtrate_type="";


    private String exclude_item="";

    private String back_count="";

    private String item="";
    private String discount="";
    private String collect="";
    private String classify ="";

    private String cid = KU_CID;
}
