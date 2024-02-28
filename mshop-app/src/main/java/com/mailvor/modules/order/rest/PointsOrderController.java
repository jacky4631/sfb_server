/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.rest;

import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.mp.service.WeixinPayService;
import com.mailvor.modules.mp.service.MwWechatTemplateService;
import com.mailvor.modules.order.param.ConfirmIntegralParam;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.order.service.MwStoreOrderCartInfoService;
import com.mailvor.modules.order.service.MwStoreOrderStatusService;
import com.mailvor.modules.services.CreatShareProductService;
import com.mailvor.modules.services.OrderSupplyService;
import com.mailvor.modules.user.domain.MwUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
public class PointsOrderController {

    private final SuStoreOrderService suStoreOrderService;
    private final OrderSupplyService orderSupplyService;
    private final CreatShareProductService creatShareProductService;
    private final MwWechatTemplateService mwWechatTemplateService;
    private final MwStoreOrderStatusService orderStatusService;

    private final MwStoreOrderCartInfoService storeOrderCartInfoService;
    private final WeixinPayService weixinPayService;

    @Value("${file.path}")
    private String path;


    /**
     * 判断积分是否足够
     */
    @AppLog(value = "积分确认", type = 1)
    @AuthCheck
    @PostMapping("/integral/confirm")
    @ApiOperation(value = "积分确认", notes = "积分确认")
    public ApiResult<Boolean> confirm(@Validated @RequestBody ConfirmIntegralParam param) {
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(suStoreOrderService.confirmIntegral(mwUser, param.getId()));

    }

}

