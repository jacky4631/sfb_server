/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.pay.domain.MwPayBind;
import com.mailvor.modules.pay.service.MwPayBindService;
import com.mailvor.modules.pay.service.mapper.PayBindMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.mailvor.config.PayConfig.PAY_NAME;


/**
* @author wangke
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwPayBindServiceImpl extends BaseServiceImpl<PayBindMapper, MwPayBind> implements MwPayBindService {

    private final IGenerator generator;


    private final PayBindMapper mapper;

    @Override
    public MwPayBind findBindChannel(Long uid, Integer payType){
        return getById(getBindId(uid, payType));
    }

    @Override
    public void bindChannel(Long uid , Long channelId, Integer payType) {
        //通道绑定用户
        MwPayBind payBind = MwPayBind.builder()
                .id(getBindId(uid, payType))
                .channelId(channelId)
                .build();
        saveOrUpdate(payBind);
    }

    @Override
    public void rebindChannel(Long uid , Long channelId, Integer payType) {
        //通道绑定用户
        MwPayBind payBind = MwPayBind.builder()
                .id(getBindId(uid, payType))
                .channelId(channelId)
                .build();
        updateById(payBind);
    }

    protected String getBindId(Long uid, Integer payType) {
        return PAY_NAME + "_" + uid+"_"+payType;
    }
}
