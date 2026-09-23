package com.mailvor.modules.tk.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "mw_reconcile_diff", autoResultMap = true)
public class MwReconcileDiff implements Serializable {

    @TableId
    private Long id;

    private Long logId;

    private String platform;

    private Integer diffType;

    private String orderNo;

    private BigDecimal platformAmount;

    private BigDecimal localAmount;

    private BigDecimal platformCommission;

    private BigDecimal localCommission;

    private Integer handleStatus;

    private String handleRemark;

    private Date handleTime;

    private Date createTime;
}
