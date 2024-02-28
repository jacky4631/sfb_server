/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.activity.domain.MwStoreCouponUser;
import com.mailvor.modules.activity.vo.StoreCouponUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
* @author huangyu
* @date 2020-05-13
*/
@Repository
public interface MwStoreCouponUserMapper extends CoreMapper<MwStoreCouponUser> {
    @Select("select A.id,A.coupon_title as couponTitle,A.coupon_price as couponPrice," +
            "A.end_time as endTime,B.use_min_price as useMinPrice,B.type," +
            "B.product_id as productId" +
            " from mw_store_coupon_user A left join mw_store_coupon B " +
            "on A.cid = B.id " +
            "where A.status = 0" +
            " AND A.end_time > #{now}  " +
            " AND A.uid = #{uid}  AND A.use_min_price <= #{price} " +
            " ORDER BY B.id DESC")
    List<StoreCouponUserVo> selectCouponList(@Param("now") Date now, @Param("price") double price,
                                             @Param("uid") Long uid);
}
