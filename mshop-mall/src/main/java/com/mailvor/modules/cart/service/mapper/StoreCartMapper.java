/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.cart.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.cart.domain.MwStoreCart;
import com.mailvor.modules.order.service.dto.CountDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author huangyu
* @date 2020-05-12
*/
@Repository
public interface StoreCartMapper extends CoreMapper<MwStoreCart> {

    @Select("select IFNULL(sum(cart_num),0) from mw_store_cart " +
            "where is_pay=0 and is_del=0 and is_new=0 and uid=#{uid}")
    int cartSum(@Param("uid") Long uid);


    @Select("SELECT t.cate_name as catename from mw_store_cart c  " +
            "LEFT JOIN mw_store_product p on c.product_id = p.id  " +
            "LEFT JOIN mw_store_category t on p.cate_id = t.id " +
            "WHERE c.is_pay = 1")
    List<CountDto> findCateName();
}
