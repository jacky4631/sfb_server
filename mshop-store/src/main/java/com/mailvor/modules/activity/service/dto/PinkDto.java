package com.mailvor.modules.activity.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName PinkDto
 * @author huangyu
 * @Date 2019/11/19
 **/
@Data
public class PinkDto implements Serializable {

    @Schema(description = "拼团ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "拼团总人数")
    private Integer people;

    @Schema(description = "拼团产品单价")
    private Double price;

    @Schema(description = "拼团结束时间")
    private Date stopTime;

    @Schema(description = "拼团用户昵称")
    private String nickname;

    @Schema(description = "拼团用户头像")
    private String avatar;

    @Schema(description = "参与的拼团的id集合")
    private String count;

    @Schema(description = "拼团时效：小时")
    private String h;

    @Schema(description = "拼团时效：分钟")
    private String i;

    @Schema(description = "拼团时效：秒")
    private String s;


}
