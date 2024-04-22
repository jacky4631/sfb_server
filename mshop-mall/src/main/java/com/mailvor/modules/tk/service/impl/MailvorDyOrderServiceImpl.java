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
import com.mailvor.modules.tk.domain.MailvorDyOrder;
import com.mailvor.modules.tk.service.MailvorDyOrderService;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorDyOrderQueryCriteria;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import com.mailvor.modules.tk.service.mapper.MailvorDyOrderMapper;
import com.mailvor.modules.tools.utils.CashUtils;
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

import static com.mailvor.utils.OrderUtil.DY_NOT_VALID_ORDER_STATUS;

/**
* @author shenji
* @date 2022-09-07
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mailvorDyOrder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MailvorDyOrderServiceImpl extends BaseServiceImpl<MailvorDyOrderMapper, MailvorDyOrder> implements MailvorDyOrderService {

    private final IGenerator generator;
    @Autowired
    private MailvorDyOrderMapper orderMapper;

    @Override
    //@Cacheable
    public PageResult<MailvorDyOrderDto> queryAll(MailvorDyOrderQueryCriteria criteria, Pageable pageable) {
        return queryAll(criteria, pageable, 0);
    }

    @Override
    public PageResult<MailvorDyOrderDto> queryAll(MailvorDyOrderQueryCriteria criteria, Pageable pageable, Integer unlockDay) {
        getPage(pageable);
        PageInfo<MailvorDyOrder> page = new PageInfo<>(queryAll(criteria));
        PageResult<MailvorDyOrderDto> results = generator.convertPageInfo(page,MailvorDyOrderDto.class);
        results.getContent().stream().forEach(orderDto -> {
            if(DY_NOT_VALID_ORDER_STATUS.equals(orderDto.getFlowPoint())
                    || orderDto.getTotalPayAmount() <= 0) {
                orderDto.setRemain("err");
            } else {
                Date createTime = orderDto.getPaySuccessTime();
                orderDto.setRemain(CashUtils.getRemainDate(unlockDay, createTime.getTime()/1000,
                        orderDto.getEstimatedTotalCommission(), orderDto.getInnerType()));
            }
        });

        return results;
    }



    @Override
    //@Cacheable
    public List<MailvorDyOrder> queryAll(MailvorDyOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MailvorDyOrder.class, criteria));
    }


    @Override
    public void download(List<MailvorDyOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MailvorDyOrderDto mailvorDyOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单金额", mailvorDyOrder.getTotalPayAmount());
            map.put("预估佣金", mailvorDyOrder.getEstimatedTotalCommission());
            map.put("下单时间", mailvorDyOrder.getPaySuccessTime());
            map.put("商品标题", mailvorDyOrder.getProductName());
            map.put("商品id", mailvorDyOrder.getProductId());
            map.put("佣金比例", mailvorDyOrder.getCommissionRate());
            map.put("商品主图", mailvorDyOrder.getProductImg());
            map.put("订单状态，PAY_SUCC", mailvorDyOrder.getFlowPoint());
            map.put("退款时间", mailvorDyOrder.getRefundTime());
            map.put("结算时间", mailvorDyOrder.getSettleTime());
            map.put("商品数量", mailvorDyOrder.getItemNum());
            map.put("店铺名称", mailvorDyOrder.getShopName());
            map.put("店铺id", mailvorDyOrder.getShopId());
            map.put("实际结算金额", mailvorDyOrder.getRealCommission());
            map.put("媒体名称", mailvorDyOrder.getMediaTypeName());
            map.put("媒体类型", mailvorDyOrder.getMediaType());
            map.put(" estimatedCommission",  mailvorDyOrder.getEstimatedCommission());
            map.put(" estimatedTechServiceFee",  mailvorDyOrder.getEstimatedTechServiceFee());
            map.put(" estimatedTotalCommission0",  mailvorDyOrder.getEstimatedTotalCommission0());
            map.put(" app",  mailvorDyOrder.getApp());
            map.put("是否绑定用户，0 未绑定 1 绑定", mailvorDyOrder.getBind());
            map.put("用户ID", mailvorDyOrder.getUid());
            map.put("奖励积分", mailvorDyOrder.getIntegral());
            map.put("是否已提现，0 未提现 1 已提现", mailvorDyOrder.getCash());
            map.put("创建时间", mailvorDyOrder.getCreateTime());
            map.put("修改时间", mailvorDyOrder.getUpdateTime());
            map.put("删除标识", mailvorDyOrder.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<MailvorDyOrder> getUnbindOrderList(Integer prior) {
        //抖音订单状态 PAY_SUCC CONFIRM REFUND
        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorDyOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.ne(MailvorDyOrder::getFlowPoint, DY_NOT_VALID_ORDER_STATUS);
        wrapper.isNull(MailvorDyOrder::getUid);

        LocalDateTime now = LocalDateTime.now().minusSeconds(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorDyOrder::getPaySuccessTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.orderByDesc(MailvorDyOrder::getPaySuccessTime);
        return orderMapper.selectList(wrapper);
    }
    @Override
    public boolean hasUnlockOrder(Long uid, Integer innerType, Integer unlockDay) {
        //抖音订单状态 PAY_SUCC CONFIRM REFUND
        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        if(innerType != null) {
            wrapper.eq(MailvorDyOrder::getInnerType, innerType);
        }
        wrapper.eq(MailvorDyOrder::getUid, uid);
        wrapper.eq(MailvorDyOrder::getBind, 0);
        wrapper.ne(MailvorDyOrder::getFlowPoint, DY_NOT_VALID_ORDER_STATUS);

        LocalDateTime now = LocalDateTime.now().minusDays(unlockDay);
        wrapper.le(MailvorDyOrder::getPaySuccessTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        return orderMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Double totalFee(Integer type, List<Long> uid, Integer innerType) {
        return totalFee(type, uid, innerType, false);
    }

    @Override
    public List<MailvorDyOrder> getRefundAndBindOrderList(Integer prior) {
        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorDyOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.eq(MailvorDyOrder::getFlowPoint, DY_NOT_VALID_ORDER_STATUS);
        wrapper.eq(MailvorDyOrder::getBind, 1);

        LocalDateTime now = LocalDateTime.now().minusDays(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorDyOrder::getPaySuccessTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        return orderMapper.selectList(wrapper);
    }



    @Override
    public void bindUser(Long uid, String orderId) {
        orderMapper.bindUser(uid, orderId);
    }
    @Override
    public void refundOrder(String id) {
        orderMapper.refundOrder(id);
    }
    @Override
    public Double totalCash(Long uid, LocalDateTime time) {
        //付款满七天的订单可以提现
        return orderMapper.totalCash(uid, Date.from(time.atZone( ZoneId.systemDefault()).toInstant()))/100;
    }

    @Override
    public Set<MailvorDyOrder> selectCashOrderIds(Long uid, LocalDateTime time) {
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

    private LambdaQueryWrapper<MailvorDyOrder> getWrapper(List<Long> uid, Integer innerType, DateTime start, boolean skipInvalid) {
        return getWrapperPro(uid, innerType, start, null, skipInvalid);
    }

    private LambdaQueryWrapper<MailvorDyOrder> getWrapperPro(List<Long> uid, Integer innerType, Date start, Date end, boolean skipInvalid) {
        LambdaQueryWrapper<MailvorDyOrder> wrapperTwo = new LambdaQueryWrapper<>();
        if(uid != null) {
            wrapperTwo.in(MailvorDyOrder::getUid, uid);
        }
        if(innerType != null) {
            wrapperTwo.eq(MailvorDyOrder::getInnerType, innerType);
        }
        if(start != null) {
            wrapperTwo.ge(MailvorDyOrder::getPaySuccessTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MailvorDyOrder::getPaySuccessTime, end);
        }
        if(skipInvalid) {
            wrapperTwo.ne(MailvorDyOrder::getFlowPoint, DY_NOT_VALID_ORDER_STATUS);
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
    public MailvorDyOrder mockOrder(String orderId, String productId, Long uid, Date createTime, String shopName,
                                    Double rate, Double fee, Double payPrice, String img, String title, Integer innerType) {
        MailvorDyOrder order = MailvorDyOrder.builder()
                .orderId(orderId)
                .totalPayAmount(payPrice)
                .estimatedTotalCommission(fee)
                .paySuccessTime(createTime)
                .productName(title)
                .productId(productId)
                .commissionRate(rate)
                .productImg(img)
                .flowPoint("PAY_SUCC")
                .itemNum(1)
                .shopName(shopName)
                .realCommission(0.0)
                .mediaTypeName("ProductDetail")
                .estimatedCommission(fee)
                .estimatedTechServiceFee(0.0)
                .estimatedTotalCommission0(fee)
                .app("抖音")
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
    public Double totalHb(Integer type, boolean isVip){
        switch (type){
            case 1:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getToday(), null, isVip));
            case 2:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getProDay(), DateUtils.getToday(), isVip));
            case 3:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getMonth(), null, isVip));
            case 4:
                return orderMapper.sumHb(getHbWrapper(DateUtils.getProMonth(), DateUtils.getMonth(), isVip));
            case 5:
                return orderMapper.sumHb(getHbWrapper(DateUtils.get30Day(), null, isVip));
            case 6:
                return orderMapper.sumHb(getHbWrapper(DateUtils.get7Day(), null, isVip));
        }
        return 0.0;
    }

    private LambdaQueryWrapper<MailvorDyOrder> getHbWrapper(Date start, Date end, boolean isVip) {
        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        if(isVip) {
            wrapper.gt(MailvorDyOrder::getShopHb, 0);
        } else {
            wrapper.eq(MailvorDyOrder::getShopHb, 0);
        }

        wrapper.eq(MailvorDyOrder::getInnerType, 0);
        if(start != null) {
            wrapper.ge(MailvorDyOrder::getPaySuccessTime, start);
        }
        if(end != null) {
            wrapper.lt(MailvorDyOrder::getPaySuccessTime, end);
        }
        return wrapper;
    }

    @Override
    public List<MailvorDyOrder> getSelfUnspreadHbList(Integer limit) {
        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorDyOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.eq(MailvorDyOrder::getBind, 0);
        wrapper.isNotNull(MailvorDyOrder::getUid);
        wrapper.eq(MailvorDyOrder::getInnerType, 0);
        wrapper.ne(MailvorDyOrder::getFlowPoint, DY_NOT_VALID_ORDER_STATUS);

//        LocalDateTime now = LocalDateTime.now().minusDays(day);
//        wrapper.lt(MailvorDyOrder::getPaySuccessTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.last("limit " + limit);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Long getSpreadCountToday(Long uid) {
        Date now = new Date();
        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorDyOrder::getUid, uid);
        wrapper.eq(MailvorDyOrder::getInnerType, 0);
        wrapper.le(MailvorDyOrder::getSpreadHbTime, DateUtil.endOfDay(now));
        wrapper.ge(MailvorDyOrder::getSpreadHbTime, DateUtil.beginOfDay(now));

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
        LambdaQueryWrapper<MailvorDyOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(MailvorDyOrder::getUid);
        wrapper.eq(MailvorDyOrder::getInnerType, 0);
        wrapper.ne(MailvorDyOrder::getFlowPoint, DY_NOT_VALID_ORDER_STATUS);
        wrapper.gt(MailvorDyOrder::getCommissionRate, scale);

        Date yesterdayStart = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -prior));
        wrapper.ge(MailvorDyOrder::getPaySuccessTime, yesterdayStart);
        wrapper.groupBy(MailvorDyOrder::getUid);
        return orderMapper.checkCount(wrapper);
    }
}
