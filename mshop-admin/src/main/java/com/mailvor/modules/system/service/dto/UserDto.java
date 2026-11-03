/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
* @author huangyu
* @date 2020-05-14
*/
@Data
public class UserDto implements Serializable {


    @Schema(hidden = true)
    private Long id;

    private String username;

    private String nickName;

    private String sex;

    private String avatar;

    private String email;

    private String phone;

    private Boolean enabled;

    @JsonIgnore
    private String password;

    private Timestamp lastPasswordResetTime;

    @Schema(hidden = true)
    private Set<RoleSmallDto> roles;

    @Schema(hidden = true)
    private JobSmallDto job;

    private DeptSmallDto dept;

    private Long deptId;

    private Timestamp createTime;
}
