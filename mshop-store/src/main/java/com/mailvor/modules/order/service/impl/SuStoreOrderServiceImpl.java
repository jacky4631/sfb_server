/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.order.service.dto.UserRefundDto;
import com.mailvor.modules.order.service.mapper.StoreOrderMapper;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.push.service.JPushService;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.config.TbConfig;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.tools.utils.CashUtils;
import com.mailvor.modules.user.config.AppDataConfig;
import com.mailvor.modules.user.config.HbUnlockConfig;
import com.mailvor.modules.user.domain.*;
import com.mailvor.modules.user.service.*;
import com.mailvor.modules.utils.TkOrderFee;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mailvor.enums.ShopCommonEnum.IS_ORDER_STATUS_1;
import static com.mailvor.utils.OrderUtil.*;


/**
 * @author huangyu
 * @date 2020-05-12
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class SuStoreOrderServiceImpl extends BaseServiceImpl<StoreOrderMapper, MwStoreOrder> implements SuStoreOrderService {

    @Autowired
    private MwUserBillService billService;
    @Autowired
    private MwUserService userService;
    @Autowired
    private MwStoreProductService productService;
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
    private MwSystemUserLevelService systemUserLevelService;

    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private MwUserPoolService poolService;

    @Resource
    private TbConfig tbConfig;

    @Resource
    private MwUserUnionService userUnionService;

    /**
     * 返回订单确认数据
     *
     * @param mwUser  mwUser
     * @param goodsId 购物车id
     * @return ConfirmOrderVO
     */
    @Override
    public boolean confirmIntegral(MwUser mwUser, Long goodsId) {
        MwStoreProductQueryVo productQueryVo = productService.getStoreProductById(goodsId);
        if (mwUser.getIntegral().compareTo(productQueryVo.getPrice()) < 0) {
            throw new MshopException("积分不足");
        }
        return true;

    }

    /**
     * 淘客下单奖励积分
     *
     */
