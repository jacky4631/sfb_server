/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;


import com.alibaba.fastjson.JSON;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.aop.NoRepeatSubmit;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.modules.activity.param.UserExtParam;
import com.mailvor.modules.activity.service.MwUserExtractConfigService;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.activity.service.dto.MwExtractConfigDto;
import com.mailvor.modules.activity.service.dto.MwUserExtractQueryCriteria;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.enums.PayChannelEnum;
import com.mailvor.modules.pay.param.ExtractBankBindConfirmParam;
import com.mailvor.modules.pay.param.ExtractBankBindParam;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.ysepay.YsePayService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.domain.MwUserExtra;
import com.mailvor.modules.user.service.MwUserBankService;
import com.mailvor.modules.user.service.MwUserCardService;
import com.mailvor.modules.user.service.MwUserExtraService;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import com.yinsheng.command.smsVerify.ConfirmVerifyRespCommand;
import com.yinsheng.command.wallet.BindCardRespCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * <p>
 * 用户提现 前端控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-11-11
 */
@Slf4j
@RestController
@Api(value = "用户提现", tags = "用户:用户提现")
public class UserExtractController {
    @Resource
    private MwUserExtractService userExtractService;
    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MwPayChannelService payChannelService;

    @Resource
    private YsePayService ysePayService;
    @Resource
    private MwUserCardService cardService;
    @Resource
    private MwUserExtraService userExtraService;

    @Resource
    private MwUserBankService userBankService;

    @Resource
    private MwUserExtractConfigService extractConfigService;

    /**
     * 提现参数
     */
    @AuthCheck
    @GetMapping("/extract/bank")
    @ApiOperation(value = "提现参数",notes = "提现参数")
    public ApiResult<Object> bank(){
        MwUser mwUser = LocalUser.getUser();
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("commissionCount",mwUser.getBrokeragePrice());
        map.put("minPrice",systemConfigService.getData(SystemConfigConstants.USER_EXTRACT_MIN_PRICE));
        return ApiResult.ok(map);
    }


    @AppLog(value = "用户提现配置", type = 1)
    @AuthCheck
    @GetMapping("/extract/config")
    @ApiOperation(value = "用户提现配置",notes = "用户提现配置")
    public ApiResult<MwExtractConfigDto> extractConfig(){
        MwExtractConfigDto obj = systemConfigService.getAppExtractConfig();
        return ApiResult.ok(obj);
    }
    /**
    * 用户提现
    */
    @AppLog(value = "用户提现", type = 1)
    @AuthCheck
    @NoRepeatSubmit
    @PostMapping("/extract/cash")
    @ApiOperation(value = "用户提现",notes = "用户提现")
    public ApiResult<String> addMwUserExtract(HttpServletRequest request, @Valid @RequestBody UserExtParam param){
        if("bank".equals(param.getExtractType())) {
            if(param.getBankId() == null) {
                throw new MshopException("银行id必传");
            }
            MwUserBank userBank = userBankService.getById(param.getBankId());
            if(userBank == null) {
                throw new MshopException("银行卡不存在");
            }
            if(StringUtils.isBlank(userBank.getLinkId())) {
                throw new MshopException("提现银行卡未绑定");
            }
        }
        MwUser mwUser = LocalUser.getUser();
        if(!extractConfigService.canExtract(mwUser.getUid())) {
            log.error("用户uid:{}被禁止提现", mwUser.getUid());
            throw new MshopException("提现失败");
        }
        String ip = StringUtils.getIp(request);
        userExtractService.userExtract(mwUser,param, ip);
        return ApiResult.ok("申请提现成功");
    }

    /**
     * 提现列表
     */
    @AppLog(value = "提现列表", type = 1)
    @AuthCheck
    @GetMapping("/extract/list")
    @ApiOperation(value = "提现列表",notes = "提现列表")
    public ApiResult<Map> getMwUserExtractList(Pageable pageable){
        MwUser mwUser = LocalUser.getUser();
        MwUserExtractQueryCriteria criteria = new MwUserExtractQueryCriteria();
        criteria.setUid(mwUser.getUid());
        Map<String,Object> map = userExtractService.queryAll(criteria, pageable);
        return ApiResult.ok(map);
    }


