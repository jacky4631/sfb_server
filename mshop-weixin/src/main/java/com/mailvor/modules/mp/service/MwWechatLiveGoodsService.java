/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.mp.service.dto.MwWechatLiveGoodsDto;
import com.mailvor.modules.mp.service.dto.MwWechatLiveGoodsQueryCriteria;
import com.mailvor.modules.mp.domain.MwWechatLiveGoods;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author mazhongjun
* @date 2020-08-11
*/
public interface MwWechatLiveGoodsService extends BaseService<MwWechatLiveGoods>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwWechatLiveGoodsQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwWechatLiveGoodsDto>
    */
    List<MwWechatLiveGoods> queryAll(MwWechatLiveGoodsQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwWechatLiveGoodsDto> all, HttpServletResponse response) throws IOException;

    /**
     * 保存直播商品信息
     * @param resources
     * @return
     */
    boolean saveGoods(MwWechatLiveGoods resources);

    /**
     * 同步商品更新审核状态
     * @param goodsIds
     * @return
     */
    boolean synchroWxOlLive(List<Integer> goodsIds);

    /**
     * 根据id删除直播商品信息
     * @param id
     */
    void removeGoods(Long id);

    /**
     * 更新直播商品信息
     * @param resources
     */
    void updateGoods(MwWechatLiveGoods resources);
}
