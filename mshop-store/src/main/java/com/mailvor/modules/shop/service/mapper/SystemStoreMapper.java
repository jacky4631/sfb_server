/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.product.vo.MwSystemStoreQueryVo;
import com.mailvor.modules.shop.domain.MwSystemStore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface SystemStoreMapper extends CoreMapper<MwSystemStore> {

    @Select("SELECT*,ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - latitude * PI() / 180" +
            "    ) / 2),2) + COS(40.0497810000 * PI() / 180) * COS(latitude * PI() / 180) * POW(" +
            "    SIN((#{lon} * PI() / 180 - longitude * PI() / 180) / 2),2))) * 1000) AS distance" +
            "    FROM mw_system_store WHERE is_del=0 AND is_show = 1 ORDER BY distance ASC"
    )
    List<MwSystemStoreQueryVo> getStoreList(Page page, @Param("lon") double lon, @Param("lat") double lat);
}
