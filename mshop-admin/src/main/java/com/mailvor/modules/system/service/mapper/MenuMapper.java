/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.system.domain.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
* @author huangyu
* @date 2020-05-14
*/
@Repository
@Mapper
public interface MenuMapper extends CoreMapper<Menu> {


    /**
     * 根据菜单的 PID 查询
     * @param pid /
     * @return /
     */
    @Select("SELECT * from system_menu m where m.pid = #{pid} and m.is_del = 0 ")
    List<Menu> findByPid(@Param("pid") long pid);

    @Select("select m.* from system_menu m LEFT JOIN system_roles_menus t on m.id= t.menu_id LEFT JOIN system_role r on r.id = t.role_id where r.id = #{roleId} and m.is_del=0")
    Set<Menu> findMenuByRoleId(@Param("roleId") Long roleId);
    @Select("<script>select m.* from system_menu m LEFT OUTER JOIN system_roles_menus t on m.id= t.menu_id LEFT OUTER JOIN system_role r on r.id = t.role_id where m.is_del=0 and m.type!=2 and  r.id in <foreach collection=\"roleIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach> order by m.sort asc</script>")
    List<Menu> selectListByRoles(@Param("roleIds") List<Long> roleIds);
}
