package com.mailvor.modules.user.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.mailvor.modules.user.domain.MwUserFeeLog;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName PromUserDto
 * @author huangyu
 * @Date 2019/11/12
 **/
@Data
public class PromUserDto {
    private String avatar;
    private String  nickname;
    private String  phone;
    private Integer childCount;
    private Long uid;
    private String time;

    private String updateTime;

    private String fee;

    private Integer level;
    private Integer levelJd;
    private Integer levelPdd;
    private Integer levelDy;
    private Integer levelVip;

}
