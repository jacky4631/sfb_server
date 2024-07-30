/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.enums.VipEnum;
import com.mailvor.modules.energy.dto.MonthCardConfigDto;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserLevel;
import com.mailvor.modules.user.service.MwSystemUserLevelService;
import com.mailvor.modules.user.service.MwUserLevelService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.mapper.MwUserLevelMapper;
import com.mailvor.utils.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.mailvor.utils.DateUtils.getBaseDate;


/**
 * <p>
 * 用户等级记录表 服务实现类
 * </p>
 *
 * @author huangyu
 * @since 2019-12-06
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MwUserLevelServiceImpl extends BaseServiceImpl<MwUserLevelMapper, MwUserLevel> implements MwUserLevelService {

    @Autowired
    private MwUserLevelMapper mwUserLevelMapper;
    @Autowired
    private MwUserService userService;
    @Autowired
    private MwSystemUserLevelService systemUserLevelService;
    @Resource
    private MwSystemConfigService systemConfigService;


    /**
     * 检查是否能成为会员
     * @param uid 用户id
     */
    @Override
    public boolean setLevelComplete(Long uid) {
        //获取当前用户级别
        int levelId = 0;
        MwUserLevel mwUserLevel = this.getUserLevel(uid,null);
        if(mwUserLevel != null ){
            levelId =  mwUserLevel.getLevelId();
        }
        int nextLevelId = systemUserLevelService.getNextLevelId(levelId);
        if(nextLevelId == 0) {
            return false;
        }
        return false;
    }



    /**
     * 获取当前用户会员等级返回当前用户等级
     * @param uid uid
     * @param grade 用户级别
     * @return MWUserLevel
     */
    @Override
    public MwUserLevel getUserLevel(Long uid, Integer grade) {
       LambdaQueryWrapper<MwUserLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(MwUserLevel::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .eq(MwUserLevel::getUid,uid)
                .orderByDesc(MwUserLevel::getGrade);
        if(grade != null) {
            wrapper.lt(MwUserLevel::getGrade,grade);
        }
        MwUserLevel userLevel = this.getOne(wrapper,false);
        if(ObjectUtil.isNull(userLevel)) {
            return null;
        }
        if(ShopCommonEnum.IS_FOREVER_1.getValue().equals(userLevel.getIsForever())) {
            return userLevel;
        }
        int nowTime = OrderUtil.getSecondTimestampTwo();
        if(nowTime > userLevel.getValidTime()){
            if(ShopCommonEnum.IS_STATUS_1.getValue().equals(userLevel.getStatus())){
                userLevel.setStatus(ShopCommonEnum.IS_STATUS_0.getValue());
                mwUserLevelMapper.updateById(userLevel);
            }
            return this.getUserLevel(uid,userLevel.getGrade());
        }
        return userLevel;
    }


    /**
     * 设置年卡等级
     * @param uid 用户id
     * @param levelId 等级id
     */
    @Override
    public void setUserLevel(Long uid, int levelId, String platform){
        MwSystemUserLevel systemUserLevelQueryVo = systemUserLevelService
                .getById(levelId);
        if(ObjectUtil.isNull(systemUserLevelQueryVo)) {
            return;
        }

        int validTime = systemUserLevelQueryVo.getValidDate() * 86400;

        MwUserLevel mwUserLevel = new MwUserLevel();
        mwUserLevel.setIsForever(systemUserLevelQueryVo.getIsForever());
        mwUserLevel.setStatus(ShopCommonEnum.IS_STATUS_1.getValue());
        mwUserLevel.setGrade(systemUserLevelQueryVo.getGrade());
        mwUserLevel.setUid(uid);
        mwUserLevel.setLevelId(levelId);
        mwUserLevel.setDiscount(systemUserLevelQueryVo.getDiscount().intValue());
        mwUserLevel.setPlatform(platform);

        if(ShopCommonEnum.IS_FOREVER_1.getValue().equals(systemUserLevelQueryVo.getIsForever())){
            mwUserLevel.setValidTime(0); //永久
        }else{
            mwUserLevel.setValidTime(validTime+OrderUtil.getSecondTimestampTwo());
        }

        mwUserLevel.setMark("恭喜你成为了"+systemUserLevelQueryVo.getName());
        mwUserLevelMapper.insert(mwUserLevel);

        MwUser findUser = userService.getById(uid);
        //更新用户等级
        //如果过期时间小于当前时间，在当前时间累加
        if("tb".equals(platform)) {
            findUser.setLevel(levelId);
            Date date = getBaseDate(findUser.getExpired());
            date = incOneYear(date);
            //设置会员过期时间
            findUser.setExpired(date);
        } else if("jd".equals(platform)) {
            findUser.setLevelJd(levelId);
            Date date = getBaseDate(findUser.getExpiredJd());
            date = incOneYear(date);
            //设置会员过期时间
            findUser.setExpiredJd(date);
        } else if("pdd".equals(platform)) {
            findUser.setLevelPdd(levelId);
            Date date = getBaseDate(findUser.getExpiredPdd());
            date = incOneYear(date);
            //设置会员过期时间
            findUser.setExpiredPdd(date);
        } else if("dy".equals(platform)) {
            findUser.setLevelDy(levelId);
            Date date = getBaseDate(findUser.getExpiredDy());
            date = incOneYear(date);
            //设置会员过期时间
            findUser.setExpiredDy(date);
        } else if("vip".equals(platform)) {
            findUser.setLevelVip(levelId);
            Date date = getBaseDate(findUser.getExpiredVip());
            date = incOneYear(date);
            //设置会员过期时间
            findUser.setExpiredVip(date);
        }
        userService.updateById(findUser);

    }

    /**
     * 设置月卡等级
     * @param uid 用户id
     * @param levelId 等级id
     */
    @Override
    public void setUserLevelMonth(Long uid, int levelId, String platform){
        MonthCardConfigDto monthCardConfigDto = systemConfigService.getMonthCardConfig();

        int validTime = monthCardConfigDto.getExpired() * 86400;

        MwUserLevel mwUserLevel = new MwUserLevel();
        mwUserLevel.setIsForever(0);
        mwUserLevel.setStatus(ShopCommonEnum.IS_STATUS_1.getValue());
        mwUserLevel.setGrade(VipEnum.VIP.getLevel());
        mwUserLevel.setUid(uid);
        mwUserLevel.setLevelId(levelId);
        mwUserLevel.setDiscount(20);
        mwUserLevel.setPlatform(platform);

        mwUserLevel.setValidTime(validTime+OrderUtil.getSecondTimestampTwo());

        mwUserLevel.setMark("恭喜你加盟月卡");
        mwUserLevelMapper.insert(mwUserLevel);

        MwUser user = userService.getById(uid);
        //更新用户等级
        if("tb".equals(platform)) {
            user.setLevel(levelId);
            Date date = getBaseDate(user.getExpired());
            date = incOneMonth(date);
            //设置会员过期时间
            user.setExpired(date);
        } else if("jd".equals(platform)) {
            user.setLevelJd(levelId);
            Date date = getBaseDate(user.getExpiredJd());
            date = incOneMonth(date);
            //设置会员过期时间
            user.setExpiredJd(date);
        } else if("pdd".equals(platform)) {
            user.setLevelPdd(levelId);
            Date date = getBaseDate(user.getExpiredPdd());
            date = incOneMonth(date);
            //设置会员过期时间
            user.setExpiredPdd(date);
        } else if("dy".equals(platform)) {
            user.setLevelDy(levelId);
            Date date = getBaseDate(user.getExpiredDy());
            date = incOneMonth(date);
            //设置会员过期时间
            user.setExpiredDy(date);
        } else if("vip".equals(platform)) {
            user.setLevelVip(levelId);
            Date date = getBaseDate(user.getExpiredVip());
            date = incOneMonth(date);
            //设置会员过期时间
            user.setExpiredVip(date);
        }
        userService.updateById(user);

    }
    protected Date incOneYear(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // 把日期往后增加一年,整数  往后推,负数往前移动
        calendar.add(Calendar.DATE, 366);
        // 这个时间就是日期往后推一天的结果
        return calendar.getTime();
    }
    protected Date incOneMonth(Date date) {
        return DateUtil.offsetDay(date, 31);
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.offsetDay(new Date(), 31));
    }
}
