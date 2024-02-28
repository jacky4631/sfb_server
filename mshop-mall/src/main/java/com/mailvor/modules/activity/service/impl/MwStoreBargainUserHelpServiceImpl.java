/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.activity.domain.MwStoreBargainUser;
import com.mailvor.modules.activity.domain.MwStoreBargainUserHelp;
import com.mailvor.modules.activity.service.MwStoreBargainUserHelpService;
import com.mailvor.modules.activity.service.MwStoreBargainUserService;
import com.mailvor.modules.activity.service.mapper.MwStoreBargainUserHelpMapper;
import com.mailvor.modules.activity.vo.MwStoreBargainUserHelpQueryVo;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


/**
 * <p>
 * 砍价用户帮助表 服务实现类
 * </p>
 *
 * @author huangyu
 * @since 2019-12-21
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MwStoreBargainUserHelpServiceImpl extends BaseServiceImpl<MwStoreBargainUserHelpMapper, MwStoreBargainUserHelp> implements MwStoreBargainUserHelpService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private MwStoreBargainUserHelpMapper mwStoreBargainUserHelpMapper;

    @Autowired
    private MwStoreBargainUserService storeBargainUserService;
    @Autowired
    private MwUserService userService;




    /**
     * 获取砍价帮
     * @param bargainId 砍价商品id
     * @param bargainUserUid 砍价用户id
     * @param page page
     * @param limit limit
     * @return list
     */
    @Override
    public List<MwStoreBargainUserHelpQueryVo> getList(Long bargainId, Long bargainUserUid,
                                                       int page, int limit) {
        MwStoreBargainUser storeBargainUser = storeBargainUserService
                .getBargainUserInfo(bargainId,bargainUserUid);
        if(ObjectUtil.isNull(storeBargainUser)) {
            return Collections.emptyList();
        }
        Page<MwStoreBargainUserHelp> pageModel = new Page<>(page, limit);
        LambdaQueryWrapper<MwStoreBargainUserHelp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreBargainUserHelp::getBargainUserId,storeBargainUser.getId())
                .orderByDesc(MwStoreBargainUserHelp::getId);
        List<MwStoreBargainUserHelpQueryVo> storeBargainUserHelpQueryVos = generator
                .convert(mwStoreBargainUserHelpMapper.selectPage(pageModel,wrapper).getRecords(),
                        MwStoreBargainUserHelpQueryVo.class);

        storeBargainUserHelpQueryVos.forEach(item->{
            MwUser mwUser = userService.getById(item.getUid());
            item.setAvatar(mwUser.getAvatar());
            item.setNickname(mwUser.getNickname());
        });

        return storeBargainUserHelpQueryVos;
    }

    /**
     * 获取砍价帮总人数
     *
     * @param bargainId      砍价产品ID
     * @param bargainUserUid 用户参与砍价表id
     * @return int
     */
    @Override
    public Long getBargainUserHelpPeopleCount(Long bargainId, Long bargainUserUid) {
        return this.lambdaQuery()
                .eq(MwStoreBargainUserHelp::getBargainUserId,bargainUserUid)
                .eq(MwStoreBargainUserHelp::getBargainId,bargainId)
                .count();
    }





}
