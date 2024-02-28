/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.impl;


import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.modules.shop.domain.MwSystemAttachment;
import com.mailvor.modules.shop.service.MwSystemAttachmentService;
import com.mailvor.modules.shop.service.mapper.MwSystemAttachmentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 * 附件管理表 服务实现类
 * </p>
 *
 * @author huangyu
 * @since 2019-11-11
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MwSystemAttachmentServiceImpl extends BaseServiceImpl<MwSystemAttachmentMapper, MwSystemAttachment> implements MwSystemAttachmentService {

    private final MwSystemAttachmentMapper mwSystemAttachmentMapper;

    /**
     *  根据名称获取
     * @param name name
     * @return MwSystemAttachment
     */
    @Override
    public MwSystemAttachment getInfo(String name) {
       LambdaQueryWrapper<MwSystemAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwSystemAttachment::getName,name)
                .last("limit 1");
        return mwSystemAttachmentMapper.selectOne(wrapper);
    }

    /**
     *  根据code获取
     * @param code code
     * @return MwSystemAttachment
     */
    @Override
    public MwSystemAttachment getByCode(String code) {
       LambdaQueryWrapper<MwSystemAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwSystemAttachment::getInviteCode,code)
                .last("limit 1");
        return mwSystemAttachmentMapper.selectOne(wrapper);
    }

    /**
     * 添加附件记录
     * @param name 名称
     * @param attSize 附件大小
     * @param attDir 路径
     * @param sattDir 路径
     */
    @Override
    public void attachmentAdd(String name, String attSize, String attDir,String sattDir) {
        MwSystemAttachment attachment =  MwSystemAttachment.builder()
                .name(name)
                .attSize(attSize)
                .attDir(attDir)
                .attType("image/jpeg")
                .sattDir(sattDir)
                .build();
        mwSystemAttachmentMapper.insert(attachment);
    }

    /**
     * 添加附件记录
     * @param name 名称
     * @param attSize 附件大小
     * @param attDir 路径
     * @param sattDir 路径
     * @param uid 用户id
     * @param code 邀请码
     */
    @Override
    public void newAttachmentAdd(String name, String attSize, String attDir, String sattDir,
                                 Long uid, String code) {

        MwSystemAttachment attachment =  MwSystemAttachment.builder()
                .name(name)
                .attSize(attSize)
                .attDir(attDir)
                .attType("image/jpeg")
                .sattDir(sattDir)
                .uid(uid)
                .inviteCode(code)
                .build();
        mwSystemAttachmentMapper.insert(attachment);
    }



}
