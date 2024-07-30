/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.rest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.enums.OrderLogEnum;
import com.mailvor.enums.ShipperCodeEnum;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.domain.MwStoreOrderStatus;
import com.mailvor.modules.order.domain.TkOrderParam;
import com.mailvor.modules.order.param.ExpressParam;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.service.MwStoreOrderStatusService;
import com.mailvor.modules.order.service.dto.*;
import com.mailvor.modules.tk.service.TkService;
import com.mailvor.modules.tools.express.ExpressService;
import com.mailvor.modules.tools.express.config.ExpressAutoConfiguration;
import com.mailvor.modules.tools.express.dao.ExpressInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author mazhongjun
 * @date 2019-10-14
 */
@Api(tags = "商城:订单管理")
@RestController
@RequestMapping("api")
@Slf4j
@SuppressWarnings("unchecked")
public class StoreOrderController {

    @Value("${mshop.apiUrl}")
    private String apiUrl;

    private final TkService tkService;
    private final IGenerator generator;
    private final MwStoreOrderService mwStoreOrderService;
    private final MwStoreOrderStatusService mwStoreOrderStatusService;


    public StoreOrderController(IGenerator generator, MwStoreOrderService mwStoreOrderService,
                                MwStoreOrderStatusService mwStoreOrderStatusService, TkService tkService) {
        this.generator = generator;
        this.mwStoreOrderService = mwStoreOrderService;
        this.mwStoreOrderStatusService = mwStoreOrderStatusService;
        this.tkService = tkService;
    }

    /**@Valid
     * 根据商品分类统计订单占比
     */
    @GetMapping("/mwStoreOrder/orderCount")
    @ApiOperation(value = "根据商品分类统计订单占比",notes = "根据商品分类统计订单占比",response = ExpressParam.class)
    public ResponseEntity orderCount(){
        OrderCountDto orderCountDto  = mwStoreOrderService.getOrderCount();
        return new ResponseEntity<>(orderCountDto, HttpStatus.OK);
    }

    /**
     * 首页订单/用户等统计
     * @return OrderTimeDataDto
     */
    @GetMapping(value = "/data/count")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_SELECT','MWEXPRESS_SELECT')")
    public ResponseEntity getCount() {
        return new ResponseEntity<>(mwStoreOrderService.getOrderTimeData(), HttpStatus.OK);
    }
    @GetMapping(value = "/data/income")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_SELECT','MWEXPRESS_SELECT')")
    public ResponseEntity getIncomeData() {
        return new ResponseEntity<>(mwStoreOrderService.getIncomeData(), HttpStatus.OK);
    }

    /**
     * 返回本月订单金额与数量chart
     * @return map
     */
    @GetMapping(value = "/data/chart")
    public ResponseEntity getChart(@RequestParam(name = "type") String type,
                                   @RequestParam(name = "platform") String platform) {
        return new ResponseEntity<>(mwStoreOrderService.chartCount(platform, type), HttpStatus.OK);
    }


