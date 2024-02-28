package com.mailvor.modules.order.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.tools.utils.CashUtils;
import com.mailvor.utils.OrderUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;


class SuStoreOrderServiceImplTest {


    private SuStoreOrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new SuStoreOrderServiceImpl();
    }
    @Test
    void calIntegral() {
        MwSystemUserLevel mwUserLevel = new MwSystemUserLevel();
        mwUserLevel.setDiscount(BigDecimal.valueOf(45));
        BigDecimal bigDecimal = orderService.calIntegral(mwUserLevel, 2.98);
        Assertions.assertEquals(bigDecimal, BigDecimal.valueOf(134));
    }

    @Test
    void getHb() {
        double total = 0;
        double zhichu = 0;
        Integer curOderCount = 0;
        for(int i = 0; i< 1000; i++) {
            Double commission = CashUtils.randomD(2, 60);
            total += commission;
            double hb = SuStoreOrderServiceImplTest.getHb(commission, true, 3, curOderCount);
            zhichu += hb;
            curOderCount++;
            System.out.println("第"+ (i+1) + "次拆红包，订单佣金："+ String.format("%.2f", commission)
                    + "元  会员总得到红包: " + String.format("%.2f", hb) + "元");
        }
        //上级30% 上上级10%的支出
        System.out.println("总佣金："+ String.format("%.2f", total)
                + "  红包支出: " + String.format("%.2f", zhichu)
                + " 上级支出："+ String.format("%.2f", zhichu*0.3 )
                + " 盈亏：" + String.format("%.2f", (total-zhichu-zhichu*0.3)));

    }

    public static double getHb(double commission, boolean isMember, Integer prefixOrderCount, Integer curOrderCount) {
        if(PlatformEnum.TB.getDesc().equals("淘宝")) {
            //如果是淘宝，扣除服务费
            commission = commission*0.9;
        }
        BigDecimal baseRate = BigDecimal.valueOf(70);

        double actMin = 0.25;
        double actMax = 4;
        // 70%＝10元。这10元先抽，抽多少再4倍，是前3单再1.5倍
        //会员
        Integer curRate = 280;
        if(!isMember) {
            curRate = 70;
        }
        BigDecimal times = NumberUtil.div(curRate, baseRate);
        //乘以拆红包的佣金比例
        BigDecimal comm = NumberUtil.div(NumberUtil.mul(commission, baseRate), 100);
        //如果佣金高于50 同时拆红包比例大于100的 说明是会员，走接近最低比例
        Double hb = 0d;
        if(commission > 50 && curRate > 100) {
            hb = NumberUtil.round((
                    NumberUtil.mul(NumberUtil.mul(comm, CashUtils.randomD(actMin, actMin+0.03)), times)), 2).doubleValue();
        } else {
            //计算拆红包金额 在一定订单数量内保证佣金比例在commissionInfoQueryVo.getHbRebateScale()
            hb = CashUtils.getHb(comm.doubleValue(), actMin, actMax);
            hb = NumberUtil.mul(hb, times).doubleValue();

            //获取订单数量，如果订单数量少于配置的数量，倍数为TK_HB_ACT_ORDER_TIMES
            if(prefixOrderCount > 0 && curOrderCount <= prefixOrderCount) {
                //获取所有订单数量
                    Double prefixOrderTimes = 1.5;
                    //红包加上0.1 防止误差
                    hb = OrderUtil.getRoundFee(NumberUtil.add(NumberUtil.mul(hb, prefixOrderTimes), 0.1)).doubleValue();
            } else {
                hb = NumberUtil.round(hb+0.1, 2).doubleValue();
            }

        }
        if(hb > commission*4) {
            hb = hb/2;
        }
        if(hb > commission*4) {
            hb = hb/2;
        }
        return hb;
    }

}
