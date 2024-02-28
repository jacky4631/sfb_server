/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.product.domain.MwStoreProductReply;
import com.mailvor.modules.product.service.dto.MwStoreProductReplyDto;
import com.mailvor.modules.product.service.dto.MwStoreProductReplyQueryCriteria;
import com.mailvor.modules.product.vo.ReplyCountVo;
import com.mailvor.modules.product.vo.MwStoreProductReplyQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwStoreProductReplyService extends BaseService<MwStoreProductReply>{

    /**
     * 评价数据
     * @param productId 商品id
     * @return ReplyCountVO
     */
    ReplyCountVo getReplyCount(long productId);

    /**
     * 处理评价
     * @param replyQueryVo replyQueryVo
     * @return MwStoreProductReplyQueryVo
     */
    MwStoreProductReplyQueryVo handleReply(MwStoreProductReplyQueryVo replyQueryVo);

    /**
     * 获取单条评价
     * @param productId 商品di
     * @return MwStoreProductReplyQueryVo
     */
    MwStoreProductReplyQueryVo getReply(long productId);

    /**
     * 获取评价列表
     * @param productId 商品id
     * @param type 0-全部 1-好评 2-中评 3-差评
     * @param page page
     * @param limit limit
     * @return list
     */
    List<MwStoreProductReplyQueryVo> getReplyList(long productId, int type, int page, int limit);

    Long getInfoCount(Integer oid, String unique);

    Long productReplyCount(long productId);

    Long replyCount(String unique);

    String replyPer(long productId);


    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreProductReplyQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreProductReplyDto>
    */
    List<MwStoreProductReply> queryAll(MwStoreProductReplyQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreProductReplyDto> all, HttpServletResponse response) throws IOException;
}
