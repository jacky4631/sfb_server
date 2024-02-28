/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.CouponEnum;
import com.mailvor.modules.activity.domain.MwStoreCouponIssue;
import com.mailvor.modules.activity.domain.MwStoreCouponIssueUser;
import com.mailvor.modules.activity.service.MwStoreCouponIssueService;
import com.mailvor.modules.activity.service.MwStoreCouponIssueUserService;
import com.mailvor.modules.activity.service.MwStoreCouponUserService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueDto;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreCouponIssueMapper;
import com.mailvor.modules.activity.vo.MwStoreCouponIssueQueryVo;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
* @author huangyu
* @date 2020-05-13
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreCouponIssueServiceImpl extends BaseServiceImpl<MwStoreCouponIssueMapper, MwStoreCouponIssue> implements MwStoreCouponIssueService {

    private final IGenerator generator;

    private final MwStoreCouponIssueMapper mwStoreCouponIssueMapper;


    private final MwStoreCouponUserService storeCouponUserService;
    private final MwStoreCouponIssueUserService storeCouponIssueUserService;


    /**
     * 领取优惠券
     * @param id id 优惠券id
     * @param uid uid
     */
    @Override
    public void issueUserCoupon(Integer id, Long uid) {
        MwStoreCouponIssueQueryVo couponIssueQueryVo = mwStoreCouponIssueMapper
                .selectOne(id);
        if(ObjectUtil.isNull(couponIssueQueryVo)) {
            throw new MshopException("领取的优惠劵已领完或已过期");
        }

        Long count = this.couponCount(id,uid);
        if(count > 0) {
            throw new MshopException("已领取过该优惠劵");
        }

        if(couponIssueQueryVo.getRemainCount() <= 0
                && CouponEnum.PERMANENT_0.getValue().equals(couponIssueQueryVo.getIsPermanent())){
            throw new MshopException("抱歉优惠卷已经领取完了");
        }

        storeCouponUserService.addUserCoupon(uid,couponIssueQueryVo.getCid());

        storeCouponIssueUserService.addUserIssue(uid,id);

        if(couponIssueQueryVo.getTotalCount() > 0){
            mwStoreCouponIssueMapper.decCount(id);
        }

    }


    /**
     * 优惠券列表
     * @param page page
     * @param limit limit
     * @param uid  用户id
     * @return list
     */
    @Override
    public List<MwStoreCouponIssueQueryVo> getCouponList(int page, int limit, Long uid, Long productId, Integer type) {
        Page<MwStoreCouponIssue> pageModel = new Page<>(page, limit);

        if(type == null) {
            type = CouponEnum.TYPE_0.getValue();
        }
        List<MwStoreCouponIssueQueryVo> list = mwStoreCouponIssueMapper
                .selecCoupontList(pageModel,type,productId);
        for (MwStoreCouponIssueQueryVo couponIssue : list) {
            Long count = this.couponCount(couponIssue.getId(),uid);
            if(count > 0){
                couponIssue.setIsUse(true);
            }else{
                couponIssue.setIsUse(false);
            }

        }
        return list;
    }


    /**
     * 获取用户领取优惠券数量
     * @param id 前台优惠券id
     * @param uid 用户id
     * @return int
     */
    private Long couponCount(Integer id, Long uid) {
        return storeCouponIssueUserService.lambdaQuery()
                .eq(MwStoreCouponIssueUser::getUid,uid)
                .eq(MwStoreCouponIssueUser::getIssueCouponId,id)
                .count();
    }

   //============================================================//


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreCouponIssueQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCouponIssue> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwStoreCouponIssueDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreCouponIssue> queryAll(MwStoreCouponIssueQueryCriteria criteria){
        return this.list(QueryHelpPlus.getPredicate(MwStoreCouponIssue.class, criteria));
    }


    @Override
    public void download(List<MwStoreCouponIssueDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCouponIssueDto mwStoreCouponIssue : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" cname",  mwStoreCouponIssue.getCname());
            map.put("优惠券ID", mwStoreCouponIssue.getCid());
            map.put("优惠券领取开启时间", mwStoreCouponIssue.getStartTime());
            map.put("优惠券领取结束时间", mwStoreCouponIssue.getEndTime());
            map.put("优惠券领取数量", mwStoreCouponIssue.getTotalCount());
            map.put("优惠券剩余领取数量", mwStoreCouponIssue.getRemainCount());
            map.put("是否无限张数", mwStoreCouponIssue.getIsPermanent());
            map.put("1 正常 0 未开启 -1 已无效", mwStoreCouponIssue.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
