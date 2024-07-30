/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.service.MwUserBankService;
import com.mailvor.modules.user.service.mapper.MwUserBankMapper;
import com.mailvor.modules.user.vo.MwUserBankQueryVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mailvor.enums.ShopCommonEnum.DEFAULT_1;
import static com.mailvor.enums.ShopCommonEnum.SIGN_1;


/**
 * <p>
 * 用户地址表 服务实现类
 * </p>
 *
 * @author huangyu
 * @since 2019-10-28
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MwUserBankServiceImpl extends BaseServiceImpl<MwUserBankMapper, MwUserBank> implements MwUserBankService {

    private final MwUserBankMapper mwUserAddressMapper;
    private final IGenerator generator;

    /**
     * 设置默认支付卡
     * @param uid uid
     * @param bankId 银行卡id
     */
    @Override
    public void setDefault(Long uid,Long bankId){
        MwUserBank bank = new MwUserBank();
        bank.setIsDefault(ShopCommonEnum.DEFAULT_0.getValue());
        mwUserAddressMapper.update(bank,
                new LambdaQueryWrapper<MwUserBank>().eq(MwUserBank::getUid,uid));

        MwUserBank userBank = new MwUserBank();
        userBank.setIsDefault(DEFAULT_1.getValue());
        userBank.setId(bankId);
        mwUserAddressMapper.updateById(userBank);
    }

    /**
     * 设置默认提现卡
     * @param uid uid
     * @param bankId 银行卡id
     */
    @Override
    public void setExtractDefault(Long uid,Long bankId){
        MwUserBank bank = new MwUserBank();
        bank.setExtractDefault(ShopCommonEnum.DEFAULT_0.getValue());
        mwUserAddressMapper.update(bank,
                new LambdaQueryWrapper<MwUserBank>().eq(MwUserBank::getUid,uid));

        MwUserBank userBank = new MwUserBank();
        userBank.setExtractDefault(DEFAULT_1.getValue());
        userBank.setId(bankId);
        mwUserAddressMapper.updateById(userBank);
    }
    /**
     * 获取用户地址
     * @param uid uid
     * @param page page
     * @param limit limit
     * @return List
     */
    @Override
    public List<MwUserBankQueryVo> getList(Long uid, int page, int limit){
        Page<MwUserBank> pageModel = new Page<>(page, limit);
        IPage<MwUserBank> pageList = this.lambdaQuery()
                .eq(MwUserBank::getUid,uid)
                .eq(MwUserBank::getSign, SIGN_1.getValue())
                .page(pageModel);
        return generator.convert(pageList.getRecords(), MwUserBankQueryVo.class);
    }

    @Override
    public List<MwUserBankQueryVo> getExtractList(Long uid, int page, int limit){
        Page<MwUserBank> pageModel = new Page<>(page, limit);
        IPage<MwUserBank> pageList = this.lambdaQuery()
                .eq(MwUserBank::getUid,uid)
                .isNotNull(MwUserBank::getBankName)
                .page(pageModel);
        return generator.convert(pageList.getRecords(), MwUserBankQueryVo.class);
    }

    /**
     * 获取默认地址
     * @param uid uid
     * @return MWUserAddress
     */
    @Override
    public MwUserBank getUserDefaultBank(Long uid) {
        LambdaQueryWrapper<MwUserBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserBank::getExtractDefault,DEFAULT_1.getValue()).
                eq(MwUserBank::getUid,uid)
                .last("limit 1");
        return getOne(wrapper);
    }

    @Override
    public MwUserBank findOne(Long uid, String bankNo) {
        LambdaQueryWrapper<MwUserBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserBank::getBankNo,bankNo).
                eq(MwUserBank::getUid,uid)
                .last("limit 1");
        return getOne(wrapper);
    }
    @Override
    public MwUserBank findByRequestNo(String requestNo) {
        LambdaQueryWrapper<MwUserBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserBank::getRequestNo,requestNo)
                .last("limit 1");
        return getOne(wrapper);
    }

}
