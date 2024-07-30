package com.mailvor.modules.utils;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserFeeLog;
import com.mailvor.modules.user.domain.MwUserFeeLogOpt;
import com.mailvor.modules.user.vo.MwCommissionInfoQueryVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Data
public class FeeUtil {
    public static final Double DEFAULT_FEE_SCALE = 0.7;

    public static MwUserFeeLog createGeneralFeeLogs(Long uid, Integer type, Double min, Double max, Double fee, Long count, String platform, String channel, Integer cid) {
        return MwUserFeeLog.builder()
                .id(getFeeLogId(uid, type, platform, cid))
                .uid(uid)
                .type(type)
                .platform(platform)
                .channel(channel)
                .count(count)
                .feeValue(BigDecimal.valueOf(fee))
                .fee(getFeeRange(fee, min, max))
                .cid(cid)
                .build();
    }
    public static MwUserFeeLog createFansFeeLogs(Long uid, Integer type, BigDecimal min, BigDecimal max, Double fee, Long count, String platform, String channel, Integer cid) {
        return MwUserFeeLog.builder()
                .id(getFeeLogId(uid, type, platform, cid))
                .uid(uid)
                .type(type)
                .platform(platform)
                .channel(channel)
                .count(count)
                .feeValue(BigDecimal.valueOf(fee))
                .fee(getFeeRange(min, max))
                .cid(cid)
                .build();
    }
    public static MwUserFeeLog createFixedFeeLogs(Long uid, Integer type, Double fee, Long count, String platform, String channel, Integer cid) {
        return MwUserFeeLog.builder()
                .id(getFeeLogId(uid, type, platform, cid))
                .uid(uid)
                .type(type)
                .platform(platform)
                .channel(channel)
                .count(count)
                .cid(cid)
                .feeValue(BigDecimal.valueOf(fee))
                .fee(fee ==0 ? "￥0": ("￥" + String.format("%.2f",fee)))
                .build();
    }

    private static String getFeeLogId(Long uid, Integer type, String platform, Integer cid) {

        String id = uid + type.toString() + platform;
        if(cid != 1) {
            id += cid;
        }
        return id;
    }


    private static String getFeeRange(Double fee, Double min, Double max) {
        double maxFee = NumberUtil.mul(max, fee);
        if(maxFee <= 0) {
            return "￥0";
        }
        double minFee = NumberUtil.mul(min, fee);
        return "￥" + NumberUtil.round(minFee, 2)
                + "-￥" + NumberUtil.round(maxFee, 2);
    }
    private static String getFeeRange(BigDecimal minFee, BigDecimal maxFee) {
        if(maxFee.doubleValue() <= 0) {
            return "￥0";
        }
        return "￥" + NumberUtil.round(minFee, 2)
                + "-￥" + NumberUtil.round(maxFee, 2);
    }

    public static double getPlatformTimes(MwUser user, PlatformEnum platformEnum, MwCommissionInfoQueryVo comInfo) {
        switch (platformEnum) {
            case JD:
                Integer levelJd = user.getLevelJd();
                if(levelJd < 4) {
                    return 1;
                }
                return comInfo.getJdTimes();
            case PDD:
                Integer levelPdd = user.getLevelPdd();
                if(levelPdd < 4) {
                    return 1;
                }
                return comInfo.getPddTimes();
            case DY:
                Integer levelDy = user.getLevelDy();
                if(levelDy < 4) {
                    return 1;
                }
                return comInfo.getDyTimes();
            case VIP:
                Integer levelVip = user.getLevelVip();
                if(levelVip < 4) {
                    return 1;
                }
                return comInfo.getVipTimes();
            default:
                Integer level = user.getLevel();
                if(level < 4) {
                    return 1;
                }
                return comInfo.getTbTimes();
        }
    }

    public static Map<String, MwSystemUserLevel> initUserLevelMap(List<MwSystemUserLevel> systemUserLevels, MwUser user, boolean multiVipOpen) {
        //如果多会员打开，单独计算，如果关闭，以淘宝为准
        Map<String, MwSystemUserLevel> systemUserLevelMap = new HashMap<>(5);
        if(multiVipOpen) {
            for(MwSystemUserLevel systemUserLevel : systemUserLevels) {
                if(user.getLevel().equals(systemUserLevel.getGrade()) && PlatformEnum.TB.getValue().equals(systemUserLevel.getType())) {
                    systemUserLevelMap.put(systemUserLevel.getType(), systemUserLevel);
                } else if(user.getLevelJd().equals(systemUserLevel.getGrade()) && PlatformEnum.JD.getValue().equals(systemUserLevel.getType())) {
                    systemUserLevelMap.put(systemUserLevel.getType(), systemUserLevel);
                } else if(user.getLevelPdd().equals(systemUserLevel.getGrade()) && PlatformEnum.PDD.getValue().equals(systemUserLevel.getType())) {
                    systemUserLevelMap.put(systemUserLevel.getType(), systemUserLevel);
                } else if(user.getLevelDy().equals(systemUserLevel.getGrade()) && PlatformEnum.DY.getValue().equals(systemUserLevel.getType())) {
                    systemUserLevelMap.put(systemUserLevel.getType(), systemUserLevel);
                } else if(user.getLevelVip().equals(systemUserLevel.getGrade()) && PlatformEnum.VIP.getValue().equals(systemUserLevel.getType())) {
                    systemUserLevelMap.put(systemUserLevel.getType(), systemUserLevel);
                }
            }
        } else {
            MwSystemUserLevel systemUserLevel = systemUserLevels.stream().filter(mwSystemUserLevel ->
                    mwSystemUserLevel.getGrade().equals(user.getLevel())).findFirst().orElse(null);
            for(PlatformEnum platformEnum : PlatformEnum.values()){
                systemUserLevelMap.put(platformEnum.getValue(), systemUserLevel);
            }
        }
        return systemUserLevelMap;
    }


