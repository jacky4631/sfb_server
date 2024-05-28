/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.energy.service.UserEnergyLogService;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.services.CreatShareProductService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.domain.TkOrder;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.param.MwUserBillQueryParam;
import com.mailvor.modules.user.param.PromParam;
import com.mailvor.modules.user.service.MwSystemUserLevelService;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.dto.PromUserDto;
import com.mailvor.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * @ClassName UserBillController
 * @author huangyu
 * @Date 2019/11/10
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "用户分销", tags = "用户:用户分销")
public class UserBillController {

    private final MwUserBillService userBillService;
    private final MwUserExtractService extractService;
    private final MwSystemConfigService systemConfigService;
    private final MwUserService mwUserService;
    private final CreatShareProductService creatShareProductService;
    private final MwSystemUserLevelService systemUserLevelService;

    private final MailvorTbOrderService tbOrderService;
    private final MailvorJdOrderService jdOrderService;
    private final MailvorPddOrderService pddOrderService;
    private final MailvorVipOrderService vipOrderService;
    private final MailvorDyOrderService dyOrderService;
    private final MailvorMtOrderService mtOrderService;
    private final SuStoreOrderService suStoreOrderService;

    private final UserEnergyLogService userEnergyLogService;
    @Value("${file.path}")
    private String path;

    /**
     * 推广数据    昨天的佣金   累计提现金额  当前佣金
     */
    @AppLog(value = "查看推广数据", type = 1)
    @AuthCheck
    @GetMapping("/commission")
    @ApiOperation(value = "推广数据",notes = "推广数据")
    public ApiResult<Map<String,Object>> commission(){
        MwUser mwUser = LocalUser.getUser();

        //昨天的佣金
        double lastDayCount = userBillService.yesterdayCommissionSum(mwUser.getUid());
        //累计提现金额
        double extractCount = extractService.extractSum(mwUser.getUid());

        Map<String,Object> map = Maps.newHashMap();
        map.put("lastDayCount",lastDayCount);
        map.put("extractCount",extractCount);
        map.put("commissionCount",mwUser.getBrokeragePrice());

        return ApiResult.ok(map);
    }

