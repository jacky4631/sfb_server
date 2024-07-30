/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName DetailDTO
 * @author huangyu
 * @Date 2019/10/12
 **/
@Data
public class DetailDto {
    private List<String> data;

    //private List<Map<String,List<Map<String,String>>>> res;

    private List<Map<String,Map<String,String>>> res;
}
