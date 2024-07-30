/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwExpendEditParam extends MwExpendParam {
    @NotNull(message = "id不能为空")
    private Long id;


}
