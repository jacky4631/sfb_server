/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.WechatUserDto;
import com.mailvor.modules.user.service.mapper.UserUnionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mailvor.config.PayConfig.PAY_NAME;

/**
* @author huangyu
* @date 2023-05-12
*/
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserUnionServiceImpl extends BaseServiceImpl<UserUnionMapper, MwUserUnion> implements MwUserUnionService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private UserUnionMapper mapper;

    @Override
    //@Cacheable
    public List<MwUserUnion> queryAllContainsChannelId(){
        LambdaQueryWrapper<MwUserUnion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserUnion::getName, PAY_NAME);
        wrapper.isNotNull(MwUserUnion::getTbPid);
        return mapper.selectList(wrapper);
    }

    @Override
    public MwUserUnion getOne(Long uid) {
        return mapper.selectOne((Wrappers.<MwUserUnion>lambdaQuery()
                .eq(MwUserUnion::getUid, uid).eq(MwUserUnion::getName, PAY_NAME)));
    }

    @Override
    public List<MwUserUnion> getByUid(Long uid) {
        return mapper.selectList((Wrappers.<MwUserUnion>lambdaQuery()
                .eq(MwUserUnion::getUid, uid)));
    }
    @Override
    public MwUserUnion getByOpenId(String openId) {
        return mapper.selectOne((Wrappers.<MwUserUnion>lambdaQuery()
                .eq(MwUserUnion::getOpenId, openId).eq(MwUserUnion::getName, PAY_NAME)));
    }
    @Override
    public void save(Long uid, WechatUserDto wechatUserDto) {
        String openid = wechatUserDto.getOpenid();

        //如果开启了UnionId
        if (StrUtil.isNotBlank(wechatUserDto.getUnionId())) {
            openid = wechatUserDto.getUnionId();
        }
        MwUserUnion userUnion = MwUserUnion.builder().uid(uid).name(PAY_NAME).wxProfile(wechatUserDto).openId(openid).build();
        mapper.insert(userUnion);
    }

    @Override
    public void update(MwUserUnion userUnion, WechatUserDto wechatUserDto) {
        String openid = wechatUserDto.getOpenid();

        //如果开启了UnionId
        if (StrUtil.isNotBlank(wechatUserDto.getUnionId())) {
            openid = wechatUserDto.getUnionId();
        }
        userUnion.setOpenId(openid);
        userUnion.setWxProfile(wechatUserDto);
        mapper.updateById(userUnion);
    }

    @Override
    public void remove(Long uid) {
        mapper.delete((Wrappers.<MwUserUnion>lambdaQuery()
                .eq(MwUserUnion::getUid, uid).eq(MwUserUnion::getName, PAY_NAME)));
    }


    @Override
    public List<MwUserUnion> listByTbPid(List<String> pids){
        LambdaQueryWrapper<MwUserUnion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserUnion::getName, PAY_NAME);
        wrapper.in(MwUserUnion::getTbPid, pids);
        return mapper.selectList(wrapper);
    }
}
