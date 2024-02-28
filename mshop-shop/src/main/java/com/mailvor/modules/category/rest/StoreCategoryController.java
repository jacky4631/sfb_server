/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.category.rest;

import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.category.domain.MwStoreCategory;
import com.mailvor.modules.category.service.MwStoreCategoryService;
import com.mailvor.modules.category.service.dto.MwStoreCategoryDto;
import com.mailvor.modules.category.service.dto.MwStoreCategoryQueryCriteria;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.service.MwStoreProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* @author huangyu
* @date 2019-10-03
*/
@Api(tags = "商城:商品分类管理")
@RestController
@RequestMapping("api")
public class StoreCategoryController {


    private final MwStoreCategoryService mwStoreCategoryService;
    private final MwStoreProductService mwStoreProductService;


    public StoreCategoryController(MwStoreCategoryService mwStoreCategoryService,
                                   MwStoreProductService mwStoreProductService) {
        this.mwStoreCategoryService = mwStoreCategoryService;
        this.mwStoreProductService = mwStoreProductService;
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/mwStoreCategory/download")
    @PreAuthorize("@el.check('admin','MWSTORECATEGORY_SELECT')")
    public void download(HttpServletResponse response, MwStoreCategoryQueryCriteria criteria) throws IOException {
        mwStoreCategoryService.download(mwStoreCategoryService.queryAll(criteria), response);
    }


    @Log("查询商品分类")
    @ApiOperation(value = "查询商品分类")
    @GetMapping(value = "/mwStoreCategory")
    @PreAuthorize("hasAnyRole('admin','MWSTORECATEGORY_ALL','MWSTORECATEGORY_SELECT')")
    public ResponseEntity getMwStoreCategorys(MwStoreCategoryQueryCriteria criteria, Pageable pageable){
        List<MwStoreCategoryDto> categoryDTOList = mwStoreCategoryService.queryAll(criteria);
        return new ResponseEntity<>(mwStoreCategoryService.buildTree(categoryDTOList),HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增商品分类")
    @ApiOperation(value = "新增商品分类")
    @PostMapping(value = "/mwStoreCategory")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PreAuthorize("hasAnyRole('admin','MWSTORECATEGORY_ALL','MWSTORECATEGORY_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwStoreCategory resources){
        if(resources.getPid() != null && resources.getPid() > 0 && StrUtil.isBlank(resources.getPic())) {
            throw new MshopException("子分类图片必传");
        }

        boolean checkResult = mwStoreCategoryService.checkCategory(resources.getPid());
        if(!checkResult) {
            throw new MshopException("分类最多能添加2级哦");
        }

        return new ResponseEntity<>(mwStoreCategoryService.save(resources),HttpStatus.CREATED);
    }

    @ForbidSubmit
    @Log("修改商品分类")
    @ApiOperation(value = "修改商品分类")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PutMapping(value = "/mwStoreCategory")
    @PreAuthorize("hasAnyRole('admin','MWSTORECATEGORY_ALL','MWSTORECATEGORY_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreCategory resources){
        if(resources.getPid() != null && resources.getPid() > 0 && StrUtil.isBlank(resources.getPic())) {
            throw new MshopException("子分类图片必传");
        }

        if(resources.getId().equals(resources.getPid())){
            throw new MshopException("自己不能选择自己哦");
        }

        boolean checkResult = mwStoreCategoryService.checkCategory(resources.getPid());

        if(!checkResult) {
            throw new MshopException("分类最多能添加2级哦");
        }

        mwStoreCategoryService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除商品分类")
    @ApiOperation(value = "删除商品分类")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @DeleteMapping(value = "/mwStoreCategory/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTORECATEGORY_ALL','MWSTORECATEGORY_DELETE')")
    public ResponseEntity delete(@PathVariable String id){
        String[] ids = id.split(",");
        for (String newId: ids) {
            this.delCheck(Integer.valueOf(newId));
            mwStoreCategoryService.removeById(Integer.valueOf(newId));
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * 检测删除分类
     * @param id 分类id
     */
    private void delCheck(Integer id){
        Long count = mwStoreCategoryService.lambdaQuery()
                .eq(MwStoreCategory::getPid,id)
                .count();
        if(count > 0) {
            throw new MshopException("请先删除子分类");
        }

        Long countP = mwStoreProductService.lambdaQuery()
                .eq(MwStoreProduct::getCateId,id)
                .count();

        if(countP > 0) {
            throw new MshopException("当前分类下有商品不可删除");
        }
    }
}
