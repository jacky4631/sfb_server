package com.mailvor.modules.tk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowQueryResult;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.tk.domain.MailvorDyOrder;
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import com.mailvor.modules.tk.domain.MailvorPddOrder;
import com.mailvor.modules.tk.domain.MailvorTbOrder;
import com.mailvor.modules.tk.domain.MwReconcileDiff;
import com.mailvor.modules.tk.domain.MwReconcileLog;
import com.mailvor.modules.tk.param.QueryDyParam;
import com.mailvor.modules.tk.param.QueryJdParam;
import com.mailvor.modules.tk.param.QueryPddParam;
import com.mailvor.modules.tk.param.QueryTBParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.JdService;
import com.mailvor.modules.tk.service.MwReconcileService;
import com.mailvor.modules.tk.service.PddService;
import com.mailvor.modules.tk.service.mapper.MailvorDyOrderMapper;
import com.mailvor.modules.tk.service.mapper.MailvorJdOrderMapper;
import com.mailvor.modules.tk.service.mapper.MailvorPddOrderMapper;
import com.mailvor.modules.tk.service.mapper.MailvorTbOrderMapper;
import com.mailvor.modules.tk.service.mapper.MwReconcileDiffMapper;
import com.mailvor.modules.tk.service.mapper.MwReconcileLogMapper;
import com.mailvor.modules.tk.vo.DyResVo;
import com.mailvor.modules.tk.vo.TBResVo;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwReconcileServiceImpl extends BaseServiceImpl<MwReconcileLogMapper, MwReconcileLog> implements MwReconcileService {

    private static final BigDecimal AMOUNT_TOLERANCE = new BigDecimal("0.01");
    private static final int TB_CHUNK_HOURS = 3;
    private static final int JD_CHUNK_HOURS = 1;

    private final IGenerator generator;
    private final MwReconcileDiffMapper diffMapper;
    private final DataokeService dataokeService;
    private final JdService jdService;
    private final PddService pddService;
    private final MailvorTbOrderMapper tbOrderMapper;
    private final MailvorJdOrderMapper jdOrderMapper;
    private final MailvorPddOrderMapper pddOrderMapper;
    private final MailvorDyOrderMapper dyOrderMapper;

    @Override
    public Map<String, Object> queryAll(Map<String, Object> criteria, Pageable pageable) {
        getPage(pageable);
        LambdaQueryWrapper<MwReconcileLog> wrapper = buildLogWrapper(criteria);
        wrapper.orderByDesc(MwReconcileLog::getReconcileDate);
        List<MwReconcileLog> list = baseMapper.selectList(wrapper);
        PageInfo<MwReconcileLog> pageInfo = new PageInfo<>(list);
        Map<String, Object> result = new HashMap<>();
        result.put("content", generator.convertPageInfo(pageInfo, MwReconcileLog.class));
        result.put("totalElements", pageInfo.getTotal());
        return result;
    }

    @Override
    public List<MwReconcileLog> queryAll(Map<String, Object> criteria) {
        return baseMapper.selectList(buildLogWrapper(criteria));
    }

    @Override
    public Map<String, Object> queryDiffAll(Map<String, Object> criteria, Pageable pageable) {
        getPage(pageable);
        LambdaQueryWrapper<MwReconcileDiff> wrapper = buildDiffWrapper(criteria);
        wrapper.orderByDesc(MwReconcileDiff::getCreateTime);
        List<MwReconcileDiff> list = diffMapper.selectList(wrapper);
        PageInfo<MwReconcileDiff> pageInfo = new PageInfo<>(list);
        Map<String, Object> result = new HashMap<>();
        result.put("content", list);
        result.put("totalElements", pageInfo.getTotal());
        return result;
    }

    @Override
    public List<MwReconcileDiff> queryDiffAll(Map<String, Object> criteria) {
        return diffMapper.selectList(buildDiffWrapper(criteria));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleDiff(Long diffId, String action, String remark) {
        MwReconcileDiff diff = diffMapper.selectById(diffId);
        if (diff == null) {
            throw new RuntimeException("差异记录不存在");
        }
        int handleStatus;
        switch (action) {
            case "ignore":
                handleStatus = 1;
                break;
            case "supplement":
                handleStatus = 2;
                break;
            case "retry":
                handleStatus = 3;
                break;
            default:
                throw new RuntimeException("不支持的操作: " + action);
        }
        diff.setHandleStatus(handleStatus);
        diff.setHandleRemark(remark);
        diff.setHandleTime(new Date());
        diffMapper.updateById(diff);

        MwReconcileLog logEntry = baseMapper.selectById(diff.getLogId());
        if (logEntry != null && logEntry.getStatus() == 1) {
            logEntry.setStatus(2);
            baseMapper.updateById(logEntry);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MwReconcileLog triggerReconcile(Date date, List<String> platforms) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        log.info("开始对账: date={}, platforms={}", dateStr, platforms);

        MwReconcileLog logEntry = MwReconcileLog.builder()
                .reconcileDate(date)
                .platform(String.join(",", platforms))
                .totalPlatform(0)
                .totalLocal(0)
                .matchCount(0)
                .missingCount(0)
                .extraCount(0)
                .amountDiffCount(0)
                .status(0)
                .createTime(new Date())
                .build();
        baseMapper.insert(logEntry);

        int totalPlatform = 0;
        int totalLocal = 0;
        int matchCount = 0;
        int missingCount = 0;
        int extraCount = 0;
        int amountDiffCount = 0;

        for (String platform : platforms) {
            try {
                int[] counts;
                switch (platform) {
                    case "TB":
                        counts = reconcileTb(date, logEntry.getId());
                        break;
                    case "JD":
                        counts = reconcileJd(date, logEntry.getId());
                        break;
                    case "PDD":
                        counts = reconcilePdd(date, logEntry.getId());
                        break;
                    case "DY":
                        counts = reconcileDy(date, logEntry.getId());
                        break;
                    default:
                        log.warn("未知平台: {}", platform);
                        continue;
                }
                totalPlatform += counts[0];
                totalLocal += counts[1];
                matchCount += counts[2];
                missingCount += counts[3];
                extraCount += counts[4];
                amountDiffCount += counts[5];
            } catch (Exception e) {
                log.error("对账失败 platform={}: {}", platform, e.getMessage(), e);
            }
        }

        logEntry.setTotalPlatform(totalPlatform);
        logEntry.setTotalLocal(totalLocal);
        logEntry.setMatchCount(matchCount);
        logEntry.setMissingCount(missingCount);
        logEntry.setExtraCount(extraCount);
        logEntry.setAmountDiffCount(amountDiffCount);
        logEntry.setStatus((missingCount + extraCount + amountDiffCount) > 0 ? 1 : 0);
        logEntry.setFinishTime(new Date());
        baseMapper.updateById(logEntry);

        log.info("对账完成: date={}, platform={}, totalPlatform={}, totalLocal={}, match={}, missing={}, extra={}, amountDiff={}",
                dateStr, platforms, totalPlatform, totalLocal, matchCount, missingCount, extraCount, amountDiffCount);
        return logEntry;
    }

    private int[] reconcileTb(Date date, Long logId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dayStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = cal.getTime();

        Map<String, TbOrderInfo> platformOrders = new LinkedHashMap<>();
        cal.setTime(dayStart);
        while (cal.getTime().before(dayEnd)) {
            Date chunkStart = cal.getTime();
            cal.add(Calendar.HOUR_OF_DAY, TB_CHUNK_HOURS);
            Date chunkEnd = cal.getTime().after(dayEnd) ? dayEnd : cal.getTime();

            QueryTBParam param = new QueryTBParam();
            param.setQueryType(4);
            param.setStartTime(sdf.format(chunkStart));
            param.setEndTime(sdf.format(chunkEnd));
            param.setPageSize(100);

            String positionIndex = "";
            boolean hasMore = true;
            while (hasMore) {
                param.setPositionIndex(positionIndex);
                try {
                    TBResVo res = dataokeService.queryTBList(param);
                    if (res != null && res.getData() != null && res.getData().getResults() != null
                            && res.getData().getResults().getPublisher_order_dto() != null) {
                        List<MailvorTbOrder> orders = res.getData().getResults().getPublisher_order_dto();
                        for (MailvorTbOrder order : orders) {
                            String orderKey = String.valueOf(order.getTradeParentId());
                            platformOrders.put(orderKey, new TbOrderInfo(
                                    orderKey,
                                    toBD(order.getAlipayTotalPrice()),
                                    toBD(order.getTotalCommissionFee())
                            ));
                        }
                        positionIndex = res.getData().getPosition_index();
                        hasMore = res.getData().isHas_next() && positionIndex != null && !positionIndex.isEmpty();
                    } else {
                        hasMore = false;
                    }
                } catch (Exception e) {
                    log.error("TB对账API调用失败 chunk={}-{}: {}", sdf.format(chunkStart), sdf.format(chunkEnd), e.getMessage());
                    hasMore = false;
                }
            }
            cal.setTime(chunkEnd);
        }

        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MailvorTbOrder::getTkCreateTime, dayStart);
        wrapper.lt(MailvorTbOrder::getTkCreateTime, dayEnd);
        List<MailvorTbOrder> localOrders = tbOrderMapper.selectList(wrapper);

        Map<String, MailvorTbOrder> localMap = new LinkedHashMap<>();
        for (MailvorTbOrder order : localOrders) {
            localMap.put(String.valueOf(order.getTradeParentId()), order);
        }

        return compareAndCreateDiffs(logId, "TB", platformOrders, localMap);
    }

    private int[] reconcileJd(Date date, Long logId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dayStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = cal.getTime();

        Map<String, PlatformOrderInfo> platformOrders = new LinkedHashMap<>();
        cal.setTime(dayStart);
        while (cal.getTime().before(dayEnd)) {
            Date chunkStart = cal.getTime();
            cal.add(Calendar.HOUR_OF_DAY, JD_CHUNK_HOURS);
            Date chunkEnd = cal.getTime().after(dayEnd) ? dayEnd : cal.getTime();

            int pageNo = 1;
            boolean hasMore = true;
            while (hasMore) {
                QueryJdParam param = new QueryJdParam();
                param.setType(3);
                param.setStartTime(sdf.format(chunkStart));
                param.setEndTime(sdf.format(chunkEnd));
                param.setPageNo(pageNo);
                param.setPageSize(100);
                try {
                    OrderRowQueryResult result = jdService.order(param);
                    if (result != null && result.getData() != null && result.getData().length > 0) {
                        for (OrderRowResp resp : result.getData()) {
                            String orderKey = String.valueOf(resp.getOrderId());
                            platformOrders.put(orderKey, new PlatformOrderInfo(
                                    orderKey,
                                    toBD(resp.getEstimateCosPrice()),
                                    toBD(resp.getEstimateFee())
                            ));
                        }
                        hasMore = result.getHasMore() != null && result.getHasMore();
                        pageNo++;
                    } else {
                        hasMore = false;
                    }
                } catch (Exception e) {
                    log.error("JD对账API调用失败 chunk={}-{}: {}", sdf.format(chunkStart), sdf.format(chunkEnd), e.getMessage());
                    hasMore = false;
                }
            }
            cal.setTime(chunkEnd);
        }

        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MailvorJdOrder::getOrderTime, dayStart);
        wrapper.lt(MailvorJdOrder::getOrderTime, dayEnd);
        List<MailvorJdOrder> localOrders = jdOrderMapper.selectList(wrapper);

        Map<String, LocalOrderInfo> localMap = new LinkedHashMap<>();
        for (MailvorJdOrder order : localOrders) {
            localMap.put(order.getOrderId() != null ? String.valueOf(order.getOrderId()) : order.getId(),
                    new LocalOrderInfo(toBD(order.getEstimateCosPrice()), toBD(order.getEstimateFee())));
        }

        return compareAndCreateDiffsGeneric(logId, "JD", platformOrders, localMap);
    }

    private int[] reconcilePdd(Date date, Long logId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dayStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = cal.getTime();

        Map<String, PlatformOrderInfo> platformOrders = new LinkedHashMap<>();
        String lastOrderId = null;
        boolean hasMore = true;
        while (hasMore) {
            QueryPddParam param = new QueryPddParam();
            param.setStartTime(sdf.format(dayStart));
            param.setEndTime(sdf.format(dayEnd));
            param.setPageSize(100);
            param.setLastOrderId(lastOrderId);
            try {
                PddDdkOrderListRangeGetResponse res = pddService.queryPddOrderList(param);
                if (res != null && res.getOrderListGetResponse() != null
                        && res.getOrderListGetResponse().getOrderList() != null
                        && !res.getOrderListGetResponse().getOrderList().isEmpty()) {
                    List<PddDdkOrderListRangeGetResponse.OrderListGetResponseOrderListItem> items =
                            res.getOrderListGetResponse().getOrderList();
                    for (PddDdkOrderListRangeGetResponse.OrderListGetResponseOrderListItem item : items) {
                        String orderSn = item.getOrderSn();
                        if (orderSn == null) continue;
                        BigDecimal amount = fenToYuan(item.getOrderAmount());
                        BigDecimal commission = fenToYuan(item.getPromotionAmount());
                        platformOrders.put(orderSn, new PlatformOrderInfo(orderSn, amount, commission));
                    }
                    lastOrderId = res.getOrderListGetResponse().getLastOrderId();
                    hasMore = lastOrderId != null && !lastOrderId.isEmpty();
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("PDD对账API调用失败: {}", e.getMessage());
                hasMore = false;
            }
        }

        LambdaQueryWrapper<MailvorPddOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MailvorPddOrder::getOrderCreateTime, dayStart);
        wrapper.lt(MailvorPddOrder::getOrderCreateTime, dayEnd);
        List<MailvorPddOrder> localOrders = pddOrderMapper.selectList(wrapper);

        Map<String, LocalOrderInfo> localMap = new LinkedHashMap<>();
        for (MailvorPddOrder order : localOrders) {
            localMap.put(order.getOrderSn(),
                    new LocalOrderInfo(fenToYuan(order.getOrderAmount()), fenToYuan(order.getPromotionAmount())));
        }

        return compareAndCreateDiffsGeneric(logId, "PDD", platformOrders, localMap);
    }

    private int[] reconcileDy(Date date, Long logId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dayStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = cal.getTime();

        Map<String, PlatformOrderInfo> platformOrders = new LinkedHashMap<>();
        int page = 1;
        int size = 50;
        boolean hasMore = true;
        while (hasMore) {
            QueryDyParam param = new QueryDyParam();
            param.setData_type(3);
            param.setStart_time(sdf.format(dayStart));
            param.setEnd_time(sdf.format(dayEnd));
            param.setPage(page);
            param.setSize(size);
            try {
                DyResVo res = dataokeService.queryDyList(param);
                if (res != null && res.getData() != null && res.getData().getList() != null
                        && !res.getData().getList().isEmpty()) {
                    ArrayList<MailvorDyOrder> orders = res.getData().getList();
                    for (MailvorDyOrder order : orders) {
                        if (order.getOrderId() == null) continue;
                        platformOrders.put(order.getOrderId(), new PlatformOrderInfo(
                                order.getOrderId(),
                                toBD(order.getTotalPayAmount()),
                                toBD(order.getEstimatedTotalCommission())
                        ));
                    }
                    hasMore = res.getData().getTotal() > (long) page * size;
                    page++;
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("DY对账API调用失败: {}", e.getMessage());
                hasMore = false;
            }
        }

        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MailvorDyOrder::getPaySuccessTime, dayStart);
        wrapper.lt(MailvorDyOrder::getPaySuccessTime, dayEnd);
        List<MailvorDyOrder> localOrders = dyOrderMapper.selectList(wrapper);

        Map<String, LocalOrderInfo> localMap = new LinkedHashMap<>();
        for (MailvorDyOrder order : localOrders) {
            localMap.put(order.getOrderId(),
                    new LocalOrderInfo(toBD(order.getTotalPayAmount()), toBD(order.getEstimatedTotalCommission())));
        }

        return compareAndCreateDiffsGeneric(logId, "DY", platformOrders, localMap);
    }

    // --- TB-specific comparison (uses TbOrderInfo for tradeParentId matching) ---

    private int[] compareAndCreateDiffs(Long logId, String platform,
                                         Map<String, TbOrderInfo> platformOrders,
                                         Map<String, MailvorTbOrder> localMap) {
        int matchCount = 0, missingCount = 0, extraCount = 0, amountDiffCount = 0;
        Date now = new Date();

        Set<String> allKeys = new LinkedHashSet<>();
        allKeys.addAll(platformOrders.keySet());
        allKeys.addAll(localMap.keySet());

        for (String key : allKeys) {
            TbOrderInfo pOrder = platformOrders.get(key);
            MailvorTbOrder lOrder = localMap.get(key);

            if (pOrder != null && lOrder == null) {
                missingCount++;
                saveDiff(logId, platform, 1, key, pOrder.amount, null, pOrder.commission, null, now);
            } else if (pOrder == null) {
                extraCount++;
                BigDecimal localAmount = toBD(lOrder.getAlipayTotalPrice());
                BigDecimal localCommission = toBD(lOrder.getTotalCommissionFee());
                saveDiff(logId, platform, 2, key, null, localAmount, null, localCommission, now);
            } else {
                boolean amountMatch = amountEquals(pOrder.amount, toBD(lOrder.getAlipayTotalPrice()));
                boolean commissionMatch = amountEquals(pOrder.commission, toBD(lOrder.getTotalCommissionFee()));
                if (amountMatch && commissionMatch) {
                    matchCount++;
                } else {
                    amountDiffCount++;
                    saveDiff(logId, platform, 3, key,
                            pOrder.amount, toBD(lOrder.getAlipayTotalPrice()),
                            pOrder.commission, toBD(lOrder.getTotalCommissionFee()), now);
                }
            }
        }

        return new int[]{platformOrders.size(), localMap.size(), matchCount, missingCount, extraCount, amountDiffCount};
    }

    // --- Generic comparison for JD/PDD/DY ---

    private int[] compareAndCreateDiffsGeneric(Long logId, String platform,
                                                Map<String, PlatformOrderInfo> platformOrders,
                                                Map<String, LocalOrderInfo> localMap) {
        int matchCount = 0, missingCount = 0, extraCount = 0, amountDiffCount = 0;
        Date now = new Date();

        Set<String> allKeys = new LinkedHashSet<>();
        allKeys.addAll(platformOrders.keySet());
        allKeys.addAll(localMap.keySet());

        for (String key : allKeys) {
            PlatformOrderInfo pOrder = platformOrders.get(key);
            LocalOrderInfo lOrder = localMap.get(key);

            if (pOrder != null && lOrder == null) {
                missingCount++;
                saveDiff(logId, platform, 1, key, pOrder.amount, null, pOrder.commission, null, now);
            } else if (pOrder == null) {
                extraCount++;
                saveDiff(logId, platform, 2, key, null, lOrder.amount, null, lOrder.commission, now);
            } else {
                boolean amountMatch = amountEquals(pOrder.amount, lOrder.amount);
                boolean commissionMatch = amountEquals(pOrder.commission, lOrder.commission);
                if (amountMatch && commissionMatch) {
                    matchCount++;
                } else {
                    amountDiffCount++;
                    saveDiff(logId, platform, 3, key,
                            pOrder.amount, lOrder.amount,
                            pOrder.commission, lOrder.commission, now);
                }
            }
        }

        return new int[]{platformOrders.size(), localMap.size(), matchCount, missingCount, extraCount, amountDiffCount};
    }

    private void saveDiff(Long logId, String platform, int diffType, String orderNo,
                          BigDecimal platformAmount, BigDecimal localAmount,
                          BigDecimal platformCommission, BigDecimal localCommission, Date now) {
        MwReconcileDiff diff = MwReconcileDiff.builder()
                .logId(logId)
                .platform(platform)
                .diffType(diffType)
                .orderNo(orderNo)
                .platformAmount(platformAmount)
                .localAmount(localAmount)
                .platformCommission(platformCommission)
                .localCommission(localCommission)
                .handleStatus(0)
                .createTime(now)
                .build();
        diffMapper.insert(diff);
    }

    private boolean amountEquals(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.subtract(b).abs().compareTo(AMOUNT_TOLERANCE) <= 0;
    }

    private BigDecimal toBD(Double val) {
        return val != null ? BigDecimal.valueOf(val) : BigDecimal.ZERO;
    }

    private BigDecimal fenToYuan(Long fen) {
        if (fen == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(fen).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean existsReconcileForDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        try {
            Date normalizedDate = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(normalizedDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date nextDay = cal.getTime();

            LambdaQueryWrapper<MwReconcileLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(MwReconcileLog::getReconcileDate, normalizedDate);
            wrapper.lt(MwReconcileLog::getReconcileDate, nextDay);
            return baseMapper.selectCount(wrapper) > 0;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public List<MwReconcileDiff> exportDiffs(Long logId) {
        LambdaQueryWrapper<MwReconcileDiff> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwReconcileDiff::getLogId, logId);
        wrapper.orderByAsc(MwReconcileDiff::getDiffType).orderByAsc(MwReconcileDiff::getCreateTime);
        return diffMapper.selectList(wrapper);
    }

    // --- Inner classes for order info ---

    private static class TbOrderInfo {
        final String orderNo;
        final BigDecimal amount;
        final BigDecimal commission;
        TbOrderInfo(String orderNo, BigDecimal amount, BigDecimal commission) {
            this.orderNo = orderNo;
            this.amount = amount;
            this.commission = commission;
        }
    }

    private static class PlatformOrderInfo {
        final String orderNo;
        final BigDecimal amount;
        final BigDecimal commission;
        PlatformOrderInfo(String orderNo, BigDecimal amount, BigDecimal commission) {
            this.orderNo = orderNo;
            this.amount = amount;
            this.commission = commission;
        }
    }

    private static class LocalOrderInfo {
        final BigDecimal amount;
        final BigDecimal commission;
        LocalOrderInfo(BigDecimal amount, BigDecimal commission) {
            this.amount = amount;
            this.commission = commission;
        }
    }

    // --- Query wrapper builders ---

    private LambdaQueryWrapper<MwReconcileLog> buildLogWrapper(Map<String, Object> criteria) {
        LambdaQueryWrapper<MwReconcileLog> wrapper = new LambdaQueryWrapper<>();
        if (criteria.get("platform") != null && !criteria.get("platform").toString().isEmpty()) {
            wrapper.eq(MwReconcileLog::getPlatform, criteria.get("platform"));
        }
        if (criteria.get("status") != null) {
            wrapper.eq(MwReconcileLog::getStatus, Integer.parseInt(criteria.get("status").toString()));
        }
        if (criteria.get("reconcileDate") != null) {
            wrapper.eq(MwReconcileLog::getReconcileDate, criteria.get("reconcileDate"));
        }
        return wrapper;
    }

    private LambdaQueryWrapper<MwReconcileDiff> buildDiffWrapper(Map<String, Object> criteria) {
        LambdaQueryWrapper<MwReconcileDiff> wrapper = new LambdaQueryWrapper<>();
        if (criteria.get("logId") != null) {
            wrapper.eq(MwReconcileDiff::getLogId, Long.parseLong(criteria.get("logId").toString()));
        }
        if (criteria.get("platform") != null && !criteria.get("platform").toString().isEmpty()) {
            wrapper.eq(MwReconcileDiff::getPlatform, criteria.get("platform"));
        }
        if (criteria.get("diffType") != null) {
            wrapper.eq(MwReconcileDiff::getDiffType, Integer.parseInt(criteria.get("diffType").toString()));
        }
        if (criteria.get("handleStatus") != null) {
            wrapper.eq(MwReconcileDiff::getHandleStatus, Integer.parseInt(criteria.get("handleStatus").toString()));
        }
        return wrapper;
    }
}
