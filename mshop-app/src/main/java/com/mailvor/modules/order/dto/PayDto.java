package com.mailvor.modules.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName PayDto
 * @author huangyu
 * @Date 2019/11/7
 **/
@Data
public class PayDto implements Serializable {
    private String from;
    private String paytype;
    private String uni;
}
