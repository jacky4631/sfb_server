package com.mailvor.modules.tk.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "mw_reconcile_log", autoResultMap = true)
public class MwReconcileLog implements Serializable {

    @TableId
    private Long id;

    private Date reconcileDate;

    private String platform;

    private Integer totalPlatform;

    private Integer totalLocal;

    private Integer matchCount;

    private Integer missingCount;

    private Integer extraCount;

    private Integer amountDiffCount;

    private Integer status;

    private Date createTime;

    private Date finishTime;
}
