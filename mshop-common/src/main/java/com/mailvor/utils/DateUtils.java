/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 时间工具类
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static final long ONE_DAY_MILLIS = 24*60*60*1000;

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 秒转换为指定格式的日期
     *
     * @param second
     * @return
     */
    public static String secondToDate(long second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = format.format(date);
        return dateString;
    }

    public static String getAdditionalNo(String goodsId) {
        return goodsId.substring(goodsId.length() -6);
    }

    public static long betweenDays(Date start, Date end) {
        long s = end.getTime()-start.getTime();  //endeTime和startTime是两个要计算的时间
        TimeUnit time = TimeUnit.DAYS;
        return time.convert(s, TimeUnit.MILLISECONDS);   //days即相差天数
    }

    public static int mockIntBetween2(int begin, int end) {
        int between = end - begin;
        double random = Math.random();
        int randomBetween = new Double(random * between).intValue();
        int result = begin + randomBetween;
        return result;
    }

    /**
     *
     * @param n 数量
     * @param L 数字
     */
    public static List<Integer> random(int n, int L){
        List<Integer> result = new ArrayList();
        int num = n;
        int min = 2;
        int max = L;
        int num1 = getRandom(min, (max / num + 1));
        result.add(num1);
        int total = max;
        for (int i = 1; i < num; i++) {
            total = total - num1;
            while (total < min) {
                int maxc = Collections.max(result);
                result.set(result.indexOf(maxc), min);
                int s = result.stream().reduce(Integer::sum).get();
                total = max - s;
            }
            if (i != num - 1) {
                num1 = getRandom(min, total);
                result.add(num1);
            } else {
                result.add(total);
            }
        }
        return result;

    }
    public static int getRandom(int MIN, int MAX) {
        Random random = new Random();
        return random.nextInt(MAX - MIN + 1) + MIN;
    }

    public static Date randomDate(Date start, Date end){
        long randomDate = randomLong(start.getTime(), end.getTime());
        return new Date(randomDate);
    }
    public static long randomLong(long start, long end){
        Random r = new Random();
        return (long) (start + (r.nextFloat() * (end - start + 1)));
    }
    public static int randomInt(long start, long end){
        Random r = new Random();
        return (int) (start + (r.nextFloat() * (end - start + 1)));
    }
    /**
     * 生成size数量的随机时间，位于[start,end)范围内 时间倒序排列
     * @param start 开始时间
     * @param end 结束时间
     * @param size 生成时间个数
     * @param order 结果排序：-1时间倒叙，0 乱序，1时间正序
     * @return List<Date>
     * @throws Exception
     */
    public static List<Date> randomDate(Date start, Date end, int size, int order) {
        Random random = new Random();
        List<Date> dates = random.longs(size, start.getTime(), end.getTime()).mapToObj(t -> new Date(t)).collect(Collectors.toList());

        dates.sort((t1,t2)-> t1.compareTo(t2) * order);

        return dates;
    }
    public static List<Date> randomDate(Date start, Date end, int size) {
        return randomDate(start, end, size, 1);
    }
    public static List<Date> getDateList(Integer orderCount) {
        Date start = DateUtil.beginOfDay(new Date());
        Date end = DateUtil.endOfDay(new Date());
        //生成时间数据
        //时间主要分布在5:00:00-23:59:59点
        //小部分在00:00:00-01:23:00
        Integer secCount = DateUtils.randomInt(0, orderCount/8);
        Integer firstCount = orderCount - secCount;
        List<Date> firstDateList = DateUtils.randomDate(
                DateUtil.offsetHour(start, 5),
                end, firstCount);
        if(secCount > 0) {
            List<Date> secDateList = DateUtils.randomDate(
                    start,
                    DateUtil.offsetHour(start, 2) , secCount);
            firstDateList.addAll(secDateList);
        }

        return firstDateList;
    }

    public static void main(String[] args) {
//        int total = 0;
//        int max = 10000;
//        for (int i =0; i< max;i++) {
//            int value = DateUtils.mockIntBetween2(10, 20);
//            System.out.println(value);
//            total+=value;
//        }
//        System.out.println("avg: " + total/max);
//        long millis = System.currentTimeMillis();
//        int day = 10;
//        Date randomDate = DateUtils.randomDate(new Date(millis), new Date(millis + day*24*60*60*1000));
//        System.out.println(randomDate);
//        List<Date> randomDates = DateUtils.randomDate(new Date(millis), new Date(millis + day*24*60*60*1000), 50);
//        SimpleDateFormat s =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(randomDates);
//        for(Date date: randomDates) {
//            System.out.println(s.format(date));
//        }
//
//        DateUtils.random(40, 22563);
//        System.out.println(System.currentTimeMillis()*3);
//        System.out.println(DateUtils.randomInt(0, 100));

//        List<Integer> ints = DateUtils.random(50, 300);
//        for(Integer i: ints) {
//            System.out.println(i);
//        }
//        System.out.println(DateUtils.getProMonth());

        //生成时间数据
//        List<Date> firstDateList = DateUtils.getDateList(40);
//        System.out.println(firstDateList);
//
//        for(int i = 0; i < 1000;i++) {
//
//            int orderTypeInt = DateUtils.randomInt(0, 4);
//            System.out.println(orderTypeInt);
//        }
        System.out.println(betweenHour(0, 8));

        for(int i = 0; i < 1000; i++) {
            System.out.println(DateUtils.randomInt(0,5));
        }
    }

    public static boolean betweenHour(int start, int end) {
        int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return curHour>=start && curHour<=end;
    }
    /**
     * Gets today.
     * 当天0点
     *
     * @return the today
     */
    public static DateTime getToday() {
        return DateUtil.beginOfDay(new Date());
    }

    /**
     * 昨天0点
     *
     * @return the pro day
     */
    public static DateTime getProDay() {
        return DateUtil.beginOfDay(DateUtil.yesterday());
    }

    /**
     * Gets month.
     * 当月0点
     *
     * @return the month
     */
    public static DateTime getMonth() {
        return DateUtil.beginOfMonth(new Date());
    }

    /**
     * Gets month.
     * 当月0点
     *
     * @return the month
     */
    public static DateTime getProMonth() {
        return DateUtil.beginOfMonth(DateUtil.lastMonth());
    }
    public static DateTime get30Day() {
        return DateUtil.offsetDay(DateUtil.date(), -30);
    }
    public static DateTime get7Day() {
        return DateUtil.offsetDay(DateUtil.date(), -7);
    }

    public static DateTime getDateTimeStart(Integer type){
        switch (type){
            case 1:
                return DateUtils.getToday();
            case 2:
                return DateUtils.getProDay();
            case 3:
                return DateUtils.getMonth();
            case 4:
                return DateUtils.getProMonth();
            case 5:
                return DateUtils.get30Day();
            case 6:
                return DateUtils.get7Day();
        }
        return DateUtils.getToday();
    }

    public static DateTime getDateTimeEnd(Integer type){
        switch (type){
            case 1:
            case 3:
            case 5:
            case 6:
                return null;
            case 2:
                return DateUtils.getToday();
            case 4:
                return DateUtils.getMonth();
        }
        return null;
    }


    public static Date getBaseDate(Date expiredDate) {
        Date now = new Date();
        //如果过期时间不存在 直接返回当前时间
        //如果过期时间小于当前时间，直接返回当前时间
        if(expiredDate == null || DateUtil.compare(expiredDate, now) < 0) {
            return now;
        }
        return expiredDate;
    }
}
