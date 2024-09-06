/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chaijing
 * @date 2022-10-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("system_demand_sensitive_word")
public class SensitiveWord implements Serializable {

    @TableId
    private Long id;

    private String badword;

}
