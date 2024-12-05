/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.enums.PayChannelEnum;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.ysepay.YsePayService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.config.AppDataConfig;
import com.mailvor.modules.user.config.HbUnlockConfig;
import com.mailvor.modules.user.domain.*;
import com.mailvor.modules.user.param.MwUserParam;
import com.mailvor.modules.user.rest.param.BankBindConfirmParam;
import com.mailvor.modules.user.rest.param.BankBindParam;
import com.mailvor.modules.user.rest.param.BankExtractParam;
import com.mailvor.modules.user.service.*;
import com.mailvor.modules.user.service.dto.MwUserQueryCriteria;
import com.mailvor.modules.user.service.dto.UserIntegralDto;
import com.mailvor.modules.user.service.dto.UserMoneyDto;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import com.yinsheng.command.smsVerify.ConfirmVerifyRespCommand;
import com.yinsheng.command.wallet.BindCardRespCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

/**
* @author huangyu
* @date 2019-10-06
*/
@Slf4j
@Api(tags = "商城：会员管理")
@RestController
@RequestMapping("api")
public class MemberController {

    @Resource
    private MwUserService mwUserService;

    @Resource
    private MwUserUnionService userUnionService;
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MwUserPoolService userPoolService;
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
    private MwSystemConfigService systemConfigService;

    @Log("查看下级")
    @ApiOperation(value = "查看下级")
    @PostMapping(value = "/mwUser/spread")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity getSpread(@RequestBody MwUserQueryCriteria criteria){
        return new ResponseEntity<>(mwUserService.querySpread(criteria.getUid(),criteria.getGrade()),
                HttpStatus.OK);
    }

