/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.dto;

import lombok.Data;

/**
 * @ClassName UserBillDTO
 * @author huangyu
 * @Date 2019/12/11
 **/
@Data
public class UserBillDto {
    private Integer pm;
    private String gtitle;
    private String category;
    private String type;
    private Double number;
    private String nickname;
}
