package com.mailvor.modules.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户账单表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-27
 */
@Data
@ApiModel(value = "MwUserBillQueryVo对象", description = "用户账单表查询参数")
public class MwUserBillQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户账单id")
    private Long id;



    @ApiModelProperty(value = "0 = 支出 1 = 获得")
    private Integer pm;

    @ApiModelProperty(value = "账单标题")
    private String title;

    @ApiModelProperty(value = "明细数字")
    private BigDecimal number;


    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "添加时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    private BigDecimal balance;

    /** 明细类型 */
    private String type;

    private Date orderCreateTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date unlockTime;


    /** 0=已解锁 默认值 1=待解锁 2=失效  */
    private Integer unlockStatus;

}
