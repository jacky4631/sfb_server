/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.CommonEnum;
import com.mailvor.modules.order.service.dto.ChartDataDto;
import com.mailvor.modules.tk.domain.MailvorVipOrder;
import com.mailvor.modules.tk.service.MailvorVipOrderService;
import com.mailvor.modules.tk.service.dto.MailvorVipOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorVipOrderQueryCriteria;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import com.mailvor.modules.tk.service.mapper.MailvorVipOrderMapper;
import com.mailvor.modules.tools.utils.CashUtils;
import com.mailvor.modules.user.service.dto.VipOrderDetailDto;
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

import static com.mailvor.utils.OrderUtil.VIP_NOT_VALID_ORDER_STATUS;

/**
* @author wangjun
* @date 2022-09-07
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mailvorVipOrder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MailvorVipOrderServiceImpl extends BaseServiceImpl<MailvorVipOrderMapper, MailvorVipOrder> implements MailvorVipOrderService {

    private final IGenerator generator;
    @Autowired
    private MailvorVipOrderMapper orderMapper;

    @Override
    //@Cacheable
    public PageResult<MailvorVipOrderDto> queryAll(MailvorVipOrderQueryCriteria criteria, Pageable pageable) {
        return queryAll(criteria, pageable, 0);

    }
    @Override
    public PageResult<MailvorVipOrderDto> queryAll(MailvorVipOrderQueryCriteria criteria, Pageable pageable, Integer unlockDay) {
        getPage(pageable);
        PageInfo<MailvorVipOrder> page = new PageInfo<>(queryAll(criteria));
        PageResult<MailvorVipOrderDto> results = generator.convertPageInfo(page,MailvorVipOrderDto.class);
        results.getContent().stream().forEach(orderDto -> {
            if(VIP_NOT_VALID_ORDER_STATUS.equals(orderDto.getOrderSubStatusName())
                    || Double.parseDouble(orderDto.getTotalCost()) <= 0) {
                orderDto.setRemain("err");
            } else {
                Date createTime = orderDto.getOrderTime();
                orderDto.setRemain(CashUtils.getRemainDate(unlockDay, createTime.getTime()/1000,
                        Double.parseDouble(orderDto.getCommission()), orderDto.getInnerType()));
            }
        });

        return results;
    }


    @Override
    //@Cacheable
    public List<MailvorVipOrder> queryAll(MailvorVipOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MailvorVipOrder.class, criteria));
    }


    @Override
    public void download(List<MailvorVipOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MailvorVipOrderDto mailvorVipOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单状态", mailvorVipOrder.getOrderSubStatusName());
            map.put("下单时间", mailvorVipOrder.getOrderTime());
            map.put("自定义统计参数", mailvorVipOrder.getStatParam());
            map.put("订单支付金额:单位元", mailvorVipOrder.getTotalCost());
            map.put("appKey", mailvorVipOrder.getAppKey());
            map.put("渠道商模式下表示自定义渠道标识；工具商模式下表示pid", mailvorVipOrder.getChannelTag());
            map.put("商品佣金金额(元,保留两位小数)", mailvorVipOrder.getCommission());
            map.put("入账时间，时间戳，单位毫秒", mailvorVipOrder.getCommissionEnterTime());
            map.put("是否预付订单:0-否，1-是", mailvorVipOrder.getIsPrepay());
            map.put("订单拆单标识: 0-否，1-是", mailvorVipOrder.getIsSplit());
            map.put("订单上次更新时间 时间戳 单位毫秒", mailvorVipOrder.getLastUpdateTime());
            map.put("新老客：0-待定，1-新客，2-老客", mailvorVipOrder.getNewCustomer());
            map.put("订单来源", mailvorVipOrder.getOrderSource());
            map.put("订单归因方式：0-常规推广,1-惊喜红包,2-锁粉,3-超级红包", mailvorVipOrder.getOrderTrackReason());
            map.put("推广PID:目前等同于channelTag", mailvorVipOrder.getPid());
            map.put("是否自推自买 0-否，1-是", mailvorVipOrder.getSelfBuy());
            map.put("订单结算状态 0-未结算,1-已结算", mailvorVipOrder.getSettled());
            map.put("订单状态:0-不合格，1-待定，2-已完结", mailvorVipOrder.getStatus());
            map.put("商品json array信息", mailvorVipOrder.getDetailList());
            map.put("是否绑定用户，0 未绑定 1 绑定", mailvorVipOrder.getBind());
            map.put("用户ID", mailvorVipOrder.getUid());
            map.put("奖励积分", mailvorVipOrder.getIntegral());
            map.put("是否已提现，0 未提现 1 已提现", mailvorVipOrder.getCash());
            map.put("创建时间", mailvorVipOrder.getCreateTime());
            map.put("修改时间", mailvorVipOrder.getUpdateTime());
            map.put("删除标识", mailvorVipOrder.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<MailvorVipOrder> getUnbindOrderList(Long prior) {
        LambdaQueryWrapper<MailvorVipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorVipOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.ne(MailvorVipOrder::getOrderSubStatusName, VIP_NOT_VALID_ORDER_STATUS);
        wrapper.isNull(MailvorVipOrder::getUid);
        LocalDateTime now = LocalDateTime.now().minusSeconds(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorVipOrder::getOrderTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.orderByDesc(MailvorVipOrder::getOrderTime);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public boolean hasUnlockOrder(Long uid, Integer innerType, Integer unlockDay) {
        LambdaQueryWrapper<MailvorVipOrder> wrapper = new LambdaQueryWrapper<>();
        if(innerType != null) {
            wrapper.eq(MailvorVipOrder::getInnerType, innerType);
        }
        wrapper.eq(MailvorVipOrder::getUid, uid);
        wrapper.eq(MailvorVipOrder::getBind, 0);
        wrapper.ne(MailvorVipOrder::getOrderSubStatusName, VIP_NOT_VALID_ORDER_STATUS);

        LocalDateTime now = LocalDateTime.now().minusDays(unlockDay);
        wrapper.le(MailvorVipOrder::getOrderTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        return orderMapper.selectCount(wrapper) > 0;
    }
    @Override
    public List<MailvorVipOrder> getRefundAndBindOrderList(Integer prior) {
        LambdaQueryWrapper<MailvorVipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorVipOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.eq(MailvorVipOrder::getOrderSubStatusName, VIP_NOT_VALID_ORDER_STATUS);
        wrapper.eq(MailvorVipOrder::getBind, 1);

        LocalDateTime now = LocalDateTime.now().minusDays(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorVipOrder::getOrderTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        return orderMapper.selectList(wrapper);
    }

    @Override
    public void bindUser(Long uid, String orderSn) {
        orderMapper.bindUser(uid, orderSn);
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
    public Set<MailvorVipOrder> selectCashOrderIds(Long uid, LocalDateTime time) {
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

    private LambdaQueryWrapper<MailvorVipOrder> getWrapper(List<Long> uid, Integer innerType, DateTime start, boolean skipInvalid) {
        return getWrapperPro(uid, innerType, start, null, skipInvalid);
    }

    private LambdaQueryWrapper<MailvorVipOrder> getWrapperPro(List<Long> uid, Integer innerType, Date start, Date end, boolean skipInvalid) {
        LambdaQueryWrapper<MailvorVipOrder> wrapperTwo = new LambdaQueryWrapper<>();
        if(uid != null) {
            wrapperTwo.in(MailvorVipOrder::getUid, uid);
        }
        if(innerType != null) {
            wrapperTwo.eq(MailvorVipOrder::getInnerType, innerType);
        }
        if(start != null) {
            wrapperTwo.ge(MailvorVipOrder::getOrderTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MailvorVipOrder::getOrderTime, end);
        }
        if(skipInvalid) {
            wrapperTwo.ne(MailvorVipOrder::getOrderSubStatusName, VIP_NOT_VALID_ORDER_STATUS);
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
    public MailvorVipOrder mockOrder(String orderId, Long uid, Date createTime, String shopName,
                                    Double rate, Double fee, Double payPrice, String img, String title,
                                    String itemId, Integer innerType) {
        ArrayList<JSONObject> detailDtos = new ArrayList<>(1);
        String feeStr = String.format("%.2f", fee);
        VipOrderDetailDto detailDto = VipOrderDetailDto.builder()
                .goodsName(title)
                .goodsThumb(img)
                .spuId(itemId)
                .sizeId(itemId)
                .status(2)
                .goodsId(itemId)
                .commCode("338")
                .commName("其他")
                .commission(feeStr)
                .goodsCount(1)
                .brandStoreName(shopName)
                .orderSource("app-android")
                .commissionRate(rate+"")
                .goodsFinalPrice(payPrice+"")
                .isSubsidyTaskOrder(false)
                .commissionTotalCost(payPrice + "")
                .build();
        detailDtos.add(JSON.parseObject(JSON.toJSONString(detailDto)));
        MailvorVipOrder order = MailvorVipOrder.builder()
                .orderSn(orderId)
                .orderSubStatusName("已付款")
                .orderTime(createTime)
                .statParam(4+"")
                .totalCost(payPrice + "")
                .appKey("0a0007bb")
                .channelTag("5cb48570ea42f03970c1c40a2b8e2146")
                .commission(feeStr)
                .isPrepay(0)
                .isSplit(0)
                .lastUpdateTime(createTime)
                .newCustomer(2)
                .orderSource("app-android")
                .orderTrackReason(0)
                .pid("5cb48570ea42f03970c1c40a2b8e2146")
                .selfBuy(0)
                .settled(0)
                .status(1)
                .detailList(detailDtos)
                .build();
        order.setBind(0);
        order.setUid(uid);
        order.setInnerType(innerType);
        return order;
    }

    @Override
    public void invalidRefundOrders(List<String> ids) {
        for(String id : ids) {
            orderMapper.invalidRefundOrders(id);
        }
    }
    @Override
    public Double totalFee(Integer type, List<Long> uid, Integer innerType){
        return totalFee(type, uid, innerType, false);
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
                return orderMapper.sumFee(getWrapperPro(uid, innerType, DateUtils.getProMonth(), DateUtils.getMonth(),skipInvalid));
            case 5:
                return orderMapper.sumFee(getWrapper(uid, innerType, DateUtils.get30Day(),skipInvalid));
            case 6:
                return orderMapper.sumFee(getWrapper(uid, innerType, DateUtils.get7Day(),skipInvalid));
        }
        return 0.0;
    }
    public Long totalCount(Integer type, List<Long> uid, Integer innerType){
        return totalCount(type, uid, innerType, false);
    }
    @Override
    public Long totalCount(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid){
        switch (type){
            case 1:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.getToday(),skipInvalid));
            case 2:
                return orderMapper.selectCount(getWrapperPro(uid, innerType, DateUtils.getProDay(), DateUtils.getToday(),skipInvalid));
            case 3:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.getMonth(),skipInvalid));
            case 4:
                return orderMapper.selectCount(getWrapperPro(uid, innerType, DateUtils.getProMonth(), DateUtils.getMonth(),skipInvalid));
            case 5:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.get30Day(),skipInvalid));
            case 6:
                return orderMapper.selectCount(getWrapper(uid, innerType, DateUtils.get7Day(),skipInvalid));
        }
        return 0L;
    }
    @Override
    public Double totalPrice(Integer type, List<Long> uid, Integer innerType){
        return totalPrice(type, uid, innerType, false);
    }
    @Override
    public Double totalPrice(Integer type, List<Long> uid, Integer innerType, boolean skipInvalid){
        switch (type){
            case 1:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.getToday(),skipInvalid));
            case 2:
                return orderMapper.sumPrice(getWrapperPro(uid, innerType, DateUtils.getProDay(), DateUtils.getToday(),skipInvalid));
            case 3:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.getMonth(),skipInvalid));
            case 4:
                return orderMapper.sumPrice(getWrapperPro(uid, innerType, DateUtils.getProMonth(), DateUtils.getMonth(),skipInvalid));
            case 5:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.get30Day(),skipInvalid));
            case 6:
                return orderMapper.sumPrice(getWrapper(uid, innerType, DateUtils.get7Day(),skipInvalid));
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

    private LambdaQueryWrapper<MailvorVipOrder> getHbWrapper(Date start, Date end) {
        LambdaQueryWrapper<MailvorVipOrder> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(MailvorVipOrder::getInnerType, 0);

        if(start != null) {
            wrapper.ge(MailvorVipOrder::getOrderTime, start);
        }
        if(end != null) {
            wrapper.lt(MailvorVipOrder::getOrderTime, end);
        }

        return wrapper;
    }

    @Override
    public List<MailvorVipOrder> getSelfUnspreadHbList(Integer limit) {
        LambdaQueryWrapper<MailvorVipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorVipOrder::getBind, 0);
        wrapper.isNotNull(MailvorVipOrder::getUid);
        wrapper.eq(MailvorVipOrder::getInnerType, 0);

        wrapper.ne(MailvorVipOrder::getOrderSubStatusName, VIP_NOT_VALID_ORDER_STATUS);

//        LocalDateTime now = LocalDateTime.now().minusDays(day);
//        wrapper.le(MailvorVipOrder::getOrderTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.last("limit " + limit);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Long getSpreadCountToday(Long uid) {
        Date now = new Date();
        LambdaQueryWrapper<MailvorVipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorVipOrder::getUid, uid);
        wrapper.eq(MailvorVipOrder::getInnerType, 0);
        wrapper.le(MailvorVipOrder::getSpreadHbTime, DateUtil.endOfDay(now));
        wrapper.ge(MailvorVipOrder::getSpreadHbTime, DateUtil.beginOfDay(now));

        return orderMapper.selectCount(wrapper);
    }    /**
     *
     *
     * @param prior 往前偏移多少天
     * @param scale 佣金比例
     * @return the order check dto
     */
    @Override
    public List<OrderCheckDTO> checkCount(Integer prior, Double scale) {
        LambdaQueryWrapper<MailvorVipOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(MailvorVipOrder::getUid);
        wrapper.eq(MailvorVipOrder::getInnerType, 0);
        wrapper.ne(MailvorVipOrder::getOrderSubStatusName, VIP_NOT_VALID_ORDER_STATUS);
        wrapper.gt(MailvorVipOrder::getCommissionRate, scale);

        Date yesterdayStart = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -prior));
        wrapper.ge(MailvorVipOrder::getOrderTime, yesterdayStart);
        wrapper.groupBy(MailvorVipOrder::getUid);
        return orderMapper.checkCount(wrapper);
    }
}
