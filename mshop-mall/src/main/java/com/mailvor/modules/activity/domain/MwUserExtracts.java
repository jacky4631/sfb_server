/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class MwUserExtracts implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    List<MwUserExtract> extracts;

    @NotBlank
    private String opePwd;
}
