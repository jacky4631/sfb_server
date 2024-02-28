/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import com.mailvor.api.ApiResult;
import com.mailvor.modules.mp.service.MwArticleService;
import com.mailvor.modules.mp.vo.MwArticleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 文章 前端控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-02
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/article")
@Api(value = "文章模块", tags = "商城:文章模块")
public class ArticleController {

    private final MwArticleService articleService;


    /**
    * 获取文章文章详情
    */
    @GetMapping("/details/{id}")
    @ApiOperation(value = "文章详情",notes = "文章详情")
    public ApiResult<MwArticleQueryVo> getMwArticle(@PathVariable Integer id){
        MwArticleQueryVo mwArticleQueryVo = articleService.getDetail(id);
        articleService.incVisitNum(id);
        return ApiResult.ok(mwArticleQueryVo);
    }

    /**
     * 文章列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "文章列表",notes = "文章列表")
    public ApiResult<List<MwArticleQueryVo>> getMwArticlePageList(@RequestParam(value = "page",defaultValue = "1") int page,
                                                                  @RequestParam(value = "limit",defaultValue = "10") int limit){
        return ApiResult.resultPage(articleService.getList(page,limit),limit);
    }

}

