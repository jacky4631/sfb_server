/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.aop.NoRepeatSubmit;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.enums.OrderLogEnum;
import com.mailvor.enums.PayTypeEnum;
import com.mailvor.enums.ShipperCodeEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.mp.domain.MwWechatTemplate;
import com.mailvor.modules.mp.service.MwWechatTemplateService;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.domain.MwStoreOrderCartInfo;
import com.mailvor.modules.order.dto.OrderExtendDto;
import com.mailvor.modules.order.dto.OrderTabDto;
import com.mailvor.modules.order.dto.OrderTabFirstDto;
import com.mailvor.modules.order.param.*;
import com.mailvor.modules.order.service.MwStoreOrderCartInfoService;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.service.MwStoreOrderStatusService;
import com.mailvor.modules.order.service.dto.MwStoreOrderDto;
import com.mailvor.modules.order.vo.ConfirmOrderVo;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.order.vo.OrderCartInfoVo;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.services.CreatShareProductService;
import com.mailvor.modules.services.OrderSupplyService;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.tools.express.ExpressService;
import com.mailvor.modules.tools.express.config.ExpressAutoConfiguration;
import com.mailvor.modules.tools.express.dao.ExpressInfo;
import com.mailvor.modules.user.domain.MwUser;
import com.vdurmont.emoji.EmojiParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.mailvor.config.PayConfig.PAY_NAME;

