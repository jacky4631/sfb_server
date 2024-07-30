package com.mailvor.modules.tk.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.MwUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.mailvor.enums.BillDetailEnum.TYPE_12;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2022/11/11 14:55
 * @description:
 */
@Slf4j
@Component
public class FeeOrderServiceImpl implements FeeOrderService{

    @Resource
    private MailvorTbOrderService tbOrderService;

    @Resource
    private MailvorJdOrderService jdOrderService;

    @Resource
    private MailvorPddOrderService pddOrderService;

    @Resource
    private MailvorVipOrderService vipOrderService;

    @Resource
    protected MwUserService mwUserService;
    @Resource
    private MailvorDyOrderService dyOrderService;
    @Resource
    private MwUserBillService billService;

    @Override
    public Double getSelfOrderFee(Integer type, List<Long> uid) throws ExecutionException, InterruptedException {
        return getOrderFee(type, uid, 0);
    }
    @Override
    public Long getSelfOrderCount(Integer type, List<Long> uid) throws ExecutionException, InterruptedException {
        return getOrderCount(type, uid, 0);
    }

    /**
     * 获得会员升级费用
     *
     * @param uid      the uid
     * @return the fans upgrade fee
     */
    @Override
    public Double getFansUpgradeFee(Long uid, DateTime start, DateTime end) {
        return getFansUpgradeFee(uid, null, start, end);

    }
    @Override
    public Long getFansUpgradeCount(Long uid, DateTime start, DateTime end) {
        return getFansUpgradeCount(uid, null, start, end);

    }

    @Override
    public Double getFansUpgradeFee(Long uid, List<Long> uids, DateTime start, DateTime end) {
        List<MwUserBill> userBills = billService.list(getFansUpgradeWrapper(uid, uids, start, end));
        if(userBills.isEmpty()) {
            return 0.0;
        }
        List<BigDecimal> numbers = userBills.stream().map(mwUserBill -> mwUserBill.getNumber()).collect(Collectors.toList());
        return NumberUtil.add(numbers.toArray(new BigDecimal[0])).doubleValue();

    }
    @Override
    public Long getFansUpgradeCount(Long uid, List<Long> uids, DateTime start, DateTime end) {
        return billService.count(getFansUpgradeWrapper(uid, uids, start, end));

    }

    @Override
    public Double getSettledFee(Long uid, String platform, DateTime start, DateTime end) {
        List<MwUserBill> userBills = billService.list(getSettledWrapper(uid, platform, start, end));
        if(userBills.isEmpty()) {
            return 0.0;
        }
        List<BigDecimal> numbers = userBills.stream().map(mwUserBill -> mwUserBill.getNumber()).collect(Collectors.toList());
        return NumberUtil.add(numbers.toArray(new BigDecimal[0])).doubleValue();

    }
    @Override
    public Long getSettledCount(Long uid, String platform, DateTime start, DateTime end) {
        return billService.count(getSettledWrapper(uid, platform, start, end));

    }

    protected Double getOrderFee(Integer type, List<Long> uid, Integer innerType) throws ExecutionException, InterruptedException {

        CompletableFuture<Double> tbTodayFuture = CompletableFuture.supplyAsync(()->tbOrderService.totalFee(type, uid, innerType));
        CompletableFuture<Double> pddTodayFuture = CompletableFuture.supplyAsync(()->pddOrderService.totalFee(type, uid, innerType));
        CompletableFuture<Double> jdTodayFuture = CompletableFuture.supplyAsync(()->jdOrderService.totalFee(type, uid, innerType));
        CompletableFuture<Double> vipTodayFuture = CompletableFuture.supplyAsync(()->vipOrderService.totalFee(type, uid, innerType));
        CompletableFuture<Double> dyTodayFuture = CompletableFuture.supplyAsync(()->dyOrderService.totalFee(type, uid, innerType));
        CompletableFuture.allOf(tbTodayFuture, pddTodayFuture, jdTodayFuture, vipTodayFuture, dyTodayFuture);
        return tbTodayFuture.get() + pddTodayFuture.get() + jdTodayFuture.get() + vipTodayFuture.get() + dyTodayFuture.get();
    }
    protected Long getOrderCount(Integer type, List<Long> uid, Integer innerType) throws ExecutionException, InterruptedException {
        CompletableFuture<Long> tbSFuture = CompletableFuture.supplyAsync(()->tbOrderService.totalCount(type, uid, innerType));
        CompletableFuture<Long> pddSFuture = CompletableFuture.supplyAsync(()->pddOrderService.totalCount(type, uid, innerType));
        CompletableFuture<Long> jdSFuture = CompletableFuture.supplyAsync(()->jdOrderService.totalCount(type, uid, innerType));
        CompletableFuture<Long> vipSFuture = CompletableFuture.supplyAsync(()->vipOrderService.totalCount(type, uid, innerType));
        CompletableFuture<Long> dySFuture = CompletableFuture.supplyAsync(()->dyOrderService.totalCount(type, uid, innerType));
        CompletableFuture.allOf(tbSFuture,pddSFuture,jdSFuture,vipSFuture,dySFuture);
        return tbSFuture.get() + pddSFuture.get() + jdSFuture.get() + vipSFuture.get() + dySFuture.get();
    }

    public Wrapper<MwUserBill> getFansUpgradeWrapper(Long uid, List<Long> uids, DateTime start, DateTime end) {
        LambdaQueryWrapper<MwUserBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserBill::getUid, uid);
        if(CollectionUtils.isNotEmpty(uids)) {
            wrapper.in(MwUserBill::getOrigUid, uids);
        }
        wrapper.eq(MwUserBill::getType, TYPE_12.getValue());
        if(start != null) {
            wrapper.ge(MwUserBill::getCreateTime, start);
        }
        if(end!= null) {
            wrapper.lt(MwUserBill::getCreateTime, end);
        }
        return wrapper;
    }
    public Wrapper<MwUserBill> getSettledWrapper(Long uid, String platform, DateTime start, DateTime end) {
        LambdaQueryWrapper<MwUserBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserBill::getUid, uid);
        wrapper.eq(MwUserBill::getPlatform, platform);
        wrapper.eq(MwUserBill::getCategory, BillDetailEnum.CATEGORY_1.getValue());
        if(start != null) {
            wrapper.ge(MwUserBill::getCreateTime, start);
        }
        if(end!= null) {
            wrapper.lt(MwUserBill::getCreateTime, end);
        }
        return wrapper;
    }
}
