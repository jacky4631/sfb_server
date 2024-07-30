/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreCouponIssue;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueDto;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueQueryCriteria;
import com.mailvor.modules.activity.vo.MwStoreCouponIssueQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-13
*/
public interface MwStoreCouponIssueService extends BaseService<MwStoreCouponIssue>{

    /**
     * 领取优惠券
     * @param id id 优惠券id
     * @param uid uid
     */
    void issueUserCoupon(Integer id, Long uid);

    /**
     * 优惠券列表
     * @param page page
     * @param limit limit
     * @param uid  用户id
     * @return list
     */
    List<MwStoreCouponIssueQueryVo> getCouponList(int page, int limit, Long uid, Long productId, Integer type);

    //int couponCount(int id, int uid);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreCouponIssueQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreCouponIssueDto>
    */
    List<MwStoreCouponIssue> queryAll(MwStoreCouponIssueQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreCouponIssueDto> all, HttpServletResponse response) throws IOException;
}