    public static BigDecimal getScale(Map<String, MwSystemUserLevel> systemUserLevelMap, String platform, Integer level) {
        MwSystemUserLevel userLevel = systemUserLevelMap.get(platform);
        if(userLevel == null) {
            return BigDecimal.ZERO;
        }
        if(level == 1) {
            return userLevel.getDiscountOne();
        }
        return userLevel.getDiscountTwo();
    }

    public static MwUserFeeLogOpt initFeeLogOpt(Long uid) {
        MwUserFeeLogOpt logOpt = new MwUserFeeLogOpt();
        logOpt.setUid(uid);
        logOpt.setTt("[]");
        logOpt.setTs("[]");
        logOpt.setTm("[]");
        logOpt.setTl("[]");
        return logOpt;
    }

    public static List<MwUserFeeLog> getCurrentLog(Integer type, MwUserFeeLogOpt logOpt) {
        switch (type){
            case 1:
                return JSON.parseArray(logOpt.getTt(), MwUserFeeLog.class);
            case 6:
                return JSON.parseArray(logOpt.getTs(), MwUserFeeLog.class);
            case 3:
                return JSON.parseArray(logOpt.getTm(), MwUserFeeLog.class);
            case 4:
                return JSON.parseArray(logOpt.getTl(), MwUserFeeLog.class);
        }
        return JSON.parseArray(logOpt.getTt(), MwUserFeeLog.class);
    }

    public static List<MwUserFeeLog> getLogs(Integer type, Integer cid, List<MwUserFeeLogOpt> oneOpts) {
        List<MwUserFeeLog> allLogs = new ArrayList<>();
        for(MwUserFeeLogOpt logOpt : oneOpts) {
            List<MwUserFeeLog> oneLogs = FeeUtil.getCurrentLog(type, logOpt);
            oneLogs = oneLogs.stream().filter(feeLog -> feeLog.getCid().equals(cid)).collect(toList());
            allLogs.addAll(oneLogs);
        }
        return allLogs;
    }

    public static Map<String, MwUserFeeLog> getCurrentLogMap(Integer type, MwUserFeeLogOpt logOpt) {
        List<MwUserFeeLog> feeLogs = getCurrentLog(type, logOpt);
        return feeLogs.stream().collect(Collectors.toMap(MwUserFeeLog::getId, Function.identity()));

    }

    public static void setFeeOptData(MwUserFeeLogOpt feeLogOpt, Integer type, Map<String, MwUserFeeLog> optLogMap) {
        //根据类型设置预估数据
        String opsStr = JSON.toJSONString(optLogMap.values());
        switch (type) {
            case 1:
                feeLogOpt.setTt(opsStr);
                break;
            case 6:
                feeLogOpt.setTs(opsStr);
                break;
            case 3:
                feeLogOpt.setTm(opsStr);
                break;
            case 4:
                feeLogOpt.setTl(opsStr);
                break;
        }
    }


    public static void initPreviewFee(Long uid, Integer type, Double min, Double max,
                                  Double tbFee, Long tbCount,
                                  Double jdFee, Long jdCount,
                                  Double pddFee, Long pddCount,
                                  Double dyFee, Long dyCount,
                                  Double vipFee, Long vipCount,
                                  Double mtFee, Long mtCount,
                                  Map<String, MwUserFeeLog> optLogMap) {

        //保存总览淘宝
        MwUserFeeLog allTbLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, tbFee, tbCount,
                PlatformEnum.TB.getValue(), PlatformEnum.TB.getDesc(), 1);
        optLogMap.put(allTbLog.getId(), allTbLog);

        //保存总览京东
        MwUserFeeLog allJdLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, jdFee, jdCount,
                PlatformEnum.JD.getValue(), PlatformEnum.JD.getDesc(), 1);
        optLogMap.put(allJdLog.getId(), allJdLog);

        //保存总览拼多多
        MwUserFeeLog allPddLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, pddFee, pddCount,
                PlatformEnum.PDD.getValue(), PlatformEnum.PDD.getDesc(), 1);
        optLogMap.put(allPddLog.getId(), allPddLog);

        //保存总览抖音
        MwUserFeeLog allDyLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, dyFee, dyCount,
                PlatformEnum.DY.getValue(), PlatformEnum.DY.getDesc(), 1);
        optLogMap.put(allDyLog.getId(), allDyLog);


        //保存总览唯品会
        MwUserFeeLog allVipLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, vipFee, vipCount,
                PlatformEnum.VIP.getValue(), PlatformEnum.VIP.getDesc(), 1);
        optLogMap.put(allVipLog.getId(), allVipLog);


        //保存总览美团
        MwUserFeeLog allMtLog = FeeUtil.createGeneralFeeLogs(uid, type, min, max, mtFee, mtCount,
                PlatformEnum.MT.getValue(), PlatformEnum.MT.getDesc(), 1);
        optLogMap.put(allMtLog.getId(), allMtLog);
    }

}
