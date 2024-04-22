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
import com.mailvor.modules.tk.config.TbConfig;
import com.mailvor.modules.tk.domain.MailvorTbOrder;
import com.mailvor.modules.tk.service.MailvorTbOrderService;
import com.mailvor.modules.tk.service.dto.MailvorTbOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorTbOrderQueryCriteria;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import com.mailvor.modules.tk.service.mapper.MailvorTbOrderMapper;
import com.mailvor.modules.tools.utils.CashUtils;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.OrderUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


/**
* @author shenji
* @date 2022-08-29
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mailvorTbOrder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MailvorTbOrderServiceImpl extends BaseServiceImpl<MailvorTbOrderMapper, MailvorTbOrder> implements MailvorTbOrderService {

    private final IGenerator generator;

    @Autowired
    private MailvorTbOrderMapper orderMapper;

    @Resource
    private TbConfig tbConfig;

    @Override
    //@Cacheable
    public PageResult<MailvorTbOrderDto> queryAll(MailvorTbOrderQueryCriteria criteria, Pageable pageable) {
        return queryAll(criteria, pageable, 0);

    }

    @Override
    public PageResult<MailvorTbOrderDto> queryAll(MailvorTbOrderQueryCriteria criteria, Pageable pageable, Integer unlockDay) {
        getPage(pageable);
        PageInfo<MailvorTbOrder> page = new PageInfo<>(queryAll(criteria));
        PageResult<MailvorTbOrderDto> results = generator.convertPageInfo(page,MailvorTbOrderDto.class);
        results.getContent().stream().forEach(orderDto -> {
            //淘宝订单状态失效 维权 订单金额为0
            if(OrderUtil.TB_NOT_VALID_ORDER_STATUS.equals(orderDto.getTkStatus())
                    || orderDto.getRefundTag() == 1
                    || orderDto.getAlipayTotalPrice() <= 0) {
                orderDto.setRemain("err");
            } else {
                Date createTime = orderDto.getTkCreateTime();
                orderDto.setRemain(CashUtils.getRemainDate(unlockDay, createTime.getTime()/1000,
                        orderDto.getPubSharePreFee(), orderDto.getInnerType()));
                //设置 订单是否是淘礼金0元购订单，前端显示返1元
                orderDto.setIsTlj(orderDto.getAdzoneId().equals(tbConfig.getAdZoneId()));
            }
        });

        return results;
    }
    @Override
    //@Cacheable
    public List<MailvorTbOrder> queryAll(MailvorTbOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MailvorTbOrder.class, criteria));
    }


    @Override
    public void download(List<MailvorTbOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MailvorTbOrderDto mailvorTbOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单创建时间", mailvorTbOrder.getTkCreateTime());
            map.put("付款时间", mailvorTbOrder.getTbPaidTime());
            map.put("付款时间", mailvorTbOrder.getTkPaidTime());
            map.put("payPrice",  mailvorTbOrder.getPayPrice());
            map.put("pubShareFee",  mailvorTbOrder.getPubShareFee());
            map.put("子订单编号", mailvorTbOrder.getTradeId());
            map.put("pubShareRate",  mailvorTbOrder.getPubShareRate());
            map.put("维权标签，0 含义为非维权 1 含义为维权订单", mailvorTbOrder.getRefundTag());
            map.put("实际获得收益的比率", mailvorTbOrder.getTkTotalRate());
            map.put("付款预估收入", mailvorTbOrder.getPubSharePreFee());
            map.put("买家拍下付款的金额", mailvorTbOrder.getAlipayTotalPrice());
            map.put("佣金比率", mailvorTbOrder.getTotalCommissionRate());
            map.put("商品标题", mailvorTbOrder.getItemTitle());
            map.put("推广位名称", mailvorTbOrder.getAdzoneName());
            map.put("pid=mm_1_2_3中的“2”这段数字", mailvorTbOrder.getSiteId());
            map.put("pid=mm_1_2_3中的“3”这段数字", mailvorTbOrder.getAdzoneId());
            map.put("商品链接", mailvorTbOrder.getItemLink());
            map.put("商品单价", mailvorTbOrder.getItemPrice());
            map.put("订单所属平台类型，包括天猫、淘宝、聚划算等", mailvorTbOrder.getOrderType());
            map.put("店铺名", mailvorTbOrder.getSellerShopTitle());
            map.put("媒体名", mailvorTbOrder.getSiteName());
            map.put("1）买家超时未付款； 2）买家付款前，买家/卖家取消了订单；3）订单付款后发起售中退款成功；3：订单结算，12：订单付款， 13：订单失效，14：订单成功", mailvorTbOrder.getTkStatus());
            map.put("商品id", mailvorTbOrder.getItemId());
            map.put("佣金金额=结算金额＊佣金比率", mailvorTbOrder.getTotalCommissionFee());
            map.put("会员运营id", mailvorTbOrder.getSpecialId());
            map.put("渠道关系id", mailvorTbOrder.getRelationId());
            map.put("订单更新时间", mailvorTbOrder.getModifiedTime());
            map.put("是否绑定用户，0 未绑定 1 绑定", mailvorTbOrder.getBind());
            map.put("用户ID", mailvorTbOrder.getUid());
            map.put("积分", mailvorTbOrder.getIntegral());
            map.put("cash",  mailvorTbOrder.getCash());
            map.put("itemImg",  mailvorTbOrder.getItemImg());
            map.put("创建时间", mailvorTbOrder.getCreateTime());
            map.put("修改时间", mailvorTbOrder.getUpdateTime());
            map.put("删除标识", mailvorTbOrder.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public List<MailvorTbOrder> getUnbindOrderList(Integer prior) {
        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorTbOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.ne(MailvorTbOrder::getTkStatus, OrderUtil.TB_NOT_VALID_ORDER_STATUS);
        wrapper.isNull(MailvorTbOrder::getUid);

        LocalDateTime now = LocalDateTime.now().minusMinutes(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorTbOrder::getTkCreateTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.orderByDesc(MailvorTbOrder::getTkCreateTime);
        return orderMapper.selectList(wrapper);
    }
    @Override
    public boolean hasUnlockOrder(Long uid, Integer innerType, Integer unlockDay) {
        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorTbOrder::getUid, uid);
        if(innerType != null) {
            wrapper.eq(MailvorTbOrder::getInnerType, innerType);
        }
        wrapper.eq(MailvorTbOrder::getBind, 0);
        wrapper.ne(MailvorTbOrder::getTkStatus, OrderUtil.TB_NOT_VALID_ORDER_STATUS);
        wrapper.eq(MailvorTbOrder::getRefundTag, 0);

        LocalDateTime now = LocalDateTime.now().minusDays(unlockDay);
        wrapper.le(MailvorTbOrder::getTkCreateTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        return orderMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Double totalFee(Integer type, List<Long> uid, Integer innerType) {
        return totalFee(type, uid, innerType, false);
    }

    @Override
    public List<MailvorTbOrder> getRefundAndBindOrderList(Integer prior) {
        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorTbOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.and(i->i.eq(MailvorTbOrder::getTkStatus, OrderUtil.TB_NOT_VALID_ORDER_STATUS).or().eq(MailvorTbOrder::getRefundTag, 1));
        wrapper.eq(MailvorTbOrder::getBind, 1);

        LocalDateTime now = LocalDateTime.now().minusDays(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorTbOrder::getTkCreateTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        return orderMapper.selectList(wrapper);
    }

    @Override
    public void bindUser(Long uid, Long id) {
        orderMapper.bindUser(uid, id);
    }
    @Override
    public void refundOrder(Long id) {
        orderMapper.refundOrder(id);
    }
    @Override
    public Double totalCash(Long uid, LocalDateTime time) {
        //付款满七天的订单可以提现
        return orderMapper.totalCash(uid, Date.from(time.atZone( ZoneId.systemDefault()).toInstant()))/100;
    }

    @Override
    public Set<MailvorTbOrder> selectCashOrderIds(Long uid, LocalDateTime time) {
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

    private LambdaQueryWrapper<MailvorTbOrder> getWrapper(List<Long> uids, Integer innerType, DateTime start, boolean skipInvalid) {
        return getWrapperPro(uids, innerType, start, null, skipInvalid);
    }

    private LambdaQueryWrapper<MailvorTbOrder> getWrapperPro(List<Long> uids, Integer innerType, Date start, Date end, boolean skipInvalid) {
        LambdaQueryWrapper<MailvorTbOrder> wrapperTwo = new LambdaQueryWrapper<>();
        if(uids != null) {
            wrapperTwo.in(MailvorTbOrder::getUid, uids);
        }
        if(innerType != null) {
            wrapperTwo.eq(MailvorTbOrder::getInnerType, innerType);
        }
        if(start != null) {
            wrapperTwo.ge(MailvorTbOrder::getTkCreateTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MailvorTbOrder::getTkCreateTime, end);
        }
        if(skipInvalid) {
            wrapperTwo.ne(MailvorTbOrder::getTkStatus, OrderUtil.TB_NOT_VALID_ORDER_STATUS);
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
    public MailvorTbOrder mockOrder(Long orderId, Long uid, Date createTime, String shopName,
                                    Double rate, Double fee, Double payPrice,String img,String title,
                                    String link, String itemId, Integer innerType) {
        int orderTypeInt = DateUtils.randomInt(0, 4);
        //如果orderTypeInt==0 淘宝订单
        MailvorTbOrder order = MailvorTbOrder.builder()
                .tradeId(orderId.toString())
                .tradeParentId(orderId)
                .parentId(orderId)
                .tradeId(orderId.toString())
                .tkCreateTime(createTime)
                .tbPaidTime(createTime)
                .tkPaidTime(createTime)
                .refundTag(0)
                .tkStatus(12)
                .siteName("网站")
                .sellerShopTitle(shopName)
                .orderType(orderTypeInt == 0 ? "淘宝":"天猫")
                .adzoneId(147982044L)
                .siteId(39582271L)
                .adzoneName("宝1")
                .pubShareRate("100")
                .pubShareFee("0")
                .pubSharePreFee(fee)
                .totalCommissionFee(0d)
                .tkTotalRate(rate)
                .totalCommissionRate(rate)
                .alipayTotalPrice(payPrice)
                .itemPrice(payPrice)
                .itemImg(img)
                .itemTitle(title)
                .itemLink(link)
                .itemId(itemId)
                .build();
        order.setBind(0);
        order.setUid(uid);
        order.setInnerType(innerType);
        return order;
    }

    @Override
    public void invalidRefundOrders(List<Long> ids) {
        //todo 批量更新sql有问题，暂未解决，暂时单条更新
        for(Long id: ids) {
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

    private LambdaQueryWrapper<MailvorTbOrder> getHbWrapper(Date start, Date end, boolean isVip) {
        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        if(isVip) {
            wrapper.gt(MailvorTbOrder::getShopHb, 0);
        } else {
            wrapper.eq(MailvorTbOrder::getShopHb, 0);
        }

        wrapper.eq(MailvorTbOrder::getInnerType, 0);

        if(start != null) {
            wrapper.ge(MailvorTbOrder::getTkCreateTime, start);
        }
        if(end != null) {
            wrapper.lt(MailvorTbOrder::getTkCreateTime, end);
        }

        return wrapper;
    }

    @Override
    public List<MailvorTbOrder> getSelfUnspreadHbList(Integer limit) {
        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorTbOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.eq(MailvorTbOrder::getBind, 0);
        wrapper.isNotNull(MailvorTbOrder::getUid);
        wrapper.eq(MailvorTbOrder::getInnerType, 0);
        wrapper.ne(MailvorTbOrder::getTkStatus, OrderUtil.TB_NOT_VALID_ORDER_STATUS);
        wrapper.eq(MailvorTbOrder::getRefundTag, 0);
        wrapper.gt(MailvorTbOrder::getAlipayTotalPrice, 0);

//        LocalDateTime now = LocalDateTime.now().minusDays(day);
        //查找day之前的订单
//        wrapper.lt(MailvorTbOrder::getTkCreateTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.last("limit " + limit);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Long getSpreadCountToday(Long uid) {
        Date now = new Date();
        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorTbOrder::getUid, uid);
        wrapper.eq(MailvorTbOrder::getInnerType, 0);
        wrapper.le(MailvorTbOrder::getSpreadHbTime, DateUtil.endOfDay(now));
        wrapper.ge(MailvorTbOrder::getSpreadHbTime, DateUtil.beginOfDay(now));

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
        LambdaQueryWrapper<MailvorTbOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(MailvorTbOrder::getUid);
        wrapper.eq(MailvorTbOrder::getInnerType, 0);
        wrapper.ne(MailvorTbOrder::getTkStatus, OrderUtil.TB_NOT_VALID_ORDER_STATUS);
        wrapper.gt(MailvorTbOrder::getTotalCommissionRate, scale);

        Date yesterdayStart = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -prior));
        wrapper.ge(MailvorTbOrder::getTkCreateTime, yesterdayStart);
        wrapper.groupBy(MailvorTbOrder::getUid);
        return orderMapper.checkCount(wrapper);
    }
}
