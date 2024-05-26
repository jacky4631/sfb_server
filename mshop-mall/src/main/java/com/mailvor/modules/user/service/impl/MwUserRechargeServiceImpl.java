/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.user.service.dto.MwUserRechargeDto;
import com.mailvor.modules.user.service.dto.MwUserRechargeQueryCriteria;
import com.mailvor.modules.user.service.mapper.UserRechargeMapper;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;



/**
* @author mazhongjun
* @date 2020-05-12
*/
@Slf4j
@SuppressWarnings("unchecked")
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserRechargeServiceImpl extends BaseServiceImpl<UserRechargeMapper, MwUserRecharge> implements MwUserRechargeService {
    @Autowired
    private IGenerator generator;

    @Autowired
    private UserRechargeMapper rechargeMapper;

    @Override
    public void updateRecharge(MwUserRecharge userRecharge) {

        log.info("更新订单1:{}", JSON.toJSONString(userRecharge));
        //修改状态
        rechargeMapper.updatePaid(userRecharge.getOrderId(), OrderInfoEnum.PAY_STATUS_1.getValue(), new Date());

        log.info("更新订单2:{}", JSON.toJSONString(getById(userRecharge.getId())));

    }

    @Override
    public MwUserRecharge getInfoByOrderId(String orderId) {
        MwUserRecharge userRecharge = new MwUserRecharge();
        userRecharge.setOrderId(orderId);

        return rechargeMapper.selectOne(Wrappers.query(userRecharge));
    }

    /**
     * 添加充值记录
     * @param user 用户
     * @param price 充值金额
     */
    @Override
    public String addRecharge(MwUser user, String price, String rechargeType, Integer grade,
                              String platform ,String rechargeId, Long channelId) {
        return addRecharge(user, price, rechargeType, grade, platform, rechargeId, channelId, null, 0);

    }

    @Override
    public String addRecharge(MwUser user, String price, String payType, Integer grade,
                              String platform , String rechargeId, Long channelId, Long uid, Integer type) {
        if(StrUtil.isBlank(price)){
            throw new MshopException("参数非法");
        }
        MwUserRecharge userRecharge = new MwUserRecharge();

        String orderSn = IdUtil.getSnowflake(0,0).nextIdStr();

        userRecharge.setNickname(user.getNickname());
        userRecharge.setOrderId(orderSn);
        userRecharge.setOid(user.getUid());
        //如果uid存在设置uid，如果uid不存在就是当前登录用户
        if(uid != null && uid > 0) {
            userRecharge.setUid(uid);
        } else {
            userRecharge.setUid(user.getUid());
        }
        if(type == null) {
            type = 0;
        }

        userRecharge.setPrice(new BigDecimal(price));
        userRecharge.setGivePrice(BigDecimal.ZERO);
        userRecharge.setRechargeType(payType);
        userRecharge.setRechargeId(rechargeId);
        userRecharge.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
        userRecharge.setType(type);
        userRecharge.setGrade(grade);
        userRecharge.setPlatform(platform);
        userRecharge.setChannelId(channelId);

        rechargeMapper.insert(userRecharge);

        return orderSn;
    }

    //==========================================================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwUserRechargeQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserRecharge> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwUserRechargeDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwUserRecharge> queryAll(MwUserRechargeQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwUserRecharge.class, criteria));
    }


    @Override
    public void download(List<MwUserRechargeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwUserRechargeDto mwUserRecharge : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("充值用户UID", mwUserRecharge.getUid());
            map.put("订单号", mwUserRecharge.getOrderId());
            map.put("充值金额", mwUserRecharge.getPrice());
            map.put("充值类型", mwUserRecharge.getRechargeType());
            map.put("是否充值", mwUserRecharge.getPaid());
            map.put("充值支付时间", mwUserRecharge.getPayTime());
            map.put("退款金额", mwUserRecharge.getRefundPrice());
            map.put("昵称", mwUserRecharge.getNickname());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public MwUserRecharge getLatestPaidInfo(Long uid, String platform, Integer grade) {
        LambdaQueryWrapper<MwUserRecharge> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(MwUserRecharge::getPaid, OrderInfoEnum.PAY_STATUS_1.getValue())
                .eq(MwUserRecharge::getUid,uid)
                .eq(MwUserRecharge::getGrade, grade)
                .eq(MwUserRecharge::getPlatform, platform)
                .orderByDesc(MwUserRecharge::getPayTime);
        return this.getOne(wrapper,false);
    }


    public Double totalRechargePrice(Integer type){
        switch (type){
            case 1:
                return rechargeMapper.sumPrice(getWrapper(DateUtils.getToday()));
            case 2:
                return rechargeMapper.sumPrice(getWrapperPro(DateUtils.getProDay(), DateUtils.getToday()));
            case 3:
                return rechargeMapper.sumPrice(getWrapper(DateUtils.getMonth()));
            case 4:
                return rechargeMapper.sumPrice(getWrapperPro(DateUtils.getProMonth(), DateUtils.getMonth()));
            case 5:
                return rechargeMapper.sumPrice(getWrapper(DateUtils.get30Day()));
            case 6:
                return rechargeMapper.sumPrice(getWrapper(DateUtils.get7Day()));
        }
        return 0.0;
    }


    private LambdaQueryWrapper<MwUserRecharge> getWrapper(DateTime start) {
        return getWrapperPro(start, null);
    }

    private LambdaQueryWrapper<MwUserRecharge> getWrapperPro(Date start, Date end) {
        LambdaQueryWrapper<MwUserRecharge> wrapperTwo = new LambdaQueryWrapper<>();
        wrapperTwo.eq(MwUserRecharge::getPaid, 1);

        if(start != null) {
            wrapperTwo.ge(MwUserRecharge::getCreateTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MwUserRecharge::getCreateTime, end);
        }
        return wrapperTwo;
    }

    @Override
    public MwUserRecharge getRecharge(String orderId) {
        //处理充值
        MwUserRecharge userRecharge = getInfoByOrderId(orderId);
        if(userRecharge == null) {
            throw new MshopException("订单不存在");
        }
        if(OrderInfoEnum.PAY_STATUS_1.getValue().equals(userRecharge.getPaid())) {
            throw new MshopException("订单已支付");
        }
        return userRecharge;
    }

}
