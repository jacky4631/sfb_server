/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.domain.MwUserSign;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.MwUserLevelService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserSignService;
import com.mailvor.modules.user.service.mapper.UserBillMapper;
import com.mailvor.modules.user.service.mapper.MwUserSignMapper;
import com.mailvor.modules.user.vo.SignVo;
import com.mailvor.modules.user.vo.MwUserQueryVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 签到记录表 服务实现类
 * </p>
 *
 * @author mazhongjun
 * @since 2019-12-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MwUserSignServiceImpl extends BaseServiceImpl<MwUserSignMapper, MwUserSign> implements MwUserSignService {

    @Autowired
    private MwUserSignMapper mwUserSignMapper;
    @Autowired
    private UserBillMapper userBillMapper;

    @Autowired
    private IGenerator generator;

    @Autowired
    private MwSystemGroupDataService systemGroupDataService;
    @Autowired
    private MwUserService mwUserService;
    @Autowired
    private MwUserBillService billService;
    @Autowired
    private MwUserLevelService userLevelService;


    /**
     *
     * @param mwUser 用户
     * @return 签到积分
     */
    @Override
    public int sign(MwUser mwUser) {
        List<JSONObject> list = systemGroupDataService.getDatas(ShopConstants.MSHOP_SIGN_DAY_NUM);
        if(ObjectUtil.isNull(list) || list.isEmpty()) {
            throw new MshopException("请先配置签到天数");
        }

        boolean isDaySign = this.getToDayIsSign(mwUser.getUid());
        if(isDaySign) {
            throw new MshopException("已签到");
        }
        //积分
        int signNumber = 0;
        //签到次数
        int userSignNum = mwUser.getSignNum();
        if(getYesterDayIsSign(mwUser.getUid())){
            if(mwUser.getSignNum() > (list.size() - 1)){
                userSignNum = 0;
            }
        }else{
            userSignNum = 0;
        }
        int index = 0;
        for (Map<String,Object> map : list) {
            if(index == userSignNum){
                signNumber = Integer.parseInt(map.get("sign_num").toString());
                break;
            }
            index++;
        }

        userSignNum += 1;

        MwUserSign userSign = new MwUserSign();
        userSign.setUid(mwUser.getUid());
        String title = "签到奖励";
        if(userSignNum == list.size()){
            title = "连续签到奖励";
        }
        userSign.setTitle(title);
        userSign.setNumber(signNumber);
        userSign.setBalance(mwUser.getIntegral().intValue());
        mwUserSignMapper.insert(userSign);

        //用户积分增加
        MwUser user = MwUser.builder()
                .integral(NumberUtil.add(mwUser.getIntegral(),signNumber))
                .uid(mwUser.getUid())
                .signNum(userSignNum)
                .build();
        boolean res = mwUserService.updateById(user);
        if(!res) {
            throw new MshopException("签到失败");
        }

        //插入流水
        billService.income(mwUser.getUid(),title, BillDetailEnum.CATEGORY_2.getValue(),
                BillDetailEnum.TYPE_10.getValue(),signNumber,mwUser.getIntegral().doubleValue(),
                "","");

        //检查是否符合开通店铺条件
        userLevelService.setLevelComplete(mwUser.getUid());
        return signNumber;
    }

    /**
     * 分页获取用户签到数据
     * @param uid 用户id
     * @param page  page
     * @param limit limit
     * @return list
     */
    @Override
    public List<SignVo> getSignList(Long uid, int page, int limit) {
        Page<MwUserBill> pageModel = new Page<>(page, limit);
        return userBillMapper.getSignList(uid,pageModel);
    }


    /**
     * 获取签到用户信息
     * @param mwUser  mwUser
     * @return MwUserQueryVo
     */
    @Override
    public MwUserQueryVo userSignInfo(MwUser mwUser) {
        MwUserQueryVo userQueryVo = generator.convert(mwUser, MwUserQueryVo.class);
        Long uid = mwUser.getUid();
        Long sumSignDay = this.getSignSumDay(uid);
        boolean isDaySign = this.getToDayIsSign(uid);
        boolean isYesterDaySign = this.getYesterDayIsSign(uid);
//        userQueryVo.setSumSignDay(sumSignDay);
//        userQueryVo.setIsDaySign(isDaySign);
//        userQueryVo.setIsYesterDaySign(isYesterDaySign);
//        if(!isDaySign && !isYesterDaySign) {
//            userQueryVo.setSignNum(0);
//        }
        return userQueryVo;
    }

    /**
     * 获取用户今天是否签到
     * @param uid uid
     * @return boolean true=YES false=NO
     */
    private boolean getToDayIsSign(Long uid) {
        Date today = DateUtil.beginOfDay(new Date());
        Long count = this.lambdaQuery().eq(MwUserSign::getUid,uid)
                .ge(MwUserSign::getCreateTime,today)
                .count();
        if(count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取用户昨天是否签到
     * @param uid uid
     * @return boolean
     */
    private boolean getYesterDayIsSign(Long uid) {
        Date today = DateUtil.beginOfDay(new Date());
        Date yesterday = DateUtil.beginOfDay(DateUtil.yesterday());

        Long count = this.lambdaQuery().eq(MwUserSign::getUid,uid)
                .lt(MwUserSign::getCreateTime,today)
                .ge(MwUserSign::getCreateTime,yesterday)
                .count();
        if(count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取用户累计签到次数
     * @param uid 用户id
     * @return int
     */
    private Long getSignSumDay(Long uid) {
        return this.lambdaQuery().eq(MwUserSign::getUid,uid).count();
    }


}