//    public void gainUserIntegral(Long uid, String orderId, Date orderCreateTime, BigDecimal gainIntegral, PlatformEnum platformEnum) {
//
//        if (gainIntegral.compareTo(BigDecimal.ZERO) > 0) {
//            MwUser user = userService.getById(uid);
//            if(user == null) {
//                return;
//            }
//            BigDecimal curIntegral = user.getIntegral();
//            if(curIntegral == null) {
//                curIntegral = BigDecimal.ZERO;
//            }
//            BigDecimal newIntegral = NumberUtil.add(curIntegral, gainIntegral);
//            user.setIntegral(newIntegral);
//            userService.updateById(user);
//
//            //增加流水
//            billService.income(user.getUid(), user.getUid(),
//                    "购买" + platformEnum.getDesc() +"商品赠送积分", BillDetailEnum.CATEGORY_2.getValue(),
//                    BillDetailEnum.TYPE_11.getValue(), platformEnum.getValue(),
//                    gainIntegral.doubleValue(),
//                    newIntegral.doubleValue(),
//                    "购买" + platformEnum.getDesc() + "商品" + orderId + "赠送" + gainIntegral + "积分", orderId, orderCreateTime);
//            //发送通知
//            jPushService.push("您有" +platformEnum.getDesc()+ "新订单啦", uid);
//
//        }
//    }
    /**
     * 订单失效扣除积分
     *
     */
    public boolean decUserHb(Long uid, String orderId, BigDecimal hb, PlatformEnum platformEnum, Date orderCreateTime) {
        if (hb.compareTo(BigDecimal.ZERO) > 0) {
            MwUser user = userService.getById(uid);
            if(user == null) {
                return false;
            }

            //增加用户退款次数
            //如果用户余额不够扣除 会无限增加用户退款次数 后期人工处理
            poolService.addRefund(uid);

            BigDecimal newHb = NumberUtil.sub(user.getNowMoney(), hb);
            String replenishMsg = "";
            if(newHb.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
            user.setNowMoney(newHb);
            userService.updateById(user);

            //增加流水
            billService.expend(user.getUid(), user.getUid(), platformEnum.getDesc() +"订单退款扣除红包", BillDetailEnum.CATEGORY_1.getValue(),
                    BillDetailEnum.TYPE_8.getValue(),
                    hb.doubleValue(),
                    newHb.doubleValue(),
                    "退款" + platformEnum.getDesc() + "商品" + orderId + "扣除" + hb + "元" + replenishMsg, orderId, orderCreateTime);
            return true;
        }
        return false;
    }

    /**
     * 扣除上级红包
     *
     * @param childUser the child user
     * @param orderId   the order id
     * @param hb        the hb
     */
    public void decParentMoney(MwUser childUser,  BigDecimal hb, String orderId, Date orderCreateTime, PlatformEnum platformEnum) {
        //找到父用户 计算积分 记录
        Long spreadUid = childUser.getSpreadUid();
        if(spreadUid == null || spreadUid == 0) {
            return;
        }
        MwUser userInfo = userService.getById(spreadUid);
        //当前用户不存在 没有上级  直接返回
        if(ObjectUtil.isNull(userInfo)) {
            return;
        }

        //根据用户当前等级获取会员详情
        MwSystemUserLevel systemUserLevel = systemUserLevelService.getUserLevel(userInfo, platformEnum.getValue());
        if(systemUserLevel == null) {
            return;
        }

        //扣除一级返佣
        BigDecimal feeOne = getFeeOne(systemUserLevel, hb);
        BigDecimal newMoney = NumberUtil.sub(userInfo.getNowMoney(), feeOne);
        //当上级积分不够扣的时候 扣到0，负数会报错
        if(newMoney.compareTo(BigDecimal.ZERO) < 0) {
            newMoney = BigDecimal.ZERO;
        }
        userInfo.setNowMoney(newMoney);
        userService.updateById(userInfo);

        //增加流水
        billService.expend(spreadUid,childUser.getUid(),  "顾客‘" + childUser.getNickname() + "’订单退款扣除红包",
                BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_8.getValue(),
                feeOne.doubleValue(),
                userInfo.getNowMoney().doubleValue(),
                "顾客‘" + childUser.getNickname() + "’订单退款扣除红包" + feeOne.setScale(2, RoundingMode.HALF_UP) + "元",
                orderId, orderCreateTime);
        //扣除二级返佣
        Long preUid = userInfo.getSpreadUid();
        if(preUid == null || preUid == 0) {
            return;
        }
        MwUser preUser = userService.getById(preUid);

        //当前用户不存在 直接返回
        if(ObjectUtil.isNull(preUser)) {
            return;
        }

        //根据用户当前等级获取会员详情
        MwSystemUserLevel preSystemLevel = systemUserLevelService.getUserLevel(preUser, platformEnum.getValue());
        if(preSystemLevel == null) {
            return;
        }
        BigDecimal feeTwo = getFeeTwo(preSystemLevel, hb);
        BigDecimal preNewMoney = NumberUtil.sub(preUser.getNowMoney(), feeTwo);
        //当上级余额不够扣的时候 扣到0，负数会报错
        if(preNewMoney.compareTo(BigDecimal.ZERO) < 0) {
            preNewMoney = BigDecimal.ZERO;
        }
        preUser.setNowMoney(preNewMoney);
        userService.updateById(preUser);

        //增加流水
        billService.expend(preUid,childUser.getUid(),  "用户‘" + userInfo.getNickname() + "’的用户订单退款扣除红包",
                BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_8.getValue(),
                feeTwo.doubleValue(),
                preUser.getNowMoney().doubleValue(),
                "用户‘" + userInfo.getNickname() + "’的用户订单退款扣除红包" + feeTwo.setScale(2, RoundingMode.HALF_UP) + "元",
                orderId, orderCreateTime);
    }

    protected BigDecimal getFeeOne(MwSystemUserLevel systemUserLevel, BigDecimal hb) {
        BigDecimal discountOne = systemUserLevel.getDiscountOne();
        return NumberUtil.div(NumberUtil.mul(hb, discountOne), 100);
    }

    protected BigDecimal getFeeTwo(MwSystemUserLevel systemUserLevel, BigDecimal hb) {
        BigDecimal discountTwo = systemUserLevel.getDiscountTwo();
        return NumberUtil.div(NumberUtil.mul(hb, discountTwo), 100);
    }

    @Override
    public BigDecimal calIntegral(MwSystemUserLevel userLevel, double preFee) {
        BigDecimal discount = BigDecimal.valueOf(30);
        if(userLevel != null) {
            discount = userLevel.getDiscount();
        }

        double integral = preFee*discount.intValue();

        return BigDecimal.valueOf((int)integral);
    }
    /**
     * 订单分销一二级返佣
     *  0=默认
     */
    public void gainParentMoney(MwUser origUser, BigDecimal hb, String orderId, Date orderCreateTime, Integer innerType,
                                PlatformEnum platformEnum,
                                double orderHb, Date unlockTime) {
        //虚拟订单不分销
        if(!TkUtil.isUserOrder(innerType)) {
            return;
        }
        //如果分销没开启直接返回
        String open = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_OPEN);
        if(StrUtil.isBlank(open) || ShopCommonEnum.ENABLE_2.getValue().toString().equals(open)) {
            return;
        }
        //如果上级用户uid不存在直接返回
        Long spreadUid = origUser.getSpreadUid();
        if(spreadUid == null || spreadUid == 0) {
            return;
        }
        //上级用户
        MwUser levelOneUser = userService.getById(spreadUid);
        //当前用户不存在 没有上级  直接返回
        if(ObjectUtil.isNull(levelOneUser)) {
            return;
        }

        //根据用户当前等级获取会员详情
        MwSystemUserLevel systemUserLevel = systemUserLevelService.getUserLevel(levelOneUser, platformEnum.getValue());
        if(systemUserLevel == null) {
            return;
        }
        //计算一级返佣
        BigDecimal discountOne = systemUserLevel.getDiscountOne();
        BigDecimal feeOne = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(hb, discountOne), 100));
