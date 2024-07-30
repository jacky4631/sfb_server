/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.mp.service.mapper.ArticleMapper;
import com.mailvor.modules.mp.domain.MwArticle;
import com.mailvor.modules.mp.service.MwArticleService;
import com.mailvor.modules.mp.service.dto.MwArticleDto;
import com.mailvor.modules.mp.service.dto.MwArticleQueryCriteria;
import com.mailvor.modules.mp.vo.MwArticleQueryVo;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
* @author huangyu
* @date 2020-05-12
*/
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwArticleServiceImpl extends BaseServiceImpl<ArticleMapper, MwArticle> implements MwArticleService {

    private final IGenerator generator;
    private final ArticleMapper articleMapper;
    @Value("${file.path}")
    private String uploadDirStr;

    public MwArticleServiceImpl(IGenerator generator, ArticleMapper articleMapper) {
        this.generator = generator;
        this.articleMapper = articleMapper;
    }

    /**
     * 获取文章列表
     * @param page 页码
     * @param limit 条数
     * @return List
     */
    @Override
    public List<MwArticleQueryVo> getList(int page, int limit){
        Page<MwArticle> pageModel = new Page<>(page, limit);

        IPage<MwArticle> pageList = articleMapper.selectPage(pageModel, Wrappers.<MwArticle>lambdaQuery()
                .orderByDesc(MwArticle::getId));

        return generator.convert(pageList.getRecords(), MwArticleQueryVo.class);
    }

    /**
     * 获取文章详情
     * @param id id
     * @return MwArticleQueryVo
     */
    @Override
    public MwArticleQueryVo getDetail(int id){
        return generator.convert(this.getById(id), MwArticleQueryVo.class);
    }


    @Override
    public void incVisitNum(int id) {
        articleMapper.incVisitNum(id);
    }

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwArticleQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwArticle> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwArticleDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwArticle> queryAll(MwArticleQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwArticle.class, criteria));
    }


    @Override
    public void download(List<MwArticleDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwArticleDto mwArticle : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("分类id", mwArticle.getCid());
            map.put("文章标题", mwArticle.getTitle());
            map.put("文章作者", mwArticle.getAuthor());
            map.put("文章图片", mwArticle.getImageInput());
            map.put("文章简介", mwArticle.getSynopsis());
            map.put(" content",  mwArticle.getContent());
            map.put("文章分享标题", mwArticle.getShareTitle());
            map.put("文章分享简介", mwArticle.getShareSynopsis());
            map.put("浏览次数", mwArticle.getVisit());
            map.put("排序", mwArticle.getSort());
            map.put("原文链接", mwArticle.getUrl());
            map.put("状态", mwArticle.getStatus());
            map.put("是否隐藏", mwArticle.getHide());
            map.put("管理员id", mwArticle.getAdminId());
            map.put("商户id", mwArticle.getMerId());
            map.put("产品关联id", mwArticle.getProductId());
            map.put("是否热门(小程序)", mwArticle.getIsHot());
            map.put("是否轮播图(小程序)", mwArticle.getIsBanner());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }




}
