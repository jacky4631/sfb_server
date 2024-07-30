/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service;


import com.mailvor.common.service.BaseService;
import com.mailvor.domain.PageResult;
import com.mailvor.modules.product.domain.MwStoreProductRelation;
import com.mailvor.modules.product.service.dto.MwStoreProductRelationDto;
import com.mailvor.modules.product.service.dto.MwStoreProductRelationQueryCriteria;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品点赞和收藏表 服务类
 * </p>
 *
 * @author huangyu
 * @since 2019-10-23
 */
public interface MwStoreProductRelationService extends BaseService<MwStoreProductRelation> {

    /**
     * 是否收藏
     * @param productId 商品ID
     * @param uid 用户ID
     * @return Boolean
     */
    Boolean isProductRelation(String productId, long uid);

    /**
     *添加收藏
     * @param productId 商品id
     * @param uid 用户id
     */
    void addProductRelation(String productId, long uid, String category
    ,String img, String title, String startPrice, String endPrice, String originalId);

    void saveOrUpdateRelation(String productId, long uid, String category);

    /**
     * 取消收藏
     * @param productId 商品id
     * @param uid 用户id
     */
    void delProductRelation(String productId, long uid);

    /**
     * 获取用户收藏列表
     * @param page page
     * @param limit limit
     * @param uid 用户id
     * @return list
     */
    Map<String,Object> userCollectProduct(int page, int limit, Long uid, String type);

    /**
     * 查询数据分页
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String,Object>
     */
    PageResult<MwStoreProductRelationDto> queryAll(MwStoreProductRelationQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有数据不分页
     * @param criteria 条件参数
     * @return List<MwStoreProductRelationDto>
     */
    List<MwStoreProductRelation> queryAll(MwStoreProductRelationQueryCriteria criteria);

    /**
     * 导出数据
     * @param all 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<MwStoreProductRelationDto> all, HttpServletResponse response) throws IOException;


    /**
     * 批量删除
     * @param ids /
     */
    void collectDelFoot(List<Long> ids);

    List<MwStoreProductRelation> queryAll(Integer interval, String category, String type);

    void addProductFoot(String productId, long uid, String category
            ,String img, String title, String startPrice, String endPrice, String originalId);
}
