/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.shop.domain.MwSystemStoreStaff;
import com.mailvor.modules.shop.service.MwSystemStoreStaffService;
import com.mailvor.modules.shop.service.dto.MwSystemStoreStaffDto;
import com.mailvor.modules.shop.service.dto.MwSystemStoreStaffQueryCriteria;
import com.mailvor.modules.shop.service.mapper.SystemStoreStaffMapper;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwSystemStoreStaffServiceImpl extends BaseServiceImpl<SystemStoreStaffMapper, MwSystemStoreStaff> implements MwSystemStoreStaffService {

    private final IGenerator generator;


    /**
     * 接测店员客服状态
     * @param uid 用户id
     * @param storeId 门店id
     * @return boolean true=可核销
     */
    @Override
    public boolean checkStatus(Long uid,Integer storeId) {
        MwSystemStoreStaff storeStaff = new MwSystemStoreStaff();
        storeStaff.setUid(uid);
        storeStaff.setVerifyStatus(ShopCommonEnum.IS_STATUS_1.getValue());
        if(storeId != null) {
            storeStaff.setStoreId(storeId);
        }
        return this.baseMapper.selectCount(Wrappers.query(storeStaff)) > 0;
    }

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwSystemStoreStaffQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemStoreStaff> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwSystemStoreStaffDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwSystemStoreStaff> queryAll(MwSystemStoreStaffQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwSystemStoreStaff.class, criteria));
    }


    @Override
    public void download(List<MwSystemStoreStaffDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwSystemStoreStaffDto mwSystemStoreStaff : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("微信用户id", mwSystemStoreStaff.getUid());
            map.put("店员头像", mwSystemStoreStaff.getAvatar());
            map.put("门店id", mwSystemStoreStaff.getStoreId());
            map.put("店员名称", mwSystemStoreStaff.getStaffName());
            map.put("手机号码", mwSystemStoreStaff.getPhone());
            map.put("核销开关", mwSystemStoreStaff.getVerifyStatus());
            map.put("状态", mwSystemStoreStaff.getStatus());
            map.put("微信昵称", mwSystemStoreStaff.getNickname());
            map.put("所属门店", mwSystemStoreStaff.getStoreName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