//        levelOneUser.setNowMoney(NumberUtil.add(levelOneUser.getNowMoney(), feeOne));
//        userService.updateById(levelOneUser);
        //增加流水
        String mark = TkUtil.getOrderBillMark(origUser.getNickname(), platformEnum.getDesc(), orderId, orderHb, discountOne.intValue(),
                feeOne.doubleValue(), 1);
        billService.income(spreadUid, origUser.getUid(),
                TkUtil.getOrderBillTitle(origUser.getNickname(), platformEnum.getDesc(), 1),
                BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_13.getValue(), platformEnum.getValue(),
                feeOne.doubleValue(),
                levelOneUser.getNowMoney().doubleValue(),
                mark, orderId, orderCreateTime, IS_ORDER_STATUS_1.getValue(), unlockTime);
        //触发刷新每日预估等信息
        RedisUtil.setFeeUid(levelOneUser.getUid());
        //发送通知
        jPushService.push(mark, levelOneUser.getUid());
        AppDataConfig config = systemConfigService.getAppDataConfig();
        if(config.getSpreadLevel() == null || config.getSpreadLevel() == 3) {
            //计算二级返佣
            gainLevelTwoMoney(origUser.getUid(), levelOneUser, hb, orderId, orderCreateTime, platformEnum, orderHb, origUser, unlockTime);
        }
      }

    protected void gainLevelTwoMoney(Long origUid, MwUser userInfo, BigDecimal hb, String orderId, Date orderCreateTime,
                                     PlatformEnum platformEnum,
                                     double orderHb,MwUser baseUser, Date unlockTime) {
        //计算二级返佣
        Long preUid = userInfo.getSpreadUid();
        if(preUid == null || preUid == 0) {
            return;
        }
        MwUser preUser = userService.getById(preUid);

        //当前用户不存在 直接返回
        if(ObjectUtil.isNull(preUser)) {
            return;
        }

        //根据用户当前等级获取会员详情
        MwSystemUserLevel preSystemLevel = systemUserLevelService.getUserLevel(preUser, platformEnum.getValue());

        if(preSystemLevel == null) {
            return;
        }

        BigDecimal discountTwo = preSystemLevel.getDiscountTwo();
        BigDecimal feeTwo = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(hb, discountTwo), 100));
        preUser.setNowMoney(NumberUtil.add(preUser.getNowMoney(), feeTwo));
        userService.updateById(preUser);

        //增加流水
        String mark = TkUtil.getOrderBillMark(baseUser.getNickname(),
                platformEnum.getDesc(),
                orderId, orderHb, discountTwo.intValue(),
                feeTwo.doubleValue(), 2);
        billService.income(preUid, origUid, TkUtil.getOrderBillTitle(baseUser.getNickname(),
                        platformEnum.getDesc(), 2), BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_13.getValue(), platformEnum.getValue(),
                feeTwo.doubleValue(),
                preUser.getNowMoney().doubleValue(),
                mark, orderId, orderCreateTime, IS_ORDER_STATUS_1.getValue(), unlockTime);
        //触发刷新每日预估等信息
        RedisUtil.setFeeUid(preUser.getUid());
        //发送通知
        jPushService.push(mark, preUser.getUid());


    }

    @Override
    public void bindOrder(Long uid, MailvorTbOrder order) {
        tbOrderService.bindUser(uid, order.getTradeParentId());

        //设置需要刷新今日预估的用户uid
        RedisUtil.setFeeUid(uid);
        //如果是淘礼金订单，保存订单号 用于app端跳转订单页
        if(order.getAdzoneId().equals(tbConfig.getAdZoneId())) {
            MwUserUnion userUnion = userUnionService.getOne(uid);
            if(userUnion != null && userUnion.getTljData() != null && !CollectionUtils.isEmpty(userUnion.getTljData().getData())) {
                List<JSONObject> tljDataList = userUnion.getTljData().getData();
                for(JSONObject data : tljDataList) {
                    JSONObject detail = data.getJSONObject("detail");
                    //这里使用商品title匹配 可能不精确 后期优化
                    String title = detail.getString("title");
                    if(title.equals(order.getItemTitle())) {
                        data.put("orderId", order.getTradeParentId());
                    }
                }
                userUnionService.saveOrUpdate(userUnion);
            }
        }
//        if(order.getTkStatus() == OrderUtil.TB_NOT_VALID_ORDER_STATUS) {
//            return;
//        }
//        gainUserIntegral(uid, order.getTradeParentId().toString(), order.getTkCreateTime(), new BigDecimal(10), PlatformEnum.TB);

    }
    @Override
    public void bindOrder(Long uid, MailvorMtOrder order) {
        mtOrderService.bindUser(uid, order.getUniqueItemId());
        //设置需要刷新今日预估的用户uid
        RedisUtil.setFeeUid(uid);
//        if(!OrderUtil.MT_VALID_ORDER_STATUS.contains(order.getItemStatus())) {
//            return;
//        }
//        gainUserIntegral(uid, order.getUniqueItemId().toString(), order.getOrderPayTime(), new BigDecimal(10), PlatformEnum.MT);

    }
    @Override
    public void decHbAndRefundOrder(Long uid, MailvorTbOrder order) {
        if(uid == null) {
            return;
        }
        //如果订单退款，如果账单未解锁，账单改为失效，如果账单已解锁，直接扣除余额
        Long orderId = order.getTradeParentId();
        boolean invalid = invalidBillList(orderId.toString());
        if(!invalid) {
            //如果账单已解锁，直接扣除余额
            MwUser user = userService.getById(uid);

            BigDecimal decHb = BigDecimal.valueOf(order.getHb());
            boolean succ = decUserHb(uid, orderId.toString(), decHb, PlatformEnum.TB, order.getTkCreateTime());
            if (succ) {
                tbOrderService.refundOrder(orderId);
                decParentMoney(user, decHb, orderId.toString(), order.getTkCreateTime(), PlatformEnum.TB);
            }
        }
    }

    @Override
    public void bindOrder(Long uid, MailvorJdOrder order) {
        jdOrderService.bindUser(uid, order.getOrderId());
        //设置需要刷新今日预估的用户uid
        RedisUtil.setFeeUid(uid);
//        if(!OrderUtil.JD_VALID_ORDER_STATUS.contains(order.getValidCode())) {
//            return;
//        }
//        gainUserIntegral(uid, order.getOrderId().toString(), order.getOrderTime(), new BigDecimal(10), PlatformEnum.JD);

    }
    @Override
    public void decHbAndRefundOrder(Long uid, MailvorJdOrder order) {
        if(uid == null) {
            return;
        }
        Long orderId = order.getOrderId();
        boolean invalid = invalidBillList(orderId.toString());
        if(!invalid) {
            MwUser user = userService.getById(uid);

            BigDecimal decHb = BigDecimal.valueOf(order.getHb());
            boolean succ = decUserHb(uid, orderId.toString(), decHb, PlatformEnum.JD, order.getOrderTime());
            if(succ) {
                jdOrderService.refundOrder(orderId);
                decParentMoney(user, decHb, orderId.toString(), order.getOrderTime(), PlatformEnum.JD);
            }
        }
    }
    public boolean invalidBillList(String orderId) {
        List<MwUserBill> userBills = billService.getListByOrderId(orderId);
        if(CollectionUtils.isNotEmpty(userBills)) {
            //由于多条记录是一起解锁，所以只判断第一条是否解锁
            MwUserBill userBill = userBills.get(0);
            if (userBill.getUnlockStatus() == IS_ORDER_STATUS_1.getValue()) {
                //把订单设置为失效
                billService.invalidByOrderId(orderId);
                return true;
            }
        }
        return false;
    }
    @Override
    public void bindOrder(Long uid, MailvorPddOrder order) {
        pddOrderService.bindUser(uid, order.getOrderSn());
        //设置需要刷新今日预估的用户uid
        RedisUtil.setFeeUid(uid);
//        if(!OrderUtil.PDD_VALID_ORDER_STATUS.contains(order.getOrderStatus())) {
//            return;
//        }
//        gainUserIntegral(uid, order.getOrderSn(), order.getOrderCreateTime(), new BigDecimal(10), PlatformEnum.PDD);

    }
    @Override
    public void decHbAndRefundOrder(Long uid, MailvorPddOrder order) {
        if(uid == null) {
            return;
        }
        String orderId = order.getOrderSn();
        boolean invalid = invalidBillList(orderId);
        if(!invalid) {
            MwUser user = userService.getById(uid);

            BigDecimal decHb = BigDecimal.valueOf(order.getHb());
            boolean succ = decUserHb(uid, orderId, decHb, PlatformEnum.PDD, order.getOrderCreateTime());
            if (succ) {
                pddOrderService.refundOrder(orderId);
                decParentMoney(user, decHb, orderId, order.getOrderCreateTime(), PlatformEnum.PDD);
            }
        }
    }
    @Override
    public void bindOrder(Long uid, MailvorVipOrder order) {
        MwUser user = userService.getById(uid);
        if(user == null) {
            return;
        }
        vipOrderService.bindUser(uid, order.getOrderSn());
        //设置需要刷新今日预估的用户uid
        RedisUtil.setFeeUid(uid);
//        if(OrderUtil.VIP_NOT_VALID_ORDER_STATUS.equals(order.getOrderSubStatusName())) {
//            return;
//        }
//        gainUserIntegral(uid, order.getOrderSn(), order.getOrderTime(), new BigDecimal(10), PlatformEnum.VIP);

    }
    @Override
    public void decHbAndRefundOrder(Long uid, MailvorVipOrder order) {
        if(uid == null) {
            return;
        }
        String orderId = order.getOrderSn();
        boolean invalid = invalidBillList(orderId);
        if(!invalid) {
            MwUser user = userService.getById(uid);

            BigDecimal decHb = BigDecimal.valueOf(order.getHb());
            boolean succ = decUserHb(uid, orderId, decHb, PlatformEnum.VIP, order.getOrderTime());
            if (succ) {
                vipOrderService.refundOrder(orderId);
                decParentMoney(user, decHb, orderId, order.getOrderTime(), PlatformEnum.VIP);
            }
        }
    }
    @Override
    public void bindOrder(Long uid, MailvorDyOrder order) {
        String orderId = order.getOrderId();
        dyOrderService.bindUser(uid, orderId);
        //设置需要刷新今日预估的用户uid
        RedisUtil.setFeeUid(uid);
//        if(OrderUtil.DY_NOT_VALID_ORDER_STATUS.equals(order.getFlowPoint())) {
//            return;
//        }
//        gainUserIntegral(uid, orderId, order.getPaySuccessTime(), new BigDecimal(10), PlatformEnum.DY);
    }

    @Override
    public void decHbAndRefundOrder(Long uid, MailvorDyOrder order) {
        if(uid == null) {
            return;
        }
        String orderId = order.getOrderId();
        boolean invalid = invalidBillList(orderId);
        if(!invalid) {
            MwUser user = userService.getById(uid);

            BigDecimal decHb = BigDecimal.valueOf(order.getHb());
            boolean succ = decUserHb(uid, orderId, decHb, PlatformEnum.DY, order.getPaySuccessTime());
            if (succ) {
                dyOrderService.refundOrder(orderId);
                decParentMoney(user, decHb, orderId, order.getPaySuccessTime(), PlatformEnum.DY);
            }
        }
    }
    /**
     * 淘客下单奖励红包
     *
     */
    public void incUserMoney(Long uid, String orderId, Date orderCreateTime,
                             BigDecimal incMoney,
                             PlatformEnum platformEnum,
                             Date unlockTime) {
        if (incMoney.compareTo(BigDecimal.ZERO) > 0) {
//            userService.incMoney(uid, incMoney);
            MwUser user = userService.getById(uid);
            if(user == null) {
                return;
            }
            String mark = TkUtil.getSelfOrderBillMark(platformEnum.getDesc(),
                        orderId, incMoney.doubleValue());
            String title = TkUtil.getOrderBillTitle(null, platformEnum.getDesc(), 0);

            //增加流水
            incomeOrderBill(uid, uid, title,
                    platformEnum.getValue(),
                    incMoney.doubleValue(),
                    user.getNowMoney().doubleValue(),
                    mark, orderId, orderCreateTime, unlockTime);
        }
    }
    @Override
    public void decHbAndRefundOrder(Long uid, MailvorMtOrder order) {
        if(uid == null) {
            return;
        }
        //如果订单退款，如果账单未解锁，账单改为失效，如果账单已解锁，直接扣除余额
        Long orderId = order.getUniqueItemId();
        boolean invalid = invalidBillList(orderId.toString());
        if(!invalid) {
            //如果账单已解锁，直接扣除余额
            MwUser user = userService.getById(uid);

            BigDecimal decHb = BigDecimal.valueOf(order.getHb());
            boolean succ = decUserHb(uid, orderId.toString(), decHb, PlatformEnum.MT, order.getOrderPayTime());
            if (succ) {
                mtOrderService.refundOrder(orderId);
                decParentMoney(user, decHb, orderId.toString(), order.getOrderPayTime(), PlatformEnum.MT);
            }
        }
    }

    public void incomeOrderBill(Long uid,Long origUid,String title, String platform, double number,
                                double balance,String mark,String linkid, Date orderCreateTime, Date unlockTime) {
        //增加流水
        billService.income(uid, origUid, title,
                BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_13.getValue(),
                platform,
                number,
                balance,
                mark, linkid, orderCreateTime, IS_ORDER_STATUS_1.getValue(), unlockTime);
    }

    public double getHb(Double commission, String platform, MwUser user) {
        Integer level = TkUtil.getLevel(platform, user);

        if(PlatformEnum.TB.getValue().equals(platform)) {
            Integer tbScale = Integer.parseInt(systemConfigService.getData(SystemConfigConstants.TK_TB_REBATE_SCALE));
            //如果是淘宝，扣除服务费
            commission = commission*tbScale/100;
        }
        List<MwSystemUserLevel> systemUserLevels = systemUserLevelService.getPlatformLevels(platform);
        MwSystemUserLevel curLevel = systemUserLevels.stream()
                .filter(userLevel -> userLevel.getGrade() == level).findFirst().orElse(null);
        BigDecimal curRate;
        if(curLevel != null) {
            curRate = curLevel.getDiscount();
        } else {
            curRate = BigDecimal.valueOf(70);
        }

        //乘以拆红包的佣金比例
        Double hb = CashUtils.getHb(commission, (curRate.doubleValue()-10)/100, curRate.doubleValue()/100);

        //红包最小0.1
//        hb = NumberUtil.round(hb+0.1, 2).doubleValue();

        //防止佣金过大
        if(hb > commission*4) {
            hb = hb/2;
        }
        if(hb > commission*4) {
            hb = hb/2;
        }
        //如果退款超过3次，红包直接再除以8
        Integer refund = poolService.getRefund(user.getUid());
        if(refund != null && refund >=3) {
            hb = NumberUtil.round(hb/8, 2).doubleValue();
        }
        hb = NumberUtil.round(hb, 2).doubleValue();

        return hb;
    }

    @Override
    public Map<String, Double> incMoneyAndBindOrder(Long uid, TkOrder order, Date unlockTime) {
        TkOrderFee orderFee = TkUtil.getOrderFee(order);
        double commission = orderFee.getCommission();
        String orderId = orderFee.getOrderId();
        Date createTime = orderFee.getCreateTime();
        PlatformEnum platform = orderFee.getPlatform();
        double rate = orderFee.getRate();

        //如果佣金比例大于40% 只拿出15%抽，防止被刷单
        if(rate > 40) {
            commission = commission * (15/rate);
        }

        MwUser user = userService.getById(uid);
        if(user == null) {
            Map map = new HashMap();
            map.put("hb", 0);
            map.put("baseHb", 0);
            map.put("shopHb", 0);
            return map;
        }
        //获取商品是否是0元购
        boolean isTlj = false;
        if(order instanceof MailvorTbOrder) {
            isTlj = ((MailvorTbOrder)order).getAdzoneId().equals(tbConfig.getAdZoneId());
        }
        //0元购商品佣金1元
        double hb;
        if(isTlj) {
            hb = 1.00;
        } else {
            hb = getHb(commission, platform.getValue(), user);
        }
        BigDecimal baseHb = TkUtil.getBaseHb(BigDecimal.valueOf(hb));
        order.setHb(baseHb.doubleValue());
        order.setBind(1);
        updateOrder(order);

        //更新用户余额
        incUserMoney(uid, orderId.toString(), createTime, baseHb, platform, unlockTime);
        //1级返利 2级返利
        gainParentMoney(user, new BigDecimal(hb), orderId, createTime, order.getInnerType(), platform, order.getHb(), unlockTime);
        Map map = new HashMap();
        map.put("hb", hb);
        return map;
    }

    public void updateOrder(TkOrder order) {
        //设置拆红包时间
        order.setSpreadHbTime(new Date());
        if(order instanceof MailvorTbOrder) {
            MailvorTbOrder tbOrder = (MailvorTbOrder) order;
            tbOrderService.updateById(tbOrder);
        } else if(order instanceof MailvorJdOrder) {
            MailvorJdOrder jdOrder = (MailvorJdOrder) order;
            jdOrderService.updateById(jdOrder);
        } else if(order instanceof MailvorPddOrder) {
            MailvorPddOrder pddOrder = (MailvorPddOrder) order;
            pddOrderService.updateById(pddOrder);
        } else if(order instanceof MailvorDyOrder) {
            MailvorDyOrder dyOrder = (MailvorDyOrder) order;
            dyOrderService.updateById(dyOrder);
        } else  if(order instanceof MailvorVipOrder) {
            MailvorVipOrder vipOrder = (MailvorVipOrder) order;
            vipOrderService.updateById(vipOrder);
        } else {
            MailvorMtOrder vipOrder = (MailvorMtOrder) order;
            mtOrderService.updateById(vipOrder);
        }
    }

    @Override
    public Date checkOrder(TkOrder tkOrder, Long uid) {
        if(tkOrder == null) {
            throw new MshopException("订单不存在");
        }
        if(tkOrder.getUid() == null) {
            throw new MshopException("订单未绑定");
        }
        if(!uid.equals(tkOrder.getUid()) || !TkUtil.isUserOrder(tkOrder.getInnerType())) {
            throw new MshopException("不是您的订单");
        }
        if(tkOrder.getBind() != 0) {
            throw new MshopException("订单已拆过红包");
        }

        double fee = 0.0;
        Date createTime = null;
        MwUser user = userService.getById(uid);
        Integer level = 3;
        if(tkOrder instanceof MailvorTbOrder) {
            MailvorTbOrder tbOrder = (MailvorTbOrder) tkOrder;
            if(OrderUtil.TB_NOT_VALID_ORDER_STATUS.equals(tbOrder.getTkStatus())) {
                throw new MshopException("订单失效");
            }
            if(tbOrder.getRefundTag() == 1) {
                throw new MshopException("订单维权");
            }
            if(tbOrder.getAlipayTotalPrice() <= 0) {
                throw new MshopException("订单金额为0不能拆红包");
            }
            if(tbOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                throw new MshopException("今天拆红包额度已满，请明天再拆");
            }
            createTime = tbOrder.getTkCreateTime();
            fee = tbOrder.getPubSharePreFee();
            level = user.getLevel();
        } else if (tkOrder instanceof MailvorJdOrder) {
            MailvorJdOrder jdOrder = (MailvorJdOrder) tkOrder;
            if(!OrderUtil.JD_VALID_ORDER_STATUS.contains(jdOrder.getValidCode())) {
                throw new MshopException("订单失效");
            }
            if(jdOrder.getEstimateCosPrice() <=0) {
                throw new MshopException("订单金额为0不能拆红包");
            }
            if(jdOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                throw new MshopException("今天拆红包额度已满，请明天再拆");
            }
            createTime = jdOrder.getOrderTime();
            fee = jdOrder.getEstimateFee();
            level = user.getLevelJd();
        }else if (tkOrder instanceof MailvorPddOrder) {
            MailvorPddOrder pddOrder = (MailvorPddOrder) tkOrder;
            if(!OrderUtil.PDD_VALID_ORDER_STATUS.contains(pddOrder.getOrderStatus())) {
                throw new MshopException("订单失效");
            }
            if(pddOrder.getOrderAmount() <= 0) {
                throw new MshopException("订单金额为0不能拆红包");
            }
            if(pddOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                throw new MshopException("今天拆红包额度已满，请明天再拆");
            }
            createTime = pddOrder.getOrderCreateTime();
            fee = pddOrder.getPromotionAmount()/100;
            level = user.getLevelPdd();
        }else if (tkOrder instanceof MailvorDyOrder) {
            MailvorDyOrder dyOrder = (MailvorDyOrder) tkOrder;
            if(DY_NOT_VALID_ORDER_STATUS.equals(dyOrder.getFlowPoint())) {
                throw new MshopException("订单失效");
            }
            if(dyOrder.getTotalPayAmount() <= 0) {
                throw new MshopException("订单金额为0不能拆红包");
            }
            if(dyOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                throw new MshopException("今天拆红包额度已满，请明天再拆");
            }
            createTime = dyOrder.getPaySuccessTime();
            fee = dyOrder.getEstimatedTotalCommission();
            level = user.getLevelDy();
        }else if (tkOrder instanceof MailvorVipOrder) {
            MailvorVipOrder vipOrder = (MailvorVipOrder) tkOrder;
            if(VIP_NOT_VALID_ORDER_STATUS.equals(vipOrder.getOrderSubStatusName())) {
                throw new MshopException("订单失效");
            }
            if(Double.parseDouble(vipOrder.getTotalCost()) <= 0) {
                throw new MshopException("订单金额为0不能拆红包");
            }
            if(vipOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                throw new MshopException("今天拆红包额度已满，请明天再拆");
            }
            createTime = vipOrder.getOrderTime();
            fee = Double.parseDouble(vipOrder.getCommission());
            level = user.getLevelVip();
        } else if (tkOrder instanceof MailvorMtOrder) {
            MailvorMtOrder mtOrder = (MailvorMtOrder) tkOrder;
            if(!MT_VALID_ORDER_STATUS.contains(mtOrder.getItemStatus())
                    || !MT_VALID_ORDER_BIZ_STATUS.contains(mtOrder.getItemBizStatus())) {
                throw new MshopException("订单失效");
            }
            if(vipOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                throw new MshopException("今天拆红包额度已满，请明天再拆");
            }
            createTime = mtOrder.getOrderPayTime();
            fee = mtOrder.getBalanceAmount();
            level = user.getLevelVip();
        }
        return getUnlockTime(uid, createTime, level);
    }

    public Date getUnlockTime(Long uid, Date orderCreateTime, Integer level) {
        Integer refund = poolService.getRefund(uid);

        HbUnlockConfig unlockConfig = systemConfigService.getHbUnlockConfig();
        Integer unlockDay = TkUtil.getUnlockDay(level, refund, unlockConfig);
        return DateUtil.offsetDay(orderCreateTime, unlockDay);
    }

    /**
     * 检查自购订单 不查数据库
     * 超过解锁天数3天的解锁
     * */
    @Override
    public Date checkSelfOrder(TkOrder tkOrder, UserRefundDto user, HbUnlockConfig unlockConfig) {

        if(user == null
                || tkOrder == null
                || tkOrder.getUid()==null
                || !user.getUid().equals(tkOrder.getUid())
                || !TkUtil.isUserOrder(tkOrder.getInnerType())
                ||tkOrder.getBind() != 0) {
            return null;
        }

        Long uid = tkOrder.getUid();
        double fee = 0.0;
        Date createTime = null;
        Integer level = 3;
        if(tkOrder instanceof MailvorTbOrder) {
            MailvorTbOrder tbOrder = (MailvorTbOrder) tkOrder;
            if(OrderUtil.TB_NOT_VALID_ORDER_STATUS.equals(tbOrder.getTkStatus())
                    || tbOrder.getRefundTag() == 1
                    || tbOrder.getAlipayTotalPrice() <= 0) {
                return null;
            }
            //如果今天拆的自购红包超过设定数量 不能继续拆
            if(tbOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                return null;
            }
            createTime = tbOrder.getTkCreateTime();
            fee = tbOrder.getPubSharePreFee();
            level = user.getLevel();
        } else if (tkOrder instanceof MailvorJdOrder) {
            MailvorJdOrder jdOrder = (MailvorJdOrder) tkOrder;
            if(!OrderUtil.JD_VALID_ORDER_STATUS.contains(jdOrder.getValidCode()) || jdOrder.getEstimateCosPrice() <=0) {
                return null;
            }
            if(jdOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                return null;
            }
            createTime = jdOrder.getOrderTime();
            fee = jdOrder.getEstimateFee();
            level = user.getLevelJd();
        }else if (tkOrder instanceof MailvorPddOrder) {
            MailvorPddOrder pddOrder = (MailvorPddOrder) tkOrder;
            if(!OrderUtil.PDD_VALID_ORDER_STATUS.contains(pddOrder.getOrderStatus()) || pddOrder.getOrderAmount() <= 0) {
                return null;
            }
            if(pddOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                return null;
            }
            createTime = pddOrder.getOrderCreateTime();
            fee = pddOrder.getPromotionAmount()/100;
            level = user.getLevelPdd();
        }else if (tkOrder instanceof MailvorDyOrder) {
            MailvorDyOrder dyOrder = (MailvorDyOrder) tkOrder;
            if(DY_NOT_VALID_ORDER_STATUS.equals(dyOrder.getFlowPoint()) || dyOrder.getTotalPayAmount() <= 0) {
                return null;
            }
            if(dyOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                return null;
            }
            createTime = dyOrder.getPaySuccessTime();
            fee = dyOrder.getEstimatedTotalCommission();
            level = user.getLevelDy();
        }else if (tkOrder instanceof MailvorVipOrder) {
            MailvorVipOrder vipOrder = (MailvorVipOrder) tkOrder;
            if(VIP_NOT_VALID_ORDER_STATUS.equals(vipOrder.getOrderSubStatusName()) || Double.parseDouble(vipOrder.getTotalCost()) <= 0) {
                return null;
            }
            if(vipOrderService.getSpreadCountToday(uid) >= systemConfigService.getSpreadHbCount()) {
                return null;
            }
            createTime = vipOrder.getOrderTime();
            fee = Double.parseDouble(vipOrder.getCommission());
            level = user.getLevelVip();
        }
        return getUnlockTime(uid, createTime, level);
    }
}
