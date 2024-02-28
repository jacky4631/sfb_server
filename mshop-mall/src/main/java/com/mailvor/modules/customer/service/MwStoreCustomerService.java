/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.customer.service;
import com.mailvor.common.service.BaseService;
import com.mailvor.modules.customer.domain.MwStoreCustomer;
import com.mailvor.modules.customer.service.dto.MwStoreCustomerDto;
import com.mailvor.modules.customer.service.dto.MwStoreCustomerQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.mailvor.domain.PageResult;
/**
* @author Bug
* @date 2020-12-10
*/
public interface MwStoreCustomerService extends BaseService<MwStoreCustomer>{

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MwStoreCustomerDto>  queryAll(MwStoreCustomerQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreCustomerDto>
    */
    List<MwStoreCustomer> queryAll(MwStoreCustomerQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreCustomerDto> all, HttpServletResponse response) throws IOException;
}
