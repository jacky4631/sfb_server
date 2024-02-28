/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreSeckill;
import com.mailvor.modules.activity.service.dto.MwStoreSeckillDto;
import com.mailvor.modules.activity.service.dto.MwStoreSeckillQueryCriteria;
import com.mailvor.modules.activity.vo.StoreSeckillVo;
import com.mailvor.modules.activity.vo.MwStoreSeckillQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author mazhongjun
 * @date 2020-05-13
 */
public interface MwStoreSeckillService extends BaseService<MwStoreSeckill> {


    /**
     * 产品详情
     *
     * @param id 秒杀商品id
     * @return StoreSeckillVo
     */
    StoreSeckillVo getDetail(Long id);

    /**
     * 秒杀产品列表
     *
     * @param page  page
     * @param limit limit
     * @return list
     */
    List<MwStoreSeckillQueryVo> getList(int page, int limit, int time);

    /**
     * 秒杀产品列表(首页用)
     *
     * @param page  page
     * @param limit limit
     * @return list
     */
    List<MwStoreSeckillQueryVo> getList(int page, int limit);

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String, Object>
     */
    Map<String, Object> queryAll(MwStoreSeckillQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<MwStoreSeckillDto>
     */
    List<MwStoreSeckill> queryAll(MwStoreSeckillQueryCriteria criteria);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<MwStoreSeckillDto> all, HttpServletResponse response) throws IOException;

    boolean saveSeckill(MwStoreSeckillDto resources);
}
