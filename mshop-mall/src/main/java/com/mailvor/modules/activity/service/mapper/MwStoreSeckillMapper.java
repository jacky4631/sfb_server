/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.activity.domain.MwStoreSeckill;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @author huangyu
* @date 2020-05-13
*/
@Repository
public interface MwStoreSeckillMapper extends CoreMapper<MwStoreSeckill> {

    @Select("SELECT c.id,c.image,c.price,c.title as storeName,c.is_show as isShow,c.cost," +
            "c.is_postage as isPostage,c.postage,c.sales,c.stock,c.is_del as isDel" +
            " FROM mw_store_seckill c " +
            " WHERE c.id = #{id} and c.is_del = 0 ")
    MwStoreProductQueryVo seckillInfo(Long id);
}
