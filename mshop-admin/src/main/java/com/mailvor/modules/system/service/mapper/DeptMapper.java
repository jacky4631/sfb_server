/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.system.domain.Dept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
* @author huangyu
* @date 2020-05-14
*/
@Repository
public interface DeptMapper extends CoreMapper<Dept> {

    @Select("select m.* from dept m LEFT JOIN roles_depts t on m.id= t.dept_id LEFT JOIN role r on r.id = t.role_id where r.id = ${roleId}")
    Set<Dept> findDeptByRoleId(@Param("roleId") Long roleId);

    @Select("select * from dept m LEFT JOIN roles_depts t on m.id= t.dept_id LEFT JOIN role r on r.id = t.role_id where r.id = #{roleId}")
    Set<Dept> findDeptByRoleIds(@Param("roleIds") Set<Long> roleId);
}
