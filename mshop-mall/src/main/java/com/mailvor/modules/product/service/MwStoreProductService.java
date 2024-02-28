/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.param.MwStoreProductQueryParam;
import com.mailvor.modules.product.service.dto.StoreProductDto;
import com.mailvor.modules.product.service.dto.MwStoreProductDto;
import com.mailvor.modules.product.service.dto.MwStoreProductQueryCriteria;
import com.mailvor.modules.product.vo.ProductVo;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwStoreProductService extends BaseService<MwStoreProduct>{

    /**
     * 增加库存 减少销量
     * @param num 数量
     * @param productId 商品id
     * @param unique sku唯一值
     */
    void incProductStock(Integer num, Long productId, String unique,Long activityId, String type);

    /**
     * 减少库存与增加销量
     * @param num 数量
     * @param productId 商品id
     */
    void decProductStock(int num, Long productId, Long activityId,String type);

    MwStoreProduct getProductInfo(Long id);

    /**
     * 获取单个商品
     * @param id 商品id
     * @return MwStoreProductQueryVo
     */
    MwStoreProductQueryVo getStoreProductById(Long id);

    /**
     * 返回普通商品库存
     * @param productId 商品id
     * @param unique sku唯一值
     * @return int
     */
    int getProductStock(Long productId, String unique,String type);

    /**
     * 商品列表
     * @param productQueryParam MwStoreProductQueryParam
     * @return list
     */
    List<MwStoreProductQueryVo> getGoodsList(MwStoreProductQueryParam productQueryParam);

    /**
     * 商品详情
     * @param id 商品id
     * @param uid 用户id
     * @param latitude 纬度
     * @param longitude 经度
     * @return ProductVo
     */
    ProductVo goodsDetail(Long id, Long uid, String latitude, String longitude);

    /**
     *  商品浏览量
     * @param productId
     */
    void incBrowseNum(Long productId);

    /**
     * 商品列表
     * @param page 页码
     * @param limit 条数
     * @param order ProductEnum
     * @return List
     */
    List<MwStoreProductQueryVo> getList(int page, int limit, int order);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreProductQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreProductDto>
    */
    List<MwStoreProduct> queryAll(MwStoreProductQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreProductDto> all, HttpServletResponse response) throws IOException;



    /**
     * 商品上架下架
     * @param id 商品id
     * @param status  ShopCommonEnum
     */
    void onSale(Long id,Integer status);

    /**
     * 获取生成的属性
     * @param id 商品id
     * @param jsonStr jsonStr
     * @return map
     */
    Map<String,Object> getFormatAttr(Long id, String jsonStr,boolean isActivity);




    /**
     * 新增/保存商品
     * @param storeProductDto 商品
     */
    void insertAndEditMwStoreProduct(StoreProductDto storeProductDto);

    /**+
     * 删除商品转发海报
     * @param id
     */
    void deleteForwardImg(Long id);
}
