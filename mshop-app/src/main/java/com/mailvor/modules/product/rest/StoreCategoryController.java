/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.category.service.MwStoreCategoryService;
import com.mailvor.utils.CateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 商品分类前端控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-22
 */
@Slf4j
@RestController
@Tag(name = "商城：商品分类")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreCategoryController {

    private final MwStoreCategoryService mwStoreCategoryService;


    /**
     * 商品分类列表
     */
    @AnonymousAccess
    @GetMapping("/category")
    @Operation(summary = "商品分类列表", description = "商品分类列表")
    public ApiResult<List<CateDTO>> getMwStoreCategoryPageList(){
        return ApiResult.ok(mwStoreCategoryService.getList());
    }

}

