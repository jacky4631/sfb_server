/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreCouponUser;
import com.mailvor.modules.activity.service.dto.MwStoreCouponUserDto;
import com.mailvor.modules.activity.service.dto.MwStoreCouponUserQueryCriteria;
import com.mailvor.modules.activity.vo.StoreCouponUserVo;
import com.mailvor.modules.activity.vo.MwStoreCouponUserQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author mazhongjun
 * @date 2020-05-13
 */
public interface MwStoreCouponUserService extends BaseService<MwStoreCouponUser> {

    /**
     * 获取当前用户优惠券数量
     *
     * @param uid uid
     * @return int
     */
    Long getUserValidCouponCount(Long uid);

    void useCoupon(int id);

    /**
     * 获取用户优惠券
     *
     * @param id  优惠券id
     * @param uid 用户id
     * @return MwStoreCouponUser
     */
    MwStoreCouponUser getCoupon(Integer id, Long uid);


    /**
     * 获取满足条件的可用优惠券
     *
     * @param cartIds 购物车ids
     * @return list
     */
    List<StoreCouponUserVo> beUsableCouponList(Long uid, String cartIds);

    /**
     * 获取下单时候满足的优惠券
     *
     * @param uid        uid
     * @param price      总价格
     * @param productIds list
     * @return list
     */
    List<StoreCouponUserVo> getUsableCouponList(Long uid, double price, List<String> productIds);


    /**
     * 获取用户优惠券
     *
     * @param uid uid
     * @return list
     */
    List<MwStoreCouponUserQueryVo> getUserCoupon(Long uid);

    /**
     * 添加优惠券记录
     *
     * @param uid 用户id
     * @param cid 优惠券id
     */
    void addUserCoupon(Long uid, Integer cid);

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String, Object>
     */
    Map<String, Object> queryAll(MwStoreCouponUserQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<MwStoreCouponUserDto>
     */
    List<MwStoreCouponUser> queryAll(MwStoreCouponUserQueryCriteria criteria);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<MwStoreCouponUserDto> all, HttpServletResponse response) throws IOException;

    /**
     * pc端查询优惠券
     *
     * @param uid   用户id
     * @param page  当前页码
     * @param limit 一页多少
     * @param type
     * @return /
     */
    Map<String, Object> getUserPCCoupon(Long uid, int page, int limit, Integer type);
}