    @AppLog(value = "提现绑卡", type = 1)
    @AuthCheck
    @PostMapping("/extract/bank/bind")
    @ApiOperation(value = "提现绑卡",notes = "提现绑卡")
    public ApiResult extractBankBind(@Valid @RequestBody ExtractBankBindParam param) {
        if(param.getBankId() == null && !StringUtils.isPhone(param.getPhone())) {
            throw new MshopException("不是正确的手机号");
        }
        Long uid = LocalUser.getUser().getUid();
        if(param.getBankId() != null) {
            MwUserBank userBank = userBankService.getById(param.getBankId());
            if(userBank == null) {
                throw new MshopException("银行卡不存在");
            }
            if(StringUtils.isNotBlank(userBank.getLinkId())) {
                throw new MshopException("提现银行卡已绑定");
            }
            if(userBank.getUid() != uid) {
                throw new MshopException("不是该用户的银行卡");
            }
            param.setBankNo(userBank.getBankNo());
            param.setPhone(userBank.getPhone());
        } else {
            if(StringUtils.isBlank(param.getBankNo()) || StringUtils.isBlank(param.getPhone())) {
                throw new MshopException("银行卡号和手机号不能为空");
            }
        }

        PayChannelDto payChannel = payChannelService.channelDto(uid, 4);

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }

        MwUserCardQueryVo userCard = cardService.getUserCardById(uid);

        MwUserExtra userExtra = userExtraService.getById(uid);
        if(userExtra == null) {
            userExtra = ysePayService.walletRegister(payChannel, uid, userCard.getCardName(), userCard.getCardNo(), param.getPhone());
        }


        String key = payChannel.getChannelKey();
        Map<String, Object> data = new HashMap<>();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:
                BindCardRespCommand resp = ysePayService.bindCard(payChannel, userExtra.getMerchantNo(), param.getBankNo(), param.getPhone());
                log.info("extractBankBind param: {}  res: {}", JSON.toJSONString(param), JSON.toJSONString(resp));
                data.put("requestNo", resp.getRequestNo());
                data.put("authSn", resp.getAuthSn());
                MwUserBank userBank = userBankService.findOne(uid, param.getBankNo());
                if(userBank == null) {
                    userBank = MwUserBank.builder().bankNo(param.getBankNo())
                            .phone(param.getPhone()).uid(uid).build();
                    userBankService.save(userBank);
                    //初次绑定设置为默认提现卡
                    userBankService.setExtractDefault(uid, userBank.getId());
                }
                //保存requestNO和bankNo的关联信息到redis，短信确认时用到
                redisUtils.set("bing:card:requestNo:" + resp.getRequestNo(), param.getBankNo(), 600);
                break;
        }
        return ApiResult.ok(data);
    }

    @AppLog(value = "提现绑卡短信确认", type = 1)
    @AuthCheck
    @PostMapping("/extract/bank/bind/confirm")
    @ApiOperation(value = "提现绑卡短信确认",notes = "提现绑卡短信确认")
    public ApiResult extractBankBindConfirm(@Valid @RequestBody ExtractBankBindConfirmParam param){
        MwUser loginUser = LocalUser.getUser();
        PayChannelDto payChannel = payChannelService.channelDto(loginUser.getUid(), 4);

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }
        Object bankNoObj = redisUtils.get("bing:card:requestNo:" + param.getRequestNo());
        if(bankNoObj == null) {
            throw new MshopException("短信确认失败");
        }
        String bankNo = (String)bankNoObj;
        String key = payChannel.getChannelKey();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:
                //绑卡确认
                ConfirmVerifyRespCommand resp = ysePayService.confirmVerify(payChannel, param.getRequestNo(), param.getAuthSn(), param.getCode());
                //保存绑卡标识
                MwUserBank userBank = userBankService.findOne(loginUser.getUid(), bankNo);
                Map<String, String> cardMap = (Map)resp.getCardInfo();
                userBank.setLinkId(cardMap.get("linkId"));
                if(StringUtils.isBlank(userBank.getBankName())) {
                    userBank.setBankName(cardMap.get("bankName"));
                }
                if(StringUtils.isBlank(userBank.getCardType())) {
                    userBank.setCardType(cardMap.get("cardType"));
                }
                userBankService.updateById(userBank);

                log.info("payBankBind param: {}", JSON.toJSONString(param));
                break;
        }
        return ApiResult.ok();

    }

}

