/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;
import com.mailvor.common.service.BaseService;
import com.mailvor.modules.shop.domain.MwAppVersion;
import com.mailvor.modules.shop.service.dto.MwAppVersionDto;
import com.mailvor.modules.shop.service.dto.MwAppVersionQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.mailvor.domain.PageResult;
/**
* @author lioncity
* @date 2020-12-09
*/
public interface MwAppVersionService extends BaseService<MwAppVersion>{

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<MwAppVersionDto>  queryAll(MwAppVersionQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWAppVersionDto>
    */
    List<MwAppVersion> queryAll(MwAppVersionQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwAppVersionDto> all, HttpServletResponse response) throws IOException;
}
