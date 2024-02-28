/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.activity.domain.MwStoreCombination;
import com.mailvor.modules.activity.vo.MwStoreCombinationQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author huangyu
* @date 2020-05-13
*/
@Repository
public interface MwStoreCombinationMapper extends CoreMapper<MwStoreCombination> {

    @Select("SELECT c.id,c.effective_time as effectiveTime,c.info,c.image,c.people,c.price, s.sales as sales," +
            "c.title,c.unit_name as unitName,s.price as productPrice FROM mw_store_combination c " +
            "INNER JOIN mw_store_product s ON s.id=c.product_id " +
            " WHERE c.is_show = 1 AND c.is_del = 0 AND c.start_time < now() " +
            " AND c.stop_time > now() ORDER BY c.sort desc,c.id desc")
    List<MwStoreCombinationQueryVo> getCombList(Page page);

    @Override
    <E extends IPage<MwStoreCombination>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<MwStoreCombination> queryWrapper);

    @Select("SELECT c.id,c.effective_time as effectiveTime,c.image,c.people,c.price,c.browse," +
            "c.description,c.image,c.images,c.info," +
            "c.product_id as productId,c.sales,c.start_time as startTime" +
            ",c.stock,c.stop_time stopTime," +
            "c.title,c.unit_name as unitName,s.price as productPrice FROM mw_store_combination c " +
            "INNER JOIN mw_store_product s ON s.id=c.product_id " +
            " WHERE c.is_show = 1 AND c.is_del = 0 AND c.id = #{id} ")
    MwStoreCombinationQueryVo getCombDetail(Long id);

    @Select("SELECT c.id,c.image,c.price,c.title as storeName,c.is_show as isShow,c.cost," +
            "c.sales,c.stock,c.is_del as isDel" +
            " FROM mw_store_combination c " +
            " WHERE c.id = #{id} and c.is_del = 0 ")
    MwStoreProductQueryVo combinatiionInfo(Long id);

    /**
     * 商品浏览量
     * @param productId
     * @return
     */
    @Update("update mw_store_combination set browse=browse+1 " +
            "where id=#{productId}")
    int incBrowseNum(@Param("productId") Long productId);
}
