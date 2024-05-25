/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.pay.domain.MwPayCompany;
import com.mailvor.modules.pay.dto.PayCompanyQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwPayCompanyService extends BaseService<MwPayCompany>{

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(PayCompanyQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWSystemGroupDataDto>
    */
    List<MwPayCompany> queryAll(PayCompanyQueryCriteria criteria);

    /**
     * 校验公司是否存在
     * @param id 公司id
     * @return 是否存在
     */
    boolean checkCompany(Long id);
}
