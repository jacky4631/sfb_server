/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.product.domain.MwStoreProductRule;
import com.mailvor.modules.product.service.dto.MwStoreProductRuleQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-06-28
*/
public interface MwStoreProductRuleService extends BaseService<MwStoreProductRule> {

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreProductRuleQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreProductRuleDto>
    */
    List<MwStoreProductRule> queryAll(MwStoreProductRuleQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreProductRule> all, HttpServletResponse response) throws IOException;
}
