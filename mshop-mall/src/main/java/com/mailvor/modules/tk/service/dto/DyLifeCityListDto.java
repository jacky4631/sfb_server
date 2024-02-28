/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* @author shenji
* @date 2022-12-17
*/
@Data
public class DyLifeCityListDto extends DyLifeCityChildrenDto implements Serializable {
    private List<DyLifeCityChildrenDto> children;
}
