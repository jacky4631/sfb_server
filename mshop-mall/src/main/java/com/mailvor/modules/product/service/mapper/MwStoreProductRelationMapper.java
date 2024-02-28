/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.mapper;


import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.product.domain.MwStoreProductRelation;
import com.mailvor.modules.product.vo.MwStoreProductRelationQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 商品点赞和收藏表 Mapper 接口
 * </p>
 *
 * @author huangyu
 * @since 2019-10-23
 */
@Repository
public interface MwStoreProductRelationMapper extends CoreMapper<MwStoreProductRelation> {

//    @Select("select B.id pid,A.type as category,B.store_name as storeName,B.price,B.is_integral as isIntegral,A.id id," +
//            "B.ot_price as otPrice,B.sales,B.image,B.is_show as isShow" +
//            " from mw_store_product_relation A left join mw_store_product B " +
//            "on A.product_id = B.id where A.type=#{type} and A.uid=#{uid} and A.is_del = 0 and B.is_del = 0 order by A.create_time desc")
//    List<MwStoreProductRelationQueryVo> selectRelationList(Page page, @Param("uid") Long uid, @Param("type") String type);

    @Select("select * from mw_store_product_relation A " +
            "where A.type=#{type} and A.uid=#{uid} and A.is_del = 0 order by A.create_time desc")
    List<MwStoreProductRelationQueryVo> selectRelationList(Page page, @Param("uid") Long uid, @Param("type") String type);

}
