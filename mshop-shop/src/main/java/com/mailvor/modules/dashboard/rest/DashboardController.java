/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.dashboard.rest;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.domain.MwUserFeeLog;
import com.mailvor.modules.user.domain.MwUserFeeLogOpt;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.MwUserFeeLogOptService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据看板 — 管理端核心指标
 */
@AllArgsConstructor
@Api(tags = "商城：数据看板")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final MwUserFeeLogOptService feeLogOptService;
    private final MwUserBillService userBillService;
    private final MwUserService userService;

    @Log("查询数据看板")
    @ApiOperation("查询数据看板概览")
    @GetMapping(value = "/overview")
    @PreAuthorize("hasAnyRole('admin','DASHBOARD')")
    public ResponseEntity getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 1. 今日预估佣金 — 汇总所有用户的 tt 字段中 cid=1(总览) 的 feeValue
        BigDecimal todayEstimated = calculateTodayEstimatedCommission();
        result.put("todayEstimatedCommission", todayEstimated);

        // 2. 本月已结算佣金 — 本月已解锁(unlockStatus=0)的收入账单
        BigDecimal monthSettled = calculateMonthSettledCommission();
        result.put("monthSettledCommission", monthSettled);

        // 3. 本月预估佣金 — 汇总所有用户的 tm 字段中 cid=1(总览) 的 feeValue
        BigDecimal monthEstimated = calculateMonthEstimatedCommission();
        result.put("monthEstimatedCommission", monthEstimated);

        // 4. 用户总数
        Long totalUsers = userService.count();
        result.put("totalUsers", totalUsers);

        // 5. 今日新增用户
        DateTime todayStart = DateUtils.getToday();
        Long todayNewUsers = userService.count(new LambdaQueryWrapper<MwUser>()
                .ge(MwUser::getCreateTime, todayStart));
        result.put("todayNewUsers", todayNewUsers);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 计算今日预估佣金：遍历所有用户的 feeLogOpt，解析 tt(JSON数组)，
     * 筛选 cid=1(总览) 的记录，累加 feeValue
     */
    private BigDecimal calculateTodayEstimatedCommission() {
        List<MwUserFeeLogOpt> allFeeLogs = feeLogOptService.list();
        BigDecimal total = BigDecimal.ZERO;
        for (MwUserFeeLogOpt opt : allFeeLogs) {
            if (opt.getTt() == null) {
                continue;
            }
            List<MwUserFeeLog> logs = JSON.parseArray(opt.getTt(), MwUserFeeLog.class);
            for (MwUserFeeLog log : logs) {
                if (log.getCid() != null && log.getCid().equals(1) && log.getFeeValue() != null) {
                    total = total.add(log.getFeeValue());
                }
            }
        }
        return total;
    }

    /**
     * 计算本月预估佣金：遍历所有用户的 feeLogOpt，解析 tm(JSON数组)，
     * 筛选 cid=1(总览) 的记录，累加 feeValue
     */
    private BigDecimal calculateMonthEstimatedCommission() {
        List<MwUserFeeLogOpt> allFeeLogs = feeLogOptService.list();
        BigDecimal total = BigDecimal.ZERO;
        for (MwUserFeeLogOpt opt : allFeeLogs) {
            if (opt.getTm() == null) {
                continue;
            }
            List<MwUserFeeLog> logs = JSON.parseArray(opt.getTm(), MwUserFeeLog.class);
            for (MwUserFeeLog log : logs) {
                if (log.getCid() != null && log.getCid().equals(1) && log.getFeeValue() != null) {
                    total = total.add(log.getFeeValue());
                }
            }
        }
        return total;
    }

    /**
     * 计算本月已结算佣金：查询 mw_user_bill 中本月已解锁的收入账单
     * 条件：pm=1(获得), unlockStatus=0(已解锁), category=now_money, 本月创建
     */
    private BigDecimal calculateMonthSettledCommission() {
        DateTime monthStart = DateUtils.getMonth();
        LambdaQueryWrapper<MwUserBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserBill::getPm, 1)
                .eq(MwUserBill::getUnlockStatus, 0)
                .eq(MwUserBill::getCategory, BillDetailEnum.CATEGORY_1.getValue())
                .ge(MwUserBill::getCreateTime, monthStart);

        List<MwUserBill> bills = userBillService.list(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (MwUserBill bill : bills) {
            if (bill.getNumber() != null) {
                total = total.add(bill.getNumber());
            }
        }
        return total;
    }
}
