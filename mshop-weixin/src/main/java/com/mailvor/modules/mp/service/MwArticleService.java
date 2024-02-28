/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.mp.service.dto.MwArticleDto;
import com.mailvor.modules.mp.service.dto.MwArticleQueryCriteria;
import com.mailvor.modules.mp.domain.MwArticle;
import com.mailvor.modules.mp.vo.MwArticleQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author mazhongjun
* @date 2020-05-12
*/
public interface MwArticleService extends BaseService<MwArticle>{

    /**
     * 获取文章列表
     * @param page 页码
     * @param limit 条数
     * @return List
     */
    List<MwArticleQueryVo> getList(int page, int limit);

    /**
     * 获取文章详情
     * @param id id
     * @return MwArticleQueryVo
     */
    MwArticleQueryVo getDetail(int id);

    void incVisitNum(int id);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwArticleQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwArticleDto>
    */
    List<MwArticle> queryAll(MwArticleQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwArticleDto> all, HttpServletResponse response) throws IOException;


}
