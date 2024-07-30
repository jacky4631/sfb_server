/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.category.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.category.domain.MwStoreCategory;
import com.mailvor.modules.category.service.dto.MwStoreCategoryDto;
import com.mailvor.modules.category.service.dto.MwStoreCategoryQueryCriteria;
import com.mailvor.utils.CateDTO;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author mazhongjun
* @date 2020-05-12
*/
public interface MwStoreCategoryService extends BaseService<MwStoreCategory>{

    List<CateDTO> getList();

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreCategoryQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreCategoryDto>
    */
    List<MwStoreCategoryDto> queryAll(MwStoreCategoryQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreCategoryDto> all, HttpServletResponse response) throws IOException;

    /**
     * 构建树形
     * @param categoryDTOS 分类列表
     * @return map
     */
    Map<String,Object> buildTree(List<MwStoreCategoryDto> categoryDTOS);

    /**
     * 检测分类是否操过二级
     * @param pid 父级id
     * @return boolean
     */
    boolean checkCategory(int pid);

    /**
     * 检测商品分类必选选择二级
     * @param id 分类id
     * @return boolean
     */
    boolean checkProductCategory(int id);
}
