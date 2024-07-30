/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.domain.MwStoreOrderCartInfo;
import com.mailvor.modules.order.param.OrderParam;
import com.mailvor.modules.order.service.dto.*;
import com.mailvor.modules.order.vo.ConfirmOrderVo;
import com.mailvor.modules.order.vo.OrderDataVo;
import com.mailvor.modules.order.vo.ShoperOrderTimeDataVo;
import com.mailvor.modules.order.vo.UserOrderCountVo;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.user.domain.MwUser;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwStoreOrderService extends BaseService<MwStoreOrder>{
    String cacheOrderInfo(Long uid, BigDecimal price);
    /**
     * 订单评价
     * @param orderCartInfo
     * @param user user
     * @param unique 订单orderCart唯一值
     * @param comment 评论内容
     * @param pics 图片
     * @param productScore 评分
     * @param serviceScore 评分
     */
    void orderComment(MwStoreOrderCartInfo orderCartInfo, MwUser user, String unique, String comment, String pics, String productScore,
                      String serviceScore);

    /**
     * 返回订单确认数据
     * @param mwUser mwUser
     * @return ConfirmOrderVO
     */
    ConfirmOrderVo confirmOrder(MwUser mwUser, Long goodsIs);



    /**
     * 确认订单退款
     * @param orderId 单号
     * @param price 金额
     * @param type ShopCommonEnum
     */
    void orderRefund(String orderId, BigDecimal price, Integer type);

    /**
     * 订单发货
     * @param orderId 单号
     * @param deliveryId 快递单号
     * @param deliveryName 快递公司code
     */
    void orderDelivery(String orderId,String deliveryId,String deliveryName);

    /**
     * 修改快递单号
     * @param orderId 单号
     * @param deliveryId 快递单号
     * @param deliveryName 快递公司code
     */
    void updateDelivery(String orderId,String deliveryId,String deliveryName);
    /**
     * 修改订单价格
     * @param orderId 单号
     * @param price 价格
     */
    void editOrderPrice(String orderId,String price);

    /**
     * 订单每月统计数据
     * @param page page
     * @param limit list
     * @return List<OrderDataVo>
     */
    List<OrderDataVo> getOrderDataPriceCount(int page, int limit);

    /**
     * 获取 今日 昨日 本月 订单金额
     * @return ShoperOrderTimeDataVo
     */
    ShoperOrderTimeDataVo getShoperOrderTimeData();

    /**
     * 未付款取消订单
     * @param orderId 订单号
     * @param uid 用户id
     */
    void cancelOrder(String orderId,Long uid);


    /**
     * 申请退款
     * @param explain 退款备注
     * @param Img 图片
     * @param text 理由
     * @param orderId 订单号
     * @param uid uid
     */
    void orderApplyRefund(String explain,String Img,String text,String orderId, Long uid);

    /**
     * 删除订单
     * @param orderId 单号
     * @param uid uid
     */
    void removeOrder(String orderId,Long uid);



    /**
     * 订单确认收货
     * @param orderId 单号
     * @param uid uid
     */
    void takeOrder(String orderId,Long uid);

    /**
     * 核销订单
     * @param isConfirm OrderInfoEnum
     * @param uid uid
     * @return MwStoreOrderQueryVo
     */
    MwStoreOrderQueryVo verifyOrder(Integer isConfirm , Long uid);

    /**
     * 订单列表
     * @param uid 用户id
     * @param type OrderStatusEnum
     * @param page page
     * @param limit limit
     * @return list
     */
    Map<String,Object> orderList(Long uid,int type,int page,int limit);

    /**
     * 获取某个用户的订单统计数据
     * @param uid uid>0 取用户 否则取所有
     * @return UserOrderCountVo
     */
    UserOrderCountVo orderData(Long uid);

    /**
     * 处理订单返回的状态
     * @param order order
     * @return MwStoreOrderQueryVo
     */
    MwStoreOrderQueryVo handleOrder(MwStoreOrderQueryVo order);

    /**
     * 支付成功后操作
     * @param orderId 订单号
     * @param payType 支付方式
     */
    void paySuccess(String orderId,String payType);

    /**
     * 余额支付
     * @param orderId 订单号
     * @param uid 用户id
     */
    void yuePay(String orderId,Long uid);

    /**
     * 积分兑换
     * @param orderId 订单号
     * @param uid 用户id
     */
    void integralPay(String orderId,Long uid);

    /**
     * 创建订单
     * @param userInfo 用户信息
     * @param key key
     * @param param param
     * @return MwStoreOrder
     */
    MwStoreOrder createOrder(MwUser userInfo, String key, OrderParam param, MwStoreProductQueryVo productQueryVo);

    /**
     * 订单信息
     * @param unique 唯一值或者单号
     * @param uid 用户id
     * @return MwStoreOrderQueryVo
     */
    MwStoreOrderQueryVo getOrderInfo(String unique, Long uid);


    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwStoreOrderQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwStoreOrderDto>
    */
    List<MwStoreOrder> queryAll(MwStoreOrderQueryCriteria criteria);


    //MwStoreOrderDto create(MwStoreOrder resources);

    void update(MwStoreOrder resources);
    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwStoreOrderDto> all, HttpServletResponse response) throws IOException;

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    MwStoreOrderDto getOrderDetail(Long orderId);

    Map<String,Object> queryAll(List<String> ids);




    /**
     * 根据商品分类统计订单占比
     * @return OrderCountDto
     */
    OrderCountDto getOrderCount();

    /**
     * 首页订单/用户等统计
     * @return OrderTimeDataDto
     */
    OrderTimeDataDto getOrderTimeData();

    /**
     * 返回本月订单金额与数量
     * @return map
     */
    Map<String,Object> chartCount(String platform, String type);


   void retrunStock(String orderId);

    IncomeDataDto getIncomeData();
}
