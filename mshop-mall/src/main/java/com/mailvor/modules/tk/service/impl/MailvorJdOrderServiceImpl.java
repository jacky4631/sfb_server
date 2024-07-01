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
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import com.mailvor.modules.tk.service.MailvorJdOrderService;
import com.mailvor.modules.tk.service.dto.MailvorJdOrderDto;
import com.mailvor.modules.tk.service.dto.MailvorJdOrderQueryCriteria;
import com.mailvor.modules.tk.service.dto.OrderCheckDTO;
import com.mailvor.modules.tk.service.mapper.MailvorJdOrderMapper;
import com.mailvor.modules.user.service.dto.GoodsInfoDto;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.OrderUtil;
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

import static com.mailvor.utils.OrderUtil.JD_VALID_ORDER_STATUS;

/**
* @author shenji
* @date 2022-09-05
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mailvorJdOrder")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MailvorJdOrderServiceImpl extends BaseServiceImpl<MailvorJdOrderMapper, MailvorJdOrder> implements MailvorJdOrderService {

    private final IGenerator generator;

    @Autowired
    private MailvorJdOrderMapper orderMapper;

    @Override
    public PageResult<MailvorJdOrderDto> queryAll(MailvorJdOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MailvorJdOrder> page = new PageInfo<>(queryAll(criteria));
        PageResult<MailvorJdOrderDto> results = generator.convertPageInfo(page,MailvorJdOrderDto.class);
        results.getContent().stream().forEach(orderDto -> {
            if(!OrderUtil.JD_VALID_ORDER_STATUS.contains(orderDto.getValidCode())
                    || orderDto.getEstimateCosPrice()== null || orderDto.getEstimateCosPrice() <=0) {
                orderDto.setRemain("err");
            } else {
                orderDto.setRemain("ok");
            }
        });

        return results;
    }
    @Override
    //@Cacheable
    public List<MailvorJdOrder> queryAll(MailvorJdOrderQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MailvorJdOrder.class, criteria));
    }


    @Override
    public void download(List<MailvorJdOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MailvorJdOrderDto mailvorJdOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", mailvorJdOrder.getOrderId());
            map.put("父单的订单号", mailvorJdOrder.getParentId());
            map.put("下单时间", mailvorJdOrder.getOrderTime());
            map.put("完成时间", mailvorJdOrder.getFinishTime());
            map.put("更新时间", mailvorJdOrder.getModifyTime());
            map.put("下单设备 1.pc 2.无线", mailvorJdOrder.getOrderEmt());
            map.put("下单用户是否为PLUS会员 0：否，1：是", mailvorJdOrder.getPlus());
            map.put("推客ID", mailvorJdOrder.getUnionId());
            map.put("商品ID", mailvorJdOrder.getSkuId());
            map.put("商品名称", mailvorJdOrder.getSkuName());
            map.put("商品数量", mailvorJdOrder.getSkuNum());
            map.put("商品已退货数量", mailvorJdOrder.getSkuReturnNum());
            map.put("商品售后中数量", mailvorJdOrder.getSkuFrozenNum());
            map.put("商品单价", mailvorJdOrder.getPrice());
            map.put("佣金比例", mailvorJdOrder.getCommissionRate());
            map.put("分成比例（单位：%）", mailvorJdOrder.getSubsideRate());
            map.put("补贴比例（单位：%）", mailvorJdOrder.getSubsidyRate());
            map.put("最终分佣比例", mailvorJdOrder.getFinalRate());
            map.put("预估计订单金额", mailvorJdOrder.getEstimateCosPrice());
            map.put("预估佣金，以这个为准", mailvorJdOrder.getEstimateFee());
            map.put("实际订单金额", mailvorJdOrder.getActualCosPrice());
            map.put("实际佣金，未结算时为0", mailvorJdOrder.getActualFee());
            map.put("sku维度的有效码（-1：未知,2.无效-拆单,3.无效-取消,4.无效-京东帮帮主订单,5.无效-账号异常,6.无效-赠品类目不返佣,7.无效-校园订单,8.无效-企业订单,9.无效-团购订单,11.无效-乡村推广员下单,13.无效-违规订单,14.无效-来源与备案网址不符,15.待付款,16.已付款,17.已完成（购买用户确认收货）,20.无效-此复购订单对应的首购订单无效,21.无效-云店订单", mailvorJdOrder.getValidCode());
            map.put("同跨店：2同店 3跨店", mailvorJdOrder.getTraceType());
            map.put("推广位ID", mailvorJdOrder.getPositionId());
            map.put("应用id（网站id、appid、社交媒体id）", mailvorJdOrder.getSiteId());
            map.put("ID所属母账号平台名称", mailvorJdOrder.getUnionAlias());
            map.put("格式:子推客ID_子站长应用ID_子推客推广位ID", mailvorJdOrder.getPid());
            map.put("一级类目id", mailvorJdOrder.getCid1());
            map.put("二级类目id", mailvorJdOrder.getCid2());
            map.put("三级类目id", mailvorJdOrder.getCid3());
            map.put("子渠道标识，在转链时可自定义传入，格式要求：字母、数字或下划线，最多支持80个字符", mailvorJdOrder.getSubUnionId());
            map.put("联盟标签数据", mailvorJdOrder.getUnionTag());
            map.put("商家ID", mailvorJdOrder.getPopId());
            map.put("推客生成推广链接时传入的扩展字段", mailvorJdOrder.getExt1());
            map.put("预估结算时间 yyyyMMdd", mailvorJdOrder.getPayMonth());
            map.put("团长渠道ID", mailvorJdOrder.getRid());
            map.put("微信用户json信息", mailvorJdOrder.getGoodsInfo());
            map.put("创建时间", mailvorJdOrder.getCreateTime());
            map.put("修改时间", mailvorJdOrder.getUpdateTime());
            map.put("删除标识", mailvorJdOrder.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<MailvorJdOrder> getUnbindOrderList(Integer prior) {
        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorJdOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.in(MailvorJdOrder::getValidCode, JD_VALID_ORDER_STATUS);
        wrapper.isNull(MailvorJdOrder::getUid);

        LocalDateTime now = LocalDateTime.now().minusMinutes(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorJdOrder::getOrderTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.orderByDesc(MailvorJdOrder::getOrderTime);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public boolean hasUnlockOrder(Long uid, Integer innerType) {
        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorJdOrder::getUid, uid);
        if(innerType != null) {
            wrapper.eq(MailvorJdOrder::getInnerType, innerType);
        }
        wrapper.eq(MailvorJdOrder::getBind, 0);
        wrapper.gt(MailvorJdOrder::getEstimateCosPrice, 0);
        wrapper.in(MailvorJdOrder::getValidCode, JD_VALID_ORDER_STATUS);

        return orderMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Double totalFee(Integer type, List<Long> uid, Integer innerType) {
        return totalFee(type, uid, innerType, false);
    }

    @Override
    public List<MailvorJdOrder> getRefundAndBindOrderList(Integer prior) {
        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorJdOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        /*
        *
sku维度的有效码（-1：未知,2.无效-拆单,3.无效-取消,4.无效-京东帮帮主订单,5.无效-账号异常,
* 6.无效-赠品类目不返佣,7.无效-校园订单,8.无效-企业订单,9.无效-团购订单,11.无效-乡村推广员下单,
* 13.无效-违规订单,14.无效-来源与备案网址不符,15.待付款,
* 16.已付款,17.已完成（购买用户确认收货）,20.无效-此复购订单对应的首购订单无效,21.无效-云店订单
        * */
        wrapper.notIn(MailvorJdOrder::getValidCode, JD_VALID_ORDER_STATUS);
        wrapper.eq(MailvorJdOrder::getBind, 1);

        LocalDateTime now = LocalDateTime.now().minusDays(prior);
        //查找prior秒之前的订单
        wrapper.ge(MailvorJdOrder::getOrderTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
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
    public Set<MailvorJdOrder> selectCashOrderIds(Long uid, LocalDateTime time) {
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


    private LambdaQueryWrapper<MailvorJdOrder> getWrapper(List<Long> uid, Integer innerType, DateTime start, boolean skipInvalid) {
        return getWrapperPro(uid, innerType, start, null, skipInvalid);
    }

    private LambdaQueryWrapper<MailvorJdOrder> getWrapperPro(List<Long> uid, Integer innerType, Date start, Date end, boolean skipInvalid) {
        LambdaQueryWrapper<MailvorJdOrder> wrapperTwo = new LambdaQueryWrapper<>();
        if(uid != null) {
            wrapperTwo.in(MailvorJdOrder::getUid, uid);
        }
        if(innerType != null) {
            wrapperTwo.eq(MailvorJdOrder::getInnerType, innerType);
        }
        if(start != null) {
            wrapperTwo.ge(MailvorJdOrder::getOrderTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MailvorJdOrder::getOrderTime, end);
        }
        if(skipInvalid) {
            wrapperTwo.in(MailvorJdOrder::getValidCode, JD_VALID_ORDER_STATUS);
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
    public MailvorJdOrder mockOrder(Long orderId, Long skuId, Long uid, Date createTime, String shopName,
                                    Double rate,  Double fee, Double payPrice, String img, String title, Integer innerType) {
//        "{\"owner\": \"g\", " +
//                "\"shopId\": 1000094403, " +
//                "\"imageUrl\": \"http://img11.360buyimg.com/n0/jfs/t1/110776/40/5957/339361/5ea78ce7E48a4b3b5/6b317ccde37e7849.jpg\", " +
//                "\"shopName\": \"TCL环境电器京东自营旗舰店\", \"mainSkuId\": 6863099, \"productId\": 6863099}"
        GoodsInfoDto goodsInfo = GoodsInfoDto.builder().imageUrl(img).shopName(shopName).productId(orderId).build();

        MailvorJdOrder order = MailvorJdOrder.builder()
                .id(orderId.toString())
                .orderId(orderId)
                .parentId(0L)
                .orderTime(createTime)
                .modifyTime(createTime)
                .orderEmt(2)
                .plus(0)
                .unionId(1000482154L)
                .skuId(skuId)
                .skuName(title)
                .skuNum(1L)
                .skuReturnNum(0L)
                .skuFrozenNum(0L)
                .commissionRate(rate)
                .estimateFee(fee)
                .actualCosPrice(payPrice)
                .estimateCosPrice(payPrice)
                .validCode(16L)
                .positionId(3100121128L)
                .siteId(0L)
                .GoodsInfo(goodsInfo)
                .build();
        order.setBind(0);
        order.setUid(uid);
        order.setInnerType(innerType);
        return order;
    }

    @Override
    public void invalidRefundOrders(List<Long> ids) {
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

    private LambdaQueryWrapper<MailvorJdOrder> getHbWrapper(Date start, Date end) {
        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(MailvorJdOrder::getInnerType, 0);

        if(start != null) {
            wrapper.ge(MailvorJdOrder::getOrderTime, start);
        }
        if(end != null) {
            wrapper.lt(MailvorJdOrder::getOrderTime, end);
        }

        return wrapper;
    }


    @Override
    public List<MailvorJdOrder> getSelfUnspreadHbList(Integer limit) {
        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorJdOrder::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
        wrapper.eq(MailvorJdOrder::getBind, 0);
        wrapper.isNotNull(MailvorJdOrder::getUid);
        wrapper.eq(MailvorJdOrder::getInnerType, 0);
        wrapper.gt(MailvorJdOrder::getEstimateCosPrice, 0);
        wrapper.in(MailvorJdOrder::getValidCode, JD_VALID_ORDER_STATUS);

        LocalDateTime now = LocalDateTime.now().minusDays(2);
        //查找2天之前的订单
        wrapper.lt(MailvorJdOrder::getOrderTime, Date.from(now.atZone( ZoneId.systemDefault()).toInstant()));
        wrapper.last("limit " + limit);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Long getSpreadCountToday(Long uid) {
        Date now = new Date();
        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailvorJdOrder::getUid, uid);
        wrapper.eq(MailvorJdOrder::getInnerType, 0);
        wrapper.le(MailvorJdOrder::getSpreadHbTime, DateUtil.endOfDay(now));
        wrapper.ge(MailvorJdOrder::getSpreadHbTime, DateUtil.beginOfDay(now));

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
        LambdaQueryWrapper<MailvorJdOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(MailvorJdOrder::getUid);
        wrapper.eq(MailvorJdOrder::getInnerType, 0);
        wrapper.in(MailvorJdOrder::getValidCode, JD_VALID_ORDER_STATUS);
        wrapper.gt(MailvorJdOrder::getCommissionRate, scale);

        Date yesterdayStart = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -prior));
        wrapper.ge(MailvorJdOrder::getOrderTime, yesterdayStart);
        wrapper.groupBy(MailvorJdOrder::getUid);
        return orderMapper.checkCount(wrapper);
    }
}
