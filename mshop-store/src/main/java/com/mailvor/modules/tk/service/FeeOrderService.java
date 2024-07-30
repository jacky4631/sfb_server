package com.mailvor.modules.tk.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import com.mailvor.modules.user.domain.MwUserBill;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2022/11/11 14:55
 * @description:
 */
public interface FeeOrderService {

    Double getSelfOrderFee(Integer type, List<Long> uid) throws ExecutionException, InterruptedException;

    Long getSelfOrderCount(Integer type, List<Long> uid) throws ExecutionException, InterruptedException;

    Double getFansUpgradeFee(Long uid, DateTime start, DateTime end);

    Long getFansUpgradeCount(Long uid, DateTime start, DateTime end);

    Double getFansUpgradeFee(Long uid, List<Long> uids, DateTime start, DateTime end);

    Long getFansUpgradeCount(Long uid, List<Long> uids, DateTime start, DateTime end);

    Double getSettledFee(Long uid, String platform, DateTime start, DateTime end);
    Long getSettledCount(Long uid, String platform, DateTime start, DateTime end);
}
