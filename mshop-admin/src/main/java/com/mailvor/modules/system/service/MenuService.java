/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.system.domain.Menu;
import com.mailvor.modules.system.domain.vo.MenuVo;
import com.mailvor.modules.system.service.dto.MenuDto;
import com.mailvor.modules.system.service.dto.MenuQueryCriteria;
import com.mailvor.modules.system.service.dto.RoleSmallDto;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author huangyu
* @date 2020-05-14
*/
public interface MenuService  extends BaseService<Menu>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MenuQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MenuDto>
    */
    List<Menu> queryAll(MenuQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MenuDto> all, HttpServletResponse response) throws IOException;

    /**
     * 构建菜单树
     * @param menuDtos 原始数据
     * @return /
     */
    Map<String,Object> buildTree(List<MenuDto> menuDtos);

    /**
     * 构建菜单树
     * @param menuDtos /
     * @return /
     */
    List<MenuVo> buildMenus(List<MenuDto> menuDtos);

    /**
     * 获取菜单树
     * @param menus /
     * @return /
     */
    Object getMenuTree(List<Menu> menus);


    /**
     * 获取待删除的菜单
     * @param menuList /
     * @param menuSet /
     * @return /
     */
    Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * 根据pid查询
     * @param pid /
     * @return /
     */
    List<Menu> findByPid(long pid);

    /**
     * 根据角色查询
     * @param roles /
     * @return /
     */
    List<MenuDto> findByRoles(List<RoleSmallDto> roles);

    /**
     * 删除
     * @param menuSet /
     */
    void delete(Set<Menu> menuSet);

    /**
     * 编辑
     * @param resources /
     */
    void update(Menu resources);

    Object create(Menu resources);
}
