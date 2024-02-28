/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.mp.service.dto.MwWechatReplyDto;
import com.mailvor.modules.mp.service.dto.MwWechatReplyQueryCriteria;
import com.mailvor.modules.mp.domain.MwWechatReply;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwWechatReplyService extends BaseService<MwWechatReply>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwWechatReplyQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwWechatReplyDto>
    */
    List<MwWechatReply> queryAll(MwWechatReplyQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwWechatReplyDto> all, HttpServletResponse response) throws IOException;

    MwWechatReply isExist(String subscribe);

    void create(MwWechatReply mwWechatReply);

    void upDate(MwWechatReply mwWechatReply);
}
