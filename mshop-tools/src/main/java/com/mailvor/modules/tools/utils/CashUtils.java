/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

/**
 */

public class CashUtils {

    public static final long DAY = 24*60*60L;
    public static final long HOUR = 60*60L;
    public static final DecimalFormat df = new DecimalFormat("######0.0");
    /**
     * 获取七天之前的时间作为提现时间
     *
     * @return the valid time
     */
    public static LocalDateTime getValidTime() {
        return LocalDateTime.now().minusDays(7);
    }

    /**
     * Gets remain date.
     *
     * @param unlockDay  the unlock day
     * @param createTime 订单创建时间秒
     * @return the remain date
     */
    public static String getRemainDate(long unlockDay, long createTime, double fee, Integer innerType) {
        //不是扶持订单 当订单佣金大于50，并且订单佣金大于30%，解锁时间变成30天，防止被刷单
        if((innerType == null || innerType == 0) && fee >= 50) {
            unlockDay = 30;
        }
        //订单解锁时间
        long unlockTime = createTime + unlockDay*DAY;
        //转成秒
        long remain = unlockTime - System.currentTimeMillis()/1000;
        //如果解锁时间减去当前时间小于等于0，说明已解锁
        if(remain <= 0) {
            return "ok";
        }
        long day = remain/DAY;
        long hour = remain%DAY/HOUR;
        if(day ==0 && hour == 0) {
            return "1小时内";
        }
        String remainDate = "";
        if(hour>0) {
            remainDate = hour + "小时";
        }
        if(day > 0) {
            remainDate = day + "天"+remainDate;
        }
        return remainDate;
    }

    public static double getHb(double commission, double min, double max) {
        //红包加上0.1 防止出现小数 取整后 太小
        return NumberUtil.round(NumberUtil.add(openHb(commission, min, max), 0.1), 2).doubleValue();
    }
    /**
     *
     *
     * @param commission 佣金，已经扣除淘宝服务费，拆红包比例
     * @param min        拆红包实际最小倍数
     * @param max        拆红包实际最大倍数
     * @return 最终获得的红包
     */
    private static double openHb(double commission, double min, double max) {
        double rate = randomD(min, max);
        System.out.println("当前倍数：" + rate);
        return commission*rate;

    }

    public static BigDecimal randomBD(double begin, double end) {
        BigDecimal between = new BigDecimal(end - begin);
        BigDecimal point = new BigDecimal(Math.random());
        BigDecimal pointBetween = point.multiply(between);
        return pointBetween.add(new BigDecimal(begin));
    }
    public static double randomD(double begin, double end) {
        return randomBD(begin, end).doubleValue();
    }

    public static int randomInt(int start, int end){
        Random r = new Random();
        return (int) (start + (r.nextFloat() * (end - start + 1)));
    }
    public static void main(String[] args) {
//        System.out.println(CashUtils.getRemainDate(7, 1670403417));
        double total = 0;
        double zhichu = 0;
        for(int i = 0; i< 1000; i++) {
            Double commission = randomD(0.1, 30);
            total += commission;
            double hb = getHb(commission, 0.25, 4);
            zhichu += hb;
            System.out.println("佣金："+ String.format("%.2f", commission)
                    + "  拆红包: " + String.format("%.2f", hb));
        }
        //上级30% 上上级10%的支出
        System.out.println("总佣金："+ String.format("%.2f", total)
                + "  红包支出: " + String.format("%.2f", zhichu));
//        System.out.println(DateUtil.offsetDay(DateUtil.endOfMonth(new Date()), 20));
//        Date start = new Date();
//        Date end = DateUtil.offsetDay(DateUtil.endOfMonth(start), 20);
//        System.out.println(DateUtil.betweenDay(start, end, true));
//
//        String cash = CashUtils.getRemainDate(0, DateUtil.offsetMinute(new Date(), 50).getTime()/1000, 40, 40, 0);
//        System.out.println(cash);

//        double hb = getHb(43.33, 1, 2, 100);
//        System.out.println(hb);
    }

}
