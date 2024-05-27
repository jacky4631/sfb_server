/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.api.ApiResult;
import com.mailvor.api.BusinessException;
import com.mailvor.api.MshopException;
import com.mailvor.enums.AppFromEnum;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.modules.pay.adapay.AdaPayService;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.pay.allinpay.syb.SybService;
import com.mailvor.modules.pay.wechat.WechatPayService;
import com.mailvor.modules.pay.yeepay.YeePayService;
import com.mailvor.modules.pay.ysepay.YsePayService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.StringUtils;
import com.yinsheng.utils.YsChannelClientException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName WechatController
 * @author huangyu
 * @Date 2019/11/5
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "支付回调", tags = "支付:回调模块")
public class PayNotifyController {

    // 沙盒环境
    private static final String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";
    // 生产环境
    private static final String url_verify = "https://buy.itunes.apple.com/verifyReceipt";

    @Resource
    private SybService sybService;

    @Resource
    private AdaPayService adaPayService;

    @Resource
    private AliPayService aliPayService;

    @Resource
    private WechatPayService wechatPayService;

    @Resource
    private YeePayService yeePayService;

    @Resource
    private YsePayService ysePayService;

    @Resource
    private MwUserRechargeService userRechargeService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private MwUserService userService;
    /**
     * 微信支付/充值回调
     */
    @AnonymousAccess
    @PostMapping("/pay/notify/syb")
    @ApiOperation(value = "充值回调",notes = "充值回调")
    @Transactional
    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sybService.notify(request, response);
    }

    @AnonymousAccess
    @PostMapping("/pay/notify/ada")
    @ApiOperation(value = "汇付充值回调",notes = "汇付充值回调")
    @Transactional
    public void adaPayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        adaPayService.notify(request, response);
    }
    @AnonymousAccess
    @PostMapping("/pay/notify/ali")
    @ApiOperation(value = "支付宝充值回调",notes = "支付宝充值回调")
    @Transactional
    public String aliPayNotify(HttpServletRequest request) throws IOException, AlipayApiException {
        return aliPayService.notify(request);
    }

    /**
     * 微信支付/充值回调
     */
    @AnonymousAccess
    @PostMapping("/pay/notify/wechat")
    @ApiOperation(value = "微信APP支付回调",notes = "微信APP支付回调")
    public String wechatNotify(@RequestBody String xmlData) throws AlipayApiException, IOException {
        return wechatPayService.notify(AppFromEnum.APP, xmlData);

    }

    @AnonymousAccess
    @PostMapping("/pay/notify/yee")
    @ApiOperation(value = "易宝充值回调",notes = "易宝充值回调")
    public String yeePayNotify(HttpServletRequest request) throws IOException {
        return yeePayService.notify(request);
    }

    @AnonymousAccess
    @PostMapping("/pay/notify/yse")
    @ApiOperation(value = "银盛充值回调",notes = "银盛充值回调")
    public String ysePayNotify(HttpServletRequest request) throws YsChannelClientException {
        return ysePayService.notify(request);
    }

    @AnonymousAccess
    @PostMapping("/ios/notify")
    @ApiOperation(value = "ios回调",notes = "ios回调")
    public ApiResult iosNotify(@RequestBody JSONObject body) {
        log.info("body:{}", JSON.toJSONString(body));
        String orderSn = body.getString("orderSn");
        String receipt = body.getString("receipt");
        if(StringUtils.isBlank(orderSn) || StringUtils.isBlank(receipt)) {
            throw new BusinessException("参数错误");
        }
        //找回充值的订单
        MwUserRecharge userRecharge = userRechargeService.getInfoByOrderId(orderSn);
        if(userRecharge == null) {
            throw new BusinessException("充值订单不存在");
        }
        if(userRecharge.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new MshopException("该订单已支付");
        }
        //根据不同的环境，选择是去测试环境还是开发环境验证
        //type null 0 生产 其他沙箱
        Integer type = body.getInteger("type");
        String verifyUrl;
        if(type == null || type == 0) {
            verifyUrl = url_verify;
        } else {
            verifyUrl = url_sandbox;
        }
        JSONObject obj = new JSONObject();
        obj.put("receipt-data", receipt);
        obj.put("password", "c3d6eace374c4f038c5d2c8df42b3793");
        JSONObject job = restTemplate.postForObject(verifyUrl, obj, JSONObject.class);

        if (job != null) {
            log.info("job:{}", JSON.toJSONString(job));
            String states = job.getString("status");
//21000 App Store无法读取你提供的JSON数据
//21002 收据数据不符合格式
//21003 收据无法被验证
//21004 你提供的共享密钥和账户的共享密钥不一致
//21005 收据服务器当前不可用
//21006 收据是有效的，但订阅服务已经过期。当收到这个信息时，解码后的收据信息也包含在返回内容中
//21007 收据信息是测试用（sandbox），但却被发送到产品环境中验证
//21008 收据信息是产品环境中使用，但却被发送到测试环境中验证
            if ("21007".equals(states)) {
                log.debug("是沙盒环境，应沙盒测试，否则执行下面");
                // 是沙盒环境，应沙盒测试，否则执行下面
                // 2.再沙盒测试  发送平台验证
                job = restTemplate.postForObject(url_sandbox, obj, JSONObject.class);
                log.debug("3，沙盒环境验证返回的json字符串=" + job.toString());
                states = job.getString("status");
            }
            if ("0".equals(states)) { // 前端所提供的收据是有效的    验证成功
                log.debug("前端所提供的收据是有效的    验证成功");
                JSONObject r_receipt = job.getObject("receipt", JSONObject.class);
                JSONArray in_app = r_receipt.getObject("in_app",JSONArray.class);

                /**
                 * in_app说明：
                 * 验证票据返回的receipt里面的in_app字段，这个字段包含了所有你未完成交易的票据信息。也就是在上面说到的APP完成交易之后，这个票据信息，就会从in_app中消失。
                 * 如果APP不完成交易，这个票据信息就会在in_app中一直保留。(这个情况可能仅限于你的商品类型为消耗型)
                 *
                 * 知道了事件的原委，就很好优化解决了，方案有2个
                 * 1.对票据返回的in_app数据全部进行处理，没有充值的全部进行充值
                 * 2.仅对最新的充值信息进行处理（我们采取的方案）
                 *
                 * 因为采用一方案：
                 * 如果用户仅进行了一次充值，该充值未到账，他不再进行充值了，那么会无法导致。
                 * 如果他通过客服的途径已经进行了补充充值，那么他在下一次充值的时候依旧会把之前的产品票据带回，这时候有可能出现重复充值的情况
                 *
                 */
                if (!in_app.isEmpty()) {
                    String productId;
                    //todo 月会员00003 暂时写死 后期优化
                    if(userRecharge.getType() == 2) {
                        productId = "00003";
                    } else {
                        productId = OrderUtil.getIosProductId(userRecharge.getPlatform(), userRecharge.getGrade());
                    }
                    Object findProduct = in_app.stream().filter(inAppObj-> productId.equals(((JSONObject)inAppObj).getString("product_id"))).findFirst().orElse(null);

                    //判断product_id，看返回的product_id与实际的充值金额是不是一致，防止骗单
                    if(findProduct == null){
                        throw new MshopException("该订单非法");
                    }
                    JSONObject o = (JSONObject) findProduct;
                    log.info("订单参数: {}", o.toJSONString());

                    //将订单更改为已支付
                    userService.setUserLevel(orderSn);
                    return ApiResult.ok();
                }
            }
        }else{
            //记录错误日志
        }
        return ApiResult.fail("支付失败，请联系客服");

    }

}
