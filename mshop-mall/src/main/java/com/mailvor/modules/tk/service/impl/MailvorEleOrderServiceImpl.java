/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.CommonEnum;
import com.mailvor.modules.order.service.dto.ChartDataDto;
import com.mailvor.modules.tk.domain.MailvorEleOrder;
import com.mailvor.modules.tk.service.MailvorEleOrderService;
import com.mailvor.modules.tk.service.dto.MailvorEleOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorEleOrderQueryCriteria;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import com.mailvor.modules.tk.service.mapper.MailvorEleOrderMapper;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.mailvor.utils.OrderUtil.ELE_NOT_VALID_ORDER_STATUS;

/**
* @author shenji
* @date 2022-09-07
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "MailvorEleOrder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MailvorEleOrderServiceImpl extends BaseServiceImpl<MailvorEleOrderMapper, MailvorEleOrder> implements MailvorEleOrderService {

    private final IGenerator generator;
    @Autowired
    private MailvorEleOrderMapper orderMapper;

    @Override
    public PageResult<MailvorEleOrderDto> queryAll(MailvorEleOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MailvorEleOrder> page = new PageInfo<>(queryAll(criteria));
        PageResult<MailvorEleOrderDto> results = generator.convertPageInfo(page,MailvorEleOrderDto.class);
        results.getContent().stream().forEach(orderDto -> {
            if(ELE_NOT_VALID_ORDER_STATUS.equals(orderDto.getOrderStatus())
                    || orderDto.getPayPrice() <= 0) {
                orderDto.setRemain("err");
            } else {
                orderDto.setRemain("ok");
            }
        });

        return results;
    }



    @Override
    //@Cacheable
    public List<MailvorEleOrder> queryAll(MailvorEleOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MailvorEleOrder.class, criteria));
    }


    @Override
    public void download(List<MailvorEleOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MailvorEleOrderDto MailvorEleOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
//            map.put("订单金额", MailvorEleOrder.getTotalPayAmount());
//            map.put("预估佣金", MailvorEleOrder.getEstimatedTotalCommission());
//            map.put("下单时间", MailvorEleOrder.getPaidTime());
//            map.put("商品标题", MailvorEleOrder.getProductName());
//            map.put("商品id", MailvorEleOrder.getProductId());
//            map.put("佣金比例", MailvorEleOrder.getCommissionRate());
//            map.put("商品主图", MailvorEleOrder.getProductImg());
//            map.put("订单状态，PAY_SUCC", MailvorEleOrder.getFlowPoint());
//            map.put("退款时间", MailvorEleOrder.getRefundTime());
//            map.put("结算时间", MailvorEleOrder.getSettleTime());
//            map.put("商品数量", MailvorEleOrder.getItemNum());
//            map.put("店铺名称", MailvorEleOrder.getShopName());
//            map.put("店铺id", MailvorEleOrder.getShopId());
//            map.put("实际结算金额", MailvorEleOrder.getRealCommission());
//            map.put("媒体名称", MailvorEleOrder.getMediaTypeName());
//            map.put("媒体类型", MailvorEleOrder.getMediaType());
//            map.put(" estimatedCommission",  MailvorEleOrder.getEstimatedCommission());
//            map.put(" estimatedTechServiceFee",  MailvorEleOrder.getEstimatedTechServiceFee());
//            map.put(" estimatedTotalCommission0",  MailvorEleOrder.getEstimatedTotalCommission0());
//            map.put(" app",  MailvorEleOrder.getApp());
            map.put("是否绑定用户，0 未绑定 1 绑定", MailvorEleOrder.getBind());
            map.put("用户ID", MailvorEleOrder.getUid());
            map.put("奖励积分", MailvorEleOrder.getIntegral());
            map.put("是否已提现，0 未提现 1 已提现", MailvorEleOrder.getCash());
            map.put("创建时间", MailvorEleOrder.getCreateTime());
            map.put("修改时间", MailvorEleOrder.getUpdateTime());
            map.put("删除标识", MailvorEleOrder.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<MailvorEleOrder> getUnbindOrderList(Integer prior) {
        //抖音订单状态 PAY_SUCC CONFIRM REFUND
        LambdaQueryWrapper<MailvorEleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorEleOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.ne(MailvorEleOrder::getOrderStatus, ELE_NOT_VALID_ORDER_STATUS);
        wrapper.isNull(MailvorEleOrder::getUid);

        LocalDateTime now = LocalDateTime.now().minusSeconds(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorEleOrder::getPaidTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.orderByDesc(MailvorEleOrder::getPaidTime);
        return orderMapper.selectList(wrapper);
    }
    @Override
    public boolean hasUnlockOrder(Long uid, Integer innerType) {
        //抖音订单状态 PAY_SUCC CONFIRM REFUND
        LambdaQueryWrapper<MailvorEleOrder> wrapper = new LambdaQueryWrapper<>();
        if(innerType != null) {
            wrapper.eq(MailvorEleOrder::getInnerType, innerType);
        }
        wrapper.eq(MailvorEleOrder::getUid, uid);
        wrapper.eq(MailvorEleOrder::getBind, 0);
        wrapper.ne(MailvorEleOrder::getOrderStatus, ELE_NOT_VALID_ORDER_STATUS);

        return orderMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Double totalFee(Integer type, List<Long> uid, Integer innerType) {
        return totalFee(type, uid, innerType, false);
    }

    @Override
    public List<MailvorEleOrder> getRefundAndBindOrderList(Integer prior) {
        LambdaQueryWrapper<MailvorEleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorEleOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.eq(MailvorEleOrder::getOrderStatus, ELE_NOT_VALID_ORDER_STATUS);
        wrapper.eq(MailvorEleOrder::getBind, 1);

        LocalDateTime now = LocalDateTime.now().minusDays(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorEleOrder::getPaidTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        return orderMapper.selectList(wrapper);
    }



    @Override
    public void bindUser(Long uid, String orderId) {
        orderMapper.bindUser(uid, orderId);
    }
    @Override
    public void unbindUser(String id) {
        orderMapper.unbindUser(id);
    }
    @Override
    public Double totalCash(Long uid, LocalDateTime time) {
        //付款满七天的订单可以提现
        return orderMapper.totalCash(uid, Date.from(time.atZone( ZoneId.systemDefault()).toInstant()))/100;
    }

    @Override
    public Set<MailvorEleOrder> selectCashOrderIds(Long uid, LocalDateTime time) {
        return orderMapper.selectCashOrderId(uid, Date.from(time.atZone( ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public void updateCash(Long uid, LocalDateTime time) {
        //付款满七天的订单可以提现
        orderMapper.updateCash(uid, Date.from(time.atZone( ZoneId.systemDefault()).toInstant()));
    }
    @Override
    public Double sumTotalPrice(){
        return orderMapper.sumTotalPrice();
    }

    @Override
    public Double sumTotalFee(){
        return orderMapper.sumTotalFee();
    }

    private LambdaQueryWrapper<MailvorEleOrder> getWrapper(List<Long> uid, Integer innerType, DateTime start, boolean skipInvalid) {
        return getWrapperPro(uid, innerType, start, null, skipInvalid);
    }

    private LambdaQueryWrapper<MailvorEleOrder> getWrapperPro(List<Long> uid, Integer innerType, Date start, Date end, boolean skipInvalid) {
        LambdaQueryWrapper<MailvorEleOrder> wrapperTwo = new LambdaQueryWrapper<>();
        if(uid != null) {
            wrapperTwo.in(MailvorEleOrder::getUid, uid);
        }
        if(innerType != null) {
            wrapperTwo.eq(MailvorEleOrder::getInnerType, innerType);
        }
        if(start != null) {
            wrapperTwo.ge(MailvorEleOrder::getPaidTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MailvorEleOrder::getPaidTime, end);
        }
        if(skipInvalid) {
            wrapperTwo.ne(MailvorEleOrder::getOrderStatus, ELE_NOT_VALID_ORDER_STATUS);
        }
        return wrapperTwo;
    }

    @Override
    public List<ChartDataDto> chartList(Date time){
        return orderMapper.chartList(time);
    }
    @Override
    public List<ChartDataDto> chartListT(Date time){
        return orderMapper.chartListT(time);
    }

    @Override
    public List<ChartDataDto> chartListFee(Date time){
        return orderMapper.chartListFee(time);
    }


    @Override
    public MailvorEleOrder mockOrder(String orderId, String productId, Long uid, Date createTime, String shopName,
                                    Double rate, Double fee, Double payPrice, String img, String title, Integer innerType) {
        MailvorEleOrder order = MailvorEleOrder.builder()
                .tradeId(orderId)
//                .totalPayAmount(payPrice)
//                .estimatedTotalCommission(fee)
//                .paySuccessTime(createTime)
//                .productName(title)
//                .productId(productId)
//                .commissionRate(rate)
//                .productImg(img)
//                .flowPoint("PAY_SUCC")
//                .itemNum(1)
//                .shopName(shopName)
//                .realCommission(0.0)
//                .mediaTypeName("ProductDetail")
//                .estimatedCommission(fee)
//                .estimatedTechServiceFee(0.0)
//                .estimatedTotalCommission0(fee)
//                .app("抖音")
                .build();
        order.setBind(0);
        order.setUid(uid);
        order.setInnerType(innerType);
        return order;
    }

    @Override
    public void invalidRefundOrders(List<String> ids) {
        for(String id: ids) {
            orderMapper.invalidRefundOrders(id);
        }

    }

    @Override
    public Double totalFee(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid){
        switch (type){
            case 1:
                return orderMapper.sumFee(getWrapper(uid, innerType, DateUtils.getToday(), skipInvalid));
            case 2:
                return orderMapper.sumFee(getWrapperPro(uid, innerType, DateUtils.getProDay(), DateUtils.getToday(), skipInvalid));
            case 3:
                return orderMapper.sumFee(getWrapper(uid, innerType, DateUtils.getMonth(), skipInvalid));
            case 4:
                return orderMapper.sumFee(getWrapperPro(uid, innerType, DateUtils.getProMonth(), DateUtils.getMonth(), skipInvalid));
            case 5:
                return orderMapper.sumFee(getWrapper(uid, innerType, DateUtils.get30Day(), skipInvalid));
            case 6:
                return orderMapper.sumFee(getWrapper(uid, innerType, DateUtils.get7Day(), skipInvalid));
        }
        return 0.0;
    }

    @Override
    public Long totalCount(Integer type, List<Long> uid, Integer innerType) {
        return totalCount(type, uid, innerType, false);
    }

    public Long totalCount(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid){
        switch (type){
            case 1:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.getToday(), skipInvalid));
            case 2:
                return orderMapper.selectCount(getWrapperPro(uid, innerType, DateUtils.getProDay(), DateUtils.getToday(), skipInvalid));
            case 3:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.getMonth(), skipInvalid));
            case 4:
                return orderMapper.selectCount(getWrapperPro(uid, innerType, DateUtils.getProMonth(), DateUtils.getMonth(), skipInvalid));
            case 5:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.get30Day(), skipInvalid));
            case 6:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.get7Day(), skipInvalid));
        }
        return 0L;
    }

    @Override
    public Double totalPrice(Integer type, List<Long> uid, Integer innerType) {
        return totalPrice(type, uid, innerType, false);
    }

    public Double totalPrice(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid){
        switch (type){
            case 1:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.getToday(), skipInvalid));
            case 2:
                return orderMapper.sumPrice(getWrapperPro(uid, innerType, DateUtils.getProDay(), DateUtils.getToday(), skipInvalid));
            case 3:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.getMonth(), skipInvalid));
            case 4:
                return orderMapper.sumPrice(getWrapperPro(uid, innerType, DateUtils.getProMonth(), DateUtils.getMonth(), skipInvalid));
            case 5:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.get30Day(), skipInvalid));
            case 6:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.get7Day(), skipInvalid));
        }
        return 0.0;
    }


    @Override
    public Double totalHb(Integer type){
        switch (type){
            case 1:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getToday(), null));
            case 2:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getProDay(), DateUtils.getToday()));
            case 3:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getMonth(), null));
            case 4:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getProMonth(), DateUtils.getMonth()));
            case 5:
                return orderMapper.sumHb(getHbWrapper(DateUtils.get30Day(), null));
            case 6:
                return orderMapper.sumHb(getHbWrapper(DateUtils.get7Day(), null));
        }
        return 0.0;
    }

    private LambdaQueryWrapper<MailvorEleOrder> getHbWrapper(Date start, Date end) {
        LambdaQueryWrapper<MailvorEleOrder> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(MailvorEleOrder::getInnerType, 0);
        if(start != null) {
            wrapper.ge(MailvorEleOrder::getPaidTime, start);
        }
        if(end != null) {
            wrapper.lt(MailvorEleOrder::getPaidTime, end);
        }
        return wrapper;
    }

    @Override
    public List<MailvorEleOrder> getSelfUnspreadHbList(Integer day, Integer limit) {
        LambdaQueryWrapper<MailvorEleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorEleOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.eq(MailvorEleOrder::getBind, 0);
        wrapper.isNotNull(MailvorEleOrder::getUid);
        wrapper.eq(MailvorEleOrder::getInnerType, 0);
        wrapper.ne(MailvorEleOrder::getOrderStatus, ELE_NOT_VALID_ORDER_STATUS);

        LocalDateTime now = LocalDateTime.now().minusDays(day);
        wrapper.lt(MailvorEleOrder::getPaidTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.last("limit " + limit);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Long getSpreadCountToday(Long uid) {
        Date now = new Date();
        LambdaQueryWrapper<MailvorEleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorEleOrder::getUid, uid);
        wrapper.eq(MailvorEleOrder::getInnerType, 0);
        wrapper.le(MailvorEleOrder::getSpreadHbTime, DateUtil.endOfDay(now));
        wrapper.ge(MailvorEleOrder::getSpreadHbTime, DateUtil.beginOfDay(now));

        return orderMapper.selectCount(wrapper);
    }
    /**
     *
     *
     * @param prior 往前偏移多少天
     * @param scale 佣金比例
     * @return the order check dto
     */
    @Override
    public List<OrderCheckDTO> checkCount(Integer prior, Double scale) {
        LambdaQueryWrapper<MailvorEleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(MailvorEleOrder::getUid);
        wrapper.eq(MailvorEleOrder::getInnerType, 0);
        wrapper.ne(MailvorEleOrder::getOrderStatus, ELE_NOT_VALID_ORDER_STATUS);
//        wrapper.gt(MailvorEleOrder::getCommissionRate, scale);

        Date yesterdayStart = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -prior));
        wrapper.ge(MailvorEleOrder::getPaidTime, yesterdayStart);
        wrapper.groupBy(MailvorEleOrder::getUid);
        return orderMapper.checkCount(wrapper);
    }
}
