/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.mailvor.api.BusinessException;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.*;
import com.mailvor.event.TemplateBean;
import com.mailvor.event.TemplateEvent;
import com.mailvor.event.TemplateListenEnum;
import com.mailvor.exception.BadRequestException;
import com.mailvor.exception.EntityExistException;
import com.mailvor.modules.activity.domain.MwStorePink;
import com.mailvor.modules.activity.service.MwStorePinkService;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.cart.service.MwStoreCartService;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.order.domain.MwExpress;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.domain.MwStoreOrderCartInfo;
import com.mailvor.modules.order.domain.MwStoreOrderStatus;
import com.mailvor.modules.order.param.OrderParam;
import com.mailvor.modules.order.service.MwExpressService;
import com.mailvor.modules.order.service.MwStoreOrderCartInfoService;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.service.MwStoreOrderStatusService;
import com.mailvor.modules.order.service.dto.*;
import com.mailvor.modules.order.service.mapper.StoreOrderMapper;
import com.mailvor.modules.order.vo.*;
import com.mailvor.modules.product.domain.MwStoreProductReply;
import com.mailvor.modules.product.service.MwStoreProductReplyService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.sales.domain.StoreAfterSales;
import com.mailvor.modules.sales.service.StoreAfterSalesService;
import com.mailvor.modules.shop.service.ExpendService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.shop.service.MwSystemStoreService;
import com.mailvor.modules.template.domain.MwShippingTemplates;
import com.mailvor.modules.template.domain.MwShippingTemplatesFree;
import com.mailvor.modules.template.domain.MwShippingTemplatesRegion;
import com.mailvor.modules.template.service.MwShippingTemplatesFreeService;
import com.mailvor.modules.template.service.MwShippingTemplatesRegionService;
import com.mailvor.modules.template.service.MwShippingTemplatesService;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.tools.service.AlipayConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserAddress;
import com.mailvor.modules.user.service.*;
import com.mailvor.modules.user.service.dto.MwUserDto;
import com.mailvor.modules.user.vo.MwUserQueryVo;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author huangyu
 * @date 2020-05-12
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MwStoreOrderServiceImpl extends BaseServiceImpl<StoreOrderMapper, MwStoreOrder> implements MwStoreOrderService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private MwStorePinkService storePinkService;
    @Autowired
    private MwStoreOrderCartInfoService storeOrderCartInfoService;
    @Autowired
    private MwStoreCartService storeCartService;
    @Autowired
    private MwUserAddressService userAddressService;
    @Autowired
    private MwStoreOrderCartInfoService orderCartInfoService;
    @Autowired
    private MwStoreOrderStatusService orderStatusService;
    @Autowired
    private MwUserBillService billService;
    @Autowired
    private MwUserService userService;
    @Autowired
    private MwStoreProductService productService;
    @Autowired
    private MwExpressService expressService;
    @Autowired
    private AlipayConfigService alipayService;
    @Autowired
    private MwSystemStoreService systemStoreService;
    @Autowired
    private MwStoreProductReplyService productReplyService;
    @Autowired
    private MwShippingTemplatesService shippingTemplatesService;
    @Autowired
    private MwShippingTemplatesRegionService shippingTemplatesRegionService;
    @Autowired
    private MwShippingTemplatesFreeService shippingTemplatesFreeService;
    @Autowired
    private MwSystemConfigService systemConfigService;
    @Autowired
    private MwUserLevelService userLevelService;

    @Autowired
    private StoreOrderMapper mwStoreOrderMapper;

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private StoreAfterSalesService storeAfterSalesService;


    @Resource
    private MailvorTbOrderService tbOrderService;

    @Resource
    private MailvorJdOrderService jdOrderService;

    @Resource
    private MailvorPddOrderService pddOrderService;

    @Resource
    private MailvorVipOrderService vipOrderService;

    @Resource
    private MailvorDyOrderService dyOrderService;

    @Resource
    private MwUserExtractService extractService;

    @Resource
    private MwUserRechargeService rechargeService;


    @Resource
    private ExpendService expendService;
    /**
     * 返回订单确认数据
     *
     * @param mwUser  mwUser
     * @param goodsId 购物车id
     * @return ConfirmOrderVO
     */
    @Override
    public ConfirmOrderVo confirmOrder(MwUser mwUser, Long goodsId) {
//        Long uid = mwUser.getUid();
//
//        OtherDto other = new OtherDto();
//        other.setIntegralRatio(systemConfigService.getData(SystemConfigConstants.INTERGRAL_RATIO));
//        other.setIntegralFull(systemConfigService.getData(SystemConfigConstants.INTERGRAL_FULL));
//        other.setIntegralMax(systemConfigService.getData(SystemConfigConstants.INTERGRAL_MAX));
//
//        boolean enableIntegral = true;
//
//        MwStoreProductQueryVo productQueryVo = productService.getStoreProductById(goodsId);
//        //判断积分是否满足订单额度
//        if (mwUser.getIntegral().compareTo(productQueryVo.getPrice()) < 0) {
//            enableIntegral = false;
//        }
//
//        String cacheKey = this.cacheOrderInfo(uid, other);



        return ConfirmOrderVo.builder()
                .userInfo(generator.convert(mwUser, MwUserQueryVo.class))
                .systemStore(systemStoreService.getStoreInfo("", ""))
                .build();

    }


    /**
     * 创建订单
     *
     * @param userInfo 用户信息
     * @param key      key
     * @param param    param
     * @return MwStoreOrder
     */
    @Override
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    public MwStoreOrder createOrder(MwUser userInfo, String key, OrderParam param, MwStoreProductQueryVo productQueryVo) {

        Long uid = userInfo.getUid();

        //处理选择门店与正常选择地址下单
        MwUserAddress userAddress = null;
            if (StrUtil.isEmpty(param.getAddressId())) {
                throw new MshopException("请选择收货地址");
            }
            userAddress = userAddressService.getById(param.getAddressId());
            if (ObjectUtil.isNull(userAddress)) {
                throw new MshopException("地址选择有误");
            }

        //生成分布式唯一值
        String orderSn = IdUtil.getSnowflake(0, 0).nextIdStr();
        //组合数据
        MwStoreOrder storeOrder = new MwStoreOrder();
        storeOrder.setUid(Long.valueOf(String.valueOf(uid)));
        storeOrder.setOrderId(orderSn);
        storeOrder.setRealName(userAddress.getRealName());
        storeOrder.setUserPhone(userAddress.getPhone());
        storeOrder.setUserAddress(userAddress.getProvince() + " " + userAddress.getCity() +
                " " + userAddress.getDistrict() + " " + userAddress.getDetail());

        storeOrder.setPayIntegral(productQueryVo.getPrice());

        BigDecimal useIntegral = BigDecimal.valueOf(1);
        storeOrder.setUseIntegral(useIntegral);
        storeOrder.setMark(param.getMark() == null ? "": param.getMark());
        storeOrder.setUnique(key);

        storeOrder.setGoodsId(param.getGoodsId());
        storeOrder.setGoodsImg(productQueryVo.getImage());
        storeOrder.setGoodsName(productQueryVo.getStoreName());

        //保存订单
        boolean res = this.save(storeOrder);
        if (!res) {
            throw new MshopException("订单生成失败");
        }

        //使用了积分扣积分
        this.decIntegral(userInfo, useIntegral.doubleValue(),
                productQueryVo.getPrice().doubleValue(),orderSn);

        deStockIncSale(param.getGoodsId());
        //增加状态
        orderStatusService.create(storeOrder.getId(), OrderLogEnum.CREATE_ORDER.getValue(),
                OrderLogEnum.CREATE_ORDER.getDesc());

        return storeOrder;
    }


    /**
     * 订单评价
     *
     * @param orderCartInfo
     * @param user          user
     * @param unique        订单orderCart唯一值
     * @param comment       评论内容
     * @param pics          图片
     * @param productScore  评分
     * @param serviceScore  评分
     */
    @Override
    public void orderComment(MwStoreOrderCartInfo orderCartInfo, MwUser user, String unique, String comment, String pics, String productScore,
                             String serviceScore) {

        if (ObjectUtil.isEmpty(orderCartInfo)) {
            throw new MshopException("评价产品不存在");
        }

        Long count = productReplyService.count(Wrappers.<MwStoreProductReply>lambdaQuery()
                .eq(MwStoreProductReply::getOid, orderCartInfo.getOid())
                .eq(MwStoreProductReply::getProductId, orderCartInfo.getProductId()));
        if (count > 0) {
            throw new MshopException("该产品已评价");
        }


        MwStoreProductReply storeProductReply = MwStoreProductReply.builder()
                .uid(user.getUid())
                .oid(orderCartInfo.getOid())
                .productId(orderCartInfo.getProductId())
                .productScore(Integer.valueOf(productScore))
                .serviceScore(Integer.valueOf(serviceScore))
                .comment(comment)
                .pics(pics)
                .unique(unique)
                .build();

        productReplyService.save(storeProductReply);
        //获取评价商品数量
        Long replyCount = productReplyService.count(new LambdaQueryWrapper<MwStoreProductReply>().eq(MwStoreProductReply::getOid, orderCartInfo.getOid()));
        //购买商品数量
        Long cartCount = storeOrderCartInfoService.count(new LambdaQueryWrapper<MwStoreOrderCartInfo>().eq(MwStoreOrderCartInfo::getOid, orderCartInfo.getOid()));
        if (replyCount == cartCount) {
            MwStoreOrder storeOrder = new MwStoreOrder();
            storeOrder.setStatus(OrderInfoEnum.STATUS_3.getValue());
            storeOrder.setId(orderCartInfo.getOid());
            mwStoreOrderMapper.updateById(storeOrder);

        }
    }

    /**
     * 确认订单退款
     *
     * @param orderId 单号
     * @param price   金额
     * @param type    ShopCommonEnum
     */
    @Override
    public void orderRefund(String orderId, BigDecimal price, Integer type) {

        MwStoreOrderQueryVo orderQueryVo = getOrderInfo(orderId, null);
        if (ObjectUtil.isNull(orderQueryVo)) {
            throw new MshopException("订单不存在");
        }

        MwUserQueryVo userQueryVo = userService.getMwUserById(orderQueryVo.getUid());
        if (ObjectUtil.isNull(userQueryVo)) {
            throw new MshopException("用户不存在");
        }

        MwStoreOrder storeOrder = new MwStoreOrder();
        //修改状态
        storeOrder.setId(orderQueryVo.getId());
        if (ShopCommonEnum.AGREE_2.getValue().equals(type)) {
            mwStoreOrderMapper.updateById(storeOrder);
            StoreAfterSales storeAfterSales = storeAfterSalesService.lambdaQuery()
                    .eq(StoreAfterSales::getUserId, orderQueryVo.getUid())
                    .eq(StoreAfterSales::getOrderCode, orderQueryVo.getOrderId()).one();
            if (ObjectUtil.isNotNull(storeAfterSales)) {
                storeAfterSalesService.lambdaUpdate()
                        .eq(StoreAfterSales::getId, storeAfterSales.getId())
                        .set(StoreAfterSales::getSalesState, ShopCommonEnum.AGREE_2.getValue())
                        .update();
            }
            return;
        }
            mwStoreOrderMapper.updateById(storeOrder);

            orderStatusService.create(orderQueryVo.getId(), OrderLogEnum.ORDER_EDIT.getValue(), "退款给用户：" + orderQueryVo.getPayIntegral() + "分");
            this.retrunStock(orderQueryVo.getOrderId());

        orderStatusService.create(orderQueryVo.getId(), OrderLogEnum.REFUND_ORDER_SUCCESS.getValue(), "退款给用户：" + price + "元");

        TemplateBean templateBean = TemplateBean.builder()
                .orderId(orderQueryVo.getOrderId())
                .price(orderQueryVo.getPayIntegral().toString())
                .uid(orderQueryVo.getUid())
                .templateType(TemplateListenEnum.TYPE_2.getValue())
                .time(DateUtil.formatTime(new Date()))
                .build();
        publisher.publishEvent(new TemplateEvent(this, templateBean));


    }


    /**
     * 订单发货
     *
     * @param orderId      单号
     * @param deliveryId   快递单号
     * @param deliveryName 快递公司code
     */
    @Override
    public void orderDelivery(String orderId, String deliveryId, String deliveryName) {
        MwStoreOrderQueryVo orderQueryVo = this.getOrderInfo(orderId, null);
        if (ObjectUtil.isNull(orderQueryVo)) {
            throw new MshopException("订单不存在");
        }

        if (!OrderInfoEnum.STATUS_0.getValue().equals(orderQueryVo.getStatus())) {
            throw new MshopException("订单状态错误");
        }

        MwExpress expressQueryVo = expressService.getOne(new LambdaQueryWrapper<MwExpress>().eq(MwExpress::getName, deliveryName));
        if (ObjectUtil.isNull(expressQueryVo)) {
            throw new MshopException("请后台先添加快递公司");
        }

        MwStoreOrder storeOrder = MwStoreOrder.builder()
                .id(orderQueryVo.getId())
                .status(OrderInfoEnum.STATUS_1.getValue())
                .deliveryId(deliveryId)
                .deliveryName(expressQueryVo.getName())
                .deliverySn(expressQueryVo.getCode())
                .build();

        mwStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(orderQueryVo.getId(), OrderLogEnum.DELIVERY_GOODS.getValue(),
                "已发货 快递公司：" + expressQueryVo.getName() + "快递单号：" + deliveryId);

        //模板消息发布事件
        TemplateBean templateBean = TemplateBean.builder()
                .orderId(orderQueryVo.getOrderId())
                .deliveryId(deliveryId)
                .deliveryName(expressQueryVo.getName())
                .uid(orderQueryVo.getUid())
                .templateType(TemplateListenEnum.TYPE_3.getValue())
                .build();
        publisher.publishEvent(new TemplateEvent(this, templateBean));


        //加入redis，7天后自动确认收货
        String redisKey = String.valueOf(StrUtil.format("{}{}",
                ShopConstants.REDIS_ORDER_OUTTIME_UNCONFIRM, orderQueryVo.getId()));
        redisTemplate.opsForValue().set(redisKey, orderQueryVo.getOrderId(),
                ShopConstants.ORDER_OUTTIME_UNCONFIRM, TimeUnit.DAYS);

    }

    /**
     * 修改快递单号
     *
     * @param orderId      单号
     * @param deliveryId   快递单号
     * @param deliveryName 快递公司code
     */
    @Override
    public void updateDelivery(String orderId, String deliveryId, String deliveryName) {
        MwStoreOrderQueryVo orderQueryVo = this.getOrderInfo(orderId, null);
        if (ObjectUtil.isNull(orderQueryVo)) {
            throw new MshopException("订单不存在");
        }

        if (!OrderInfoEnum.STATUS_1.getValue().equals(orderQueryVo.getStatus())) {
            throw new MshopException("订单状态错误");
        }

        MwExpress expressQueryVo = expressService.getOne(new LambdaQueryWrapper<MwExpress>().eq(MwExpress::getName, deliveryName));
        if (ObjectUtil.isNull(expressQueryVo)) {
            throw new MshopException("请后台先添加快递公司");
        }


        MwStoreOrder storeOrder = MwStoreOrder.builder()
                .id(orderQueryVo.getId())
                .deliveryId(deliveryId)
                .deliveryName(expressQueryVo.getName())
                .deliverySn(expressQueryVo.getCode())
                .build();

        mwStoreOrderMapper.updateById(storeOrder);
    }


    /**
     * 修改订单价格
     *
     * @param orderId 单号
     * @param price   价格
     */
    @Override
    public void editOrderPrice(String orderId, String price) {
        MwStoreOrderQueryVo orderQueryVo = getOrderInfo(orderId, null);
        if (ObjectUtil.isNull(orderQueryVo)) {
            throw new MshopException("订单不存在");
        }


        MwStoreOrder storeOrder = new MwStoreOrder();
        storeOrder.setId(orderQueryVo.getId());

        //判断金额是否有变动,生成一个额外订单号去支付
        if (orderQueryVo.getPayIntegral().compareTo(new BigDecimal(price)) != 0) {
            String orderSn = IdUtil.getSnowflake(0, 0).nextIdStr();
        }

        mwStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(storeOrder.getId(), OrderLogEnum.ORDER_EDIT.getValue(), "修改实际支付金额");

    }

    /**
     * 未付款取消订单
     *
     * @param orderId 订单号
     * @param uid     用户id
     */
    @Override
    public void cancelOrder(String orderId, Long uid) {
        MwStoreOrderQueryVo order = this.getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(order)) {
            throw new MshopException("订单不存在");
        }

        this.regressionIntegral(order, 0);

        this.regressionStock(order, 0);

        this.regressionCoupon(order, 0);

        mwStoreOrderMapper.deleteById(order.getId());
    }


    /**
     * 删除订单
     *
     * @param orderId 单号
     * @param uid     uid
     */
    @Override
    public void removeOrder(String orderId, Long uid) {
        MwStoreOrderQueryVo order = getOrderInfo(orderId, (long) uid);
        if (order == null) {
            throw new MshopException("订单不存在");
        }
        order = handleOrder(order);
        if (!OrderInfoEnum.STATUS_3.getValue().equals(order.getStatus())) {
            throw new MshopException("该订单无法删除");
        }

        mwStoreOrderMapper.deleteById(order.getId());

        //增加状态
        orderStatusService.create(order.getId(),
                OrderLogEnum.REMOVE_ORDER.getValue(),
                OrderLogEnum.REMOVE_ORDER.getDesc());
    }

    /**
     * 订单确认收货
     *
     * @param orderId 单号
     * @param uid     uid
     */
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @Override
    public void takeOrder(String orderId, Long uid) {
        MwStoreOrderQueryVo order = this.getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(order)) {
            throw new MshopException("订单不存在");
        }
        order = handleOrder(order);
        if (!OrderStatusEnum.STATUS_2.getValue().toString().equals(order.get_status().get_type())) {
            throw new BusinessException("订单状态错误");
        }

        MwStoreOrder storeOrder = new MwStoreOrder();
        storeOrder.setStatus(OrderInfoEnum.STATUS_2.getValue());
        storeOrder.setId(order.getId());
        mwStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(order.getId(), OrderLogEnum.TAKE_ORDER_DELIVERY.getValue(), OrderLogEnum.TAKE_ORDER_DELIVERY.getDesc());

        //奖励积分
        this.gainUserIntegral(order);

        //分销计算
        userService.backOrderBrokerage(order);

        //检查是否符合开通店铺条件
        // userLevelService.setLevelComplete(uid);
    }


    /**
     * 核销订单
     *
     * @param isConfirm  OrderInfoEnum
     * @param uid        uid
     * @return MwStoreOrderQueryVo
     */
    @Override
    public MwStoreOrderQueryVo verifyOrder(Integer isConfirm, Long uid) {

        MwStoreOrder order = this.getOne(Wrappers.<MwStoreOrder>lambdaQuery());
        if (order == null) {
            throw new MshopException("核销的订单不存在或未支付或已退款");
        }

        if (!OrderInfoEnum.STATUS_0.getValue().equals(order.getStatus())) {
            throw new MshopException("订单已经核销");
        }

        MwStoreOrderQueryVo orderQueryVo = generator.convert(order, MwStoreOrderQueryVo.class);
        if (OrderInfoEnum.CONFIRM_STATUS_0.getValue().equals(isConfirm)) {
            return orderQueryVo;
        }


        MwStoreOrder storeOrder = new MwStoreOrder();
        storeOrder.setStatus(OrderInfoEnum.STATUS_2.getValue());
        storeOrder.setId(order.getId());
        mwStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(order.getId(), OrderLogEnum.TAKE_ORDER_DELIVERY.getValue(), "已核销");

        //奖励积分
        this.gainUserIntegral(orderQueryVo);

        //分销计算
        userService.backOrderBrokerage(orderQueryVo);

        //检查是否符合开通店铺条件
        userLevelService.setLevelComplete(order.getUid());

        return null;
    }

    /**
     * 申请退款
     *
     * @param explain 退款备注
     * @param Img     图片
     * @param text    理由
     * @param orderId 订单号
     * @param uid     uid
     */
    @Override
    public void orderApplyRefund(String explain, String Img, String text, String orderId, Long uid) {
        MwStoreOrderQueryVo order = getOrderInfo(orderId, uid);
        if (order == null) {
            throw new MshopException("订单不存在");
        }

        if (OrderInfoEnum.STATUS_1.getValue().equals(order.getStatus())) {
            throw new MshopException("订单当前无法退款");
        }


        MwStoreOrder storeOrder = new MwStoreOrder();
        storeOrder.setId(order.getId());
        mwStoreOrderMapper.updateById(storeOrder);

        //增加状态
        orderStatusService.create(order.getId(),
                OrderLogEnum.REFUND_ORDER_APPLY.getValue(),
                "用户申请退款，原因：" + text);

        //模板消息发布事件
        TemplateBean templateBean = TemplateBean.builder()
                .orderId(order.getOrderId())
                .price(order.getPayIntegral().toString())
                .uid(order.getUid())
                .templateType(TemplateListenEnum.TYPE_9.getValue())
                .time(DateUtil.formatTime(new Date()))
                .build();
        publisher.publishEvent(new TemplateEvent(this, templateBean));

    }

    /**
     * 订单列表
     *
     * @param uid   用户id
     * @param type  OrderStatusEnum
     * @param page  page
     * @param limit limit
     * @return list
     */
    @Override
    public Map<String, Object> orderList(Long uid, int type, int page, int limit) {
        LambdaQueryWrapper<MwStoreOrder> wrapper = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapper.eq(MwStoreOrder::getUid, uid);
        }
        wrapper.orderByDesc(MwStoreOrder::getId);

        switch (OrderStatusEnum.toType(type)) {
            case STATUS__1:
                break;
            //未支付
            case STATUS_0:
                wrapper .eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_0.getValue());
                break;
            //待发货
            case STATUS_1:
                wrapper.eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_0.getValue());
                break;
            //待收货
            case STATUS_2:
                wrapper .eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_1.getValue());
                break;
            //待评价
            case STATUS_3:
                wrapper .eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_2.getValue());
                break;
            //已完成
            case STATUS_4:
                wrapper .eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_3.getValue());
                break;
            //退款中
            case STATUS_MINUS_1:
                break;
            //已退款
            case STATUS_MINUS_2:
                break;
            //退款
            case STATUS_MINUS_3:
                break;
            default:
        }

        Page<MwStoreOrder> pageModel = new Page<>(page, limit);
        IPage<MwStoreOrder> pageList = mwStoreOrderMapper.selectPage(pageModel, wrapper);
        List<MwStoreOrderQueryVo> list = generator.convert(pageList.getRecords(), MwStoreOrderQueryVo.class);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list.stream()
                .map(this::handleOrder)
                .collect(Collectors.toList()));
        map.put("total", pageList.getTotal());
        map.put("totalPage", pageList.getPages());
        return map;

    }

    /**
     * 获取 今日 昨日 本月 订单金额
     * @return ShoperOrderTimeDataVo
     */
    @Override
    public ShoperOrderTimeDataVo getShoperOrderTimeData() {

        ShoperOrderTimeDataVo dataVo = new ShoperOrderTimeDataVo();

        Integer todayType = 1;
        boolean skipInvalid = true;
        dataVo.setTodayPrice(tbOrderService.totalPrice(todayType, null, 0,skipInvalid)
                + jdOrderService.totalPrice(todayType, null, 0,skipInvalid)
                + pddOrderService.totalPrice(todayType, null, 0,skipInvalid)
                + dyOrderService.totalPrice(todayType, null, 0,skipInvalid)
                + vipOrderService.totalPrice(todayType, null, 0,skipInvalid));
        dataVo.setTodayFee(tbOrderService.totalFee(todayType, null, 0,skipInvalid)
                + jdOrderService.totalFee(todayType, null, 0,skipInvalid)
                + pddOrderService.totalFee(todayType, null, 0,skipInvalid)
                + dyOrderService.totalFee(todayType, null, 0,skipInvalid)
                + vipOrderService.totalFee(todayType, null, 0,skipInvalid));
        //今日订单数
        dataVo.setTodayCount(tbOrderService.totalCount(todayType, null, 0,skipInvalid)
                + jdOrderService.totalCount(todayType, null, 0,skipInvalid)
                + pddOrderService.totalCount(todayType, null, 0,skipInvalid)
                + dyOrderService.totalCount(todayType, null, 0,skipInvalid)
                + vipOrderService.totalCount(todayType, null, 0,skipInvalid));
        Integer proType = 2;
        dataVo.setProPrice(tbOrderService.totalPrice(proType, null, 0,skipInvalid)
                + jdOrderService.totalPrice(proType, null, 0,skipInvalid)
                + pddOrderService.totalPrice(proType, null, 0,skipInvalid)
                + dyOrderService.totalPrice(proType, null, 0,skipInvalid)
                + vipOrderService.totalPrice(proType, null, 0,skipInvalid));
        dataVo.setProFee(tbOrderService.totalFee(proType, null, 0,skipInvalid)
                + jdOrderService.totalFee(proType, null, 0,skipInvalid)
                + pddOrderService.totalFee(proType, null, 0,skipInvalid)
                + dyOrderService.totalFee(proType, null, 0,skipInvalid)
                + vipOrderService.totalFee(proType, null, 0,skipInvalid));
        //今日订单数
        dataVo.setProCount(tbOrderService.totalCount(proType, null, 0,skipInvalid)
                + jdOrderService.totalCount(proType, null, 0,skipInvalid)
                + pddOrderService.totalCount(proType, null, 0,skipInvalid)
                + dyOrderService.totalCount(proType, null, 0,skipInvalid)
                + vipOrderService.totalCount(proType, null, 0,skipInvalid));

        //本月
        Integer monthType = 3;
        dataVo.setMonthPrice(tbOrderService.totalPrice(monthType,null, 0,skipInvalid)
                + jdOrderService.totalPrice(monthType,null, 0,skipInvalid)
                + pddOrderService.totalPrice(monthType,null, 0,skipInvalid)
                + dyOrderService.totalPrice(monthType,null, 0,skipInvalid)
                + vipOrderService.totalPrice(monthType,null, 0,skipInvalid));
        dataVo.setMonthFee(tbOrderService.totalFee(monthType,null, 0,skipInvalid)
                + jdOrderService.totalFee(monthType,null, 0,skipInvalid)
                + pddOrderService.totalFee(monthType,null, 0,skipInvalid)
                + dyOrderService.totalFee(monthType,null, 0,skipInvalid)
                + vipOrderService.totalFee(monthType,null, 0,skipInvalid));
        dataVo.setMonthCount(tbOrderService.totalCount(monthType,null, 0,skipInvalid)
                + jdOrderService.totalCount(monthType,null, 0,skipInvalid)
                + pddOrderService.totalCount(monthType,null, 0,skipInvalid)
                + dyOrderService.totalCount(monthType,null, 0,skipInvalid)
                + vipOrderService.totalCount(monthType,null, 0,skipInvalid));

        //上月
        Integer proMonthType = 4;
        dataVo.setProMonthPrice(tbOrderService.totalPrice(proMonthType,null, 0,skipInvalid)
                + jdOrderService.totalPrice(proMonthType,null, 0,skipInvalid)
                + pddOrderService.totalPrice(proMonthType,null, 0,skipInvalid)
                + dyOrderService.totalPrice(proMonthType,null, 0,skipInvalid)
                + vipOrderService.totalPrice(proMonthType,null, 0,skipInvalid));
        dataVo.setProMonthFee(tbOrderService.totalFee(proMonthType,null, 0,skipInvalid)
                + jdOrderService.totalFee(proMonthType,null, 0,skipInvalid)
                + pddOrderService.totalFee(proMonthType,null, 0,skipInvalid)
                + dyOrderService.totalFee(proMonthType,null, 0,skipInvalid)
                + vipOrderService.totalFee(proMonthType,null, 0,skipInvalid));
        dataVo.setProMonthCount(tbOrderService.totalCount(proMonthType,null, 0,skipInvalid)
                + jdOrderService.totalCount(proMonthType,null, 0,skipInvalid)
                + pddOrderService.totalCount(proMonthType,null, 0,skipInvalid)
                + dyOrderService.totalCount(proMonthType,null, 0,skipInvalid)
                + vipOrderService.totalCount(proMonthType,null, 0,skipInvalid));
        return dataVo;
    }

    /**
     * 订单每月统计数据
     *
     * @param page  page
     * @param limit list
     * @return List
     */
    @Override
    public List<OrderDataVo> getOrderDataPriceCount(int page, int limit) {
        Page<MwStoreOrder> pageModel = new Page<>(page, limit);
        return mwStoreOrderMapper.getOrderDataPriceList(pageModel);
    }

    /**
     * 获取某个用户的订单统计数据
     *
     * @param uid uid>0 取用户 否则取所有
     * @return UserOrderCountVo
     */
    @Override
    public UserOrderCountVo orderData(Long uid) {

        //订单支付没有退款 数量
        LambdaQueryWrapper<MwStoreOrder> wrapperOne = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapperOne.eq(MwStoreOrder::getUid, uid);
        }
        Long orderCount = mwStoreOrderMapper.selectCount(wrapperOne);

        //订单支付没有退款 支付总金额
        double sumPrice = mwStoreOrderMapper.sumPrice(uid);

        //订单待支付 数量
        LambdaQueryWrapper<MwStoreOrder> wrapperTwo = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapperTwo.eq(MwStoreOrder::getUid, uid);
        }
        wrapperTwo.eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_0.getValue());
        Long unpaidCount = mwStoreOrderMapper.selectCount(wrapperTwo);

        //订单待发货 数量
        LambdaQueryWrapper<MwStoreOrder> wrapperThree = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapperThree.eq(MwStoreOrder::getUid, uid);
        }
        wrapperThree.eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_0.getValue());
        Long unshippedCount = mwStoreOrderMapper.selectCount(wrapperThree);

        //订单待收货 数量
        LambdaQueryWrapper<MwStoreOrder> wrapperFour = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapperFour.eq(MwStoreOrder::getUid, uid);
        }
        wrapperFour.eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_1.getValue());
        Long receivedCount = mwStoreOrderMapper.selectCount(wrapperFour);

        //订单待评价 数量
        LambdaQueryWrapper<MwStoreOrder> wrapperFive = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapperFive.eq(MwStoreOrder::getUid, uid);
        }
        wrapperFive.eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_2.getValue());
        Long evaluatedCount = mwStoreOrderMapper.selectCount(wrapperFive);

        //订单已完成 数量
        LambdaQueryWrapper<MwStoreOrder> wrapperSix = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapperSix.eq(MwStoreOrder::getUid, uid);
        }
        wrapperSix.eq(MwStoreOrder::getStatus, OrderInfoEnum.STATUS_3.getValue());
        Long completeCount = mwStoreOrderMapper.selectCount(wrapperSix);

        //订单退款
        LambdaQueryWrapper<MwStoreOrder> wrapperSeven = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapperSeven.eq(MwStoreOrder::getUid, uid);
        }
        Long refundCount = mwStoreOrderMapper.selectCount(wrapperSeven);


        return UserOrderCountVo.builder()
                .orderCount(orderCount)
                .sumPrice(sumPrice)
                .unpaidCount(unpaidCount)
                .unshippedCount(unshippedCount)
                .receivedCount(receivedCount)
                .evaluatedCount(evaluatedCount)
                .completeCount(completeCount)
                .refundCount(refundCount)
                .build();
    }

    /**
     * 处理订单返回的状态
     *
     * @param order order
     * @return MwStoreOrderQueryVo
     */
    @Override
    public MwStoreOrderQueryVo handleOrder(MwStoreOrderQueryVo order) {
        LambdaQueryWrapper<MwStoreOrderCartInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreOrderCartInfo::getOid, order.getId());
        List<MwStoreOrderCartInfo> cartInfos = orderCartInfoService.list(wrapper);

        List<MwStoreCartQueryVo> cartInfo = cartInfos.stream()
                .map(cart -> {
                    MwStoreCartQueryVo cartQueryVo = JSON.parseObject(cart.getCartInfo(), MwStoreCartQueryVo.class);
                    cartQueryVo.setUnique(cart.getUnique());
                    cartQueryVo.setIsReply(productReplyService.replyCount(cart.getUnique()));
                    return cartQueryVo;
                })
                .collect(Collectors.toList());
        order.setCartInfo(cartInfo);

        StatusDto statusDTO = new StatusDto();
        if (OrderInfoEnum.STATUS_0.getValue().equals(order.getStatus())) {

                    statusDTO.set_class("state-nfh");
                    statusDTO.set_msg("商家未发货,请耐心等待");
                    statusDTO.set_type("1");
                    statusDTO.set_title("未发货");


        } else if (OrderInfoEnum.STATUS_1.getValue().equals(order.getStatus())) {
            statusDTO.set_class("state-ysh");
            statusDTO.set_msg("服务商已发货");
            statusDTO.set_type("2");
            statusDTO.set_title("待收货");
        } else if (OrderInfoEnum.STATUS_2.getValue().equals(order.getStatus())) {
            statusDTO.set_class("state-ypj");
            statusDTO.set_msg("已收货,快去评价一下吧");
            statusDTO.set_type("3");
            statusDTO.set_title("待评价");
        } else if (OrderInfoEnum.STATUS_3.getValue().equals(order.getStatus())) {
            statusDTO.set_class("state-ytk");
            statusDTO.set_msg("交易完成,感谢您的支持");
            statusDTO.set_type("4");
            statusDTO.set_title("交易完成");
        }

        statusDTO.set_payType("积分支付");

        order.set_status(statusDTO);


        return order;
    }

    /**
     * 支付成功后操作
     *
     * @param orderId 订单号
     * @param payType 支付方式
     */
    @Override
    public void paySuccess(String orderId, String payType) {
        MwStoreOrderQueryVo orderInfo = getOrderInfo(orderId, null);

        //更新订单状态
        LambdaQueryWrapper<MwStoreOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreOrder::getOrderId, orderId);
        MwStoreOrder storeOrder = new MwStoreOrder();
        mwStoreOrderMapper.update(storeOrder, wrapper);

        //增加用户购买次数
        userService.incPayCount(orderInfo.getUid());
        //增加状态
        orderStatusService.create(orderInfo.getId(), OrderLogEnum.PAY_ORDER_SUCCESS.getValue(),
                OrderLogEnum.PAY_ORDER_SUCCESS.getDesc());

        MwUser userInfo = userService.getById(orderInfo.getUid());

        //模板消息支付成功发布事件
        TemplateBean templateBean = TemplateBean.builder()
                .orderId(orderInfo.getOrderId())
                .price(orderInfo.getPayIntegral().toString())
                .uid(orderInfo.getUid())
                .templateType(TemplateListenEnum.TYPE_1.getValue())
                .build();
        publisher.publishEvent(new TemplateEvent(this, templateBean));

    }

    /**
     * 余额支付
     *
     * @param orderId 订单号
     * @param uid     用户id
     */
    @Override
    public void yuePay(String orderId, Long uid) {
        MwStoreOrderQueryVo orderInfo = getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(orderInfo)) {
            throw new MshopException("订单不存在");
        }

        MwUserQueryVo userInfo = userService.getMwUserById(uid);

        if (userInfo.getNowMoney().compareTo(orderInfo.getPayIntegral()) < 0) {
            throw new MshopException("余额不足");
        }

        userService.decPrice(uid, orderInfo.getPayIntegral());

        //支付成功后处理
        this.paySuccess(orderInfo.getOrderId(), PayTypeEnum.YUE.getValue());
    }


    /**
     * 积分兑换
     *
     * @param orderId 订单号
     * @param uid     用户id
     */
    @Override
    public void integralPay(String orderId, Long uid) {
        MwStoreOrderQueryVo orderInfo = getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(orderInfo)) {
            throw new MshopException("订单不存在");
        }

        orderInfo = handleOrder(orderInfo);
        orderInfo.getCartInfo().forEach(cart -> {
            if (cart.getProductInfo().getIsIntegral() == 0) {
                throw new MshopException("该商品不为积分商品");
            }
        });
        MwUser userInfo = userService.getById(uid);

        if (userInfo.getIntegral().compareTo(orderInfo.getPayIntegral()) < 0) {
            throw new MshopException("积分不足");
        }

        //扣除积分
        //userService.decIntegral(uid,orderInfo.getPayIntegral().doubleValue());
        BigDecimal newIntegral = NumberUtil.sub(userInfo.getIntegral(), orderInfo.getPayIntegral());
        userInfo.setIntegral(newIntegral);
        userService.updateById(userInfo);
        //增加流水
        billService.expend(userInfo.getUid(), userInfo.getUid(), "兑换商品", BillDetailEnum.CATEGORY_2.getValue(),
                BillDetailEnum.TYPE_8.getValue(),
                orderInfo.getPayIntegral().doubleValue(),
                newIntegral.doubleValue(),
                "兑换商品" + orderId + "扣除" + orderInfo.getPayIntegral().doubleValue() + "积分", orderId, null);
        //支付成功后处理
        this.paySuccess(orderInfo.getOrderId(), PayTypeEnum.INTEGRAL.getValue());
    }


    /**
     * 订单信息
     *
     * @param unique 唯一值或者单号
     * @param uid    用户id
     * @return MwStoreOrderQueryVo
     */
    @Override
    public MwStoreOrderQueryVo getOrderInfo(String unique, Long uid) {
        LambdaQueryWrapper<MwStoreOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(
                i -> i.eq(MwStoreOrder::getOrderId, unique).or().eq(MwStoreOrder::getUnique, unique).or());
        if (uid != null) {
            wrapper.eq(MwStoreOrder::getUid, uid);
        }

        return generator.convert(mwStoreOrderMapper.selectOne(wrapper), MwStoreOrderQueryVo.class);
    }


    /**
     * 奖励积分
     *
     * @param order 订单
     */
    private void gainUserIntegral(MwStoreOrderQueryVo order) {

    }

    /**
     * 减库存增加销量
     *
     */
    public void deStockIncSale(Long productId) {

        productService.decProductStock(1, productId, 0L, "");


    }


    /**
     * 积分抵扣
     *
     * @param userInfo       用户信息
     * @param usedIntegral   使用得积分
     * @param deductionPrice 抵扣的金额
     */
    private void decIntegral(MwUser userInfo, double usedIntegral, double deductionPrice, String orderSn) {
        userService.decIntegral(userInfo.getUid(), usedIntegral);
//        billService.expend(userInfo.getUid(), "积分抵扣", BillDetailEnum.CATEGORY_2.getValue(),
//                BillDetailEnum.TYPE_8.getValue(), usedIntegral, userInfo.getIntegral().doubleValue(),
//                "购买商品使用" + usedIntegral + "积分抵扣" + deductionPrice + "元", orderSn);
    }

    /**
     * 计算奖励的积分
     *
     * @param cartInfo cartInfo
     * @return double
     */
    private BigDecimal getGainIntegral(List<MwStoreCartQueryVo> cartInfo) {
        BigDecimal gainIntegral = BigDecimal.ZERO;
        for (MwStoreCartQueryVo cart : cartInfo) {
            if (cart.getCombinationId() > 0 || cart.getSeckillId() > 0 || cart.getBargainId() > 0) {
                continue;
            }
            BigDecimal cartInfoGainIntegral = BigDecimal.ZERO;
            Double gain = cart.getProductInfo().getGiveIntegral().doubleValue();
            if (gain > 0) {
                cartInfoGainIntegral = OrderUtil.getRoundFee(NumberUtil.mul(cart.getCartNum(), gain));
            }
            gainIntegral = NumberUtil.add(gainIntegral, cartInfoGainIntegral);
        }
        return gainIntegral;
    }


    /**
     * 退回优惠券
     *
     * @param order 订单
     */
    private void regressionCoupon(MwStoreOrderQueryVo order, Integer type) {
        if (type == 0) {
            if (OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())) {
                return;
            }
        }
    }

    /**
     * 退回库存
     *
     * @param order 订单
     */
    private void regressionStock(MwStoreOrderQueryVo order, Integer type) {
        if (type == 0) {
            if (OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())) {
                return;
            }
        }
        LambdaQueryWrapper<MwStoreOrderCartInfo> wrapper = new LambdaQueryWrapper<>();

        List<MwStoreOrderCartInfo> cartInfoList = orderCartInfoService.list(wrapper);
        for (MwStoreOrderCartInfo cartInfo : cartInfoList) {
            MwStoreCartQueryVo cart = JSONObject.parseObject(cartInfo.getCartInfo()
                    , MwStoreCartQueryVo.class);

                productService.incProductStock(cart.getCartNum(), cart.getProductId()
                        , cart.getProductAttrUnique(), 0L, null);


        }
    }

    /**
     * 退回积分
     *
     * @param order 订单
     */
    private void regressionIntegral(MwStoreOrderQueryVo order, Integer type) {
        if (type == 0) {
            if (OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())) {
                return;
            }
        }

        if (order.getPayIntegral().compareTo(BigDecimal.ZERO) > 0) {
            order.setUseIntegral(order.getPayIntegral());
        }
        if (order.getUseIntegral().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (!OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())) {
            return;
        }

        MwUser mwUser = userService.getById(order.getUid());

        //增加积分
        BigDecimal newIntegral = NumberUtil.add(order.getUseIntegral(), mwUser.getIntegral());
        mwUser.setIntegral(newIntegral);
        userService.updateById(mwUser);

        //增加流水
        billService.income(mwUser.getUid(), "积分回退", BillDetailEnum.CATEGORY_2.getValue(),
                BillDetailEnum.TYPE_8.getValue(),
                order.getUseIntegral().doubleValue(),
                newIntegral.doubleValue(),
                "购买商品失败,回退积分" + order.getUseIntegral(), order.getId().toString());

        //更新回退积分
        MwStoreOrder storeOrder = new MwStoreOrder();
        storeOrder.setId(order.getId());
        mwStoreOrderMapper.updateById(storeOrder);
    }

    /**
     * 获取订单缓存
     *
     * @param uid uid
     * @param key key
     * @return CacheDto
     */
    private CacheDto getCacheOrderInfo(Long uid, String key) {
        Object obj = redisUtils.get(ShopConstants.MSHOP_ORDER_CACHE_KEY + uid + key);
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(), CacheDto.class);
    }


    /**
     * 删除订单缓存
     *
     * @param uid uid
     * @param key key
     */
    private void delCacheOrderInfo(Long uid, String key) {
        redisUtils.del(ShopConstants.MSHOP_ORDER_CACHE_KEY + uid + key);
    }

    /**
     * 缓存订单
     *
     * @param uid        uid
     * @param price      price
     * @return string
     */
    public String cacheOrderInfo(Long uid, BigDecimal price) {
        String key = IdUtil.simpleUUID();
        CacheDto cacheDTO = new CacheDto();
        cacheDTO.setPrice(price);
        redisUtils.set(ShopConstants.MSHOP_ORDER_CACHE_KEY + uid + key,
                JSON.toJSONString(cacheDTO),
                ShopConstants.MSHOP_ORDER_CACHE_TIME);
        return key;
    }

    /**
     * 获取订单价格
     *
     * @param cartInfo 购物车列表
     * @return PriceGroupDto
     */
    private PriceGroupDto getOrderPriceGroup(List<MwStoreCartQueryVo> cartInfo, MwUserAddress userAddress) {

        BigDecimal storePostage = BigDecimal.ZERO;


        String storeFreePostageStr = systemConfigService.getData(SystemConfigConstants.STORE_FREE_POSTAGE);//满额包邮
        BigDecimal storeFreePostage = BigDecimal.ZERO;
        if (NumberUtil.isNumber(storeFreePostageStr) && StrUtil.isNotBlank(storeFreePostageStr)) {
            storeFreePostage = new BigDecimal(storeFreePostageStr);
        }


        BigDecimal totalPrice = this.getOrderSumPrice(cartInfo, "truePrice");//获取订单总金额
        BigDecimal costPrice = this.getOrderSumPrice(cartInfo, "costPrice");//获取订单成本价
        BigDecimal vipPrice = this.getOrderSumPrice(cartInfo, "vipTruePrice");//获取订单会员优惠金额
        BigDecimal payIntegral = this.getOrderSumPrice(cartInfo, "payIntegral");//获取订单需要的积分

        //如果设置满包邮0 表示全局包邮，如果设置大于0表示满这价格包邮，否则走运费模板算法
        if (storeFreePostage.compareTo(BigDecimal.ZERO) != 0 && totalPrice.compareTo(storeFreePostage) <= 0) {
            storePostage = this.handlePostage(cartInfo, userAddress);
        }
        if (cartInfo.size() == 1 && cartInfo.get(0).getProductInfo().getIsIntegral() != null
                && cartInfo.get(0).getProductInfo().getIsIntegral() == 1) {
            totalPrice = BigDecimal.ZERO;
        }

        PriceGroupDto priceGroupDTO = new PriceGroupDto();
        priceGroupDTO.setStorePostage(storePostage);
        priceGroupDTO.setStoreFreePostage(storeFreePostage);
        priceGroupDTO.setTotalPrice(totalPrice);
        priceGroupDTO.setCostPrice(costPrice);
        priceGroupDTO.setVipPrice(vipPrice);
        priceGroupDTO.setPayIntegral(payIntegral);
        return priceGroupDTO;
    }


    /**
     * 根据运费模板算法返回邮费
     *
     * @param cartInfo    购物车
     * @param userAddress 地址
     * @return double
     */
    private BigDecimal handlePostage(List<MwStoreCartQueryVo> cartInfo, MwUserAddress userAddress) {
        BigDecimal storePostage = BigDecimal.ZERO;
        if (userAddress != null) {
            if (userAddress.getCityId() == null) {
                return storePostage;
            }
            //城市包括默认
            int cityId = userAddress.getCityId();
            List<Integer> citys = new ArrayList<>();
            citys.add(cityId);
            citys.add(0);

            List<MwStoreProductQueryVo> storeProductVOList = cartInfo
                    .stream()
                    .map(MwStoreCartQueryVo::getProductInfo)
                    .collect(Collectors.toList());
            List<Integer> tempIdS = storeProductVOList
                    .stream()
                    .map(MwStoreProductQueryVo::getTempId)
                    .collect(Collectors.toList());


            //获取商品用到的运费模板
            List<MwShippingTemplates> shippingTemplatesList = shippingTemplatesService
                    .list(Wrappers.<MwShippingTemplates>lambdaQuery()
                            .in(MwShippingTemplates::getId, tempIdS));
            //获取运费模板区域列表按照城市排序
            List<MwShippingTemplatesRegion> shippingTemplatesRegionList = shippingTemplatesRegionService
                    .list(Wrappers.<MwShippingTemplatesRegion>lambdaQuery()
                            .in(MwShippingTemplatesRegion::getTempId, tempIdS)
                            .in(MwShippingTemplatesRegion::getCityId, citys)
                            .orderByAsc(MwShippingTemplatesRegion::getCityId));
            //提取运费模板类型
            Map<Integer, Integer> shippingTemplatesMap = shippingTemplatesList
                    .stream()
                    .collect(Collectors.toMap(MwShippingTemplates::getId,
                            MwShippingTemplates::getType));
            //提取运费模板有相同值覆盖
            Map<Integer, MwShippingTemplatesRegion> shippingTemplatesRegionMap =
                    shippingTemplatesRegionList.stream()
                            .collect(Collectors.toMap(MwShippingTemplatesRegion::getTempId,
                                    MwShippingTemplatesRegion -> MwShippingTemplatesRegion,
                                    (key1, key2) -> key2));


            Map<Integer, TemplateDto> templateDTOMap = new HashMap<>();
            for (MwStoreCartQueryVo storeCartVO : cartInfo) {
                Integer tempId = storeCartVO.getProductInfo().getTempId();

                //处理拼团等营销商品没有设置运费模板
                if (tempId == null) {
                    return storePostage;
                }

                //根据模板类型获取相应的数量
                double num = 0d;
                if (ShippingTempEnum.TYPE_1.getValue().equals(shippingTemplatesMap.get(tempId))) {
                    num = storeCartVO.getCartNum().doubleValue();
                } else if (ShippingTempEnum.TYPE_2.getValue().equals(shippingTemplatesMap.get(tempId))) {
                    num = NumberUtil.mul(storeCartVO.getCartNum(),
                            storeCartVO.getProductInfo().getAttrInfo().getWeight()).doubleValue();
                } else if (ShippingTempEnum.TYPE_3.getValue().equals(shippingTemplatesMap.get(tempId))) {
                    num = NumberUtil.mul(storeCartVO.getCartNum(),
                            storeCartVO.getProductInfo().getAttrInfo().getVolume()).doubleValue();
                }

                MwShippingTemplatesRegion shippingTemplatesRegion = shippingTemplatesRegionMap.get(tempId);
                BigDecimal price = OrderUtil.getRoundFee(NumberUtil.mul(storeCartVO.getCartNum(),
                        storeCartVO.getTruePrice()));
                if (!templateDTOMap.containsKey(tempId)) {
                    TemplateDto templateDTO = TemplateDto.builder()
                            .number(num)
                            .price(price)
                            .first(shippingTemplatesRegion.getFirst().doubleValue())
                            .firstPrice(shippingTemplatesRegion.getFirstPrice())
                            ._continue(shippingTemplatesRegion.getContinues().doubleValue())
                            .continuePrice(shippingTemplatesRegion.getContinuePrice())
                            .tempId(tempId)
                            .cityId(cityId)
                            .build();
                    templateDTOMap.put(tempId, templateDTO);
                } else {
                    TemplateDto templateDTO = templateDTOMap.get(tempId);
                    templateDTO.setNumber(templateDTO.getNumber() + num);
                    templateDTO.setPrice(NumberUtil.add(templateDTO.getPrice().doubleValue(), price));
                }


            }

            //处理包邮情况
            jj: for (Map.Entry<Integer, TemplateDto> entry : templateDTOMap.entrySet()) {
                Integer mapKey = entry.getKey();
                TemplateDto mapValue = entry.getValue();

                Long count = shippingTemplatesFreeService.count(Wrappers.<MwShippingTemplatesFree>lambdaQuery()
                        .eq(MwShippingTemplatesFree::getTempId, mapValue.getTempId())
                        .eq(MwShippingTemplatesFree::getCityId, mapValue.getCityId())
                        .le(MwShippingTemplatesFree::getNumber, mapValue.getNumber())
                        .le(MwShippingTemplatesFree::getPrice, mapValue.getPrice()));
                //满足包邮条件剔除
                if (count > 0) {
                    templateDTOMap.remove(mapKey);
                    break jj;
                }
            }

            //处理区域邮费
            boolean isFirst = true; //用来是否多个产品的标识 false表示数量大于1
            for (TemplateDto templateDTO : templateDTOMap.values()) {
                if (isFirst) {//首件
                    //只满足首件
                    if (Double.compare(templateDTO.getNumber(), templateDTO.getFirst()) <= 0) {
                        storePostage = OrderUtil.getRoundFee(NumberUtil.add(storePostage,
                                templateDTO.getFirstPrice()));
                    } else {
                        BigDecimal firstPrice = NumberUtil.add(storePostage, templateDTO.getFirstPrice());

                        if (templateDTO.get_continue() <= 0) {
                            storePostage = firstPrice;
                        } else {
                            //续件平均值且向上取整数
                            double average = Math.ceil(NumberUtil.div(NumberUtil.sub(templateDTO.getNumber(),
                                    templateDTO.getFirst()),
                                    templateDTO.get_continue().doubleValue()));
                            //最终邮费
                            storePostage = NumberUtil.add(firstPrice, NumberUtil.mul(average,
                                    templateDTO.getContinuePrice()));
                        }

                    }

                    isFirst = false;
                } else {
                    //多件直接在以前的基数继续续建
                    if (templateDTO.get_continue() > 0) {
                        //续件平均值且向上取整数
                        double average = Math.ceil(
                                NumberUtil.div(
                                        templateDTO.getNumber(),
                                        templateDTO.get_continue()
                                )
                        );
                        //最终邮费
                        storePostage = NumberUtil.add(storePostage.doubleValue(), NumberUtil.mul(average,
                                templateDTO.getContinuePrice()));
                    }
                }
            }
        }


        return storePostage;
    }

    /**
     * 获取某字段价格
     *
     * @param cartInfo 购物车
     * @param key      key值
     * @return Double
     */
    private BigDecimal getOrderSumPrice(List<MwStoreCartQueryVo> cartInfo, String key) {
        BigDecimal sumPrice = BigDecimal.ZERO;

        if ("truePrice".equals(key)) {
            for (MwStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice, NumberUtil.mul(storeCart.getCartNum(), storeCart.getTruePrice()));
            }
        } else if ("costPrice".equals(key)) {
            for (MwStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(), storeCart.getCostPrice()));
            }
        } else if ("vipTruePrice".equals(key)) {
            for (MwStoreCartQueryVo storeCart : cartInfo) {
                sumPrice = NumberUtil.add(sumPrice,
                        NumberUtil.mul(storeCart.getCartNum(), storeCart.getVipTruePrice()));
            }
        } else if ("payIntegral".equals(key)) {
            for (MwStoreCartQueryVo storeCart : cartInfo) {
                if (storeCart.getProductInfo().getAttrInfo() != null && storeCart.getProductInfo().getAttrInfo().getIntegral() != null) {
                    sumPrice = NumberUtil.add(sumPrice,
                            NumberUtil.mul(storeCart.getCartNum(), storeCart.getProductInfo().getAttrInfo().getIntegral()));
                }

            }
        }

        return sumPrice;
    }


    //=======================================================//


    /**
     * 根据商品分类统计订单占比
     *
     * @return OrderCountDto
     */
    @Override
    public OrderCountDto getOrderCount() {
        //获取所有订单转态为已支付的
        List<CountDto> nameList = storeCartService.findCateName();
        Map<String, Integer> childrenMap = new HashMap<>();
        nameList.forEach(i -> {
            if (i != null) {
                if (childrenMap.containsKey(i.getCatename())) {
                    childrenMap.put(i.getCatename(), childrenMap.get(i.getCatename()) + 1);
                } else {
                    childrenMap.put(i.getCatename(), 1);
                }
            }

        });
        List<OrderCountDto.OrderCountData> list = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        childrenMap.forEach((k, v) -> {
            OrderCountDto.OrderCountData orderCountData = new OrderCountDto.OrderCountData();
            orderCountData.setName(k);
            orderCountData.setValue(v);
            columns.add(k);
            list.add(orderCountData);
        });
        OrderCountDto orderCountDto = new OrderCountDto();
        orderCountDto.setColumn(columns);
        orderCountDto.setOrderCountDatas(list);
        return orderCountDto;
    }

    /**
     * 首页订单/用户等统计
     *
     * @return OrderTimeDataDto
     */
    @Override
    public OrderTimeDataDto getOrderTimeData() {
        OrderTimeDataDto orderTimeDataDto = new OrderTimeDataDto();

        ShoperOrderTimeDataVo shoperOrderTimeData = this.getShoperOrderTimeData();

        BeanUtil.copyProperties(shoperOrderTimeData, orderTimeDataDto);


        orderTimeDataDto.setUserCount(userService.count());

        orderTimeDataDto.setUserTodayCount(userService.todayCount());
        orderTimeDataDto.setUserProCount(userService.proCount());
        orderTimeDataDto.setUserMonthCount(userService.monthCount());
        return orderTimeDataDto;
    }
    @Override
    public IncomeDataDto getIncomeData() {
        IncomeDataDto dataDto = new IncomeDataDto();

        Integer todayType = 1;
        Integer monthType = 3;
        Integer proMonthType = 4;

        Double todayShopIn = rechargeService.totalRechargePrice(todayType);
        Double monthShopIn = rechargeService.totalRechargePrice(monthType);
        Double proMonthShopIn = rechargeService.totalRechargePrice(proMonthType);
        dataDto.setTodayShopIn(todayShopIn);
        dataDto.setMonthShopIn(monthShopIn);
        dataDto.setProMonthShopIn(proMonthShopIn);


        Double todayExtractOut = extractService.totalExtractPrice(todayType);
        Double monthExtractOut = extractService.totalExtractPrice(monthType);
        Double proMonthExtractOut = extractService.totalExtractPrice(proMonthType);

        dataDto.setTodayExtractOut(todayExtractOut);
        dataDto.setMonthExtractOut(monthExtractOut);
        dataDto.setProMonthExtractOut(proMonthExtractOut);

        //todo
        Double todayOtherOut = expendService.totalExpend(todayType);
        Double monthOtherOut = expendService.totalExpend(monthType);
        Double proMonthOtherOut = expendService.totalExpend(proMonthType);

        dataDto.setTodayOtherOut(todayOtherOut);
        dataDto.setMonthOtherOut(monthOtherOut);
        dataDto.setProMonthOtherOut(proMonthOtherOut);

        Double todayIn = todayShopIn - todayExtractOut - todayOtherOut;
        Double monthIn = monthShopIn - monthExtractOut - monthOtherOut;
        Double proMonthIn = proMonthShopIn - proMonthExtractOut - proMonthOtherOut;
        dataDto.setTodayIn(todayIn);
        dataDto.setMonthIn(monthIn);
        dataDto.setProMonthIn(proMonthIn);

        return dataDto;
    }
    /**
     * 返回本月订单金额与数量
     *
     * @return map
     */
    @Override
    public Map<String, Object> chartCount(String platform, String type) {
        Map<String, Object> map = new LinkedHashMap<>();
        Date last30Day = DateUtil.offsetDay(DateUtil.date(), -30);
        if("tb".equals(platform)) {
            if("price".equals(type)) {
                map.put("chart", tbOrderService.chartList(last30Day));
            } else if("count".equals(type)) {
                map.put("chart", tbOrderService.chartListT(last30Day));
            } else if("fee".equals(type)) {
                map.put("chart", tbOrderService.chartListFee(last30Day));
            }
        }else if("pdd".equals(platform)) {
            if("price".equals(type)) {
                map.put("chart", pddOrderService.chartList(last30Day));
            } else if("count".equals(type)) {
                map.put("chart", pddOrderService.chartListT(last30Day));
            } else if("fee".equals(type)) {
                map.put("chart", pddOrderService.chartListFee(last30Day));
            }
        } else if("jd".equals(platform)) {
            if("price".equals(type)) {
                map.put("chart", jdOrderService.chartList(last30Day));
            } else if("count".equals(type)) {
                map.put("chart", jdOrderService.chartListT(last30Day));
            } else if("fee".equals(type)) {
                map.put("chart", jdOrderService.chartListFee(last30Day));
            }
        } else if("dy".equals(platform)) {
            if("price".equals(type)) {
                map.put("chart", dyOrderService.chartList(last30Day));
            } else if("count".equals(type)) {
                map.put("chart", dyOrderService.chartListT(last30Day));
            } else if("fee".equals(type)) {
                map.put("chart", dyOrderService.chartListFee(last30Day));
            }
        } else if("vip".equals(platform)) {
            if("price".equals(type)) {
                map.put("chart", vipOrderService.chartList(last30Day));
            } else if("count".equals(type)) {
                map.put("chart", vipOrderService.chartListT(last30Day));
            } else if("fee".equals(type)) {
                map.put("chart", vipOrderService.chartListFee(last30Day));
            }
        }
        return map;
    }

    @Override
    public void retrunStock(String orderId) {
        MwStoreOrderQueryVo order = this.getOrderInfo(orderId, null);
        this.regressionIntegral(order, 1);
        this.regressionStock(order, 1);
        this.regressionCoupon(order, 1);
    }

    @Override
    public Map<String, Object> queryAll(MwStoreOrderQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreOrder> page = new PageInfo<>(queryAll(criteria));
        List<MwStoreOrderDto> storeOrderDTOS = new ArrayList<>();
        for (MwStoreOrder mwStoreOrder : page.getList()) {
            this.orderList(storeOrderDTOS, mwStoreOrder);

        }
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", storeOrderDTOS);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    public List<MwStoreOrder> queryAll(MwStoreOrderQueryCriteria criteria) {
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreOrder.class, criteria));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MwStoreOrder resources) {
        MwStoreOrder mwStoreOrder = this.getById(resources.getId());
        MwStoreOrder mwStoreOrder1 = this.getOne(new LambdaQueryWrapper<MwStoreOrder>()
                .eq(MwStoreOrder::getUnique, resources.getUnique()));
        if (mwStoreOrder1 != null && !mwStoreOrder1.getId().equals(mwStoreOrder.getId())) {
            throw new EntityExistException(MwStoreOrder.class, "unique", resources.getUnique());
        }
        mwStoreOrder.copy(resources);
        this.saveOrUpdate(mwStoreOrder);
    }


    @Override
    public void download(List<MwStoreOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreOrderDto mwStoreOrder : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("订单号", mwStoreOrder.getOrderId());
            map.put("用户id", mwStoreOrder.getUid());
            map.put("用户姓名", mwStoreOrder.getRealName());
            map.put("用户电话", mwStoreOrder.getUserPhone());
            map.put("详细地址", mwStoreOrder.getUserAddress());
            map.put("购物车id", mwStoreOrder.getCartId());
            map.put("运费金额", mwStoreOrder.getFreightPrice());
            map.put("订单商品总数", mwStoreOrder.getTotalNum());
            map.put("订单总价", mwStoreOrder.getTotalPrice());
            map.put("邮费", mwStoreOrder.getTotalPostage());
            map.put("实际支付金额", mwStoreOrder.getPayPrice());
            map.put("支付邮费", mwStoreOrder.getPayPostage());
            map.put("抵扣金额", mwStoreOrder.getDeductionPrice());
            map.put("优惠券id", mwStoreOrder.getCouponId());
            map.put("优惠券金额", mwStoreOrder.getCouponPrice());
            map.put("支付状态", mwStoreOrder.getPaid());
            map.put("支付时间", mwStoreOrder.getPayTime());
            map.put("支付方式", mwStoreOrder.getPayType());
            map.put("订单状态（-1 : 申请退款 -2 : 退货成功 0：待发货；1：待收货；2：已收货；3：待评价；-1：已退款）", mwStoreOrder.getStatus());
            map.put("0 未退款 1 申请中 2 已退款", mwStoreOrder.getRefundStatus());
            map.put("退款图片", mwStoreOrder.getRefundReasonWapImg());
            map.put("退款用户说明", mwStoreOrder.getRefundReasonWapExplain());
            map.put("退款时间", mwStoreOrder.getRefundReasonTime());
            map.put("前台退款原因", mwStoreOrder.getRefundReasonWap());
            map.put("不退款的理由", mwStoreOrder.getRefundReason());
            map.put("退款金额", mwStoreOrder.getRefundPrice());
            map.put("快递公司编号", mwStoreOrder.getDeliverySn());
            map.put("快递名称/送货人姓名", mwStoreOrder.getDeliveryName());
            map.put("发货类型", mwStoreOrder.getDeliveryType());
            map.put("快递单号/手机号", mwStoreOrder.getDeliveryId());
            map.put("消费赚取积分", mwStoreOrder.getGainIntegral());
            map.put("使用积分", mwStoreOrder.getUseIntegral());
            map.put("给用户退了多少积分", mwStoreOrder.getBackIntegral());
            map.put("备注", mwStoreOrder.getMark());
            map.put("唯一id(md5加密)类似id", mwStoreOrder.getUnique());
            map.put("管理员备注", mwStoreOrder.getRemark());
            map.put("商户ID", mwStoreOrder.getMerId());
            map.put(" isMerCheck", mwStoreOrder.getIsMerCheck());
            map.put("拼团产品id0一般产品", mwStoreOrder.getCombinationId());
            map.put("拼团id 0没有拼团", mwStoreOrder.getPinkId());
            map.put("成本价", mwStoreOrder.getCost());
            map.put("秒杀产品ID", mwStoreOrder.getSeckillId());
            map.put("砍价id", mwStoreOrder.getBargainId());
            map.put("核销码", mwStoreOrder.getVerifyCode());
            map.put("门店id", mwStoreOrder.getStoreId());
            map.put(" isRemind", mwStoreOrder.getIsRemind());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    @Override
    public MwStoreOrderDto getOrderDetail(Long orderId) {
        MwStoreOrder mwStoreOrder = this.getById(orderId);
        if (ObjectUtil.isEmpty(mwStoreOrder)) {
            throw new BadRequestException("订单详情不存在");
        }
        MwStoreOrderDto mwStoreOrderDto = generator.convert(mwStoreOrder, MwStoreOrderDto.class);
        Integer _status = OrderUtil.orderStatus(mwStoreOrder.getStatus());

        //订单状态
        String orderStatusStr = OrderUtil.orderStatusStr(mwStoreOrder.getStatus());

        mwStoreOrderDto.setStatusName(orderStatusStr);

        mwStoreOrderDto.set_status(_status);

        //添加订单状态
        List<MwStoreOrderStatus> storeOrderStatuses = orderStatusService.list(new LambdaQueryWrapper<MwStoreOrderStatus>()
                .eq(MwStoreOrderStatus::getOid, mwStoreOrder.getId()));
        List<MwStoreOrderStatusDto> orderStatusDtos = generator.convert(storeOrderStatuses, MwStoreOrderStatusDto.class);
        mwStoreOrderDto.setStoreOrderStatusList(orderStatusDtos);
        //添加购物车详情
        List<MwStoreOrderCartInfo> cartInfos = storeOrderCartInfoService.list(
                new LambdaQueryWrapper<MwStoreOrderCartInfo>().eq(MwStoreOrderCartInfo::getOid, mwStoreOrder.getId()));
        List<StoreOrderCartInfoDto> cartInfoDTOS = new ArrayList<>();
        for (MwStoreOrderCartInfo cartInfo : cartInfos) {
            StoreOrderCartInfoDto cartInfoDTO = new StoreOrderCartInfoDto();
            cartInfoDTO.setCartInfoMap(JSON.parseObject(cartInfo.getCartInfo()));

            cartInfoDTOS.add(cartInfoDTO);
        }
        mwStoreOrderDto.setCartInfoList(cartInfoDTOS);
        //添加用户信息
        mwStoreOrderDto.setUserDTO(generator.convert(userService.getById(mwStoreOrder.getUid()), MwUserDto.class));
        if (mwStoreOrderDto.getUserDTO() == null) {
            mwStoreOrderDto.setUserDTO(new MwUserDto());
        }
        return mwStoreOrderDto;
    }

    @Override
    public Map<String, Object> queryAll(List<String> ids) {
        List<MwStoreOrder> mwStoreOrders = this.list(new LambdaQueryWrapper<MwStoreOrder>().in(MwStoreOrder::getOrderId, ids));
        List<MwStoreOrderDto> storeOrderDTOS = new ArrayList<>();
        for (MwStoreOrder mwStoreOrder : mwStoreOrders) {
            this.orderList(storeOrderDTOS, mwStoreOrder);
        }

        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", storeOrderDTOS);

        return map;
    }


    /**
     * 处理订单
     *
     * @param storeOrderDTOS 订单列表
     * @param mwStoreOrder   订单
     */
    private void orderList(List<MwStoreOrderDto> storeOrderDTOS, MwStoreOrder mwStoreOrder) {
        MwStoreOrderDto mwStoreOrderDto = generator.convert(mwStoreOrder, MwStoreOrderDto.class);
        Integer _status = OrderUtil.orderStatus(mwStoreOrder.getStatus());

        //订单状态
        String orderStatusStr = OrderUtil.orderStatusStr(mwStoreOrder.getStatus());

        mwStoreOrderDto.setStatusName(orderStatusStr);

        mwStoreOrderDto.set_status(_status);

        List<MwStoreOrderCartInfo> cartInfos = storeOrderCartInfoService.list(
                new LambdaQueryWrapper<MwStoreOrderCartInfo>().eq(MwStoreOrderCartInfo::getOid, mwStoreOrder.getId()));
        List<StoreOrderCartInfoDto> cartInfoDTOS = new ArrayList<>();
        for (MwStoreOrderCartInfo cartInfo : cartInfos) {
            StoreOrderCartInfoDto cartInfoDTO = new StoreOrderCartInfoDto();
            cartInfoDTO.setCartInfoMap(JSON.parseObject(cartInfo.getCartInfo()));

            cartInfoDTOS.add(cartInfoDTO);
        }
        mwStoreOrderDto.setCartInfoList(cartInfoDTOS);
        mwStoreOrderDto.setUserDTO(generator.convert(userService.getById(mwStoreOrder.getUid()), MwUserDto.class));
        if (mwStoreOrderDto.getUserDTO() == null) {
            mwStoreOrderDto.setUserDTO(new MwUserDto());
        }
        storeOrderDTOS.add(mwStoreOrderDto);
    }


    /**
     * 订单状态处理
     *
     * @param id            订单id
     * @param pinkId        拼团id
     * @param combinationId 拼团产品id
     * @param seckillId     秒杀id
     * @param bargainId     砍价id
     * @return string
     */
    private String orderType(Long id, Long pinkId, Long combinationId, Long seckillId,
                             Long bargainId, BigDecimal payIntegral) {
        String str = "[普通订单]";
        if (pinkId > 0 || combinationId > 0) {
            MwStorePink storePink = storePinkService.getOne(new LambdaQueryWrapper<MwStorePink>()
                    .eq(MwStorePink::getOrderIdKey, id));
            if (ObjectUtil.isNull(storePink)) {
                str = "[拼团订单]";
            } else {
                if (OrderInfoEnum.PINK_STATUS_1.getValue().equals(storePink.getStatus())) {
                    str = "[拼团订单]正在进行中";
                } else if (OrderInfoEnum.PINK_STATUS_2.getValue().equals(storePink.getStatus())) {
                    str = "[拼团订单]已完成";
                } else if (OrderInfoEnum.PINK_STATUS_3.getValue().equals(storePink.getStatus())) {
                    str = "[拼团订单]未完成";
                } else {
                    str = "[拼团订单]历史订单";
                }

            }

        } else if (seckillId > 0) {
            str = "[秒杀订单]";
        } else if (bargainId > 0) {
            str = "[砍价订单]";
        }

        if (payIntegral.compareTo(new BigDecimal("0.00")) == 1) {
            str = "[积分兑换]";
        }
        return str;
    }


}
