/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.activity.domain.MwStoreBargain;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2020-05-13
*/
@Repository
public interface MwStoreBargainMapper extends CoreMapper<MwStoreBargain> {
    @Update("update mw_store_bargain set stock=stock+#{num}, sales=sales-#{num}" +
            " where id=#{bargainId}")
    int incStockDecSales(@Param("num") int num, @Param("bargainId") Long bargainId);

    @Update("update mw_store_bargain set stock=stock-#{num}, sales=sales+#{num}" +
            " where id=#{bargainId} and stock >= #{num}")
    int decStockIncSales(@Param("num") int num,@Param("bargainId") Long bargainId);

    @Select("SELECT c.id,c.image,c.min_price as price,c.price as otPrice," +
            "c.title as storeName,c.status as isShow,c.cost," +
            "c.is_postage as isPostage,c.postage,c.sales,c.stock,c.is_del as isDel" +
            " FROM mw_store_bargain c " +
            " WHERE c.id = #{id} and c.is_del = 0")
    MwStoreProductQueryVo bargainInfo(Long id);

    @Select("select IFNULL(sum(look),0)" +
            "from mw_store_bargain")
    int lookCount();

    @Select("select IFNULL(sum(share),0) as shareCount " +
            "from mw_store_bargain")
    int shareCount();

    @Update("update mw_store_bargain set share=share+1" +
            " where id=#{id}")
    void addBargainShare(@Param("id") Long id);

    @Update("update mw_store_bargain set look=look+1" +
            " where id=#{id}")
    void addBargainLook(@Param("id") Long id);

    @Delete("delete from mw_system_attachment where name = #{name}")
    void deleteBargainImg(@Param("name") String name);
}
