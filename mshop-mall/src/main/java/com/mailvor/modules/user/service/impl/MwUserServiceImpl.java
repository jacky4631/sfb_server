/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.mailvor.api.ApiCode;
import com.mailvor.api.MshopException;
import com.mailvor.api.UnAuthenticatedException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.*;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.energy.service.UserEnergyService;
import com.mailvor.modules.order.domain.MwStoreOrderCartInfo;
import com.mailvor.modules.order.service.MwStoreOrderCartInfoService;
import com.mailvor.modules.order.service.dto.UserRefundDto;
import com.mailvor.modules.order.service.mapper.StoreOrderMapper;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.push.service.JPushService;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.user.config.HbUnlockConfig;
import com.mailvor.modules.user.domain.*;
import com.mailvor.modules.user.service.*;
import com.mailvor.modules.user.service.dto.*;
import com.mailvor.modules.user.service.mapper.UserBillMapper;
import com.mailvor.modules.user.service.mapper.UserMapper;
import com.mailvor.modules.user.vo.MwCommissionInfoQueryVo;
import com.mailvor.modules.user.vo.MwUserQueryVo;
import com.mailvor.modules.user.vo.MwUserVipQueryVo;
import com.mailvor.modules.user.vo.UserFeeQueryVo;
import com.mailvor.modules.utils.FeeUtil;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.enums.BillDetailEnum.TYPE_12;
import static com.mailvor.modules.utils.FeeUtil.DEFAULT_FEE_SCALE;
import static com.mailvor.modules.utils.TkUtil.EXPIRED_LEVEL;
import static java.util.stream.Collectors.toList;

