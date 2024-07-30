/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;


import cn.hutool.core.util.StrUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.user.domain.MwUserAddress;
import com.mailvor.modules.user.param.AddressParam;
import com.mailvor.modules.user.service.MwUserAddressService;
import com.mailvor.modules.user.service.mapper.MwUserAddressMapper;
import com.mailvor.modules.user.vo.MwUserAddressQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
public class MwUserAddressServiceImpl extends BaseServiceImpl<MwUserAddressMapper, MwUserAddress> implements MwUserAddressService {

    private final MwUserAddressMapper mwUserAddressMapper;
    private final IGenerator generator;

    /**
     * 设置默认地址
     * @param uid uid
     * @param addressId 地址id
     */
    @Override
    public void setDefault(Long uid,Long addressId){
        MwUserAddress address = new MwUserAddress();
        address.setIsDefault(ShopCommonEnum.DEFAULT_0.getValue());
        mwUserAddressMapper.update(address,
                new LambdaQueryWrapper<MwUserAddress>().eq(MwUserAddress::getUid,uid));

        MwUserAddress userAddress = new MwUserAddress();
        userAddress.setIsDefault(ShopCommonEnum.DEFAULT_1.getValue());
        userAddress.setId(addressId);
        mwUserAddressMapper.updateById(userAddress);
    }


    /**
     * 添加或者修改地址
     * @param uid uid
     * @param param AddressParam
     */
    @Override
    public Long addAndEdit(Long uid, AddressParam param){
        MwUserAddress userAddress = MwUserAddress.builder()
                .city(param.getAddress().getCity())
                .cityId(param.getAddress().getCityId())
                .district(param.getAddress().getDistrict())
                .province(param.getAddress().getProvince())
                .detail(param.getDetail())
                .uid(uid)
                .phone(param.getPhone())
                .postCode(param.getPost_code())
                .realName(param.getReal_name())
                .build();
        if("true".equals(param.getIs_default())){
            userAddress.setIsDefault(ShopCommonEnum.DEFAULT_1.getValue());
            //新增地址如果是默认，把之前的状态改掉
            MwUserAddress address = new MwUserAddress();
            address.setIsDefault(ShopCommonEnum.DEFAULT_0.getValue());
            baseMapper.update(address,new LambdaQueryWrapper<MwUserAddress>().eq(MwUserAddress::getUid,uid));
        }else{
            userAddress.setIsDefault(ShopCommonEnum.DEFAULT_0.getValue());
        }
        if(StrUtil.isBlank(param.getId())){
            this.save(userAddress);
        }else{
            userAddress.setId(Long.valueOf(param.getId()));
            this.updateById(userAddress);
        }

        return userAddress.getId();
    }

    /**
     * 地址详情
     * @param id 地址id
     * @return MWUserAddressQueryVo
     */
    @Override
    public MwUserAddressQueryVo getDetail(Long id){
        return generator.convert(this.getById(id), MwUserAddressQueryVo.class);
    }


    /**
     * 获取用户地址
     * @param uid uid
     * @param page page
     * @param limit limit
     * @return List
     */
    @Override
    public List<MwUserAddressQueryVo> getList(Long uid, int page, int limit){
        Page<MwUserAddress> pageModel = new Page<>(page, limit);
        IPage<MwUserAddress> pageList = this.lambdaQuery().eq(MwUserAddress::getUid,uid).page(pageModel);
        return generator.convert(pageList.getRecords(), MwUserAddressQueryVo.class);
    }

    /**
     * 获取默认地址
     * @param uid uid
     * @return MWUserAddress
     */
    @Override
    public MwUserAddress getUserDefaultAddress(Long uid) {
        LambdaQueryWrapper<MwUserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserAddress::getIsDefault,1).
                eq(MwUserAddress::getUid,uid)
                .last("limit 1");
        return getOne(wrapper);
    }



}
