/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.energy.dto.ExpCardConfigDto;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.mailvor.modules.user.service.dto.MwUserQueryCriteria;
import com.mailvor.modules.user.service.mapper.UserExtraMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.mailvor.modules.utils.TkUtil.EXPIRED_LEVEL;
import static com.mailvor.utils.DateUtils.getBaseDate;

/**
* @author huangyu
* @date 2020-05-12
*/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MwUserExtraServiceImpl extends BaseServiceImpl<UserExtraMapper, MwUserExtra> implements MwUserExtraService {

    @Resource
    private IGenerator generator;

    @Resource
    private UserExtraMapper mapper;


    @Resource
    private MwSystemConfigService systemConfigService;

    @Override
    public Map<String, Object> queryAll(MwUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserExtra> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public List<MwUserExtra> queryAll(MwUserQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwUser.class, criteria));
    }

    /**
     * 设置会员等级
     * @param uid 用户id
     * @param levelId 等级id
     */
    public void setUserLevel(Long uid, int levelId, String platform){
        MwUserExtra findUserExtra = getById(uid);
        if(findUserExtra == null) {
            findUserExtra = new MwUserExtra();
            findUserExtra.setUid(uid);
        }
        //更新用户体验卡等级
        ExpCardConfigDto expCardConfigDto = systemConfigService.getExpCardConfig();
        Integer expired = expCardConfigDto.getExpired();
        if("tb".equals(platform)) {
            findUserExtra.setLevel(levelId);
            Date date = getBaseDate(findUserExtra.getExpired());
            date = DateUtil.offsetDay(date, expired);
            //设置会员过期时间
            findUserExtra.setExpired(date);
        } else if("jd".equals(platform)) {
            findUserExtra.setLevelJd(levelId);
            Date date = getBaseDate(findUserExtra.getExpiredJd());
            date = DateUtil.offsetDay(date, expired);
            //设置会员过期时间
            findUserExtra.setExpiredJd(date);
        } else if("pdd".equals(platform)) {
            findUserExtra.setLevelPdd(levelId);
            Date date = getBaseDate(findUserExtra.getExpiredPdd());
            date = DateUtil.offsetDay(date, expired);
            //设置会员过期时间
            findUserExtra.setExpiredPdd(date);
        } else if("dy".equals(platform)) {
            findUserExtra.setLevelDy(levelId);
            Date date = getBaseDate(findUserExtra.getExpiredDy());
            date = DateUtil.offsetDay(date, expired);
            //设置会员过期时间
            findUserExtra.setExpiredDy(date);
        } else if("vip".equals(platform)) {
            findUserExtra.setLevelVip(levelId);
            Date date = getBaseDate(findUserExtra.getExpiredVip());
            date = DateUtil.offsetDay(date, expired);
            //设置会员过期时间
            findUserExtra.setExpiredVip(date);
        }
        saveOrUpdate(findUserExtra);

    }


    @Override
    public List<MwUserExtra> getVipList() {
        return list(new LambdaQueryWrapper<MwUserExtra>()
                .eq(MwUserExtra::getLevel, 5)
                .or()
                .eq(MwUserExtra::getLevelJd, 5)
                .or()
                .eq(MwUserExtra::getLevelPdd, 5)
                .or()
                .eq(MwUserExtra::getLevelDy, 5)
                .or()
                .eq(MwUserExtra::getLevelVip, 5));
    }

    @Override
    public List<MwUserExtra> getVipExpiredList(Date now) {
        return list(new LambdaQueryWrapper<MwUserExtra>()
                .le(MwUserExtra::getExpired, now)
                .or()
                .le(MwUserExtra::getExpiredJd, now)
                .or()
                .le(MwUserExtra::getExpiredPdd, now)
                .or()
                .le(MwUserExtra::getExpiredDy, now)
                .or()
                .le(MwUserExtra::getExpiredVip, now));
    }

    @Override
    public void expiredUser() {
        Date now = new Date();
        //计算赠送热度
        //赠送订单根据之前的记录数量生成订单 同时当天没有生成
        //找到所有热度大于等于配置的用户
        List<MwUserExtra> userExtras = getVipExpiredList(now);
        //如果没有用户热度大于配置 返回
        if(CollectionUtils.isEmpty(userExtras)) {
            return;
        }

        //level=99代表开启过 不允许再次开启
        for(MwUserExtra userExtra : userExtras) {
            if(userExtra.getExpired() != null && userExtra.getExpired().compareTo(now) <= 0 ) {
                userExtra.setLevel(EXPIRED_LEVEL);
                userExtra.setExpired(null);
            }
            if(userExtra.getExpiredJd() != null && userExtra.getExpiredJd().compareTo(now) <= 0 ) {
                userExtra.setLevelJd(EXPIRED_LEVEL);
                userExtra.setExpiredJd(null);
            }
            if(userExtra.getExpiredPdd() != null && userExtra.getExpiredPdd().compareTo(now) <= 0 ) {
                userExtra.setLevelPdd(EXPIRED_LEVEL);
                userExtra.setExpiredPdd(null);
            }
            if(userExtra.getExpiredDy() != null && userExtra.getExpiredDy().compareTo(now) <= 0 ) {
                userExtra.setLevelDy(EXPIRED_LEVEL);
                userExtra.setExpiredDy(null);
            }
            if(userExtra.getExpiredVip() != null && userExtra.getExpiredVip().compareTo(now) <= 0 ) {
                userExtra.setLevelVip(EXPIRED_LEVEL);
                userExtra.setExpiredVip(null);
            }
        }
        saveOrUpdateBatch(userExtras);
    }
}