    @ApiOperation(value = "查询订单")
    @GetMapping(value = "/mwStoreOrder")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_SELECT','MWEXPRESS_SELECT')")
    public ResponseEntity getMwStoreOrders(MwStoreOrderQueryCriteria criteria,
                                           Pageable pageable,
                                           @RequestParam(name = "orderStatus") String orderStatus,
                                           @RequestParam(name = "orderType") String orderType) {

        MwStoreOrderQueryCriteria newCriteria = this.handleQuery(criteria,orderStatus,orderType);

        return new ResponseEntity<>(mwStoreOrderService.queryAll(newCriteria, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "根据订单id获取订单详情")
    @GetMapping(value = "/getStoreOrderDetail/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_SELECT','MWEXPRESS_SELECT')")
    public ResponseEntity getMwStoreOrders(@PathVariable Long id) {
        return new ResponseEntity<>(mwStoreOrderService.getOrderDetail(id), HttpStatus.OK);
    }
    @ApiOperation(value = "查询订单当前状态流程")
    @GetMapping(value = "/getNowOrderStatus/{id}")
    public ResponseEntity getNowOrderStatus(@PathVariable Long id) {
        List<String> statusList = new ArrayList<>();
        statusList.add(OrderLogEnum.CREATE_ORDER.getValue());
        statusList.add(OrderLogEnum.PAY_ORDER_SUCCESS.getValue());
        statusList.add(OrderLogEnum.DELIVERY_GOODS.getValue());
        statusList.add(OrderLogEnum.TAKE_ORDER_DELIVERY.getValue());
        statusList.add(OrderLogEnum.EVAL_ORDER.getValue());
        List<MwStoreOrderStatus> orderStatusLogList = mwStoreOrderStatusService.list(new LambdaQueryWrapper<MwStoreOrderStatus>().eq(MwStoreOrderStatus::getOid, id).in(MwStoreOrderStatus::getChangeType, statusList).orderByDesc(MwStoreOrderStatus::getChangeTime));
        List<MwStoreOrderStatusDto> dtoList = getOrderStatusDto(orderStatusLogList);
        MwOrderNowOrderStatusDto mwOrderNowOrderStatusDto = new MwOrderNowOrderStatusDto();
        mwOrderNowOrderStatusDto.setSize(dtoList.size());
        dtoList.forEach(dto -> {
            if (OrderLogEnum.CREATE_ORDER.getDesc().equals(dto.getChangeType())) {
                mwOrderNowOrderStatusDto.setCacheKeyCreateOrder(dto.getChangeTime());
            }
            if (OrderLogEnum.PAY_ORDER_SUCCESS.getDesc().equals(dto.getChangeType())) {
                mwOrderNowOrderStatusDto.setPaySuccess(dto.getChangeTime());
            }
            if (OrderLogEnum.DELIVERY_GOODS.getDesc().equals(dto.getChangeType())) {
                mwOrderNowOrderStatusDto.setDeliveryGoods(dto.getChangeTime());
            }
            if (OrderLogEnum.TAKE_ORDER_DELIVERY.getDesc().equals(dto.getChangeType())) {
                mwOrderNowOrderStatusDto.setUserTakeDelivery(dto.getChangeTime());
                mwOrderNowOrderStatusDto.setOrderVerific(dto.getChangeTime());
            }
            if (OrderLogEnum.EVAL_ORDER.getDesc().equals(dto.getChangeType())) {
                mwOrderNowOrderStatusDto.setCheckOrderOver(dto.getChangeTime());
            }
        });


   statusList = new ArrayList<>();
        statusList.add(OrderLogEnum.REFUND_ORDER_APPLY.getValue());
        statusList.add(OrderLogEnum.REFUND_ORDER_SUCCESS.getValue());
        orderStatusLogList = mwStoreOrderStatusService.list(new LambdaQueryWrapper<MwStoreOrderStatus>().eq(MwStoreOrderStatus::getOid, id).in(MwStoreOrderStatus::getChangeType, statusList).orderByDesc(MwStoreOrderStatus::getChangeTime));
        dtoList = getOrderStatusDto(orderStatusLogList);
        dtoList.forEach(dto -> {
            if (OrderLogEnum.REFUND_ORDER_APPLY.getDesc().equals(dto.getChangeType())) {
                mwOrderNowOrderStatusDto.setApplyRefund(dto.getChangeTime());
            }
            if (OrderLogEnum.REFUND_ORDER_SUCCESS.getDesc().equals(dto.getChangeType())) {
                mwOrderNowOrderStatusDto.setRefundOrderSuccess(dto.getChangeTime());
            }

        });

        return new ResponseEntity(mwOrderNowOrderStatusDto, HttpStatus.OK);
    }
    public List<MwStoreOrderStatusDto> getOrderStatusDto(List<MwStoreOrderStatus> orderStatusLogList) {
        List<MwStoreOrderStatusDto> dtoList = orderStatusLogList.stream().map(log -> {
            MwStoreOrderStatusDto dto = generator.convert(log, MwStoreOrderStatusDto.class);
            dto.setChangeType(OrderLogEnum.getDesc(dto.getChangeType()));
            dto.setChangeTime(log.getChangeTime());
            return dto;
        }).collect(Collectors.toList());
        return dtoList;
    }
    @ApiOperation(value = "发货")
    @PutMapping(value = "/mwStoreOrder")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreOrder resources) {
        if (StrUtil.isBlank(resources.getDeliveryName())) {
            throw new BadRequestException("请选择快递公司");
        }
        if (StrUtil.isBlank(resources.getDeliveryId())) {
            throw new BadRequestException("快递单号不能为空");
        }

        mwStoreOrderService.orderDelivery(resources.getOrderId(),resources.getDeliveryId(),
                resources.getDeliveryName());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "修改快递单号")
    @PutMapping(value = "/mwStoreOrder/updateDelivery")
    @PreAuthorize("hasAnyAuthority('admin','MWSTOREORDER_ALL','MWSTOREORDER_EDIT')")
    public ResponseEntity updateDelivery(@Validated @RequestBody MwStoreOrder resources) {
        if (StrUtil.isBlank(resources.getDeliveryName())) {
            throw new BadRequestException("请选择快递公司");
        }
        if (StrUtil.isBlank(resources.getDeliveryId())) {
            throw new BadRequestException("快递单号不能为空");
        }

        mwStoreOrderService.updateDelivery(resources.getOrderId(),resources.getDeliveryId(),
                resources.getDeliveryName());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }



    @ApiOperation(value = "订单核销")
    @PutMapping(value = "/mwStoreOrder/check")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_EDIT')")
    public ResponseEntity check(@Validated @RequestBody MwStoreOrder resources) {

        MwStoreOrderDto storeOrderDTO = generator.convert(mwStoreOrderService.getById(resources.getId()), MwStoreOrderDto.class);

        if(OrderInfoEnum.PAY_STATUS_0.getValue().equals(storeOrderDTO.getPaid())){
            throw new BadRequestException("订单未支付");
        }

        mwStoreOrderService.verifyOrder(
                OrderInfoEnum.CONFIRM_STATUS_1.getValue(),null);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @ForbidSubmit
    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwStoreOrder/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id) {
        mwStoreOrderService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }


    @Log("修改订单绑定用户")
    @ApiOperation(value = "修改订单绑定用户")
    @PostMapping(value = "/mwStoreOrder/edit")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_EDIT')")
    public ResponseEntity editOrder(@RequestBody TkOrderParam param) throws ExecutionException, InterruptedException {

        tkService.submitOrder(param.getOrderId(), param.getUid(), false);
        return new ResponseEntity(HttpStatus.OK);
    }


    @Log("修改订单备注")
    @ApiOperation(value = "修改订单备注")
    @PostMapping(value = "/mwStoreOrder/remark")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_ALL','MWSTOREORDER_EDIT')")
    public ResponseEntity editOrderRemark(@RequestBody MwStoreOrder resources) {
        if (StrUtil.isBlank(resources.getRemark())) {
            throw new BadRequestException("请输入备注");
        }
        mwStoreOrderService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * 快递查询
     */
    @PostMapping("/mwStoreOrder/express")
    @ApiOperation(value = "获取物流信息",notes = "获取物流信息",response = ExpressParam.class)
    public ResponseEntity express( @RequestBody ExpressParam expressInfoDo){

        //顺丰轨迹查询处理
        String lastFourNumber = "";
        if (expressInfoDo.getShipperCode().equals(ShipperCodeEnum.SF.getValue())) {
            MwStoreOrderDto mwStoreOrderDto;
            mwStoreOrderDto = mwStoreOrderService.getOrderDetail(Long.valueOf(expressInfoDo.getOrderCode()));
            lastFourNumber = mwStoreOrderDto.getUserPhone();
            if (lastFourNumber.length()==11) {
                lastFourNumber = StrUtil.sub(lastFourNumber,lastFourNumber.length(),-4);
            }
        }

        ExpressService expressService = ExpressAutoConfiguration.expressService();
        ExpressInfo expressInfo = expressService.getExpressInfo(expressInfoDo.getOrderCode(),
                expressInfoDo.getShipperCode(), expressInfoDo.getLogisticCode(),lastFourNumber);
        if(!expressInfo.isSuccess()) {
            throw new BadRequestException(expressInfo.getReason());
        }
        return new ResponseEntity<>(expressInfo, HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/mwStoreOrder/download")
    @PreAuthorize("hasAnyRole('admin','MWSTOREORDER_SELECT')")
    public void download(HttpServletResponse response,
                         MwStoreOrderQueryCriteria criteria,
                         Pageable pageable,
                         @RequestParam(name = "orderStatus") String orderStatus,
                         @RequestParam(name = "orderType") String orderType,
                         @RequestParam(name = "listContent") String listContent) throws IOException, ParseException {
        List<MwStoreOrderDto> list;
        if(StringUtils.isEmpty(listContent)){
            list =  (List)getMwStoreList(criteria, pageable, orderStatus, orderType).get("content");
        }else {
            List<String> idList = JSONArray.parseArray(listContent).toJavaList(String.class);
            list = (List)mwStoreOrderService.queryAll(idList).get("content");
        }
        mwStoreOrderService.download(list, response);
    }

    /**
     * 下载数据
     * @param criteria criteria
     * @param pageable pageable
     * @param orderStatus orderStatus
     * @param orderType orderType
     * @return Map
     */
    private Map<String,Object> getMwStoreList(MwStoreOrderQueryCriteria criteria,
                                              Pageable pageable,
                                              String orderStatus,
                                              String orderType){

        MwStoreOrderQueryCriteria newCriteria = this.handleQuery(criteria,orderStatus,orderType);
        return mwStoreOrderService.queryAll(newCriteria, pageable);
    }



    /**
     * 处理订单查询
     * @param criteria MwStoreOrderQueryCriteria
     * @param orderStatus 订单状态
     * @param orderType 订单类型
     * @return MwStoreOrderQueryCriteria
     */
    private MwStoreOrderQueryCriteria handleQuery(MwStoreOrderQueryCriteria criteria, String orderStatus,
                                                  String orderType){

        //订单状态查询
        if (StrUtil.isNotEmpty(orderStatus)) {
            switch (orderStatus) {
                case "0":
                    criteria.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
                    criteria.setStatus(OrderInfoEnum.STATUS_0.getValue());
                    criteria.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());
                    break;
                case "1":
                    criteria.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                    criteria.setStatus(OrderInfoEnum.STATUS_0.getValue());
                    criteria.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());
                    break;
                case "2":
                    criteria.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                    criteria.setStatus(OrderInfoEnum.STATUS_1.getValue());
                    criteria.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());
                    break;
                case "3":
                    criteria.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                    criteria.setStatus(OrderInfoEnum.STATUS_2.getValue());
                    criteria.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());
                    break;
                case "4":
                    criteria.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                    criteria.setStatus(OrderInfoEnum.STATUS_3.getValue());
                    criteria.setRefundStatus(OrderInfoEnum.REFUND_STATUS_0.getValue());
                    break;
                case "-1":
                    criteria.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                    criteria.setRefundStatus(OrderInfoEnum.REFUND_STATUS_1.getValue());
                    break;
                case "-2":
                    criteria.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
                    criteria.setRefundStatus(OrderInfoEnum.REFUND_STATUS_2.getValue());
                    break;
                default:
            }
        }
        //订单类型查询
        if (StrUtil.isNotEmpty(orderType)) {
            switch (orderType) {
                case "1":
                    criteria.setBargainId(0);
                    criteria.setCombinationId(0);
                    criteria.setSeckillId(0);
                    break;
                case "2":
                    criteria.setNewCombinationId(0);
                    break;
                case "3":
                    criteria.setNewSeckillId(0);
                    break;
                case "4":
                    criteria.setNewBargainId(0);
                    break;
                case "5":
                    break;
                case "6":
                    criteria.setPayIntegral(new BigDecimal("0.00"));
                    break;
                default:
            }
        }

        return criteria;
    }



}
