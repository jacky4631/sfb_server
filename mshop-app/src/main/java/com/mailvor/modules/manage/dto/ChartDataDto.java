package com.mailvor.modules.manage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ChartDataDto
 * @author huangyu
 * @Date 2019/11/25
 **/
@Data
public class ChartDataDto implements Serializable {
    private Double num;
    private String time;
}