    @Log("查询用户")
    @ApiOperation(value = "查询用户")
    @GetMapping(value = "/mwUser")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity getMwUsers(MwUserQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwUserService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询用户额外信息")
    @ApiOperation(value = "查询用户额外信息")
    @GetMapping(value = "/mwUserExtra/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity getUserExtra(@PathVariable Long uid){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("extra", userExtraService.getById(uid));
        jsonObject.put("union", userUnionService.getByUid(uid));
        return new ResponseEntity<>(jsonObject,HttpStatus.OK);
    }
    @Log("修改用户")
    @ApiOperation(value = "修改用户")
    @PutMapping(value = "/mwUser")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwUserParam param){
        MwUser user = mwUserService.getById(param.getUid());
        if(user == null) {
            throw new MshopException("用户不存在");
        }
        user.setRealName(param.getRealName());
        user.setSpreadUid(param.getSpreadUid());
        user.setPhone(param.getPhone());
        user.setCode(param.getCode());
        user.setAdditionalNo(param.getAdditionalNo());
        user.setMark(param.getMark());
        user.setLevel(param.getLevel());
        user.setLevelJd(param.getLevelJd());
        user.setLevelPdd(param.getLevelPdd());
        user.setLevelDy(param.getLevelDy());
        user.setLevelVip(param.getLevelVip());
        user.setExpired(param.getExpired());
        user.setExpiredJd(param.getExpiredJd());
        user.setExpiredPdd(param.getExpiredPdd());
        user.setExpiredDy(param.getExpiredDy());
        user.setExpiredVip(param.getExpiredVip());

        mwUserService.saveOrUpdate(user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @Log("删除用户微信授权信息")
    @ApiOperation(value = "删除用户微信授权信息")
    @DeleteMapping(value = "/mwUser/wechat/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ResponseEntity deleteWechat(@Validated @RequestBody MwUserParam param){
        MwUser user = mwUserService.getById(param.getUid());
        if(user == null) {
            throw new MshopException("用户不存在");
        }
        MwUserUnion userUnion = userUnionService.getOne(param.getUid());
        if(userUnion != null) {
            userUnion.setWxProfile(null);
            userUnion.setOpenId(null);
        }
        userUnionService.updateById(userUnion);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @Log("删除用户支付宝授权信息")
    @ApiOperation(value = "删除用户支付宝授权信息")
    @DeleteMapping(value = "/mwUser/ali/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ResponseEntity deleteAli(@Validated @RequestBody MwUserParam param){
        MwUser user = mwUserService.getById(param.getUid());
        if(user == null) {
            throw new MshopException("用户不存在");
        }
        user.setAliProfile(null);

        mwUserService.updateById(user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    @Log("重置退款次数")
    @ApiOperation(value = "重置退款次数")
    @PostMapping(value = "/mwUser/reset/refund/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ResponseEntity resetRefund(@PathVariable Long uid, @Validated @RequestBody MwUserParam param){
        MwUser user = mwUserService.getById(uid);
        if(user == null) {
            throw new MshopException("用户不存在");
        }
        MwUserPool userPool = userPoolService.getById(uid);
        if(userPool == null || userPool.getRefund() == null || userPool.getRefund() == 0) {
            throw new MshopException("用户未退款，无需重置");
        }
        userPoolService.resetRefund(uid);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    @ForbidSubmit
    @Log("删除用户")
    @ApiOperation(value = "删除用户")
    @DeleteMapping(value = "/mwUser/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_DELETE')")
    public ResponseEntity delete(@PathVariable Integer uid){
        mwUserService.removeById(uid);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ForbidSubmit
    @ApiOperation(value = "用户禁用启用")
    @PostMapping(value = "/mwUser/onStatus/{id}")
    public ResponseEntity onStatus(@PathVariable Long id,@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Integer status = jsonObject.getInteger("status");
        mwUserService.onStatus(id,status);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "修改余额")
    @PostMapping(value = "/mwUser/money")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ResponseEntity updatePrice(@Validated @RequestBody UserMoneyDto param){
        if(param.getMoney() <= 0) {
            throw new MshopException("金额不能为0");
        }
        mwUserService.updateMoney(param);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "修改积分")
    @PostMapping(value = "/mwUser/integral")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ResponseEntity updateIntegral(@Validated @RequestBody UserIntegralDto param){
        if(param.getIntegral() <= 0) {
            throw new MshopException("积分不能为0");
        }
        mwUserService.updateIntegral(param);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("后台提现绑卡")
    @ApiOperation(value = "后台提现绑卡")
    @PostMapping("/mwUser/bank/bind/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ApiResult extractBankBind(@PathVariable Long uid, @Valid @RequestBody BankBindParam param) {
        if(!StringUtils.isPhone(param.getPhone())) {
            throw new MshopException("不是正确的手机号");
        }

        if(StringUtils.isBlank(param.getBankNo()) || StringUtils.isBlank(param.getPhone())) {
            throw new MshopException("银行卡号和手机号不能为空");
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

    @Log("后台提现绑卡短信确认")
    @ApiOperation(value = "后台提现绑卡短信确认")
    @PostMapping("/mwUser/bank/bind/confirm/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public ApiResult extractBankBindConfirm(@PathVariable Long uid, @Valid @RequestBody BankBindConfirmParam param){
        PayChannelDto payChannel = payChannelService.channelDto(uid, 4);

        if(payChannel == null) {
            throw new MshopException("无支付通道可选择");
        }
        Object bankNoObj = redisUtils.get("bing:card:requestNo:" + param.getRequestNo());
        if(bankNoObj == null) {
            throw new MshopException("短信确认失败:缓存已失效");
        }
        String bankNo = (String)bankNoObj;
        String key = payChannel.getChannelKey();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:
                //绑卡确认
                ConfirmVerifyRespCommand resp = ysePayService.confirmVerify(payChannel, param.getRequestNo(), param.getAuthSn(), param.getCode());
                //保存绑卡标识
                MwUserBank userBank = userBankService.findOne(uid, bankNo);
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

    @Log("后台提现")
    @ApiOperation(value = "后台提现")
    @PostMapping("/mwUser/extract/{uid}")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_EDIT')")
    public Map<String,Object> extract(@PathVariable Long uid, @Valid @RequestBody BankExtractParam param) {
        Map<String,Object> extractRes = new HashMap<>();
        PayChannelDto payChannel = payChannelService.getExtractChannel("bank");
        if(payChannel == null) {
            throw new MshopException("没有可选的通道");
        }
        MwUserBank userBank = userBankService.getUserDefaultBank(uid);
        if(userBank== null) {
            throw new MshopException("用户未绑卡");
        }
        MwUserExtra userExtra = userExtraService.getById(uid);
        String key = payChannel.getChannelKey();
        switch (PayChannelEnum.toKey(key)) {
            case YSEPAY_BANK_BIND:
                ysePayService.extract(payChannel, IdUtil.objectId(), param.getPrice(), userExtra.getMerchantNo(), userBank.getLinkId());
        }
        return extractRes;
    }


    @Log("查询红包解锁配置")
    @ApiOperation(value = "查询红包解锁配置")
    @GetMapping(value = "/order/unlock/config")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity getOrderUnlockConfig(){
        return new ResponseEntity<>(systemConfigService.getHbUnlockConfig(),HttpStatus.OK);
    }

    @Log("修改红包解锁配置")
    @ApiOperation(value = "修改红包解锁配置")
    @PostMapping(value = "/order/unlock/config")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity setOrderUnlockConfig(@RequestBody @Validated HbUnlockConfig param){
        systemConfigService.setHbUnlockConfig(param);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Log("查询APP基础信息配置")
    @ApiOperation(value = "查询APP基础信息配置")
    @GetMapping(value = "/app/data/config")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity getAppDataConfig(){
        return new ResponseEntity<>(systemConfigService.getAppDataConfig(),HttpStatus.OK);
    }

    @Log("修改APP基础信息配置")
    @ApiOperation(value = "修改APP基础信息配置")
    @PostMapping(value = "/app/data/config")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity setAppDataConfig(@RequestBody @Validated AppDataConfig param){
        systemConfigService.setAppDataConfig(param);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Log("查询APP分享图配置")
    @ApiOperation(value = "查询APP分享图配置")
    @GetMapping(value = "/app/share/config")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity getAppShareConfig(){
        return new ResponseEntity<>(systemConfigService.getAppShareConfig(),HttpStatus.OK);
    }

    @Log("修改APP分享图配置")
    @ApiOperation(value = "修改APP分享图配置")
    @PostMapping(value = "/app/share/config")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity setAppShareConfig(@RequestBody @Validated List<String> param){
        systemConfigService.setAppShareConfig(param);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @Log("查询APP登录白名单")
    @ApiOperation(value = "查询APP登录白名单")
    @GetMapping(value = "/app/login/whitelist")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity getAppLoginWhitelist(){
        return new ResponseEntity<>(String.join(",", systemConfigService.getAppLoginWhitelist()),HttpStatus.OK);
    }

    @Log("修改APP登录白名单")
    @ApiOperation(value = "修改APP登录白名单")
    @PostMapping(value = "/app/login/whitelist")
    @PreAuthorize("hasAnyRole('admin','MWUSER_ALL','MWUSER_SELECT')")
    public ResponseEntity setAppLoginWhitelist(@RequestBody @Validated String whitelistStr){
        try {
            String[] list = whitelistStr.split(",");
            systemConfigService.setAppLoginWhiteList(Arrays.asList(list));
        }catch (Exception e) {
            log.error("修改APP登录白名单失败", e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
