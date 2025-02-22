/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.dto;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName StoreOrderCartInfo
 * @author huangyu
 * @Date 2019/10/14
 **/


@Data
public class StoreOrderCartInfoDto {


    private Integer id;


    private Integer oid;


    private Integer cartId;


    private String cartInfo;


    private String unique;

    private Map<String,Object> cartInfoMap;


}
