package com.mailvor.modules.order.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OtherDto
 * @author huangyu
 * @Date 2019/10/27
 **/
@Data
public class OtherDto implements Serializable {
    //线下包邮
    private String offlinePostage;
    //积分抵扣
    private String integralRatio;

    //最大
    private String integralMax;

    //满多少
    private String integralFull;

}
