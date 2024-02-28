/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.activity.domain.MwStoreCouponIssueUser;
import com.mailvor.modules.activity.service.MwStoreCouponIssueUserService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueUserDto;
import com.mailvor.modules.activity.service.dto.MwStoreCouponIssueUserQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreCouponIssueUserMapper;
import com.mailvor.utils.FileUtil;
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
public class MwStoreCouponIssueUserServiceImpl extends BaseServiceImpl<MwStoreCouponIssueUserMapper, MwStoreCouponIssueUser> implements MwStoreCouponIssueUserService {

    private final IGenerator generator;

    /**
     * 添加优惠券领取记录
     * @param uid 用户id
     * @param id 前台优惠券id
     */
    @Override
    public void addUserIssue(Long uid, Integer id) {
        MwStoreCouponIssueUser couponIssueUser = new MwStoreCouponIssueUser();
        couponIssueUser.setIssueCouponId(id);
        couponIssueUser.setUid(uid);
        this.save(couponIssueUser);
    }


    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreCouponIssueUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCouponIssueUser> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwStoreCouponIssueUserDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreCouponIssueUser> queryAll(MwStoreCouponIssueUserQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreCouponIssueUser.class, criteria));
    }


    @Override
    public void download(List<MwStoreCouponIssueUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCouponIssueUserDto mwStoreCouponIssueUser : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("领取优惠券用户ID", mwStoreCouponIssueUser.getUid());
            map.put("优惠券前台领取ID", mwStoreCouponIssueUser.getIssueCouponId());
            map.put("领取时间", mwStoreCouponIssueUser.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