/**
 * <p>
 * 订单控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-27
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "订单模块", tags = "商城:订单模块")
public class StoreOrderController {

    private final MwStoreOrderService storeOrderService;
    private final OrderSupplyService orderSupplyService;
    private final CreatShareProductService creatShareProductService;
    private final MwWechatTemplateService mwWechatTemplateService;
    private final MwStoreOrderStatusService orderStatusService;

    @Autowired
    private MwStoreProductService productService;
    private final MwStoreOrderCartInfoService storeOrderCartInfoService;
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
    private MailvorMtOrderService mtOrderService;

    @Value("${file.path}")
    private String path;

    @Resource
    private TkService tkService;

    /**
     * 订单确认
     */
    @AppLog(value = "订单确认", type = 1)
    @AuthCheck
    @PostMapping("/order/confirm")
    @ApiOperation(value = "订单确认", notes = "订单确认")
    public ApiResult<ConfirmOrderVo> confirm(@Validated @RequestBody ConfirmOrderParam param) {
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(storeOrderService.confirmOrder(mwUser, param.getGoodsId()));

    }

    /**
     * 苏分宝在用，积分订单创建
     * 订单创建
     */
    @AppLog(value = "订单创建", type = 1)
    @AuthCheck
    @NoRepeatSubmit
    @PostMapping("/order/create")
    @ApiOperation(value = "订单创建", notes = "订单创建")
    public ApiResult<Map<String, Object>> create(@Valid @RequestBody OrderParam param) {
        MwUser mwUser = LocalUser.getUser();
        ComputeOrderParam computeOrderParam = new ComputeOrderParam();
        BeanUtil.copyProperties(param, computeOrderParam);
        Map<String, Object> map = new HashMap<>();
        MwStoreProductQueryVo productQueryVo = productService.getStoreProductById(param.getGoodsId());
        if (mwUser.getIntegral().compareTo(productQueryVo.getPrice()) < 0) {
            throw new MshopException("积分不足");
        }
        String cacheKey = storeOrderService.cacheOrderInfo(mwUser.getUid(), productQueryVo.getPrice());

        //创建订单
        MwStoreOrder order = storeOrderService.createOrder(mwUser, cacheKey, param, productQueryVo);

        if (ObjectUtil.isNull(order)) {
            throw new MshopException("订单生成失败");
        }

        String orderId = order.getOrderId();

        OrderExtendDto orderDTO = new OrderExtendDto();
        orderDTO.setKey(cacheKey);
        orderDTO.setOrderId(orderId);
        map.put("status", OrderLogEnum.CREATE_ORDER_SUCCESS.getValue());
        map.put("result", orderDTO);
        map.put("createTime", order.getCreateTime());

        //开始处理支付
        //处理金额为0的情况
        if(!param.getPayType().equals(PayTypeEnum.INTEGRAL.getValue())){
            storeOrderService.yuePay(orderId,mwUser.getUid());
            map.put("payMsg","支付成功");
            return ApiResult.ok(map,"支付成功");
        }

        orderSupplyService.goPay(map,orderId,mwUser.getUid(),
                param.getPayType(),param.getFrom(),orderDTO);
        return ApiResult.ok(map, map.get("payMsg").toString());
    }


    /**
     * 订单支付
     */
    @AppLog(value = "订单支付", type = 1)
    @AuthCheck
    @PostMapping("/order/pay")
    @ApiOperation(value = "订单支付", notes = "订单支付")
    public ApiResult<Map<String, Object>> pay(@Valid @RequestBody PayParam param) {
        Map<String, Object> map = new LinkedHashMap<>();
        Long uid = LocalUser.getUser().getUid();
        MwStoreOrderQueryVo storeOrder = storeOrderService
                .getOrderInfo(param.getUni(), uid);
        if (ObjectUtil.isNull(storeOrder)) {
            throw new MshopException("订单不存在");
        }

        String orderId = storeOrder.getOrderId();

        OrderExtendDto orderDTO = new OrderExtendDto();
        orderDTO.setOrderId(orderId);
        map.put("status", "SUCCESS");
        map.put("result", orderDTO);


        if (!param.getPaytype().equals(PayTypeEnum.INTEGRAL.getValue())) {
            storeOrderService.yuePay(orderId, uid);
            return ApiResult.ok(map, "支付成功");
        }

        orderSupplyService.goPay(map, orderId, uid, param.getPaytype(), param.getFrom(), orderDTO);

        return ApiResult.ok(map);
    }


    /**
     * 苏分宝在用，积分商城
     * 订单列表
     */
    @AppLog(value = "查看订单列表", type = 1)
    @AuthCheck
    @GetMapping("/order/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "商品状态,-1全部 默认为0未支付 1待发货 2待收货 3待评价 4已完成 5退款中 6已退款 7退款", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码,默认为1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "页大小,默认为10", paramType = "query", dataType = "int")
    })
    @ApiOperation(value = "订单列表", notes = "订单列表")
    public ApiResult<Object> orderList(@RequestParam(value = "type", defaultValue = "-1") int type,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Long uid = LocalUser.getUser().getUid();
        Map<String, Object> map = storeOrderService.orderList(uid, type, page, limit);
        Long total = (Long) map.get("total");
        //todo 分页貌似没实现
        Long totalPage = (Long) map.get("totalPage");
        return ApiResult.resultPage(total, totalPage.intValue(), map.get("list"));
    }


    /**
     * 订单详情
     */
    @AppLog(value = "查看订单详情", type = 1)
    @AuthCheck
    @GetMapping("/order/detail/{key}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "唯一的key", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "订单详情", notes = "订单详情")
    public ApiResult<MwStoreOrderQueryVo> detail(@PathVariable String key) {
        Long uid = LocalUser.getUser().getUid();
        if (StrUtil.isEmpty(key)) {
            throw new MshopException("参数错误");
        }
        MwStoreOrderQueryVo storeOrder = storeOrderService.getOrderInfo(key, uid);
        if (ObjectUtil.isNull(storeOrder)) {
            throw new MshopException("订单不存在");
        }
        storeOrder = creatShareProductService.handleQrcode(storeOrder, path);

        return ApiResult.ok(storeOrderService.handleOrder(storeOrder));
    }


    /**
     * 订单收货
     */
    @AppLog(value = "订单收货", type = 1)
    @AuthCheck
    @PostMapping("/order/take")
    @ApiOperation(value = "订单收货", notes = "订单收货")
    public ApiResult<Boolean> orderTake(@Validated @RequestBody DoOrderParam param) {
        Long uid = LocalUser.getUser().getUid();
        storeOrderService.takeOrder(param.getUni(), uid);
        return ApiResult.ok();
    }

    /**
     * 订单产品信息
     */
    @PostMapping("/order/product")
    @ApiOperation(value = "订单产品信息", notes = "订单产品信息")
    public ApiResult<OrderCartInfoVo> product(@Validated @RequestBody ProductOrderParam param) {
        return ApiResult.ok(orderSupplyService.getProductOrder(param.getUnique()));
    }

    /**
     * 订单评价
     */
    @AppLog(value = "订单评价", type = 1)
    @AuthCheck
    @NoRepeatSubmit
    @PostMapping("/order/comment")
    @ApiOperation(value = "订单评价", notes = "订单评价")
    public ApiResult<Boolean> comment(@Valid @RequestBody ProductReplyParam param) {
        MwUser user = LocalUser.getUser();
        MwStoreOrderCartInfo orderCartInfo = storeOrderCartInfoService
                .getOne(Wrappers.<MwStoreOrderCartInfo>lambdaQuery()
                        .eq(MwStoreOrderCartInfo::getUnique,param.getUnique()));
        storeOrderService.orderComment(orderCartInfo, user, param.getUnique(),
                param.getComment(),
                param.getPics(), param.getProductScore(), param.getServiceScore());

        //增加状态
        orderStatusService.create(orderCartInfo.getOid(),
                OrderLogEnum.EVAL_ORDER.getValue(),
                OrderLogEnum.EVAL_ORDER.getDesc());
        return ApiResult.ok();
    }

    /**
     * 订单评价
     */
    @AppLog(value = "订单评价", type = 1)
    @AuthCheck
    @NoRepeatSubmit
    @PostMapping("/order/comments")
    @ApiOperation(value = "订单评价", notes = "订单评价")
    public ApiResult<Boolean> comments(@Valid @RequestBody List<ProductReplyParam> param) {
        MwUser user = LocalUser.getUser();
        if (param.size() > 0) {
            MwStoreOrderCartInfo orderCartInfo = storeOrderCartInfoService
                    .getOne(Wrappers.<MwStoreOrderCartInfo>lambdaQuery()
                            .eq(MwStoreOrderCartInfo::getUnique,param.get(0).getUnique()));

            for (ProductReplyParam productReplyParam : param) {
                storeOrderService.orderComment(orderCartInfo , user, productReplyParam.getUnique(),
                        productReplyParam.getComment(),
                        productReplyParam.getPics(), productReplyParam.getProductScore(), productReplyParam.getServiceScore());
            }

            //增加状态
            orderStatusService.create(orderCartInfo.getOid(),
                    OrderLogEnum.EVAL_ORDER.getValue(),
                    OrderLogEnum.EVAL_ORDER.getDesc());
        }
        return ApiResult.ok();
    }


    /**
     * 订单删除
     */
    @AppLog(value = "订单删除", type = 1)
    @AuthCheck
    @PostMapping("/order/del")
    @ApiOperation(value = "订单删除", notes = "订单删除")
    public ApiResult<Boolean> orderDel(@Validated @RequestBody DoOrderParam param) {
        Long uid = LocalUser.getUser().getUid();
        storeOrderService.removeOrder(param.getUni(), uid);
        return ApiResult.ok();
    }

    /**
     * 订单退款理由
     */
    @GetMapping("/order/refund/reason")
    @ApiOperation(value = "订单退款理由", notes = "订单退款理由")
    public ApiResult<Object> refundReason() {
        ArrayList<String> list = new ArrayList<>();
        list.add("收货地址填错了");
        list.add("与描述不符");
        list.add("信息填错了，重新拍");
        list.add("收到商品损坏了");
        list.add("未按预定时间发货");
        list.add("其它原因");

        return ApiResult.ok(list);
    }

    /**
     * 订单退款审核
     */
    @AppLog(value = "订单退款审核", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/order/refund/verify")
    @ApiOperation(value = "订单退款审核", notes = "订单退款审核")
    public ApiResult<Boolean> refundVerify(@RequestBody RefundParam param) {
        Long uid = LocalUser.getUser().getUid();
        storeOrderService.orderApplyRefund(param.getRefundReasonWapExplain(),
                param.getRefundReasonWapImg(),
                EmojiParser.removeAllEmojis(param.getText()),
                param.getUni(), uid);
        return ApiResult.ok();
    }

    /**
     * 订单取消   未支付的订单回退积分,回退优惠券,回退库存
     */
    @AppLog(value = "订单取消", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/order/cancel")
    @ApiOperation(value = "订单取消", notes = "订单取消")
    public ApiResult<Boolean> cancelOrder(@Validated @RequestBody HandleOrderParam param) {
        Long uid = LocalUser.getUser().getUid();
        MwStoreOrderQueryVo orderInfo = storeOrderService.getOrderInfo(param.getId(), uid);
        if (ObjectUtil.isNull(orderInfo)) {
            throw new MshopException("订单不存在");
        }
        if (orderInfo.getStatus() != 0) {
            throw new MshopException("订单不能取消");
        }
        storeOrderService.cancelOrder(param.getId(), uid);
        return ApiResult.ok();
    }


    /**
     * 获取物流信息
     */
    @AuthCheck
    @PostMapping("/order/express")
    @ApiOperation(value = "获取物流信息", notes = "获取物流信息")
    public ApiResult<ExpressInfo> express(@RequestBody ExpressParam expressInfoDo) {

        //顺丰轨迹查询处理
        String lastFourNumber = "";
        if (expressInfoDo.getShipperCode().equals(ShipperCodeEnum.SF.getValue())) {
            MwStoreOrderDto mwStoreOrderDto;
            mwStoreOrderDto = storeOrderService.getOrderDetail(Long.valueOf(expressInfoDo.getOrderCode()));
            lastFourNumber = mwStoreOrderDto.getUserPhone();
            if (lastFourNumber.length() == 11) {
                lastFourNumber = StrUtil.sub(lastFourNumber, lastFourNumber.length(), -4);
            }
        }

        ExpressService expressService = ExpressAutoConfiguration.expressService();
        ExpressInfo expressInfo = expressService.getExpressInfo(expressInfoDo.getOrderCode(),
                expressInfoDo.getShipperCode(), expressInfoDo.getLogisticCode(), lastFourNumber);
        if (!expressInfo.isSuccess()) {
            throw new MshopException(expressInfo.getReason());
        }
        return ApiResult.ok(expressInfo);
    }

    @AuthCheck
    @GetMapping("/order/getSubscribeTemplate")
    @ApiOperation(value = "获取订阅消息模板ID", notes = "获取订阅消息模板ID")
    public ApiResult<List<String>> getSubscribeTemplate() {
        List<MwWechatTemplate> mwWechatTemplate = mwWechatTemplateService.lambdaQuery()
                .eq(MwWechatTemplate::getType, "subscribe")
                .eq(MwWechatTemplate::getStatus, ShopCommonEnum.IS_STATUS_1.getValue()).list();
        List<String> temId = mwWechatTemplate.stream().map(tem -> tem.getTempid()).collect(Collectors.toList());
        return ApiResult.ok(temId);
    }
    @AuthCheck
    @GetMapping("/order/tab/first")
    @ApiOperation(value = "订单中心tab", notes = "订单中心tab")
    public ApiResult<List<OrderTabFirstDto>> getOrderTabFirst() {
        Long uid = LocalUser.getUser().getUid();
        List<OrderTabFirstDto> tabs = new ArrayList<>();

        tabs.add(new OrderTabFirstDto("自购", false, 0, 0));
        if("tsq".equals(PAY_NAME)) {
            tabs.add(new OrderTabFirstDto("店铺订单", false, 0, 2));
        } else {
            tabs.add(new OrderTabFirstDto("热度订单", false, 0, 2));
        }
        tabs.add(new OrderTabFirstDto("金客", false, 1, 0));
        tabs.add(new OrderTabFirstDto("银客", false, 2, 0));
        tabs.add(new OrderTabFirstDto("积分订单", false, 0, null));
        return ApiResult.ok(tabs);
    }
    @AuthCheck
    @GetMapping("/order/tab")
    @ApiOperation(value = "订单中心tab", notes = "订单中心tab")
    public ApiResult<List<OrderTabDto>> getOrderTab(@RequestParam(required = false) Integer level,
                                                    @RequestParam(required = false) Integer innerType) {
        Long uid = LocalUser.getUser().getUid();
        List<OrderTabDto> tabs = new ArrayList<>();
        boolean checkOrder = true;
        if(level != null && level > 0) {
            checkOrder = false;
        }
        if(checkOrder) {
            tabs.add(new OrderTabDto("淘宝", tbOrderService.hasUnlockOrder(uid, innerType)));
            tabs.add(new OrderTabDto("京东", jdOrderService.hasUnlockOrder(uid, innerType)));
            tabs.add(new OrderTabDto("拼多多", pddOrderService.hasUnlockOrder(uid, innerType)));
            tabs.add(new OrderTabDto("抖音", dyOrderService.hasUnlockOrder(uid, innerType)));
            tabs.add(new OrderTabDto("唯品会", vipOrderService.hasUnlockOrder(uid, innerType)));
            //美团不加入热度订单
            if(innerType != 2) {
                tabs.add(new OrderTabDto("美团", mtOrderService.hasUnlockOrder(uid, innerType)));
            }
        } else {

            tabs.add(new OrderTabDto("淘宝", false));
            tabs.add(new OrderTabDto("京东", false));
            tabs.add(new OrderTabDto("拼多多", false));
            tabs.add(new OrderTabDto("抖音", false));
            tabs.add(new OrderTabDto("唯品会", false));
            if(innerType != 2) {
                tabs.add(new OrderTabDto("美团", false));
            }
        }
        return ApiResult.ok(tabs);
    }

    /**
     * 苏分宝在用，订单找回
     * */
    @NoRepeatSubmit(lockTime = 2)
    @AuthCheck
    @PostMapping("/order/submit")
    @ApiOperation(value = "提交订单", notes = "提交订单")
    public ApiResult submitOrder(@Validated @RequestBody SubmitOrderParam param) throws ExecutionException, InterruptedException {
        MwUser mwUser = LocalUser.getUser();
        tkService.submitOrder(param.getOrderId(), mwUser.getUid(), true);
        return ApiResult.ok();

    }
}