/**
* @author huangyu
* @date 2020-05-12
*/
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserServiceImpl extends BaseServiceImpl<UserMapper, MwUser> implements MwUserService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private UserMapper mwUserMapper;
    @Autowired
    private StoreOrderMapper storeOrderMapper;
    @Autowired
    private UserBillMapper userBillMapper;


    @Autowired
    private MwSystemUserLevelService systemUserLevelService;
    @Autowired
    private MwUserLevelService userLevelService;
    @Autowired
    private MwSystemConfigService systemConfigService;
    @Autowired
    private MwUserBillService billService;
    @Autowired
    private MwStoreOrderCartInfoService storeOrderCartInfoService;
    @Resource
    private MwUserExtractService extractService;
    @Resource
    private MailvorTbOrderService tbOrderService;

    @Resource
    private MailvorJdOrderService jdOrderService;

    @Resource
    private MailvorPddOrderService pddOrderService;

    @Resource
    private MailvorVipOrderService vipOrderService;

    @Resource
    private MailvorDyOrderService dyOrderService;


    @Resource
    private MailvorMtOrderService mtOrderService;

    @Resource
    private JPushService jPushService;

    @Resource
    private MwUserFeeLogOptService feeLogOptService;

    @Resource
    private FeeOrderService feeOrderService;
    @Resource
    private MwUserCardService cardService;

    @Resource
    private MwUserPoolService poolService;

    @Resource
    private UserEnergyService energyService;

    @Resource
    private MwUserUnionService userUnionService;

    /**
     * 返回用户累计充值金额与消费金额
     * @param uid uid
     * @return Double[]
     */
    @Override
    public Double[] getUserMoney(Long uid){
        double sumPrice = storeOrderMapper.sumPrice(uid);
        double sumRechargePrice = userBillMapper.sumRechargePrice(uid);
        return new Double[]{sumPrice,sumRechargePrice};
    }


    /**
     * 增加购买次数
     * @param uid uid
     */
    @Override
    public void incPayCount(Long uid) {
        mwUserMapper.incPayCount(uid);
    }

    /**
     * 减去用户余额
     * @param uid uid
     * @param payPrice 金额
     */
    @Override
    public void decPrice(Long uid, BigDecimal payPrice) {
        mwUserMapper.decPrice(payPrice,uid);
    }

    /**
     * 减去用户积分
     * @param uid 用户id
     * @param integral 积分
     */
    @Override
    public void decIntegral(Long uid, double integral) {
        mwUserMapper.decIntegral(integral,uid);
    }

    /**
     * 获取我的分销下人员列表
     * @param uid uid
     * @param page page
     * @param limit limit
     * @param grade ShopCommonEnum.GRADE_0
     * @param keyword 关键字搜索
     * @param sort 排序
     * @return list
     */
    @Override
    public List<PromUserDto> getUserSpreadGrade(Long uid, int page, int limit, Integer grade,
                                                String keyword, String sort) {
        LambdaQueryWrapper queryWrapper;
        if(StringUtils.isBlank(keyword)) {
            queryWrapper = Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getSpreadUid, uid);
        } else if (ShopCommonEnum.GRADE_0.getValue().equals(grade)) {//-级
            //查询手机号和昵称
            queryWrapper = Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getSpreadUid, uid)
                    .and(i->i.like(MwUser::getPhone,keyword).or().like(MwUser::getNickname,keyword));
        } else {
            //只查询昵称
            queryWrapper = Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getSpreadUid, uid);
        }
        List<MwUser> userList = mwUserMapper.selectList(queryWrapper);
        List<Long> userIds = userList.stream()
                .map(MwUser::getUid)
                .collect(toList());

        List<PromUserDto> list = new ArrayList<>();
        if (userIds.isEmpty()) {
            return list;
        }

        if (StrUtil.isBlank(sort)) {
            sort = "u.uid desc";
        }

        Page<MwUser> pageModel = new Page<>(page, limit);
        //上面的sort貌似不起作用，用这个
        pageModel.addOrder(OrderItem.desc("uid"));
        if (ShopCommonEnum.GRADE_0.getValue().equals(grade)) {//-级
            list = mwUserMapper.getAppUserSpreadCountList(pageModel, userIds, sort);
        } else {//二级
            List<MwUser> userListT;
            if(StringUtils.isBlank(keyword)) {
                userListT = mwUserMapper.selectList(Wrappers.<MwUser>lambdaQuery()
                        .in(MwUser::getSpreadUid, userIds));
            } else {
                userListT = mwUserMapper.selectList(Wrappers.<MwUser>lambdaQuery()
                        .in(MwUser::getSpreadUid, userIds)
                        .like(MwUser::getNickname,keyword));
            }
            List<Long> userIdsT = userListT.stream()
                    .map(MwUser::getUid)
                    .collect(toList());
            if (userIdsT.isEmpty()) {
                return list;
            }
            list = mwUserMapper.getAppUserSpreadCountList(pageModel, userIdsT, sort);

        }
        return list;
    }

    /**
     * 统计分销人员
     *
     * @param uid uid
     * @return map
     */
    @Override
    public Map<String, Long> getSpreadCount(Long uid) {
        Long countOne = mwUserMapper.selectCount(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getSpreadUid,uid));

        Long countTwo = 0L;
        List<MwUser> userList = mwUserMapper.selectList((Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getSpreadUid,uid)));
        List<Long> userIds = userList.stream().map(MwUser::getUid)
                .collect(toList());
        if(!userIds.isEmpty()){
            countTwo = mwUserMapper.selectCount(Wrappers.<MwUser>lambdaQuery()
                    .in(MwUser::getSpreadUid,userIds));
        }

        Map<String,Long> map = new LinkedHashMap<>(2);
        map.put("first",countOne); //一级
        map.put("second",countTwo);//二级

        return map;
    }
    @Override
    public Long getSpreadCount(Long uid, Integer grade) {

        if (ShopCommonEnum.GRADE_0.getValue().equals(grade)) {
            return mwUserMapper.selectCount(Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getSpreadUid,uid));
        } else if(ShopCommonEnum.GRADE_1.getValue().equals(grade)){

            Long countTwo = 0L;
            List<MwUser> userList = mwUserMapper.selectList((Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getSpreadUid,uid)));
            List<Long> userIds = userList.stream().map(MwUser::getUid)
                    .collect(toList());
            if(!userIds.isEmpty()){
                countTwo = mwUserMapper.selectCount(Wrappers.<MwUser>lambdaQuery()
                        .in(MwUser::getSpreadUid,userIds));
            }
            return countTwo;
        }
        return 0L;
    }
    /**
     * 一级返佣
     * @param order 订单
     */
    @Override
    public void backOrderBrokerage(MwStoreOrderQueryVo order) {
        //如果分销没开启直接返回
        String open = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_OPEN);
        if(StrUtil.isBlank(open) || ShopCommonEnum.ENABLE_2.getValue().toString().equals(open)) {
            return;
        }


        //获取购买商品的用户
        MwUser userInfo =  this.getById(order.getUid());
        System.out.println("userInfo:"+userInfo);
        //当前用户不存在 没有上级  直接返回
        if(ObjectUtil.isNull(userInfo) || userInfo.getSpreadUid() == 0) {
            return;
        }


        MwUser preUser = this.getById(userInfo.getSpreadUid());

        //一级返佣金额
        BigDecimal brokeragePrice = this.computeProductBrokerage(order, Brokerage.LEVEL_1);

        //返佣金额小于等于0 直接返回不返佣金

        if(brokeragePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        //计算上级推广员返佣之后的金额
        double balance = NumberUtil.add(preUser.getBrokeragePrice(),brokeragePrice).doubleValue();
        String mark = userInfo.getNickname()+"成功消费"+order.getPayIntegral()+"元,奖励推广佣金"+
                brokeragePrice;
        //增加流水
        billService.income(userInfo.getSpreadUid(),"获得推广佣金",BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_2.getValue(),brokeragePrice.doubleValue(),balance, mark,order.getId().toString());

        //添加用户余额
        mwUserMapper.incBrokeragePrice(brokeragePrice, userInfo.getSpreadUid());

        //一级返佣成功 跳转二级返佣
        this.backOrderBrokerageTwo(order);

    }

    /**
     * 更新用户余额
     * @param uid y用户id
     * @param price 金额
     */
    @Override
    public void incMoney(Long uid, BigDecimal price) {
        if(price!=null&&price.doubleValue()>0){
            mwUserMapper.incMoney(uid,price);
        }
    }

    /**
     * 获取用户信息
     * @param uid uid
     * @return MwUserQueryVo
     */
    @Override
    public MwUserQueryVo getMwUserById(Long uid) {
        MwUser user = this.getById(uid);
        if(user == null) {
            return new MwUserQueryVo();
        }
        MwUserQueryVo userQueryVo = generator.convert(user, MwUserQueryVo.class);
        //不返回具体授权信息
        MwUserUnion userUnion = userUnionService.getOne(uid);
        if(userUnion != null && StringUtils.isNotBlank(userUnion.getOpenId())) {
            userQueryVo.setWxProfile("wechat authed");
        }
        if(user.getAliProfile() != null) {
            userQueryVo.setAliProfile("ali authed");
        }
        return userQueryVo;
    }

    @Override
    public void remove(Long uid) {
        MwUser mwUser = mwUserMapper.selectById(uid);
        if(mwUser.getNowMoney().compareTo(BigDecimal.ZERO) == 1) {
            throw new MshopException("注销前请现去提现");
        }
        mwUser.setAliProfile(null);
        mwUser.setUsername(mwUser.getUsername()+"_deleted" + System.currentTimeMillis());
        mwUserMapper.updateById(mwUser);
        removeById(uid);
        userUnionService.remove(uid);
        cardService.cleanCard(uid);
    }

    @Override
    public MwCommissionInfoQueryVo getCommissionInfo(MwUser user) {

        MwCommissionInfoQueryVo queryVo = new MwCommissionInfoQueryVo();
        if(user == null) {
            //如果用户不存在读取默认拆红包比例
            queryVo.setHbRebateScale(Integer.parseInt(systemConfigService.getData(SystemConfigConstants.TK_HB_REBATE_SCALE)));
        } else {
            String open = systemConfigService.getData(SystemConfigConstants.STORE_MULTI_VIP_OPEN);
            boolean multiVipOpen = !(StrUtil.isBlank(open) || ShopCommonEnum.ENABLE_2.getValue().toString().equals(open));
            if(multiVipOpen) {
                List<MwSystemUserLevel> userLevels = systemUserLevelService.list();
                List<MwSystemUserLevel> tbLevels = new ArrayList<>(2);
                List<MwSystemUserLevel> jdLevels = new ArrayList<>(2);
                List<MwSystemUserLevel> pddLevels = new ArrayList<>(2);
                List<MwSystemUserLevel> dyLevels = new ArrayList<>(2);
                List<MwSystemUserLevel> vipLevels = new ArrayList<>(2);
                for(MwSystemUserLevel vo: userLevels) {
                    if(PlatformEnum.TB.getValue().equals(vo.getType())) {
                        if(user.getLevel().equals(vo.getGrade())) {
                            queryVo.setHbRebateScale(vo.getDiscount().intValue());
                        }
                        tbLevels.add(vo);
                    }else if(PlatformEnum.JD.getValue().equals(vo.getType())) {
                        if(user.getLevelJd().equals(vo.getGrade())) {
                            queryVo.setJdHbRebateScale(vo.getDiscount().intValue());
                        }
                        jdLevels.add(vo);
                    }else if(PlatformEnum.PDD.getValue().equals(vo.getType())) {
                        if(user.getLevelPdd().equals(vo.getGrade())) {
                            queryVo.setPddHbRebateScale(vo.getDiscount().intValue());
                        }
                        pddLevels.add(vo);
                    }else if(PlatformEnum.DY.getValue().equals(vo.getType())) {
                        if(user.getLevelDy().equals(vo.getGrade())) {
                            queryVo.setDyHbRebateScale(vo.getDiscount().intValue());
                        }
                        dyLevels.add(vo);
                    }else if(PlatformEnum.VIP.getValue().equals(vo.getType())) {
                        if(user.getLevelVip().equals(vo.getGrade())) {
                            queryVo.setVipHbRebateScale(vo.getDiscount().intValue());
                        }
                        vipLevels.add(vo);
                    }
                }
                queryVo.setTbTimes(TkUtil.getPlatformHbTimes(tbLevels).doubleValue());
                queryVo.setJdTimes(TkUtil.getPlatformHbTimes(jdLevels).doubleValue());
                queryVo.setPddTimes(TkUtil.getPlatformHbTimes(pddLevels).doubleValue());
                queryVo.setDyTimes(TkUtil.getPlatformHbTimes(dyLevels).doubleValue());
                queryVo.setVipTimes(TkUtil.getPlatformHbTimes(vipLevels).doubleValue());
            } else {
                MwSystemUserLevel systemUserLevel = systemUserLevelService.getById(user.getLevel());
                queryVo.setHbRebateScale(systemUserLevel.getDiscount().intValue());
            }

        }
        queryVo.setTbRebateScale(Integer.parseInt(systemConfigService.getData(SystemConfigConstants.TK_TB_REBATE_SCALE)));

        //最大最小倍数默认读当前平台
        String minTimes = systemConfigService.getData(SystemConfigConstants.TK_HB_MIN_TIMES + "_" + PAY_NAME);
        if(StringUtils.isBlank(minTimes)) {
            minTimes = systemConfigService.getData(SystemConfigConstants.TK_HB_MIN_TIMES);
        }
        queryVo.setHbMinTimes(Double.parseDouble(minTimes));

        String maxTimes = systemConfigService.getData(SystemConfigConstants.TK_HB_MAX_TIMES + "_" + PAY_NAME);
        if(StringUtils.isBlank(maxTimes)) {
            maxTimes = systemConfigService.getData(SystemConfigConstants.TK_HB_MAX_TIMES);
        }
        queryVo.setHbMaxTimes(Double.parseDouble(maxTimes));
        return  queryVo;

    }

    @Override
    public MwUserQueryVo getNewMwUserById(MwUser mwUser){
        return getNewMwUserById(mwUser, false);
    }
    /**
     * 获取用户个人详细信息
     * @param mwUser mwUser
     * @return MwUserQueryVo
     */
    @Override
    public MwUserQueryVo getNewMwUserById(MwUser mwUser, Boolean baseInfo) {
        Long uid = mwUser.getUid();
        MwUserQueryVo userQueryVo = getMwUserById(uid);
        if(userQueryVo == null){
            throw new UnAuthenticatedException(ApiCode.UNAUTHORIZED);
        }
        if(baseInfo != null && baseInfo) {
            return userQueryVo;
        }
        userQueryVo.setTotalCash(0.0);
        Long spreadCount = this.setUserSpreadCount(mwUser);
        userQueryVo.setSpreadCount(spreadCount);
        userQueryVo.setCanChangeCode(canChangeWord(mwUser, spreadCount));
        //设置累计提现金额
        double extractCount = extractService.extractSum(mwUser.getUid());
        userQueryVo.setTotalExtract(extractCount);

        String storeBrokerageRatioStr = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_RATIO);
        String storeBrokerageTwoStr = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_TWO);
        userQueryVo.setStoreBrokerageRatio(StringUtils.isBlank(storeBrokerageRatioStr)?"10":storeBrokerageRatioStr);
        userQueryVo.setStoreBrokerageTwo(StringUtils.isBlank(storeBrokerageTwoStr)?"5":storeBrokerageTwoStr);
        return userQueryVo;
    }

    public void spreadUserHb(MwUser parentUser, MwUser user) {
        //邀请用户奖励
        //前五位用户奖励给parentUser 0.4元 5人2元
        String spreadUserCountStr = systemConfigService.getData(SystemConfigConstants.SPREAD_USER_COUNT);
        if(StringUtils.isBlank(spreadUserCountStr)) {
            spreadUserCountStr = "5";
        }
        Integer spreadUserCount = Integer.parseInt(spreadUserCountStr);

        //如果设置为0无奖励 直接返回
        if(spreadUserCount == 0) {
            return;
        }
        String spreadUserHbStr = systemConfigService.getData(SystemConfigConstants.SPREAD_USER_HB);
        if(StringUtils.isBlank(spreadUserHbStr)) {
            spreadUserHbStr = "0.4";
        }
        Double spreadUserHb = Double.parseDouble(spreadUserHbStr);

        List<MwUser> userList = getSpreadList(parentUser.getUid(), 0);
        if(userList.size() <= spreadUserCount) {
            incMoney(parentUser.getUid(),  BigDecimal.valueOf(spreadUserHb));
            parentUser = getById(parentUser.getUid());
            //增加流水
            String mark = "邀请用户‘"+TkUtil.getAnonymousName(user.getNickname()) + "'获得"+spreadUserHb + "元";
            billService.income(parentUser.getUid(),user.getUid(), "邀请用户奖励", BillDetailEnum.CATEGORY_1.getValue(),
                    BillDetailEnum.TYPE_14.getValue(),spreadUserHb,parentUser.getNowMoney().doubleValue(), mark,user.getUid().toString());
            jPushService.push(mark, parentUser.getUid());

        }
    }

    @Override
    public MwUserVipQueryVo getUserVipInfo(MwUser mwUser) {
        Long uid = mwUser.getUid();
        MwUser user = this.getById(uid);;
        if(user == null){
            throw new UnAuthenticatedException(ApiCode.UNAUTHORIZED);
        }
        MwUserVipQueryVo userQueryVo = generator.convert(user, MwUserVipQueryVo.class);
        LambdaQueryChainWrapper<MwSystemUserLevel> levelQuery = systemUserLevelService.lambdaQuery();
        levelQuery.or(wrapper -> wrapper
                .eq(MwSystemUserLevel::getGrade, mwUser.getLevel())
                .eq(MwSystemUserLevel::getType, PlatformEnum.TB.getValue()));

        //设置多会员信息
        if(mwUser.getLevelPdd() > 0) {
            levelQuery.or(wrapper -> wrapper
                    .eq(MwSystemUserLevel::getGrade, mwUser.getLevelPdd())
                    .eq(MwSystemUserLevel::getType, PlatformEnum.PDD.getValue()));
        }
        if(mwUser.getLevelJd() > 0) {
            levelQuery.or(wrapper -> wrapper
                    .eq(MwSystemUserLevel::getGrade, mwUser.getLevelJd())
                    .eq(MwSystemUserLevel::getType, PlatformEnum.JD.getValue()));;
        }
        if(mwUser.getLevelDy() > 0) {
            levelQuery.or(wrapper -> wrapper
                    .eq(MwSystemUserLevel::getGrade, mwUser.getLevelDy())
                    .eq(MwSystemUserLevel::getType, PlatformEnum.DY.getValue()));;
        }
        if(mwUser.getLevelVip() > 0) {
            levelQuery.or(wrapper -> wrapper
                    .eq(MwSystemUserLevel::getGrade, mwUser.getLevelVip())
                    .eq(MwSystemUserLevel::getType, PlatformEnum.VIP.getValue()));;
        }
        List<MwSystemUserLevel> systemUserLevels = levelQuery.list();
        for(MwSystemUserLevel mwSystemUserLevel : systemUserLevels) {
            String type = mwSystemUserLevel.getType();
            String name = mwSystemUserLevel.getName();
            double discount = mwSystemUserLevel.getDiscount().doubleValue();
            if(PlatformEnum.TB.getValue().equals(type)) {
                userQueryVo.setLevelName(name);
                userQueryVo.setDiscount(discount);
            } else if(PlatformEnum.JD.getValue().equals(type)) {
                userQueryVo.setLevelNameJd(name);
                userQueryVo.setDiscountJd(discount);
            }else if(PlatformEnum.PDD.getValue().equals(type)) {
                userQueryVo.setLevelNamePdd(name);
                userQueryVo.setDiscountPdd(discount);
            }else if(PlatformEnum.DY.getValue().equals(type)) {
                userQueryVo.setLevelNameDy(name);
                userQueryVo.setDiscountDy(discount);
            }else if(PlatformEnum.VIP.getValue().equals(type)) {
                userQueryVo.setLevelNameVip(name);
                userQueryVo.setDiscountVip(discount);
            }
        }
        return userQueryVo;
    }
    @Override
    public Boolean hasUnlockOrder(MwUser mwUser) {
        Long uid = mwUser.getUid();
        MwUser user = getById(uid);
        Integer refund = poolService.getRefund(uid);
        HbUnlockConfig unlockConfig = systemConfigService.getHbUnlockConfig();

        CompletableFuture<Boolean> tbCashFuture = CompletableFuture.supplyAsync(()->
                tbOrderService.hasUnlockOrder(uid, 0, TkUtil.getUnlockDay(user.getLevel(), refund, unlockConfig)));
        CompletableFuture<Boolean> pddCashFuture = CompletableFuture.supplyAsync(()->
                pddOrderService.hasUnlockOrder(uid, 0,TkUtil.getUnlockDay(user.getLevelPdd(), refund, unlockConfig)));
        CompletableFuture<Boolean> jdCashFuture = CompletableFuture.supplyAsync(()->
                jdOrderService.hasUnlockOrder(uid, 0,TkUtil.getUnlockDay(user.getLevelJd(), refund, unlockConfig)));
        CompletableFuture<Boolean> vipCashFuture = CompletableFuture.supplyAsync(()->
                vipOrderService.hasUnlockOrder(uid, 0,TkUtil.getUnlockDay(user.getLevelVip(), refund, unlockConfig)));
        CompletableFuture<Boolean> dyCashFuture = CompletableFuture.supplyAsync(()->
                dyOrderService.hasUnlockOrder(uid, 0,TkUtil.getUnlockDay(user.getLevelDy(), refund, unlockConfig)));
        CompletableFuture.allOf(tbCashFuture, pddCashFuture, jdCashFuture, vipCashFuture, dyCashFuture);
        Boolean hasUnlockOrder = false;
        try {
            hasUnlockOrder = tbCashFuture.get()
                    || pddCashFuture.get()
                    || jdCashFuture.get()
                    || vipCashFuture.get()
                    || dyCashFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return hasUnlockOrder;
    }
    public Integer canChangeWord(MwUser mwUser, Long spreadCount) {
        //如果下级人数>=2 并且changeWord==0 说明可修改 用户设置完后changeWord=1
        //如果下级人数>=20 并且changeWord==1 用户设置完后设置changeWord=2
        //如果下级人数>=200 并且changeWord==2 用户设置完后设置changeWord=3

        if((spreadCount >= 2 && mwUser.getChangeWord() == 0)
                || (spreadCount >= 20 && mwUser.getChangeWord() == 1)
                || (spreadCount >= 200 && mwUser.getChangeWord() == 2)) {
            return 1;
        }
        return 0;
    }



    /**
     * 返回会员价
     * @param price 原价
     * @param uid 用户id
     * @return vip 价格
     */
    @Override
    public double setLevelPrice(double price, long uid) {
       LambdaQueryWrapper<MwUserLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserLevel::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .eq(MwUserLevel::getUid,uid)
                .orderByDesc(MwUserLevel::getGrade)
                .last("limit 1");
        MwUserLevel userLevel = userLevelService.getOne(wrapper);
        MwSystemUserLevel systemUserLevel = new MwSystemUserLevel();
        if(ObjectUtil.isNotNull(userLevel)) {
            systemUserLevel=  systemUserLevelService.getById(userLevel.getLevelId());
        }
        int discount = 100;
        if(ObjectUtil.isNotNull(userLevel)) {
            discount = systemUserLevel.getDiscount().intValue();
        }
        return NumberUtil.mul(NumberUtil.div(discount,100),price);
    }


    /**
     * 设置推广关系
     * @param spread 上级人
     * @param uid 本人
     */
    @Override
    public boolean setSpread(String spread, long uid) {
        if(StrUtil.isBlank(spread) || !NumberUtil.isNumber(spread)) {
            return false;
        }

        //如果分销没开启直接返回
        String open = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_OPEN);
        if(StrUtil.isBlank(open) || ShopCommonEnum.ENABLE_2.getValue().toString().equals(open)) {
            return false;
        }
        //当前用户信息
        MwUser userInfo = this.getById(uid);
        if(ObjectUtil.isNull(userInfo)) {
            return false;
        }

        //当前用户有上级直接返回
        if(userInfo.getSpreadUid() != null && userInfo.getSpreadUid() > 0) {
            return false;
        }
        //没有推广编号直接返回
        long spreadInt = Long.valueOf(spread);
        if(spreadInt == 0) {
            return false;
        }
        if(spreadInt == uid) {
            return false;
        }

        //不能互相成为上下级
        MwUser userInfoT = this.getById(spreadInt);
        if(ObjectUtil.isNull(userInfoT)) {
            return false;
        }

        if(userInfoT.getSpreadUid() == uid) {
            return false;
        }

        MwUser mwUser = MwUser.builder()
                .spreadUid(spreadInt)
                .spreadTime(new Date())
                .uid(uid)
                .build();

        mwUserMapper.updateById(mwUser);
        jPushService.push("你的朋友【" + userInfo.getNickname() + "】成为你的金星用户！", spreadInt);

        MwUser pUser = this.getById(spreadInt);
        if(pUser != null && pUser.getSpreadUid() != null && pUser.getSpreadUid() > 0) {
            jPushService.push("你朋友的朋友【" + userInfo.getNickname() + "】成为你的银星用户！", pUser.getSpreadUid());
        }
        return true;
    }


    /**
     * 二级返佣
     * @param order 订单
     */
    private void backOrderBrokerageTwo(MwStoreOrderQueryVo order) {

        MwUser userInfo =  this.getById(order.getUid());

        //获取上推广人
        MwUser userInfoTwo = this.getById(userInfo.getSpreadUid());

        //上推广人不存在 或者 上推广人没有上级    直接返回
        if(ObjectUtil.isNull(userInfoTwo) || userInfoTwo.getSpreadUid() == 0) {
            return;
        }


        //指定分销 判断 上上级是否时推广员  如果不是推广员直接返回
        MwUser preUser = this.getById(userInfoTwo.getSpreadUid());


        //二级返佣金额
        BigDecimal brokeragePrice = this.computeProductBrokerage(order,Brokerage.LEVEL_2);

        //返佣金额小于等于0 直接返回不返佣金
        if(brokeragePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        //获取上上级推广员信息
        double balance = NumberUtil.add(preUser.getBrokeragePrice(),brokeragePrice).doubleValue();
        String mark = "二级推广人"+userInfo.getNickname()+"成功消费"+order.getPayIntegral()+"元,奖励推广佣金"+
                brokeragePrice;

        //增加流水
        billService.income(userInfoTwo.getSpreadUid(),"获得推广佣金",BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_2.getValue(),brokeragePrice.doubleValue(),balance, mark,order.getId().toString());
        //添加用户余额
        mwUserMapper.incBrokeragePrice(brokeragePrice,
                userInfoTwo.getSpreadUid());
    }


    /**
     * 计算获取返佣金额
     * @param order 订单信息
     * @param level 分销级别
     * @return double
     */
    private BigDecimal computeProductBrokerage(MwStoreOrderQueryVo order , Brokerage level){
        List<MwStoreOrderCartInfo> storeOrderCartInfoList = storeOrderCartInfoService
                .list(Wrappers.<MwStoreOrderCartInfo>lambdaQuery());
        BigDecimal oneBrokerage = BigDecimal.ZERO;//一级返佣金额
        BigDecimal twoBrokerage = BigDecimal.ZERO;//二级返佣金额

        List<String> cartInfos = storeOrderCartInfoList.stream()
                .map(MwStoreOrderCartInfo::getCartInfo)
                .collect(toList());

        for (String cartInfo : cartInfos){
            MwStoreCartQueryVo cart = JSON.parseObject(cartInfo, MwStoreCartQueryVo.class);

            MwStoreProductQueryVo storeProductVO = cart.getProductInfo();
            //产品是否单独分销
            if(ShopCommonEnum.IS_SUB_1.getValue().equals(storeProductVO.getIsSub())){
                oneBrokerage = OrderUtil.getRoundFee(NumberUtil.add(oneBrokerage,
                        NumberUtil.mul(cart.getCartNum(),storeProductVO.getAttrInfo().getBrokerage())));

                twoBrokerage = OrderUtil.getRoundFee(NumberUtil.add(twoBrokerage,
                        NumberUtil.mul(cart.getCartNum(),storeProductVO.getAttrInfo().getBrokerageTwo())));
            }

        }

        //获取后台一级返佣比例
        String storeBrokerageRatioStr = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_RATIO);
        String storeBrokerageTwoStr = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_TWO);


        //一级返佣比例未设置直接返回
        if(StrUtil.isBlank(storeBrokerageRatioStr)
                || !NumberUtil.isNumber(storeBrokerageRatioStr)){
            return oneBrokerage;
        }
        //二级返佣比例未设置直接返回
        if(StrUtil.isBlank(storeBrokerageTwoStr)
                || !NumberUtil.isNumber(storeBrokerageTwoStr)){
            return twoBrokerage;
        }


        switch (level){
            case LEVEL_1:
                //根据订单获取一级返佣比例
                BigDecimal storeBrokerageRatio = OrderUtil.getRoundFee(NumberUtil.div(storeBrokerageRatioStr,"100"));
                BigDecimal brokeragePrice = NumberUtil
                        .round(NumberUtil.mul(order.getPayIntegral(),storeBrokerageRatio),2);
                //固定返佣 + 比例返佣 = 总返佣金额
                return NumberUtil.add(oneBrokerage,brokeragePrice);
            case LEVEL_2:
                //根据订单获取一级返佣比例
                BigDecimal storeBrokerageTwo = OrderUtil.getRoundFee(NumberUtil.div(storeBrokerageTwoStr,"100"));
                BigDecimal storeBrokerageTwoPrice = NumberUtil
                        .round(NumberUtil.mul(order.getPayIntegral(),storeBrokerageTwo),2);
                //固定返佣 + 比例返佣 = 总返佣金额
                return NumberUtil.add(twoBrokerage,storeBrokerageTwoPrice);
            default:
        }


        return BigDecimal.ZERO;

    }



    /**
     * 更新下级人数
     * @param mwUser user
     */
    private Long setUserSpreadCount(MwUser mwUser) {
        Long count = mwUserMapper.selectCount(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getSpreadUid,mwUser.getUid()));
        mwUser.setSpreadCount(count);
        mwUserMapper.updateById(mwUser);
        return count;
    }


    //===========后面管理后台部分=====================//


    /**
     * 查看下级
     * @param uid uid
     * @param grade 等级
     * @return list
     */
    @Override
    public List<PromUserDto> querySpread(Long uid, Integer grade) {
        return this.getUserSpreadGrade(uid,1, 999,grade,"","");
    }


    @Override
    public Map<String, Object> queryAll(MwUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUser> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwUserDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public PageResult<ShopDetailDto> queryAll(Wrapper<MwUser> queryWrapper, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUser> page = new PageInfo<>(baseMapper.selectList(queryWrapper));
        return generator.convertPageInfo(page,ShopDetailDto.class);

    }
    //@Cacheable
    @Override
    public List<MwUser> queryAll(MwUserQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwUser.class, criteria));
    }


    @Override
    public void download(List<MwUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwUserDto mwUser : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户账户(跟accout一样)", mwUser.getUsername());
            map.put("用户密码（跟pwd）", mwUser.getPassword());
            map.put("真实姓名", mwUser.getRealName());
            map.put("生日", mwUser.getBirthday());
            map.put("身份证号码", mwUser.getCardId());
            map.put("用户备注", mwUser.getMark());
            map.put("用户分组id", mwUser.getGroupId());
            map.put("用户昵称", mwUser.getNickname());
            map.put("用户头像", mwUser.getAvatar());
            map.put("手机号码", mwUser.getPhone());
            map.put("添加时间", mwUser.getCreateTime());
            map.put("添加ip", mwUser.getAddIp());
            map.put("用户余额", mwUser.getNowMoney());
            map.put("佣金金额", mwUser.getBrokeragePrice());
            map.put("用户剩余积分", mwUser.getIntegral());
            map.put("连续签到天数", mwUser.getSignNum());
            map.put("1为正常，0为禁止", mwUser.getStatus());
            map.put("等级", mwUser.getLevel());
            map.put("推广元id", mwUser.getSpreadUid());
            map.put("推广员关联时间", mwUser.getSpreadTime());
            map.put("用户类型", mwUser.getUserType());
            map.put("用户购买次数", mwUser.getPayCount());
            map.put("下级人数", mwUser.getSpreadCount());
            map.put("用户登陆类型，h5,wechat,routine", mwUser.getLoginType());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 更新用户状态
     * @param uid uid
     * @param status ShopCommonEnum
     */
    @Override
    public void onStatus(Long uid, Integer status) {
        if(ShopCommonEnum.IS_STATUS_1.getValue().equals(status)){
            status = ShopCommonEnum.IS_STATUS_0.getValue();
        }else{
            status = ShopCommonEnum.IS_STATUS_1.getValue();
        }
        mwUserMapper.updateOnstatus(status,uid);
    }

    /**
     * 修改余额
     * @param param UserMoneyDto
     */
    @Override
    public void updateMoney(UserMoneyDto param) {
        MwUser mwUser = this.getById(param.getUid());
        double newMoney = 0d;
        String mark = "";
        if(param.getPtype() == 1){
            mark = "系统增加了"+param.getMoney()+"余额";
            newMoney = NumberUtil.add(mwUser.getNowMoney(),param.getMoney()).doubleValue();
            billService.income(mwUser.getUid(),"系统增加余额", BillDetailEnum.CATEGORY_1.getValue(),
                    BillDetailEnum.TYPE_6.getValue(),param.getMoney(),newMoney, mark,"");
        }else{
            mark = "系统扣除了"+param.getMoney()+"余额";
            newMoney = NumberUtil.sub(mwUser.getNowMoney(),param.getMoney()).doubleValue();
            if(newMoney < 0) {
                newMoney = 0d;
            }
            billService.expend(mwUser.getUid(),0L, "系统减少余额",
                    BillDetailEnum.CATEGORY_1.getValue(),
                    BillDetailEnum.TYPE_7.getValue(),
                    param.getMoney(), newMoney, mark, null, null);
        }
//        MwUser user = new MwUser();
//        user.setUid(mwUser.getUid());
        mwUser.setNowMoney(BigDecimal.valueOf(newMoney));
        saveOrUpdate(mwUser);
    }
    /**
     * 修改积分
     * @param param UserMoneyDto
     */
    @Override
    public void updateIntegral(UserIntegralDto param) {
        MwUser mwUser = this.getById(param.getUid());
        double newIntegral = 0d;
        String mark = "";
        if(param.getPtype() == 1){
            mark = "系统增加了"+param.getIntegral()+"积分";
            newIntegral = NumberUtil.add(mwUser.getIntegral(),param.getIntegral()).doubleValue();
            billService.income(mwUser.getUid(),"系统增加积分", BillDetailEnum.CATEGORY_2.getValue(),
                    BillDetailEnum.TYPE_6.getValue(),param.getIntegral(),newIntegral, mark,"");
        }else{
            mark = "系统扣除了"+param.getIntegral()+"积分";
            newIntegral = NumberUtil.sub(mwUser.getIntegral(),param.getIntegral()).doubleValue();
            if(newIntegral < 0) {
                newIntegral = 0d;
            }
            billService.expend(mwUser.getUid(), 0L,"系统减少积分",
                    BillDetailEnum.CATEGORY_2.getValue(),
                    BillDetailEnum.TYPE_7.getValue(),
                    param.getIntegral(), newIntegral, mark, null, null);
        }
        mwUser.setIntegral(BigDecimal.valueOf(newIntegral));
        saveOrUpdate(mwUser);
    }
    @Override
    public List<MwUser> queryContainsAdditionalNo(List<String> nos){
        LambdaQueryWrapper<MwUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MwUser::getAdditionalNo, nos);
        return mwUserMapper.selectList(wrapper);
    }
    @Override
    public void updateAdditionalNo(Long uid, String goodsAddi) {
        LambdaQueryWrapper<MwUser> wrapperOne = new LambdaQueryWrapper<>();
        wrapperOne
                .ge(MwUser::getAdditionalNo, goodsAddi).last("limit 1");
        MwUser user = mwUserMapper.selectOne(wrapperOne);
        if(user == null) {
            //保存用户追单号
            mwUserMapper.updateAdditionalNo(goodsAddi, uid);
        }
        //如果已经有追单号的用户，不增加新用户的追单号
//        else {
//
//            if(user.getUid() != uid) {
//                user.setAdditionalNo(null);
//                mwUserMapper.updateById(user);
//                mwUserMapper.updateAdditionalNo(goodsAddi, uid);
//            }
//        }

    }

    @Override
    public Long todayCount(){
        LambdaQueryWrapper<MwUser> wrapperOne = new LambdaQueryWrapper<>();
        wrapperOne
                .ge(MwUser::getCreateTime, DateUtil.beginOfDay(new Date()));
        return mwUserMapper.selectCount(wrapperOne);
    }

    @Override
    public Long proCount(){
        LambdaQueryWrapper<MwUser> wrapperTwo = new LambdaQueryWrapper<>();
        wrapperTwo
                .lt(MwUser::getCreateTime, DateUtil.beginOfDay(new Date()))
                .ge(MwUser::getCreateTime, DateUtil.beginOfDay(DateUtil.yesterday()));
        return mwUserMapper.selectCount(wrapperTwo);
    }
    @Override
    public Long monthCount(){
        LambdaQueryWrapper<MwUser> wrapperTwo = new LambdaQueryWrapper<>();
        wrapperTwo
                .ge(MwUser::getCreateTime, DateUtil.beginOfMonth(new Date()));
        return mwUserMapper.selectCount(wrapperTwo);
    }
    /**
     * 开通星选会员一二级返佣
     */
    @Override
    public void gainParentMoney(Long uid, BigDecimal price, String orderId, Date orderCreateTime,
                                String platform, Integer type) {
        //如果分销没开启直接返回
        String open = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_OPEN);
        if(StrUtil.isBlank(open) || ShopCommonEnum.ENABLE_2.getValue().toString().equals(open)) {
            return;
        }
        //uid原用户uid
        MwUser childUser = getById(uid);
        if(childUser == null) {
            return;
        }
        //如果是加盟 直接赠送热度
        if(type == 0) {
            //设置自己的热度值 自己的为赠送
            energyService.addEnergy(uid, uid, platform, 0, 0);
        } else if (type == 2) {
            //月卡增加自己热度
            energyService.addMonthEnergy(uid, uid, platform, 0, 0);
        }

        Long spreadUid = childUser.getSpreadUid();
        if(spreadUid == null || spreadUid == 0) {
            return;
        }
        MwUser userInfo = getById(spreadUid);
        //当前用户不存在 没有上级  直接返回
        if(ObjectUtil.isNull(userInfo)) {
            return;
        }
        //校验用户等级是否>1，大于1才返还
        String storeBrokerageRatioStr = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_RATIO);


        //一级返佣比例未设置直接返回
        if(StrUtil.isBlank(storeBrokerageRatioStr)
                || !NumberUtil.isNumber(storeBrokerageRatioStr)){
            return;
        }

        BigDecimal storeBrokerageRatio = OrderUtil.getRoundFee(NumberUtil.div(storeBrokerageRatioStr,"100"));

        //开通星选会员一级返佣
        BigDecimal feeOne = OrderUtil.getRoundFee(NumberUtil.mul(price, storeBrokerageRatio));
        String mark;
        String shopName = getShopName(platform);
        String shopType = getShopType();

        if(TkUtil.getLevel(platform, userInfo) >= 4) {
            userInfo.setNowMoney(NumberUtil.add(userInfo.getNowMoney(), feeOne));
            updateById(userInfo);
            mark = "金客‘" + TkUtil.getAnonymousName(childUser.getNickname()) + "’加盟" + shopName + shopType + "，奖励您" + feeOne + "元";
            //增加流水
            billService.income(spreadUid, uid, "金客‘" + TkUtil.getAnonymousName(childUser.getNickname()) + "’加盟"+shopName+shopType,
                    BillDetailEnum.CATEGORY_1.getValue(),
                    TYPE_12.getValue(), "up",
                    feeOne.doubleValue(),
                    userInfo.getNowMoney().doubleValue(),
                    mark, orderId, orderCreateTime);
            if(type == 0) {
                //设置上级的拆红包佣金池
                energyService.addEnergy(userInfo.getUid(), uid, platform, 1, 1);
            } else if (type == 2) {
                //月卡增加一级热度
                energyService.addMonthEnergy(userInfo.getUid(), uid, platform, 1, 1);
            }
            //触发刷新每日预估等信息
            RedisUtil.setFeeUid(userInfo.getUid());
        } else {
            //只发送通知告知用户预计奖励
            mark = "金客‘" + TkUtil.getAnonymousName(childUser.getNickname())
                    + "’加盟"+shopName+shopType+"，预计奖励您" + feeOne + "元，由于您未加盟"+shopName+ shopType +"，奖励无效";
            log.info("发给用户uid: {} 消息: {}", userInfo.getUid(), mark);
        }
        //发送通知
        jPushService.push(mark, userInfo.getUid());
        //计算二级返佣
        gainLevelTwoMoney(uid, userInfo, price, orderId, orderCreateTime, platform, childUser, type);
    }

    protected void gainLevelTwoMoney(Long origUid, MwUser userInfo, BigDecimal price,
                                     String orderId, Date orderCreateTime, String platform,
                                     MwUser levelOneUser, Integer type) {
        //计算二级返佣
        Long preUid = userInfo.getSpreadUid();
        if(preUid == null || preUid == 0) {
            return;
        }
        MwUser preUser = getById(preUid);

        //当前用户不存在 直接返回
        if(ObjectUtil.isNull(preUser)) {
            return;
        }

        String storeBrokerageTwoStr = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_TWO);

        //二级返佣比例未设置直接返回
        if(StrUtil.isBlank(storeBrokerageTwoStr)
                || !NumberUtil.isNumber(storeBrokerageTwoStr)){
            return;
        }

        BigDecimal storeBrokerageTwo = OrderUtil.getRoundFee(NumberUtil.div(storeBrokerageTwoStr,"100"));

        //开通星选会员一级返佣
        BigDecimal feeTwo = OrderUtil.getRoundFee(NumberUtil.mul(price, storeBrokerageTwo));
        String mark;
        String shopName = getShopName(platform);
        String shopType = getShopType();

        //校验用户等级是否>4，大于4才返还
        if(TkUtil.getLevel(platform, preUser) > 4) {
            preUser.setNowMoney(NumberUtil.add(preUser.getNowMoney(), feeTwo));
            updateById(preUser);
            mark = "银客‘" + TkUtil.getAnonymousName(levelOneUser.getNickname()) + "’加盟"+shopName+shopType+"，奖励您" + feeTwo + "元";
            //增加流水
            billService.income(preUid, origUid, "银客‘" + TkUtil.getAnonymousName(levelOneUser.getNickname()) + "’加盟"+shopName+shopType,
                    BillDetailEnum.CATEGORY_1.getValue(),
                    TYPE_12.getValue(), "up",
                    feeTwo.doubleValue(),
                    preUser.getNowMoney().doubleValue(),
                    mark, orderId, orderCreateTime);
            if(type == 0) {
                //设置上上级的拆红包佣金池
                energyService.addEnergy(preUid, origUid, platform, 2, 1);
            } else if (type == 2) {
                //月卡增加二级热度
                energyService.addMonthEnergy(preUid, origUid, platform, 2, 1);
            }
            //触发刷新每日预估等信息
            RedisUtil.setFeeUid(preUser.getUid());
        } else {
            //只发送通知告知用户预计奖励
            mark = "银客‘" + TkUtil.getAnonymousName(levelOneUser.getNickname()) + "’加盟"+shopName+shopType+"，预计奖励您" + feeTwo +
                    "元，由于您未加盟"+shopName+shopType + "，奖励无效";
            log.info("发给用户uid: {} 消息: {}", preUser.getUid(), mark);
        }
        //发送通知
       jPushService.push(mark, preUser.getUid());
    }

    protected String getShopType() {
        return "会员";
    }

    protected String getShopName(String platform) {
        if("tsq".equals(PAY_NAME)) {
            switch (platform){
                case "jd":
                    return "京店";
                case "pdd":
                    return "多店";
                case "dy":
                    return "抖店";
                case "vip":
                    return "唯店";
                default:
                    return "淘店";
            }
        }
        switch (platform){
            case "jd":
                return "京星选";
            case "pdd":
                return "多星选";
            case "dy":
                return "抖星选";
            case "vip":
                return "唯星选";
            default:
                return "淘星选";
        }
    }

    @SneakyThrows
    @Override
    public UserFeeQueryVo userFeeInfo(MwUser mwUser) {
        Long uid = mwUser.getUid();
        //今日
        //cid "佣金类型 1=总览 2=自购 4=金客 5=银客 6=已结算 7=热度订单")
        //type 1=今日 2=昨日 3=本月 4=上月 5=近30日
        MwUserFeeLogOpt feeLogOpt = feeLogOptService.getById(uid);
        if(feeLogOpt == null) {
            feeLogOpt = FeeUtil.initFeeLogOpt(uid);
        }
        List<MwUserFeeLog> feeLogs = JSON.parseArray(feeLogOpt.getTt(), MwUserFeeLog.class).stream().filter(feeLog -> feeLog.getCid().equals(1)).collect(toList());
        BigDecimal minToday = BigDecimal.ZERO;
        BigDecimal maxToday = BigDecimal.ZERO;
        for(MwUserFeeLog feeLog : feeLogs) {
            String platform = feeLog.getPlatform();
            if("up".equals(platform)) {
                minToday = NumberUtil.add(minToday, feeLog.getFeeValue());
                maxToday = NumberUtil.add(maxToday, feeLog.getFeeValue());
            } else {
                String feeStr = feeLog.getFee().replace("￥", "");
                String[] split = feeStr.split("-");
                minToday = NumberUtil.add(minToday, Double.parseDouble(split[0]));
                if(split.length >1) {
                    maxToday = NumberUtil.add(maxToday, Double.parseDouble(split[1]));
                }
            }
        }
        //本月
        List<MwUserFeeLog> feeMonthLogs = JSON.parseArray(feeLogOpt.getTm(), MwUserFeeLog.class).stream().filter(feeLog -> feeLog.getCid().equals(1)).collect(toList());
        BigDecimal minMonth = BigDecimal.ZERO;
        BigDecimal maxMonth = BigDecimal.ZERO;
        for(MwUserFeeLog feeLog : feeMonthLogs) {
            String platform = feeLog.getPlatform();
            if("up".equals(platform)) {
                minMonth = NumberUtil.add(minMonth, feeLog.getFeeValue());
                maxMonth = NumberUtil.add(maxMonth, feeLog.getFeeValue());
            } else {
                String feeStr = feeLog.getFee().replace("￥", "");
                String[] split = feeStr.split("-");
                minMonth = NumberUtil.add(minMonth, Double.parseDouble(split[0]));
                if(split.length >1) {
                    maxMonth = NumberUtil.add(maxMonth, Double.parseDouble(split[1]));
                }

            }
        }
        return UserFeeQueryVo.builder()
                .minToday(minToday.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .maxToday(maxToday.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .minMonth(minMonth.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .maxMonth(maxMonth.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .uid(uid).build();
    }


    @Override
    public void initFeeLogs(Integer type, List<Long> uidList) {
        List<Long> newList = new ArrayList<>();

        newList.addAll(uidList);

        //type 1=今日 3=本月 4=上月 6=7天
        DateTime start = DateUtils.getDateTimeStart(type);
        DateTime end = DateUtils.getDateTimeEnd(type);

        Double min = Double.parseDouble(systemConfigService.getData(SystemConfigConstants.TK_HB_MIN_TIMES));
        Double max = Double.parseDouble(systemConfigService.getData(SystemConfigConstants.TK_HB_MAX_TIMES));

        List<MwSystemUserLevel> systemUserLevels = systemUserLevelService.list();
        Map<String, MwSystemUserLevel> levelMap = new HashMap<>();
        systemUserLevels.forEach(mwSystemUserLevel -> {
            Integer level = mwSystemUserLevel.getGrade();
            if(level == 5) {
                levelMap.put(mwSystemUserLevel.getType() + "_" + "vip", mwSystemUserLevel);
            } else {
                levelMap.put(mwSystemUserLevel.getType(), mwSystemUserLevel);
            }
        });

        String open = systemConfigService.getData(SystemConfigConstants.STORE_MULTI_VIP_OPEN);
        boolean multiVipOpen = !(StrUtil.isBlank(open) || ShopCommonEnum.ENABLE_2.getValue().toString().equals(open));

        newList = newList.stream().distinct().collect(toList());
        Map<Long, MwUser> userMap = listByIds(newList).stream().collect(Collectors.toMap(MwUser::getUid, Function.identity()));

        Map<Long, MwUserFeeLogOpt> logOptMap = feeLogOptService.listByIds(newList).stream()
                .collect(Collectors.toMap(MwUserFeeLogOpt::getUid, Function.identity()));

        //生成每个用户自购佣金和热度佣金
        List<MwUserFeeLogOpt> feeLogOpts = new ArrayList<>();

        for(Long uid : newList) {
            //"cid佣金类型 1=总览 2=自购 4=金客 5=银客 6=已结算 7=热度订单")
            //取得今日自己订单的预估收益
            List<Long> queryUid = Collections.singletonList(uid);
            MwUser user = userMap.get(uid);
            if(user == null) {
                continue;
            }
            //获取当前保存的预估数据
            MwUserFeeLogOpt feeLogOpt = logOptMap.get(uid);
            if(feeLogOpt == null) {
                feeLogOpt = FeeUtil.initFeeLogOpt(uid);
            }
            feeLogOpts.add(feeLogOpt);
            Map<String, MwUserFeeLog> optLogMap = new HashMap<>();


            MwCommissionInfoQueryVo comInfo = getCommissionInfo(user);
            //生成淘宝预估
            //自购淘宝
            //先计算普通用户的佣金，如果是会员乘以倍数
            Double tbTimes = FeeUtil.getPlatformTimes(user, PlatformEnum.TB, comInfo);

            Long selfTbCount = tbOrderService.totalCount(type, queryUid, 0);
            Double selfTbFee = 0.0;
            //这里是不管是不是0都需要去更新，今日0 代表就是0
            if(selfTbCount != 0) {
                selfTbFee = tbOrderService.totalFee(type, queryUid, 0);
                //淘宝需要乘以佣金比例90% 如果是会员乘以倍数
                Double bigDecimal = NumberUtil.mul(selfTbFee, tbTimes);
                selfTbFee = NumberUtil.mul(bigDecimal, DEFAULT_FEE_SCALE);
            }

            //佣金乘以基础用户的比例，如果是会员>=4 乘以相应的倍数得到预估
            MwUserFeeLog selfTbLog = FeeUtil.createGeneralFeeLogs(uid, type,
                    min, max, selfTbFee, selfTbCount,
                    PlatformEnum.TB.getValue(), PlatformEnum.TB.getDesc(), 2);
            optLogMap.put(selfTbLog.getId(), selfTbLog);

            //获取用户的当前等级，并取得一二级订单返佣比例
            Map<String, MwSystemUserLevel> systemUserLevelMap = FeeUtil.initUserLevelMap(systemUserLevels, user, multiVipOpen);

            //淘宝热度订单
            Long energyTbCount = tbOrderService.totalCount(type, queryUid, 2);
            Double energyTbFee = 0.0;
            if(energyTbCount != 0) {
                energyTbFee = tbOrderService.totalFee(type, queryUid, 2);
                //热度订单需要计算1级返佣
                BigDecimal tbScale = FeeUtil.getScale(systemUserLevelMap, PlatformEnum.TB.getValue(), 1);
                energyTbFee = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(energyTbFee, tbScale),100)).doubleValue();

            }
            MwUserFeeLog energyTbLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, energyTbFee, energyTbCount,
                     PlatformEnum.TB.getValue(), PlatformEnum.TB.getDesc(), 7);
            optLogMap.put(energyTbLog.getId(), energyTbLog);

            //保存总览淘宝
            Double tbFee = selfTbFee + energyTbFee;
            Long tbCount = selfTbCount + energyTbCount;

            //生成京东预估
            Double jdTimes = FeeUtil.getPlatformTimes(user, PlatformEnum.JD, comInfo);

            //京东自购预估
            Long selfJdCount = jdOrderService.totalCount(type, queryUid, 0);
            Double selfJdFee = 0.0;
            if(selfJdCount != 0) {
                selfJdFee = jdOrderService.totalFee(type, queryUid, 0);
                //京东如果是会员乘以倍数
                Double bigDecimal = NumberUtil.mul(selfJdFee, jdTimes);
                selfJdFee = NumberUtil.mul(bigDecimal, DEFAULT_FEE_SCALE);

            }
            MwUserFeeLog selfJdLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, selfJdFee, selfJdCount,
                    PlatformEnum.JD.getValue(), PlatformEnum.JD.getDesc(), 2);
            optLogMap.put(selfJdLog.getId(), selfJdLog);

            //京东热度订单
            Long energyJdCount = jdOrderService.totalCount(type, queryUid, 2);
            Double energyJdFee = 0.0;
            if(energyJdCount != 0) {
                energyJdFee = jdOrderService.totalFee(type, queryUid, 2);//热度订单需要计算1级返佣
                BigDecimal jdScale = FeeUtil.getScale(systemUserLevelMap, PlatformEnum.JD.getValue(), 1);
                energyJdFee = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(energyJdFee, jdScale),100)).doubleValue();
            }
            MwUserFeeLog energyJdLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, energyJdFee, energyJdCount,
                    PlatformEnum.JD.getValue(), PlatformEnum.JD.getDesc(), 7);
            optLogMap.put(energyJdLog.getId(), energyJdLog);
            //京东总览
            Double jdFee = selfJdFee + energyJdFee;
            Long jdCount = selfJdCount + energyJdCount;

            //生成拼多多预估
            Double pddTimes = FeeUtil.getPlatformTimes(user, PlatformEnum.PDD, comInfo);

            //拼多多自购预估
            Long selfPddCount = pddOrderService.totalCount(type, queryUid, 0);
            Double selfPddFee = 0.0;
            if(selfPddCount != 0) {
                selfPddFee = pddOrderService.totalFee(type, queryUid, 0);

                Double bigDecimal = NumberUtil.mul(selfPddFee, pddTimes);
                selfPddFee = NumberUtil.mul(bigDecimal, DEFAULT_FEE_SCALE);

            }
            MwUserFeeLog selfPddLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, selfPddFee, selfPddCount,
                    PlatformEnum.PDD.getValue(), PlatformEnum.PDD.getDesc(), 2);
            optLogMap.put(selfPddLog.getId(), selfPddLog);

            //拼多多热度订单
            Long energyPddCount = pddOrderService.totalCount(type, queryUid, 2);
            Double energyPddFee = 0.0;
            if(energyPddCount != 0) {
                energyPddFee = pddOrderService.totalFee(type, queryUid, 2);
                //需要计算1级返佣
                BigDecimal pddScale = FeeUtil.getScale(systemUserLevelMap, PlatformEnum.PDD.getValue(), 1);
                energyPddFee = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(energyPddFee, pddScale),100)).doubleValue();

            }
            MwUserFeeLog energyPddLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, energyPddFee, energyPddCount,
                    PlatformEnum.PDD.getValue(), PlatformEnum.PDD.getDesc(), 7);
            optLogMap.put(energyPddLog.getId(), energyPddLog);

            //拼多多总览预估
            Double pddFee = selfPddFee + energyPddFee;
            Long pddCount = selfPddCount + energyPddCount;


            //抖音预估
            Double dyTimes = FeeUtil.getPlatformTimes(user, PlatformEnum.DY, comInfo);

            //抖音自购预估
            Long selfDyCount = dyOrderService.totalCount(type, queryUid, 0);
            Double selfDyFee = 0.0;
            if(selfDyCount != 0) {
                selfDyFee = dyOrderService.totalFee(type, queryUid, 0);

                Double bigDecimal = NumberUtil.mul(selfDyFee, dyTimes);
                selfDyFee = NumberUtil.mul(bigDecimal, DEFAULT_FEE_SCALE);

            }
            MwUserFeeLog selfDyLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, selfDyFee, selfDyCount,
                    PlatformEnum.DY.getValue(), PlatformEnum.DY.getDesc(), 2);
            optLogMap.put(selfDyLog.getId(), selfDyLog);

            //抖音热度订单
            Long energyDyCount = dyOrderService.totalCount(type, queryUid, 2);
            Double energyDyFee = 0.0;
            if(energyDyCount != 0) {
                energyDyFee = dyOrderService.totalFee(type, queryUid, 2);//热度订单需要计算1级返佣
                BigDecimal dyScale = FeeUtil.getScale(systemUserLevelMap, PlatformEnum.DY.getValue(), 1);
                energyDyFee = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(energyDyFee, dyScale),100)).doubleValue();

            }
            MwUserFeeLog energyDyLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, energyDyFee, energyDyCount,
                    PlatformEnum.DY.getValue(), PlatformEnum.DY.getDesc(), 7);
            optLogMap.put(energyDyLog.getId(), energyDyLog);

            //抖音总览
            Double dyFee = selfDyFee + energyDyFee;
            Long dyCount = selfDyCount + energyDyCount;

            //生成唯品会预估
            Double vipTimes = FeeUtil.getPlatformTimes(user, PlatformEnum.VIP, comInfo);

            //唯品会自购预估
            Long selfVipCount = vipOrderService.totalCount(type, queryUid, 0);
            Double selfVipFee = 0.0;
            if(selfVipCount != 0) {
                selfVipFee = vipOrderService.totalFee(type, queryUid, 0);

                Double bigDecimal = NumberUtil.mul(selfVipFee, vipTimes);
                selfVipFee = NumberUtil.mul(bigDecimal, DEFAULT_FEE_SCALE);

            }
            MwUserFeeLog selfVipLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, selfVipFee, selfVipCount,
                    PlatformEnum.VIP.getValue(), PlatformEnum.VIP.getDesc(), 2);
            optLogMap.put(selfVipLog.getId(), selfVipLog);

            //唯品会热度订单
            Long energyVipCount = vipOrderService.totalCount(type, queryUid, 2);
            Double energyVipFee = 0.0;
            if(energyVipCount != 0) {
                energyVipFee = vipOrderService.totalFee(type, queryUid, 2);//热度订单需要计算1级返佣
                BigDecimal vipScale = FeeUtil.getScale(systemUserLevelMap, PlatformEnum.VIP.getValue(), 1);
                energyVipFee = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(energyVipFee, vipScale),100)).doubleValue();
            }
            MwUserFeeLog energyVipLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, energyVipFee, energyVipCount,
                    PlatformEnum.VIP.getValue(), PlatformEnum.VIP.getDesc(), 7);
            optLogMap.put(energyVipLog.getId(), energyVipLog);

            //唯品会总览预估
            Double vipFee = selfVipFee + energyVipFee;
            Long vipCount = selfVipCount + energyVipCount;




            //生成美团预估 美团以淘宝为准
            Double mtTimes = FeeUtil.getPlatformTimes(user, PlatformEnum.TB, comInfo);

            //美团自购预估
            Long selfMtCount = mtOrderService.totalCount(type, queryUid, 0);
            Double selfMtFee = 0.0;
            if(selfMtCount != 0) {
                selfMtFee = mtOrderService.totalFee(type, queryUid, 0);

                Double bigDecimal = NumberUtil.mul(selfMtFee, mtTimes);
                selfMtFee = NumberUtil.mul(bigDecimal, DEFAULT_FEE_SCALE);

            }
            MwUserFeeLog selfMtLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, selfMtFee, selfMtCount,
                    PlatformEnum.MT.getValue(), PlatformEnum.MT.getDesc(), 2);
            optLogMap.put(selfMtLog.getId(), selfMtLog);

            //美团总览预估
            Double mtFee = selfMtFee;
            Long mtCount = selfMtCount;



            //生成开通星选会员预估
            List<Long> spreadOneList = getSpreadIdList(uid, 0);
            List<Long> spreadTwoList = null;
            //获取下级用户，下级用户不为空
            if(!spreadOneList.isEmpty()) {
                    spreadTwoList = getSpreadIdList(uid, 1);
            }
            initFansUpFee(spreadOneList, spreadTwoList, uid, type, start, end, optLogMap);

            //生成用户预估
            //获取下级今日收益乘以百分比，只有自购订单，算在用户收益里
            //加上下下级今日收益乘以百分比
            if(!spreadOneList.isEmpty()) {
                //需要计算当前会员等级
                //计算得到所有下级用户的预估，然后拆开-，乘以下级分佣，每个用户相加就是最终预估
                Integer oneCid = 2;
                List<MwUserFeeLogOpt> oneOpts = feeLogOptService.listByIds(spreadOneList);
                List<MwUserFeeLog> oneLogs = FeeUtil.getLogs(type, oneCid, oneOpts);
                //每个平台需要区分计算 然后加到总览得对应平台
                for(PlatformEnum platformEnum: PlatformEnum.values()) {
                    //得到一级用户当前平台预估记录
                    List<MwUserFeeLog> platformLogs = oneLogs.stream().filter(feeLog ->
                            feeLog.getPlatform().equals(platformEnum.getValue())).collect(toList());

                    Long platformCount = 0L;
                    BigDecimal platformFee = BigDecimal.ZERO;

                    BigDecimal platformMinFee = BigDecimal.ZERO;
                    BigDecimal platformMaxFee = BigDecimal.ZERO;
                    //所有1级用户佣金相加
                    for(MwUserFeeLog feeLog : platformLogs) {
                        if(feeLog.getCount() > 0) {
                            //订单相加
                            platformCount += feeLog.getCount();

                            //拆分预估
                            String feeStr = feeLog.getFee();
                            String[] splitFee = feeStr.replace("￥", "").split("-");
                            platformMinFee = NumberUtil.add(platformMinFee, Double.parseDouble(splitFee[0]));
                            if(splitFee.length > 1) {
                                platformMaxFee = NumberUtil.add(platformMaxFee, Double.parseDouble(splitFee[1]));
                            }
                            platformFee = NumberUtil.add(feeLog.getFeeValue(), platformFee);
                        }

                    }
                    //计算当前平台 如淘宝总预估
                    if(platformCount > 0) {
                        //得到1级佣金比例
                        BigDecimal scale = FeeUtil.getScale(systemUserLevelMap, platformEnum.getValue(), 1);
                        //计算得到用户当前预估
                        platformMinFee = NumberUtil.div(NumberUtil.mul(platformMinFee, scale), 100);
                        platformMaxFee = NumberUtil.div(NumberUtil.mul(platformMaxFee, scale), 100);
                        platformFee = NumberUtil.div(NumberUtil.mul(platformFee, scale), 100);
                        //总览数据加上用户得预估
                        if(platformEnum == PlatformEnum.TB) {
                            tbFee += platformFee.doubleValue();
                            tbCount += platformCount;
                        } else if(platformEnum == PlatformEnum.JD) {
                            jdFee += platformFee.doubleValue();
                            jdCount += platformCount;
                        } else if(platformEnum == PlatformEnum.PDD) {
                            pddFee += platformFee.doubleValue();
                            pddCount += platformCount;
                        } else if(platformEnum == PlatformEnum.DY) {
                            dyFee += platformFee.doubleValue();
                            dyCount += platformCount;
                        } else if(platformEnum == PlatformEnum.VIP) {
                            vipFee += platformFee.doubleValue();
                            vipCount += platformCount;
                        } else if(platformEnum == PlatformEnum.VIP) {
                            mtFee += platformFee.doubleValue();
                            mtCount += platformCount;
                        }
                    }

                    MwUserFeeLog fansOneOrderLog = FeeUtil.createFansFeeLogs(uid, type, platformMinFee, platformMaxFee,
                            platformFee.doubleValue(), platformCount.longValue(),
                            platformEnum.getValue(), platformEnum.getDesc(), 4);
                    optLogMap.put(fansOneOrderLog.getId(), fansOneOrderLog);
                }
                //获取银客
                if(CollectionUtils.isNotEmpty(spreadTwoList)) {
                    //计算得到二级用户当前平台佣金
                    List<MwUserFeeLogOpt> twoOpts = feeLogOptService.listByIds(spreadTwoList);
                    List<MwUserFeeLog> twoLogs = FeeUtil.getLogs(type, oneCid, twoOpts);

                    for(PlatformEnum platformEnum: PlatformEnum.values()) {
                        //计算得到二级用户当前平台佣金
                        List<MwUserFeeLog> platformLogs = twoLogs.stream().filter(feeLog ->
                                feeLog.getPlatform().equals(platformEnum.getValue())).collect(toList());

                        Long platformCount = 0L;
                        BigDecimal platformFee = BigDecimal.ZERO;

                        BigDecimal platformMinFee = BigDecimal.ZERO;
                        BigDecimal platformMaxFee = BigDecimal.ZERO;
                        //所有2级用户佣金相加
                        for(MwUserFeeLog feeLog : platformLogs) {
                            if(feeLog.getCount() > 0) {
                                platformCount += feeLog.getCount();
                                String feeStr = feeLog.getFee();
                                String[] splitFee = feeStr.replace("￥", "").split("-");
                                platformMinFee = NumberUtil.add(platformMinFee, Double.parseDouble(splitFee[0]));
                                if(splitFee.length > 1) {
                                    platformMaxFee = NumberUtil.add(platformMaxFee, Double.parseDouble(splitFee[1]));
                                }
                                platformFee = NumberUtil.add(feeLog.getFeeValue(), platformFee);
                            }
                        }
                        if(platformCount > 0) {
                            BigDecimal scale = FeeUtil.getScale(systemUserLevelMap, platformEnum.getValue(), 2);
                            //计算得到用户当前平台佣金
                            platformMinFee = NumberUtil.div(NumberUtil.mul(platformMinFee, scale), 100);
                            platformMaxFee = NumberUtil.div(NumberUtil.mul(platformMaxFee, scale), 100);
                            platformFee = NumberUtil.div(NumberUtil.mul(platformFee, scale), 100);
                            if(platformEnum == PlatformEnum.TB) {
                                tbFee += platformFee.doubleValue();
                                tbCount += platformCount;
                            } else if(platformEnum == PlatformEnum.JD) {
                                jdFee += platformFee.doubleValue();
                                jdCount += platformCount;
                            } else if(platformEnum == PlatformEnum.PDD) {
                                pddFee += platformFee.doubleValue();
                                pddCount += platformCount;
                            } else if(platformEnum == PlatformEnum.DY) {
                                dyFee += platformFee.doubleValue();
                                dyCount += platformCount;
                            } else if(platformEnum == PlatformEnum.VIP) {
                                vipFee += platformFee.doubleValue();
                                vipCount += platformCount;
                            } else if(platformEnum == PlatformEnum.MT) {
                                mtFee += platformFee.doubleValue();
                                mtCount += platformCount;
                            }
                        }

                        MwUserFeeLog fansTwoOrderLog = FeeUtil.createFansFeeLogs(uid, type, platformMinFee, platformMaxFee,
                                platformFee.doubleValue(), platformCount.longValue(),
                                platformEnum.getValue(), platformEnum.getDesc(), 5);
                        optLogMap.put(fansTwoOrderLog.getId(), fansTwoOrderLog);
                    }
                }
            }
            //初始化总览
            FeeUtil.initPreviewFee(uid, type, min, max,
                    tbFee, tbCount, jdFee, jdCount, pddFee, pddCount,
                    dyFee, dyCount, vipFee, vipCount, mtFee, mtCount,
                    optLogMap);

            //生成已结算 收益
            initSettledFee(uid, type, start, end, optLogMap);

            //根据类型设置预估数据
            FeeUtil.setFeeOptData(feeLogOpt, type, optLogMap);

        }

        //保存
        //这里会出现死锁Deadlock found when trying to get lock; try restarting transaction，暂未找到解决方案，try catch不中断定时任务
        try{
            feeLogOptService.mysqlInsertOrUpdateBath(feeLogOpts);
        } catch (Exception e) {
            log.error("批量保存预估报错: {}", e);
        }

    }

    protected void initFansUpFee(List<Long> spreadOneList, List<Long> spreadTwoList,
                                 Long uid, Integer type, DateTime start, DateTime end,
                                 Map<String, MwUserFeeLog> optLogMap) {
        Double fansUpFee = 0.0;
        Long fansUpCount = 0L;
        //获取下级用户，下级用户不为空
        if(!spreadOneList.isEmpty()) {
            Long fansOneUpCount = feeOrderService.getFansUpgradeCount(uid, spreadOneList, start, end);
            Double fansOneUpFee = 0.0;
            if(fansOneUpCount != 0) {
                fansOneUpFee = feeOrderService.getFansUpgradeFee(uid, spreadOneList, start, end);
                fansUpFee += fansOneUpFee;
                fansUpCount += fansOneUpCount;
            }
            MwUserFeeLog fansOneUpLog = FeeUtil.createFixedFeeLogs(uid, type, fansOneUpFee, fansOneUpCount,
                    "up", "加盟星选", 4);
            optLogMap.put(fansOneUpLog.getId(), fansOneUpLog);

            if(!spreadTwoList.isEmpty()) {
                Long fansTwoUpCount = feeOrderService.getFansUpgradeCount(uid, spreadTwoList, start, end);
                Double fansTwoUpFee = 0.0;
                if(fansTwoUpCount != 0) {
                    fansTwoUpFee = feeOrderService.getFansUpgradeFee(uid, spreadTwoList, start, end);
                    fansUpFee += fansTwoUpFee;
                    fansUpCount += fansTwoUpCount;
                }
                MwUserFeeLog fansTwoUpLog = FeeUtil.createFixedFeeLogs(uid, type, fansTwoUpFee, fansTwoUpCount,
                        "up", "加盟星选", 5);
                optLogMap.put(fansTwoUpLog.getId(), fansTwoUpLog);
            }
        }
        MwUserFeeLog fansAllUpLog = FeeUtil.createFixedFeeLogs(uid, type, fansUpFee, fansUpCount,
                "up", "加盟星选", 1);
        optLogMap.put(fansAllUpLog.getId(), fansAllUpLog);
    }
    protected void initSettledFee(Long uid, Integer type, DateTime start, DateTime end, Map<String, MwUserFeeLog> optLogMap) {
        //生成已结算 收益
        for(PlatformEnum platformEnum: PlatformEnum.values()) {
            Long settledCount = feeOrderService.getSettledCount(uid, platformEnum.getValue(), start, end);
            Double settledFee = 0d;
            if(settledCount != 0) {
                settledFee = feeOrderService.getSettledFee(uid, platformEnum.getValue(), start, end);
            }
            MwUserFeeLog cashLog = FeeUtil.createFixedFeeLogs(uid, type, settledFee, settledCount,
                    platformEnum.getValue(), platformEnum.getDesc(), 6);
            optLogMap.put(cashLog.getId(), cashLog);
        }

        String upPlatform = "up";
        Long upSettledCount = feeOrderService.getSettledCount(uid, upPlatform, start, end);
        Double upSettledFee = 0d;
        if(upSettledCount != 0) {
            upSettledFee = feeOrderService.getSettledFee(uid, upPlatform, start, end);
        }
        MwUserFeeLog upSettledLog = FeeUtil.createFixedFeeLogs(uid, type, upSettledFee, upSettledCount, upPlatform, "用户", 6);
        optLogMap.put(upSettledLog.getId(), upSettledLog);

    }

    public List<MwUser> getSpreadList(Long uid, Integer grade) {
        List<MwUser> userList = mwUserMapper.selectList(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getSpreadUid, uid));
        if (ShopCommonEnum.GRADE_0.getValue().equals(grade)) {//-级
            return userList;
        } else {//二级
            List<Long> userIds = userList.stream()
                    .map(MwUser::getUid)
                    .collect(toList());
            if (userIds.isEmpty()) {
                return Collections.emptyList();
            }
            return mwUserMapper.selectList(Wrappers.<MwUser>lambdaQuery()
                    .in(MwUser::getSpreadUid, userIds));
        }
    }

    public List<Long> getSpreadIdList(Long uid, Integer grade) {
        List<MwUser> userList = getSpreadList(uid, grade);
        return userList.stream()
                .map(MwUser::getUid)
                .collect(toList());
    }

    public List<PromUserDto> selectIds(Wrapper<MwUser> wrapper){
        return mwUserMapper.selectIds(wrapper);
    }

    @Override
    public List<UserRefundDto> getUserRefunds(List<Long> uidList){
        return mwUserMapper.getUserRefunds(uidList);
    }

    @Override
    public List<MwUser> getMemberExpiredList(Date now) {
        return list(new LambdaQueryWrapper<MwUser>()
                .ne(MwUser::getLevel, EXPIRED_LEVEL).le(MwUser::getExpired, now)
                .or()
                .ne(MwUser::getLevelJd, EXPIRED_LEVEL).le(MwUser::getExpiredJd, now)
                .or()
                .ne(MwUser::getLevelPdd, EXPIRED_LEVEL).le(MwUser::getExpiredPdd, now)
                .or()
                .ne(MwUser::getLevelDy, EXPIRED_LEVEL).le(MwUser::getExpiredDy, now)
                .or()
                .ne(MwUser::getLevelVip, EXPIRED_LEVEL).le(MwUser::getExpiredVip, now));
    }

    @Override
    public void expiredUser() {
        Date now = new Date();
        //计算赠送热度
        //赠送订单根据之前的记录数量生成订单 同时当天没有生成
        //找到所有热度值大于等于配置的用户
        List<MwUser> users = getMemberExpiredList(now);
        //如果没有用户热度值大于配置 返回
        if(CollectionUtils.isEmpty(users)) {
            return;
        }

        //level=99代表开启过 不允许再次开启
        for(MwUser user : users) {
            if(user.getExpired() != null && user.getExpired().compareTo(now) <= 0 ) {
                user.setLevel(EXPIRED_LEVEL);
            }
            if(user.getExpiredJd() != null && user.getExpiredJd().compareTo(now) <= 0 ) {
                user.setLevelJd(EXPIRED_LEVEL);
            }
            if(user.getExpiredPdd() != null && user.getExpiredPdd().compareTo(now) <= 0 ) {
                user.setLevelPdd(EXPIRED_LEVEL);
            }
            if(user.getExpiredDy() != null && user.getExpiredDy().compareTo(now) <= 0 ) {
                user.setLevelDy(EXPIRED_LEVEL);
            }
            if(user.getExpiredVip() != null && user.getExpiredVip().compareTo(now) <= 0 ) {
                user.setLevelVip(EXPIRED_LEVEL);
            }
        }
        saveOrUpdateBatch(users);
    }
}
