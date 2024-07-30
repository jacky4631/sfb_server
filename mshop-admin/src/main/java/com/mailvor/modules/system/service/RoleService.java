/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.system.domain.Role;
import com.mailvor.modules.system.service.dto.RoleDto;
import com.mailvor.modules.system.service.dto.RoleQueryCriteria;
import com.mailvor.modules.system.service.dto.RoleSmallDto;
import com.mailvor.modules.system.service.dto.UserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author huangyu
* @date 2020-05-14
*/
public interface RoleService  extends BaseService<Role>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(RoleQueryCriteria criteria, Pageable pageable);


    /**
     * 查询数据分页
     * @param pageable 分页参数
     * @return Map<String,Object>
     */
    Object queryAlls(RoleQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<RoleDto>
    */
    List<Role> queryAll(RoleQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<RoleDto> all, HttpServletResponse response) throws IOException;

    /**
     * 根据用户ID查询
     * @param id 用户ID
     * @return /
     */
    List<RoleSmallDto> findByUsersId(Long id);

    /**
     * 根据角色查询角色级别
     * @param roles /
     * @return /
     */
    Integer findByRoles(Set<Role> roles);

    /**
     * 根据ID查询
     * @param id /
     * @return /
     */
    RoleDto findById(long id);

    /**
     * 修改绑定的菜单
     * @param resources /
     * @param roleDto /
     */
    void updateMenu(Role resources, RoleDto roleDto);

    /**
     * 创建
     * @param resources /
     * @return /
     */
    RoleDto create(Role resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(Role resources);

    /**
     * 获取用户权限信息
     * @param user 用户信息
     * @return 权限信息
     */
    Collection<SimpleGrantedAuthority> mapToGrantedAuthorities(UserDto user);

    void delete(Set<Long> ids);
}