    /**
     * 积分记录
     */
    @AppLog(value = "查看账单记录", type = 1)
    @AuthCheck
    @GetMapping("/integral/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码,默认为1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "页大小,默认为10", paramType = "query", dataType = "int")
    })
    @ApiOperation(value = "账单记录",notes = "账单记录")
    public ApiResult<Object> userInfo(@RequestParam(value = "page",defaultValue = "1") int page,
                                      @RequestParam(value = "limit",defaultValue = "10") int limit,
                                      @RequestParam(value = "category",defaultValue = "integral") String category,
                                      @RequestParam(value = "type") String type,
                                      @RequestParam(value = "platform") String platform,
                                      @RequestParam(value = "unlockStatus", required = false) Integer unlockStatus){
        Long uid = LocalUser.getUser().getUid();
        Map<String, Object> map = userBillService.userBillList(uid, category, type, platform
                ,page, limit, unlockStatus);
        Long total = (Long) map.get("total");
        //todo 分页貌似没实现
        Long totalPage = (Long) map.get("totalPage");
        return ApiResult.resultPage(total, totalPage.intValue(), map.get("list"));
    }
    /**
     * 积分记录
     */
    @AppLog(value = "查看热度记录", type = 1)
    @AuthCheck
    @GetMapping("/energy/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码,默认为1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "页大小,默认为10", paramType = "query", dataType = "int")
    })
    @ApiOperation(value = "热度记录",notes = "热度记录")
    public ApiResult<Object> energyList(@RequestParam(value = "page",defaultValue = "1") int page,
                                      @RequestParam(value = "limit",defaultValue = "10") int limit){
        Long uid = LocalUser.getUser().getUid();
        Map<String, Object> map = userEnergyLogService.logList(uid, page, limit);
        return ApiResult.ok(map);
    }

    /**
     * 分销二维码海报生成
     */
    @AppLog(value = "分销二维码海报生成", type = 1)
    @AuthCheck
    @GetMapping("/spread/banner")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "form", value = "来源", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "分销二维码海报生成",notes = "分销二维码海报生成")
    public ApiResult<List<Map<String,Object>>> spreadBanner(@RequestParam(value = "",required=false) String from){
        MwUser mwUser = LocalUser.getUser();
        String siteUrl = systemConfigService.getData(SystemConfigConstants.SITE_URL);
        if(StrUtil.isEmpty(siteUrl)){
            throw new MshopException("未配置h5地址!");
        }
        String apiUrl = systemConfigService.getData(SystemConfigConstants.API_URL);
        if(StrUtil.isEmpty(apiUrl)){
            throw new MshopException("未配置api地址!");
        }

        String spreadUrl = creatShareProductService.getSpreadUrl(from,mwUser,siteUrl,apiUrl,path);

        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> map = Maps.newHashMap();
        map.put("id",1);
        map.put("pic","");
        map.put("title","分享海报");
        map.put("wap_poster",spreadUrl);
        list.add(map);
        return ApiResult.ok(list);
    }


    /**
     *  推广人统计
     */
    @AppLog(value = "查看推广人统计", type = 1)
    @AuthCheck
    @PostMapping("/spread/people")
    @ApiOperation(value = "推广人统计",notes = "推广人统计")
    public ApiResult<Map<String,Object>> spreadPeople(@Valid @RequestBody PromParam param){
        Long uid = LocalUser.getUser().getUid();
        Map<String,Object> map = new LinkedHashMap<>();
        if(param.getUid() == null) {
            //查询金客
            map.put("list",mwUserService.getUserSpreadGrade(uid,param.getPage(),param.getLimit()
                    ,param.getGrade(),param.getKeyword(),param.getSort()));

            Map<String,Long> countMap = mwUserService.getSpreadCount(uid);
            if(param.getGrade() == 1) {
                map.put("total",countMap.get("second"));
            } else {
                map.put("total",countMap.get("first"));
            }
        } else {
            //查询银客
            //todo 校验param.uid是否是当前登录用户的用户
            List<PromUserDto> fansList = mwUserService.getUserSpreadGrade(param.getUid(), param.getPage(),param.getLimit()
                    ,param.getGrade(),param.getKeyword(),param.getSort());
            fansList.forEach(promUserDto -> {
                promUserDto.setChildCount(null);
            });
            map.put("list",fansList);

            Map<String,Long> countMap = mwUserService.getSpreadCount(param.getUid());
            map.put("total",countMap.get("first"));
            map.put("totalLevel",countMap.get("second"));
        }

        return ApiResult.ok(map);
    }

    /**
     * 推广佣金明细
     * type  0 全部  1 消费  2 充值  3 返佣  4 提现
     * @return mixed
     */
    @AppLog(value = "查看推广佣金明细", type = 1)
    @AuthCheck
    @GetMapping("/spread/commission/{type}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码,默认为1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "页大小,默认为10", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "类型 0所有 1消费 2充值 3返佣 4提现 5签到积分 6退款 7系统增加 8系统减少", paramType = "query", dataType = "int")
    })
    @ApiOperation(value = "推广佣金明细",notes = "推广佣金明细")
    public ApiResult<Object> spreadCommission(@RequestParam(value = "page",defaultValue = "1") int page,
                                              @RequestParam(value = "limit",defaultValue = "10") int limit,
                                              @PathVariable String type,
                                              @RequestParam(required = false, value = "month") String month){
        int newType = 0;
        if(NumberUtil.isNumber(type)) {
            newType = Integer.valueOf(type);
        }
        Long uid = LocalUser.getUser().getUid();
        Map<String, Object> map = userBillService.getUserBillList(page,limit,uid,newType, month);
        Long total = (Long)map.get("total");
        Long totalPage = (Long)map.get("totalPage");
        return ApiResult.resultPage(total,totalPage.intValue(),map.get("list"));
      //  return ApiResult.resultPage(Collections.singletonList(userBillService.getUserBillList(page,limit,uid,newType)),limit);
    }


    /**
     * 推广订单
     */
    @AppLog(value = "查看推广订单", type = 1)
    @AuthCheck
    @PostMapping("/spread/order")
    @ApiOperation(value = "推广订单",notes = "推广订单")
    public ApiResult<Object> spreadOrder(@RequestBody MwUserBillQueryParam param){
        Long uid = LocalUser.getUser().getUid();
        Map<String, Object> map = userBillService.spreadOrder(uid,param.getPage(),param.getLimit());
        return ApiResult.ok(map);
    }

    /**
     *  拆红包奖励金额
     */
    @AppLog(value = "拆红包奖励金额", type = 1)
    @AuthCheck
    @GetMapping("/spread/hb")
    @ApiOperation(value = "拆红包奖励金额",notes = "拆红包奖励金额")
    @Transactional
    public ApiResult<Object> spreadHb(@RequestParam(value = "orderId") String orderId,
                                      @RequestParam(value = "type")String type,
                                      @RequestParam(value = "skuId")Long skuId){
        if(StringUtils.isBlank(orderId) || StringUtils.isBlank(type)) {
            throw new MshopException("参数错误");
        }
        Long uid = LocalUser.getUser().getUid();
        TkOrder tkOrder = null;
        //订单校验
        if("tb".equals(type)) {
            tkOrder = tbOrderService.getById(orderId);
        }else if("jd".equals(type)) {
            LambdaQueryWrapper<MailvorJdOrder> wrapperO = new LambdaQueryWrapper<>();
            wrapperO.eq(MailvorJdOrder::getOrderId, orderId)
                    .eq(MailvorJdOrder::getBind, 0)
                    .ne(MailvorJdOrder::getPrice, 0).last("limit 1");
            tkOrder =  jdOrderService.getOne(wrapperO);
        }else if("pdd".equals(type)) {
            tkOrder = pddOrderService.getById(orderId);
        }else if("vip".equals(type)) {
            tkOrder = vipOrderService.getById(orderId);
        }else if("dy".equals(type)) {
            tkOrder = dyOrderService.getById(orderId);
        }else if("mt".equals(type)) {
            //这里有可能会查到多条美团订单，后续优化
            tkOrder = mtOrderService.getOne(Wrappers.<MailvorMtOrder>lambdaQuery()
                    .eq(MailvorMtOrder::getOrderId,orderId)
                    .last("limit 1"));
        }
        Date unlockTime = suStoreOrderService.checkOrder(tkOrder, uid);
        Map map = suStoreOrderService.incMoneyAndBindOrder(uid, tkOrder, unlockTime);
        return ApiResult.ok(map);
    }

}
