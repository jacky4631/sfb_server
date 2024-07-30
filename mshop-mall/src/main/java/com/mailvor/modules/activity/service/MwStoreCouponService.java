/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreCoupon;
import com.mailvor.modules.activity.service.dto.MwStoreCouponDto;
import com.mailvor.modules.activity.service.dto.MwStoreCouponQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-13
*/
public interface MwStoreCouponService extends BaseService<MwStoreCoupon>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreCouponQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreCouponDto>
    */
    List<MwStoreCoupon> queryAll(MwStoreCouponQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreCouponDto> all, HttpServletResponse response) throws IOException;
}
