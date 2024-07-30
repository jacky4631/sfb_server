/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.category.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.category.domain.MwStoreCategory;
import com.mailvor.modules.category.service.MwStoreCategoryService;
import com.mailvor.modules.category.service.dto.MwStoreCategoryDto;
import com.mailvor.modules.category.service.dto.MwStoreCategoryQueryCriteria;
import com.mailvor.modules.category.service.mapper.StoreCategoryMapper;
import com.mailvor.utils.CateDTO;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
* @author mazhongjun
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreCategoryServiceImpl extends BaseServiceImpl<StoreCategoryMapper, MwStoreCategory> implements MwStoreCategoryService {

    private final IGenerator generator;

    /**
     * 获取分类列表树形列表
     * @return List
     */
    @Override
    public List<CateDTO> getList() {
       LambdaQueryWrapper<MwStoreCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreCategory::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .orderByAsc(MwStoreCategory::getSort);
        List<CateDTO> list = generator.convert(baseMapper.selectList(wrapper),CateDTO.class);
        return TreeUtil.list2TreeConverter(list,0);
    }

    //===============================//

    @Override
    public Map<String, Object> queryAll(MwStoreCategoryQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCategoryDto> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    public List<MwStoreCategoryDto> queryAll(MwStoreCategoryQueryCriteria criteria){
        return generator.convert(this.baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreCategory.class, criteria)),
                MwStoreCategoryDto.class);
    }


    @Override
    public void download(List<MwStoreCategoryDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCategoryDto mwStoreCategory : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("父id", mwStoreCategory.getPid());
            map.put("分类名称", mwStoreCategory.getCateName());
            map.put("排序", mwStoreCategory.getSort());
            map.put("图标", mwStoreCategory.getPic());
            map.put("是否推荐", mwStoreCategory.getIsShow());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 构建树形
     * @param categoryDTOS 分类列表
     * @return map
     */
    @Override
    public Map<String, Object> buildTree(List<MwStoreCategoryDto> categoryDTOS) {
        Set<MwStoreCategoryDto> trees = new LinkedHashSet<>();
        Set<MwStoreCategoryDto> cates = new LinkedHashSet<>();
        List<String> deptNames = categoryDTOS.stream().map(MwStoreCategoryDto::getCateName)
                .collect(Collectors.toList());

        //MwStoreCategoryDto categoryDTO = new MwStoreCategoryDto();
        Boolean isChild;
        List<MwStoreCategory> categories = this.list();
        for (MwStoreCategoryDto deptDTO : categoryDTOS) {
            isChild = false;
            if ("0".equals(deptDTO.getPid().toString())) {
                trees.add(deptDTO);
            }
            for (MwStoreCategoryDto it : categoryDTOS) {
                if (it.getPid().equals(deptDTO.getId())) {
                    isChild = true;
                    if (deptDTO.getChildren() == null) {
                        deptDTO.setChildren(new ArrayList<MwStoreCategoryDto>());
                    }
                    deptDTO.getChildren().add(it);
                }
            }
            if (isChild) {
                cates.add(deptDTO);
            }
            for (MwStoreCategory category : categories) {
                if (category.getId().equals(deptDTO.getPid()) && !deptNames.contains(category.getCateName())) {
                    cates.add(deptDTO);
                }
            }
        }


        if (CollectionUtils.isEmpty(trees)) {
            trees = cates;
        }


        Integer totalElements = categoryDTOS != null ? categoryDTOS.size() : 0;

        Map<String, Object> map = Maps.newHashMap();
        map.put("totalElements", totalElements);
        map.put("content", CollectionUtils.isEmpty(trees) ? categoryDTOS : trees);
        return map;
    }


    /**
     * 检测分类是否操过二级
     * @param pid 父级id
     * @return boolean
     */
    @Override
    public boolean checkCategory(int pid){
        if(pid == 0) {
            return true;
        }
        MwStoreCategory mwStoreCategory =  this.getOne(Wrappers.<MwStoreCategory>lambdaQuery()
                        .eq(MwStoreCategory::getId,pid));
        return mwStoreCategory.getPid() <= 0;
    }

    /**
     * 检测商品分类必选选择二级
     * @param id 分类id
     * @return boolean
     */
    @Override
    public boolean checkProductCategory(int id){
        MwStoreCategory mwStoreCategory =  this.getOne(Wrappers.<MwStoreCategory>lambdaQuery()
                .eq(MwStoreCategory::getId,id));
        return mwStoreCategory.getPid() != 0;
    }

}
