/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@ApiModel(value = "MwUserCardQueryVo对象", description = "用户表查询参数")
public class MwUserCardQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 用户id */
    @TableId(value = "id", type = IdType.NONE)
    private Long uid;

    /** 身份证号码 */
    private String cardNo;
    private String bankNo;
    private String cardNoMd5;
    private String bankNoMd5;
    /** 姓名 */
    private String cardName;
    /** 手机号 */
    private String phone;

    private String cardExpired;

    /** 身份证人像面地址 */
    private String cardFPath;
    /** 身份证国徽面地址 */
    private String cardBPath;
    /** 合同地址 */
    private String contractPath;

    /** 人脸地址 */
    private String facePath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

}
