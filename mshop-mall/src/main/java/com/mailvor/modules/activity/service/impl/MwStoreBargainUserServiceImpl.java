/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.modules.activity.domain.MwStoreBargain;
import com.mailvor.modules.activity.domain.MwStoreBargainUser;
import com.mailvor.modules.activity.domain.MwStoreBargainUserHelp;
import com.mailvor.modules.activity.service.MwStoreBargainService;
import com.mailvor.modules.activity.service.MwStoreBargainUserHelpService;
import com.mailvor.modules.activity.service.MwStoreBargainUserService;
import com.mailvor.modules.activity.service.mapper.MwStoreBargainUserMapper;
import com.mailvor.modules.activity.vo.MwStoreBargainUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 用户参与砍价表 服务实现类
 * </p>
 *
 * @author huangyu
 * @since 2019-12-21
 */
@Slf4j
@Service
public class MwStoreBargainUserServiceImpl extends BaseServiceImpl<MwStoreBargainUserMapper, MwStoreBargainUser> implements MwStoreBargainUserService {


    @Autowired
    private IGenerator generator;

    @Autowired
    private MwStoreBargainUserMapper mwStoreBargainUserMapper;

    @Autowired
    private MwStoreBargainService storeBargainService;
    @Autowired
    private MwStoreBargainUserHelpService storeBargainUserHelpService;


    /**
     * 修改用户砍价状态
     * @param bargainId 砍价产品id
     * @param uid 用户id
     */
    @Override
    public void setBargainUserStatus(Long bargainId, Long uid) {
        MwStoreBargainUser storeBargainUser = getBargainUserInfo(bargainId.longValue(),uid);
        if(ObjectUtil.isNull(storeBargainUser)) {
            return;
        }

        if(storeBargainUser.getStatus() != 1) {
            return;
        }
        double price = NumberUtil.sub(NumberUtil.sub(storeBargainUser.getBargainPrice(),
                storeBargainUser.getBargainPriceMin()),storeBargainUser.getPrice()).doubleValue();
        if(price > 0) {
            return;
        }

        storeBargainUser.setStatus(3);

        mwStoreBargainUserMapper.updateById(storeBargainUser);
    }

    /**
     * 砍价取消
     * @param bargainId 砍价商品id
     * @param uid uid
     */
    @Override
    public void bargainCancel(Long bargainId, Long uid) {
        MwStoreBargainUser storeBargainUser = this.getBargainUserInfo(bargainId,uid);
        if(ObjectUtil.isNull(storeBargainUser)) {
            throw new MshopException("数据不存在");
        }
        if(!OrderInfoEnum.BARGAIN_STATUS_1.getValue().equals(storeBargainUser.getStatus())){
            throw new MshopException("状态错误");
        }
        mwStoreBargainUserMapper.deleteById(storeBargainUser.getId());
    }

    /**
     * 获取用户的砍价产品
     * @param bargainUserUid 用户id
     * @param page page
     * @param limit limit
     * @return List
     */
    @Override
    public List<MwStoreBargainUserQueryVo> bargainUserList(Long bargainUserUid, int page, int limit) {
        Page<MwStoreBargainUser> pageModel = new Page<>(page, limit);
        return mwStoreBargainUserMapper.getBargainUserList(bargainUserUid,pageModel);
    }

    /**
     * 判断用户是否还可以砍价
     * @param bargainId 砍价产品id
     * @param bargainUserUid 开启砍价用户id
     * @param uid  当前用户id
     * @return false=NO true=YES
     */
    @Override
    public  boolean isBargainUserHelp(Long bargainId, Long bargainUserUid, Long uid) {
        MwStoreBargainUser storeBargainUser = this.getBargainUserInfo(bargainId, bargainUserUid);
        MwStoreBargain storeBargain = storeBargainService
                .getById(bargainId);
        if(ObjectUtil.isNull(storeBargainUser) || ObjectUtil.isNull(storeBargain)){
            return false;
        }
        Long count = storeBargainUserHelpService.lambdaQuery()
                .eq(MwStoreBargainUserHelp::getBargainId,bargainId)
                .eq(MwStoreBargainUserHelp::getBargainUserId,storeBargainUser.getId())
                .eq(MwStoreBargainUserHelp::getUid,uid)
                .count();
        if(count == 0) {
            return true;
        }
        return false;
    }

    /**
     * 添加砍价记录
     * @param bargainId 砍价商品id
     * @param uid 用户id
     */
    @Override
    public void setBargain(Long bargainId, Long uid) {
        MwStoreBargainUser storeBargainUser = this.getBargainUserInfo(bargainId,uid);
        if(storeBargainUser != null) {
            throw new MshopException("你已经参与了");
        }
        MwStoreBargain storeBargain = storeBargainService.getById(bargainId);
        if(storeBargain == null) {
            throw new MshopException("砍价商品不存在");
        }
        MwStoreBargainUser mwStoreBargainUser = MwStoreBargainUser
                .builder()
                .bargainId(bargainId)
                .uid(uid)
                .bargainPrice(storeBargain.getPrice())
                .bargainPriceMin(storeBargain.getMinPrice())
                .price(BigDecimal.ZERO)
                .status(OrderInfoEnum.BARGAIN_STATUS_1.getValue())
                .build();
        mwStoreBargainUserMapper.insert(mwStoreBargainUser);
    }

//    /**
//     * 获取用户可以砍掉的价格
//     * @param id
//     * @return
//     */
//    @Override
//    public double getBargainUserDiffPrice(int id) {
//        MwStoreBargainUser storeBargainUserQueryVo = this.getById(id);
//        return NumberUtil.sub(storeBargainUserQueryVo.getBargainPrice()
//                ,storeBargainUserQueryVo.getBargainPriceMin()).doubleValue();
//    }



    /**
     * 获取某个用户参与砍价信息
     * @param bargainId 砍价id
     * @param uid 用户id
     * @return  MwStoreBargainUser
     */
    @Override
    public MwStoreBargainUser getBargainUserInfo(Long bargainId, Long uid) {
       LambdaQueryWrapper<MwStoreBargainUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreBargainUser::getBargainId,bargainId)
                .eq(MwStoreBargainUser::getUid,uid)
                .last("limit 1");
        return mwStoreBargainUserMapper.selectOne(wrapper);
    }

    /**
     * 获取参与砍价的用户数量
     *
     * @param bargainId 砍价id
     * @param status    状态  OrderInfoEnum 1 进行中  2 结束失败  3结束成功
     * @return int
     */
    @Override
    public Long getBargainUserCount(Long bargainId, Integer status) {
        return this.lambdaQuery().eq(MwStoreBargainUser::getBargainId,bargainId)
                .eq(MwStoreBargainUser::getStatus,status).count();
    }


//
//    /**
//     * 获取参与砍价的用户列表
//     * @param bargainId 砍价id
//     * @param status  状态  1 进行中  2 结束失败  3结束成功
//     * @return
//     */
//    @Override
//    public List<MwStoreBargainUserQueryVo> getBargainUserList(int bargainId, int status) {
//       LambdaQueryWrapper<MwStoreBargainUser> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq("bargain_id",bargainId).eq("status",status);
//        return generator.convert(mwStoreBargainUserMapper.selectList(wrapper),
//                MwStoreBargainUserQueryVo.class);
//    }




}
