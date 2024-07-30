/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.user.domain.MwUserPool;
import com.mailvor.modules.user.service.MwUserPoolService;
import com.mailvor.modules.user.service.mapper.UserPoolMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserPoolServiceImpl extends BaseServiceImpl<UserPoolMapper, MwUserPool> implements MwUserPoolService {

    @Resource
    private IGenerator generator;

    @Resource
    private UserPoolMapper mapper;

    @Cacheable(cacheNames = "user", key = "'refund:' +#uid")
    @Override
    public Integer getRefund(Long uid) {
        return mapper.getRefund(uid);
    }

    @CachePut(cacheNames = "user", key = "'refund:' +#uid")
    @Override
    public Integer addRefund(Long uid) {
        MwUserPool userPool = getById(uid);
        if(userPool == null) {
            save(MwUserPool.builder().uid(uid).refund(1).build());
            return 1;
        }
        mapper.addRefund(uid);
        return userPool.getRefund() + 1;

    }

    @CacheEvict(cacheNames = "user", key = "'refund:' +#uid")
    @Override
    public void resetRefund(Long uid) {
        MwUserPool userPool = getById(uid);
        if(userPool == null) {
            return;
        }
        mapper.resetRefund(uid);

    }

    @Override
    public void setRefund(Long uid, Integer refund) {
        log.info("检测到异常订单，设置用户uid的退款次数{}", uid, refund);
        MwUserPool userPool = getById(uid);
        if(userPool == null) {
            save(MwUserPool.builder().uid(uid).refund(refund).type(1).build());
            return;
        }
        userPool.setRefund(refund);
        userPool.setType(1);
        updateById(userPool);
    }
}
