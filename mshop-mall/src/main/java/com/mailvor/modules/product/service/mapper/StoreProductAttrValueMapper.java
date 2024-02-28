/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface StoreProductAttrValueMapper extends CoreMapper<MwStoreProductAttrValue> {


    /**
     * 正常商品 加库存 减销量
     * @param num
     * @param productId
     * @param unique
     * @return
     */
    @Update("update mw_store_product_attr_value set stock=stock+#{num}, sales=sales-#{num}" +
            " where product_id=#{productId} and `unique`=#{unique}")
    int incStockDecSales(@Param("num") Integer num,@Param("productId") Long productId,
                                    @Param("unique")  String unique);


    /**
     * 拼团商品 加库存 减销量
     * @param num
     * @param productId
     * @param unique
     * @return
     */

    @Update("update mw_store_product_attr_value set stock=stock+#{num}, pink_stock=pink_stock+#{num}, sales=sales-#{num}" +
            " where product_id=#{productId} and `unique`=#{unique}")
    int incCombinationStockDecSales(@Param("num") Integer num,@Param("productId") Long productId,
                         @Param("unique")  String unique);


    /**
     * 秒杀 加库存 减销量
     * @param num
     * @param productId
     * @param unique
     * @return
     */
    @Update("update mw_store_product_attr_value set stock=stock+#{num},seckill_stock=seckill_stock+#{num}, sales=sales-#{num}" +
            " where product_id=#{productId} and `unique`=#{unique}")
    int incSeckillStockDecSales(@Param("num") Integer num,@Param("productId") Long productId,
                                    @Param("unique")  String unique);


    /**
     * 普通商品 减库存 加销量
     * @param num
     * @param productId
     * @return
     */
    @Update("update mw_store_product set stock=stock-#{num}, sales=sales+#{num}" +
            " where id=#{productId} and stock >= #{num}")
    int decStockIncSales(@Param("num") Integer num, @Param("productId") Long productId);

    /**
     * 拼团产品 减库存 加销量
     * @param num
     * @param productId
     * @param unique
     * @return
     */
    @Update("update mw_store_product_attr_value set stock=stock-#{num}, pink_stock=pink_stock-#{num} ,sales=sales+#{num}" +
            " where product_id=#{productId} and `unique`=#{unique} and stock >= #{num} and pink_stock>=#{num}")
    int decCombinationStockIncSales(@Param("num") Integer num, @Param("productId") Long productId,
                                    @Param("unique") String unique);

    /**
     * 秒杀产品 减库存 加销量
     * @param num
     * @param productId
     * @param unique
     * @return
     */
    @Update("update mw_store_product_attr_value set stock=stock-#{num}, seckill_stock=seckill_stock-#{num},sales=sales+#{num}" +
            " where product_id=#{productId} and `unique`=#{unique} and stock >= #{num} and seckill_stock>=#{num}")
    int decSeckillStockIncSales(@Param("num") Integer num, @Param("productId") Long productId,
                                @Param("unique") String unique);
}
