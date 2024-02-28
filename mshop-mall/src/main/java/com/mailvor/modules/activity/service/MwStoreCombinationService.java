/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreCombination;
import com.mailvor.modules.activity.service.dto.MwStoreCombinationDto;
import com.mailvor.modules.activity.service.dto.MwStoreCombinationQueryCriteria;
import com.mailvor.modules.activity.vo.CombinationQueryVo;
import com.mailvor.modules.activity.vo.StoreCombinationVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-13
*/
public interface MwStoreCombinationService extends BaseService<MwStoreCombination>{



    /**
     * 拼团列表
     * @param page page
     * @param limit limit
     * @return list
     */
    CombinationQueryVo getList(int page, int limit);

    /**
     * 获取拼团详情
     * @param id 拼团产品id
     * @param uid uid
     * @return StoreCombinationVo
     */
    StoreCombinationVo getDetail(Long id, Long uid);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreCombinationQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreCombinationDto>
    */
    List<MwStoreCombination> queryAll(MwStoreCombinationQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreCombinationDto> all, HttpServletResponse response) throws IOException;

    /**
     * 修改状态
     * @param id 拼团产品id
     * @param status ShopCommonEnum
     */
    void onSale(Long id, Integer status);

    boolean saveCombination(MwStoreCombinationDto resources);
}
