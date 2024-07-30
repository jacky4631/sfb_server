/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.dto;


import lombok.Data;

import java.util.List;

@Data
public class OrderCountDto {

    private List<String> column;

    private List<OrderCountData> orderCountDatas;

    @Data
    public static class OrderCountData{
        private String name;

        private Integer value;
    }
}
