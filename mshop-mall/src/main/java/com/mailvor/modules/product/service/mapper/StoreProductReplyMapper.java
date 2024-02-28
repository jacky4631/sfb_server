/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.mapper;

import com.mailvor.common.mapper.CoreMapper;
import com.mailvor.modules.product.domain.MwStoreProductReply;
import com.mailvor.modules.product.vo.MwStoreProductReplyQueryVo;
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
public interface StoreProductReplyMapper extends CoreMapper<MwStoreProductReply> {

    @Select("select A.product_score as productScore,A.service_score as serviceScore," +
            "A.comment,A.merchant_reply_content as merchantReplyContent," +
            "A.merchant_reply_time as merchantReplyTime,A.pics as pictures,A.create_Time as createTime," +
            "B.nickname,B.avatar,C.cart_info as cartInfo" +
            " from mw_store_product_reply A left join mw_user B " +
            "on A.uid = B.uid left join mw_store_order_cart_info C on A.`unique` = C.`unique`" +
            " where A.product_id=#{productId} and A.is_del=0 " +
            "order by A.create_Time DESC limit 1")
    MwStoreProductReplyQueryVo getReply(long productId);

    @Select("<script>select A.product_score as productScore,A.service_score as serviceScore," +
            "A.comment,A.merchant_reply_content as merchantReplyContent," +
            "A.merchant_reply_time as merchantReplyTime,A.pics as pictures,A.create_Time as createTime," +
            "B.nickname,B.avatar,C.cart_info as cartInfo" +
            " from mw_store_product_reply A left join mw_user B " +
            "on A.uid = B.uid left join mw_store_order_cart_info C on A.`unique` = C.`unique`" +
            " where A.product_id=#{productId} and A.is_del=0 " +
            "<if test='type == 1'>and A.product_score = 5</if>" +
            "<if test='type == 2'>and A.product_score &lt; 5 and A.product_score &gt; 2</if>" +
            "<if test='type == 3'>and A.product_score &lt; 2</if>"+
            " order by A.create_Time DESC</script>")
    List<MwStoreProductReplyQueryVo> selectReplyList(Page page, @Param("productId") long productId, @Param("type") int type);

}
