package com.mailvor.modules.utils;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.MshopException;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.modules.energy.config.EnergyOrderConfig;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.user.config.HbUnlockConfig;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.dto.VipOrderDetailDto;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.StringUtils;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class TkUtil {
    public static final Integer EXPIRED_LEVEL = 99;
    public static final Integer VIP_LEVEL = 5;

    public static final List<String> EXCLUDE_KEY_WROD_LIST = Arrays.asList("vip", "会员","vip会员"
            ,"会员vip","会员年卡","年卡会员","酷狗会员","会员季卡","会员月卡","vip季卡","vip月卡","vip年卡","年费vip");
    public static final List<String> KEY_WROD_LIST = Arrays.asList("vip", "会员","年卡"
            ,"月卡","季卡","充值","血糖","尿酸","芬必得","感冒","布洛芬","退热","发烧","发热","999","消炎","三九","皮炎");

    public static TkOrderFee getOrderFee(TkOrder order) {
        double commission;
        String orderId;
        Date createTime;
        PlatformEnum platform;
        double rate = 0.0;
        if(order instanceof MailvorTbOrder) {
            MailvorTbOrder tbOrder = (MailvorTbOrder) order;
            commission = tbOrder.getPubSharePreFee();
            orderId = tbOrder.getTradeParentId().toString();
            createTime = tbOrder.getTkCreateTime();
            rate = tbOrder.getTotalCommissionRate();
            platform = PlatformEnum.TB;
        } else if(order instanceof MailvorJdOrder) {
            MailvorJdOrder jdOrder = (MailvorJdOrder) order;
            commission = jdOrder.getEstimateFee();
            orderId = jdOrder.getOrderId().toString();
            createTime = jdOrder.getOrderTime();
            rate = jdOrder.getCommissionRate();
            platform = PlatformEnum.JD;
        } else if(order instanceof MailvorPddOrder) {
            MailvorPddOrder pddOrder = (MailvorPddOrder) order;
            commission = pddOrder.getPromotionAmount()/100;
            orderId = pddOrder.getOrderSn();
            createTime = pddOrder.getOrderCreateTime();
            rate = pddOrder.getPromotionRate()/10;
            platform = PlatformEnum.PDD;
        } else if(order instanceof MailvorDyOrder) {
            MailvorDyOrder dyOrder = (MailvorDyOrder) order;
            commission = dyOrder.getEstimatedTotalCommission();
            orderId = dyOrder.getOrderId();
            createTime = dyOrder.getPaySuccessTime();
            rate = dyOrder.getCommissionRate();
            platform = PlatformEnum.DY;
        } else if(order instanceof MailvorVipOrder) {
            MailvorVipOrder vipOrder = (MailvorVipOrder) order;
            commission = Double.parseDouble(vipOrder.getCommission());
            orderId = vipOrder.getOrderSn();
            createTime = vipOrder.getOrderTime();
            if(CollectionUtils.isNotEmpty(vipOrder.getDetailList())) {
                VipOrderDetailDto vipOrderDetailDto = JSON.parseObject(JSON.toJSONString(vipOrder.getDetailList().get(0)), VipOrderDetailDto.class);
                rate = Double.parseDouble(vipOrderDetailDto.getCommissionRate());
            }
            platform = PlatformEnum.VIP;
        } else {
            MailvorMtOrder mtOrder = (MailvorMtOrder) order;
            commission = mtOrder.getBalanceAmount();
            orderId = mtOrder.getUniqueItemId().toString();
            createTime = mtOrder.getOrderPayTime();
            rate = mtOrder.getBalanceCommissionRatio();
            platform = PlatformEnum.MT;
        }
        return TkOrderFee.builder()
                .commission(commission)
                .createTime(createTime)
                .orderId(orderId)
                .platform(platform)
                .rate(rate)
                .build();

    }

    public static BigDecimal getUserHbTimes(MwUser user, String platform, List<MwSystemUserLevel> levels) {
        //todo 这里系统只能增加两个级别，一个是默认等级 一个是店主等级
        if(user == null || levels.size() != 2) {
            return BigDecimal.valueOf(1L);
        }
        Integer userLevel;
        if("pdd".equals(platform) && user.getLevelPdd() > 0) {
            userLevel = user.getLevelPdd();
        }else if("jd".equals(platform) && user.getLevelJd() > 0) {
            userLevel = user.getLevelJd();
        }else if("dy".equals(platform) && user.getLevelDy() > 0) {
            userLevel = user.getLevelDy();
        }else if("vip".equals(platform) && user.getLevelVip() > 0) {
            userLevel = user.getLevelVip();
        } else {
            //当其他平台会员不存在时，默认读取tb会员
            userLevel = user.getLevel();
        }

        MwSystemUserLevel firstLevel = levels.get(0);
        MwSystemUserLevel sLevel = levels.get(1);

        MwSystemUserLevel baseLevel;
        MwSystemUserLevel shopLevel;
        if(firstLevel.getGrade() > sLevel.getGrade()) {
            baseLevel = sLevel;
            shopLevel = firstLevel;
        } else {
            baseLevel = firstLevel;
            shopLevel = sLevel;
        }
        //用户等级和基础等级相同，没有星选会员奖励 就是1
        if(userLevel == baseLevel.getGrade()) {
            return BigDecimal.valueOf(1L);
        }
        //计算比例 当前自身比例 是基础比例的倍数，并加1 ，算出基础比例佣金
        //红包金额直接除以这个倍数 就可以计算基础红包，然后相减就是店长红包
        return NumberUtil.div(shopLevel.getDiscount(), baseLevel.getDiscount());
    }
    public static BigDecimal getPlatformHbTimes(List<MwSystemUserLevel> levels) {
        //todo 这里系统只能增加两个级别，一个是默认等级 一个是店主等级
        if(levels.size() != 2) {
            return BigDecimal.valueOf(1L);
        }

        MwSystemUserLevel firstLevel = levels.get(0);
        MwSystemUserLevel sLevel = levels.get(1);

        MwSystemUserLevel baseLevel;
        MwSystemUserLevel shopLevel;
        if(firstLevel.getGrade() > sLevel.getGrade()) {
            baseLevel = sLevel;
            shopLevel = firstLevel;
        } else {
            baseLevel = firstLevel;
            shopLevel = sLevel;
        }

        //计算比例 当前自身比例 是基础比例的倍数，并加1 ，算出基础比例佣金
        //红包金额直接除以这个倍数 就可以计算基础红包，然后相减就是店长红包
        return NumberUtil.div(shopLevel.getDiscount(), baseLevel.getDiscount());
    }
    public static BigDecimal getBaseHb(BigDecimal hb) {
        return OrderUtil.getRoundFee(hb);
    }

    public static String getSelfOrderBillMark(String platformDesc,
                                          String orderId,
                                          double baseHb) {
        StringBuilder sb = new StringBuilder();
        sb.append("自己拆开");
        sb.append(platformDesc);
        sb.append("订单");
        sb.append(orderId);
        sb.append("获得");
        sb.append(baseHb);
        sb.append("元");
        return sb.toString();

    }

    public static String getOrderBillMark(String nickname, String platformDesc,
                                   String orderId,
                                   double orderHb,
                                   int scale,
                                   double hb,
                                   int level) {
        //level 0=自己 1=上级 2=上上级
        StringBuilder sb = new StringBuilder();
        if(level == 0) {
            sb.append("自己");
        }else {
            if(level == 1) {
                sb.append("金客");
            } else if (level == 2) {
                sb.append("银客");
            }
            if(StringUtils.isNotBlank(nickname)) {
                sb.append("'");
                sb.append(getAnonymousName(nickname));
                sb.append("'");
            }
        }
        sb.append("拆开");
        sb.append(platformDesc);
        sb.append("订单");
        if(level == 0) {
            sb.append(orderId);
            sb.append("获得");
            sb.append(hb);
            sb.append("元");
        } else {
            appendOther(sb, orderId, orderHb, scale, hb);
        }
        return sb.toString();

    }

    public static String getUserOrderBillMark(String nickname, String platformDesc,
                                          String orderId,
                                          double orderHb,
                                          double hb,
                                          int level) {
        //level 0=自己 1=上级 2=上上级
        StringBuilder sb = new StringBuilder();

        if(level == 1) {
            sb.append("金客");
        } else if (level == 2) {
            sb.append("银客");
        }
        if(StringUtils.isNotBlank(nickname)) {
            sb.append("'");
            sb.append(getAnonymousName(nickname));
            sb.append("'");
        }

        sb.append("拆开");
        sb.append(platformDesc);
        sb.append("订单");
        orderId = orderId.substring(0, orderId.length()-6) + "******";
        sb.append(orderId);
        sb.append("获得");
        sb.append(orderHb);
        sb.append("元，预计奖励您");
        sb.append(hb);
        sb.append("元，由于您未加盟星选，奖励无效");

        return sb.toString();

    }
    public static String getAnonymousName(String nickname) {
        if(StringUtils.isBlank(nickname)) {
            return "";
        }
        if(StringUtils.isPhone(nickname)) {
            nickname = nickname.replaceAll(nickname.substring(3,7), "****");
        }
        return nickname;
    }

    public static String getEnergyOrderBillMark(String platformDesc,
                                                String orderId,
                                                double orderHb,
                                                int scale,
                                                double hb) {
        //level 0=自己 1=上级 2=上上级
        StringBuilder sb = new StringBuilder();

        sb.append("用户拆开");
        sb.append(platformDesc);
        sb.append("订单");
        appendOther(sb, orderId, orderHb, scale, hb);

        return sb.toString();

    }
    public static String getEnergyOrderBillTitle(String platformDesc) {
        //level 0=自己 1=上级 2=上上级 3=扶持用户，其实就是1为了区分流量扶持)
        StringBuilder sb = new StringBuilder();
        sb.append("用户拆开");
        sb.append(platformDesc);
        sb.append("订单获得红包");
        return sb.toString();

    }
    protected static void appendOther(StringBuilder sb,
                                      String orderId,
                                      double orderHb,
                                      int scale,
                                      double hb) {
        orderId = orderId.substring(0, orderId.length()-6) + "******";
        sb.append(orderId);
        sb.append("获得");
        sb.append(orderHb);
        sb.append("元，奖励您");
        sb.append(scale);
        sb.append("%，获得");
        sb.append(hb);
        sb.append("元");
    }
    public static String getOrderBillTitle(String nickname, String platformDesc, int level) {
        //level 0=自己 1=上级 2=上上级 3=扶持用户，其实就是1为了区分流量扶持)
        StringBuilder sb = new StringBuilder();
        if(level == 0) {
            sb.append("自己");
        }else {
            if(level == 1) {
                sb.append("金客");
            } else if (level == 2) {
                sb.append("银客");
            }
            if(StringUtils.isNotBlank(nickname)) {
                if(StringUtils.isPhone(nickname)) {
                    nickname = nickname.replaceAll(nickname.substring(3,7), "****");
                }
                sb.append("'");
                sb.append(nickname);
                sb.append("'");
            }
        }
        sb.append("拆开");
        sb.append(platformDesc);
        sb.append("订单获得红包");
        return sb.toString();

    }

    public static boolean hasWord(String keyWord) {
        if(StringUtils.isBlank(keyWord)) {
            return false;
        }
        for(String word: KEY_WROD_LIST) {
            if(keyWord.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static Integer getUnlockDay(Integer level, Integer refund, HbUnlockConfig unlockConfig){
        //不是店主14天拆包。提现隔天到账，这段时间可以检测下有没退款。有退款系统自动扣除。如绕过这个机制，
        //出现一次他以后的包20天拆，第二次30天拆，第三次40天拆包。并且列入黑名单（拆包往小的拆），以后拆出来的钱需先补给平台
        //店主防撸机制:出现一次此号以后10天拆包，第二次20天拆包，第三次30天拆包并且以后拆出来的钱优先补偿给平台第三次此号列入黑名单（中奖率往最低走）

        if(refund == null || refund == 0) {
            if(level == 5) {
                return unlockConfig.getUnlockVip();
            }
            return unlockConfig.getUnlock();
        } else if(refund == 1) {
            if(level == 5) {
                return unlockConfig.getUnlockVip1();
            }
            return unlockConfig.getUnlock1();
        } else if(refund == 2) {
            if(level == 5) {
                return unlockConfig.getUnlockVip2();
            }
            return unlockConfig.getUnlock2();
        } else {
            if(level == 5) {
                return unlockConfig.getUnlockVip3();
            }
            return unlockConfig.getUnlock3();
        }

    }
    public static void main(String[] args) {
//        BigDecimal times = NumberUtil.div(BigDecimal.valueOf(90L), BigDecimal.valueOf(30L));
//        System.out.println(times);
////        times = NumberUtil.add(times, 1);
////        System.out.println(times);
//        BigDecimal hb = BigDecimal.valueOf(17.26);
//        BigDecimal baseHb = getBaseHb(hb, times);
//        System.out.println(baseHb);
//        BigDecimal shopHb = getShopHb(hb, baseHb);
//        System.out.println(shopHb);
//
//        HbUnlockConfig unlockConfig = new HbUnlockConfig();
//        Integer unlockDay = getUnlockDay(5,0, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(5,1, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(5,2, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(5,3, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(5,4, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(3,0, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(3,1, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(3,2, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(3,3, unlockConfig);
//        System.out.println(unlockDay);
//        unlockDay = getUnlockDay(3,4, unlockConfig);
//        System.out.println(unlockDay);
        long start = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            System.out.println(getPriceList(BigDecimal.valueOf(126), 100, 20));
        }
        System.out.println("耗时" + (System.currentTimeMillis()-start));
    }

    public static boolean isUserOrder(Integer innerType) {
        return innerType == null || innerType == 0;
    }


    /**
     * Gets price list.
     *
     * @param price the price 分
     * @param scale the scale 百分比数字 如60
     * @param count the count 数量
     * @return the price list
     */
    public static List<Integer> getPriceList(BigDecimal price, int scale, int count) {
        BigDecimal endPrice = OrderUtil.getRoundFee(NumberUtil.div(NumberUtil.mul(price, scale), 100));

        int end = endPrice.intValue();
        if(end == 0) {
            end = 1;
        }
        //转换成分计算，生成随机分红
        return DateUtils.random(count, end);
    }

    public static EnergyOrderConfig getOrderConfig(BigDecimal energy, List<EnergyOrderConfig> orderConfigs) {
        if(orderConfigs.isEmpty()) {
            throw new MshopException("不存在热度订单配置");
        }
        for(EnergyOrderConfig orderConfig: orderConfigs) {
            if(energy.doubleValue() > orderConfig.getMin() && energy.doubleValue() <= orderConfig.getMax()) {
                return orderConfig;
            }
        }
        return orderConfigs.get(0);
    }

    public static EnergyOrderConfig getOrderTuiConfig(Long count, String platform, EnergyConfigDto configDto) {
        List<EnergyOrderConfig> orderConfigs = getEnergyOrderConfig(platform, configDto);

        return getOrderConfig(BigDecimal.valueOf(count), orderConfigs);
    }

    public static List<EnergyOrderConfig> getEnergyOrderConfig(String platform, EnergyConfigDto configDto) {
        switch (platform) {
            case "jd":
                return configDto.getOrderJdConfigs();
            case "pdd":
                return configDto.getOrderPddConfigs();
            case "dy":
                return configDto.getOrderDyConfigs();
            case "vip":
                return configDto.getOrderVipConfigs();
            default:
                return configDto.getOrderTbConfigs();
        }
    }

    public static JSONObject getVipGenRequest(String openId, String adCode) {
        JSONObject urlGenRequest = new JSONObject();
        urlGenRequest.put("openId", openId);
        urlGenRequest.put("adCode", adCode);
        urlGenRequest.put("realCall", "true");
        return urlGenRequest;
    }

    public static String getVipOpenId(MwUser user) {
        if(user != null) {
            return user.getUid().toString();
        }
        return "0";
    }

    public static Integer getLevel(String platform, MwUser user) {
        if(user == null || StringUtils.isBlank(platform)) {
            return 3;
        }
        switch (platform) {
            case "tb":
                return user.getLevel();
            case "jd":
                return user.getLevelJd();
            case "pdd":
                return user.getLevelPdd();
            case "dy":
                return user.getLevelDy();
            case "vip":
                return user.getLevelVip();
        }
        return 3;
    }
    public static Date getExpired(String platform, MwUser user) {
        if(user == null || StringUtils.isBlank(platform)) {
            return new Date();
        }
        switch (platform) {
            case "tb":
                return user.getExpired();
            case "jd":
                return user.getExpiredJd();
            case "pdd":
                return user.getExpiredPdd();
            case "dy":
                return user.getExpiredDy();
            case "vip":
                return user.getExpiredVip();
        }
        return new Date();
    }
    public static Integer getLevel(String platform, MwUserExtra user) {
        if(user == null || StringUtils.isBlank(platform)) {
            return 0;
        }
        switch (platform) {
            case "tb":
                return user.getLevel();
            case "jd":
                return user.getLevelJd();
            case "pdd":
                return user.getLevelPdd();
            case "dy":
                return user.getLevelDy();
            case "vip":
                return user.getLevelVip();
        }
        return 0;
    }
}
