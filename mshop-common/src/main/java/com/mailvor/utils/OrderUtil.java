/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.enums.ShopCommonEnum;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @ClassName OrderUtil
 * @author huangyu
 * @Date 2019/9/13
 **/
public class OrderUtil {
    public static final List JD_VALID_ORDER_STATUS = Arrays.asList(16L,17L);

    public static final Integer TB_NOT_VALID_ORDER_STATUS = 13;
    //3订单结算 12订单付款
    public static final List<Integer> TB_VALID_ORDER_STATUS = Arrays.asList(3, 12);
    public static final List PDD_VALID_ORDER_STATUS = Arrays.asList(0,1,2,3,5);
    public static final String DY_NOT_VALID_ORDER_STATUS = "REFUND";


    public static final Integer ELE_NOT_VALID_ORDER_STATUS = 3;
    public static final String VIP_NOT_VALID_ORDER_STATUS = "已失效";


    public static final List MT_VALID_ORDER_STATUS = Arrays.asList(0,1);
    public static final List MT_VALID_ORDER_BIZ_STATUS = Arrays.asList(1,2,3);
    /**
     * 获取精确到秒的时间戳
     * @return
     **/
    public static int getSecondTimestamp(){
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        return Integer.valueOf(timestamp);
    }


    /**
     * 返回活动状态
     * @param starTime 开始时间
     * @param endTime 结束时间
     * @param status  0-关闭 其他表示相反
     * @return String
     */
    public static String checkActivityStatus(Date starTime,Date endTime,Integer status){
        Date nowTime = new Date();

        if(ShopCommonEnum.IS_STATUS_0.getValue().equals(status)) {
            return "关闭";
        }

        if(DateUtil.compare(starTime,nowTime) > 0){
            return "活动未开始";
        }else if(DateUtil.compare(endTime,nowTime) < 0){
            return "活动已结束";
        }else if(DateUtil.compare(endTime,nowTime) > 0 && DateUtil.compare(starTime,nowTime) < 0){
            return "正在进行中";
        }

        return "未知";

    }

    /**
     * 生成邀请码
     *
     * @return
     */
    public static String createShareCode() {
        int maxNum = 36;
        int i;
        int count = 0;
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < 10) {
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

    /**
     * 获取俩个数之间的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static Double randomNumber(double min, double max) {
        return NumberUtil.add(min,
                NumberUtil.mul(Math.random(),
                        NumberUtil.sub(max, min)));
    }

    /**
     * 时间戳订单号
     *
     * @return
     */
    public static String orderSn() {
        Date date = DateUtil.date();
        return DateUtil.format(date, "yyyyMMddHHmmssSSS");
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s) * 1000;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为date
     */
    public static Date stampToDateObj(String s) {
        long lt = new Long(s) * 1000;
        Date date = new Date(lt);
        return date;
    }


    /**
     * 获取精确到秒的时间戳
     *
     * @return
     **/
    public static int getSecondTimestampTwo() {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return Integer.valueOf(timestamp);
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @return
     **/
    public static int dateToTimestamp(Date date) {
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @return
     **/
    public static int dateToTimestampT(DateTime date) {
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }

    public static long dateToSecond(String str) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(str);
        long ts = date.getTime();
        return ts/1000;
    }

    /**
     * 获取订单状态名称
     *
     * @param status
     * @return
     */
    public static String orderStatusStr(int status) {
        String statusName = "";
        if (status == 0) {
            statusName = "未支付";
        } else if (status == 0) {
            statusName = "未发货";
        } else if (status == 0) {
            statusName = "未核销";
        } else if (status == 1) {
            statusName = "待收货";
        } else if (status == 1) {
            statusName = "未核销";
        } else if (status == 2) {
            statusName = "待评价";
        } else if (status == 3) {
            statusName = "已完成";
        }

        return statusName;
    }


    /**
     * 获取状态数值
     *
     * @param status
     * @return
     */
    public static int orderStatus(int status) {
        //todo  1-未付款 2-未发货 3-退款中 4-待收货 5-待评价 6-已完成 7-已退款
        int _status = 0;

        if (status == 0) {
            _status = 1;
        } else if (status == 0) {
            _status = 2;
        } else if (status == 1) {
            _status = 4;
        } else if (status == 2) {
            _status = 5;
        } else if (status == 3) {
            _status = 6;
        }

        return _status;

    }


    /**
     * 支付方式
     *
     * @param pay_type
     * @param paid
     * @return
     */
    public static String payTypeName(String pay_type, int paid) {
        String payTypeName = "";
        if (paid == 1) {
            switch (pay_type) {
                case "weixin":
                    payTypeName = "微信支付";
                    break;
                case "yue":
                    payTypeName = "余额支付";
                    break;
                case "integral":
                    payTypeName = "积分兑换";
                    break;
                case "offline":
                    payTypeName = "线下支付";
                    break;
                default:
                    payTypeName = "其他支付";
                    break;
            }
        } else {
            switch (pay_type) {
                default:
                    payTypeName = "未支付";
                    break;
                case "offline":
                    payTypeName = "线下支付";
                    break;
            }
        }
        return payTypeName;
    }

    /**
     * 支付渠道(0微信公众号1微信小程序)
     *
     * @return
     */
    public static String payChannel(Integer pay_channel) {
        if (pay_channel.equals(1)) {
            return "微信小程序";
        } else {
            return "微信公众号";
        }
    }

    //todo 订单类型
    public static String orderType(int pinkId) {
        return "普通订单";
    }


    public static String getIosProductId(String platform, Integer grade) {
        return getIOSPrefixPayId(platform) + "000" + grade;
    }

    protected static String getIOSPrefixPayId(String platform) {
        if(PlatformEnum.JD.getValue().equals(platform)) {
            return "1";
        } else if(PlatformEnum.PDD.getValue().equals(platform)) {
            return "2";
        } else if(PlatformEnum.DY.getValue().equals(platform)) {
            return "3";
        } else if(PlatformEnum.VIP.getValue().equals(platform)) {
            return "4";
        }
        return "0";
    }

    public static BigDecimal getRoundFee(BigDecimal hb) {
        return NumberUtil.round(hb,2);
    }
    public static BigDecimal getRoundFee(Double hb) {
        return NumberUtil.round(hb,2);
    }
    public static String getCouponKey(String platform, Long uid) {
        return "pay:coupon:" + platform + ":" + uid;
    }
    public static void  main(String[] args) {
        for(int i = 0; i < 10; i++) {
            double d = randomNumber(1,3);
            System.out.println(d);
            BigDecimal dd = NumberUtil.round(NumberUtil.div(NumberUtil.mul(1980.00, d), 100),2);
            System.out.println(dd);
        }

    }

    public static Long getJdOrderUser(Long param) {
        if(param != null) {
            return param;
        }
        return 0L;
    }

    public static Long getPddOrderUser(String param) {
        if(StringUtils.isBlank(param)) {
            return 0L;
        }
        try{
            JSONObject obj = JSON.parseObject(param);
            return Long.parseLong(obj.getString("uid"));

        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static Long getVipOrderUser(String param) {
        if(StringUtils.isBlank(param)) {
            return 0L;
        }
        try {
            return Long.parseLong(param);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static Long getDyOrderUser(String param) {
        //773026_8_0200 中间的是用户id
        if(StringUtils.isBlank(param)) {
            return 0L;
        }
        String[] split = param.split("_");
        if(split.length<=1) {
            return 0L;
        }
        //防止中间不是数字
        try {
            return Long.parseLong(split[1]);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
