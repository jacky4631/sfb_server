/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.util.StrUtil;
import com.mailvor.common.json.JsonUtil;
import com.mailvor.common.json.JsonObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.aop.NoRepeatSubmit;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.common.util.PhoneUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.enums.BillInfoEnum;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.vo.UserOrderCountVo;
import com.mailvor.modules.product.service.MwStoreProductRelationService;
import com.mailvor.modules.push.service.JPushService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.tk.config.TbConfig;
import com.mailvor.modules.tools.domain.SensitiveWord;
import com.mailvor.modules.tools.service.SensitiveWordService;
import com.mailvor.modules.tools.utils.SensitiveWordUtil;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.param.*;
import com.mailvor.modules.user.service.*;
import com.mailvor.modules.user.vo.*;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkScPublisherInfoSaveRequest;
import com.taobao.api.response.TbkScPublisherInfoSaveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.mailvor.constant.SystemConfigConstants.MSHOP_SHOW_RECHARGE;
import static com.mailvor.config.PayConfig.PAY_NAME;

/**
 * <p>
 * 用户控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-16
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "用户:用户中心")
public class UserController {
    private static Set<String> sensitiveWordSet = new HashSet<>();

    private final MwUserService mwUserService;
    private final MwSystemGroupDataService systemGroupDataService;
    private final MwStoreOrderService orderService;
    private final MwStoreProductRelationService relationService;
    private final MwUserSignService userSignService;
    private final MwUserBillService userBillService;
    private final MwSystemConfigService systemConfigService;
    private final SensitiveWordService sensitiveWordService;

    private final JPushService jPushService;

    private final TbConfig tbConfig;

    private final RedisUtils redisUtil;

    private final MwUserUnionService userUnionService;

    @Resource
    private MwUserBankService bankService;

    @GetMapping("/push")
    @Operation(summary = "测试推送", description = "测试推送")
    public ApiResult<Object> push(@RequestParam Long uid, @RequestParam String msg){
        return ApiResult.ok(jPushService.push(msg, uid));
    }
    /**
     * 用户资料
     */
    @AuthCheck
    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    public ApiResult<Object> userInfo(Boolean baseInfo){
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(mwUserService.getNewMwUserById(mwUser, baseInfo));
    }
    @AuthCheck
    @GetMapping("/user/wxprofile")
    @Operation(summary = "获取用户微信信息", description = "获取用户微信信息")
    public ApiResult<Object> userWxProfile(){
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(userUnionService.getOne(mwUser.getUid()));
    }
    @AuthCheck
    @GetMapping("/vipinfo")
    @Operation(summary = "获取用户会员信息", description = "获取用户会员信息")
    public ApiResult<Object> userVipInfo(){
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(mwUserService.getUserVipInfo(mwUser));
    }

    @GetMapping("/loginmustcode")
    @Operation(summary = "登录强制邀请码", description = "登录强制邀请码")
    public ApiResult<Object> loginMustCode(){
        return ApiResult.ok(Integer.parseInt(systemConfigService.getData(SystemConfigConstants.LOGIN_MUST_CODE)));
    }

    @AuthCheck
    @GetMapping("/hasUnlockOrder")
    @Operation(summary = "是否包含已解锁红包", description = "是否包含已解锁红包")
    public ApiResult<Boolean> hasUnlockOrder(){
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(mwUserService.hasUnlockOrder(mwUser));
    }
    @UserCheck
    @GetMapping("/commissionInfo")
    @Operation(summary = "获取佣金信息", description = "获取佣金信息")
    public ApiResult<Object> commissionInfo(){
        MwUser mwUser = LocalUser.getUser();
        if(mwUser != null) {
            mwUser = mwUserService.getById(mwUser.getUid());
        }
        return ApiResult.ok(mwUserService.getCommissionInfo(mwUser));
    }
    /**
     * 用户删除
     */
    @AuthCheck
    @DeleteMapping("/user/delete")
    @Operation(summary = "删除用户信息", description = "删除用户信息")
    public ApiResult userDelete(){
        MwUser mwUser = LocalUser.getUser();
        mwUserService.remove(mwUser.getUid());
        return ApiResult.ok();
    }

    @AuthCheck
    @GetMapping("/user/spread")
    @Operation(summary = "获取用户上级信息", description = "获取用户上级信息")
    public ApiResult userSpread(){
        MwUser user = mwUserService.getById(LocalUser.getUser().getUid());
        if(user == null || user.getSpreadUid() == 0) {
            throw new MshopException("上级用户不存在");
        }
        MwUser spreadUser = mwUserService.getById(user.getSpreadUid());
        if(spreadUser == null) {
            throw new MshopException("上级用户不存在");
        }
        JsonObject info = new JsonObject();
        info.put("nickname", spreadUser.getNickname());
        info.put("phone", PhoneUtil.desensitize(spreadUser.getPhone()));
        info.put("code", spreadUser.getCode());

        return ApiResult.ok(info);
    }
    /**
     * 获取个人中心菜单
     */
    @GetMapping("/menu/user")
    @Operation(summary = "获取个人中心菜单", description = "获取个人中心菜单")
    public ApiResult<Map<String,Object>> userMenu(){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("routine_my_menus",systemGroupDataService.getDatas(ShopConstants.MSHOP_MY_MENUES));
        return ApiResult.ok(map);
    }



    /**
     * 订单统计数据
     */
    @AppLog(value = "查看订单统计数据", type = 1)
    @AuthCheck
    @GetMapping("/order/data")
    @Operation(summary = "订单统计数据", description = "订单统计数据")
    public ApiResult<UserOrderCountVo> orderData(){
        Long uid = LocalUser.getUser().getUid();
        return ApiResult.ok(orderService.orderData(uid));
    }

    /**
     * 获取收藏产品
     */
    @AuthCheck
    @GetMapping("/collect/user")
    @Parameters({
            @Parameter(name = "page", description = "页码,默认为1"),
            @Parameter(name = "limit", description = "页大小,默认为10"),
            @Parameter(name = "type", description = "foot为足迹,collect为收藏")
    })
    @Operation(summary = "获取收藏产品,或足迹", description = "获取收藏产品,或足迹")
    public ApiResult<Object> collectUser(@RequestParam(value = "page",defaultValue = "1") int page,
                                                                      @RequestParam(value = "limit",defaultValue = "10") int limit,
                                                                      @RequestParam(value = "type") String type){
        Long uid = LocalUser.getUser().getUid();
        Map<String, Object> map = relationService.userCollectProduct(page,limit,uid,type);
        Long total = (Long) map.get("total");
        Long totalPage = (Long) map.get("totalPage");
        return ApiResult.resultPage(total, totalPage.intValue(), map.get("list"));
    }

    /**
     * 用户资金统计
     */
    @AppLog(value = "查看用户资金统计", type = 1)
    @AuthCheck
    @GetMapping("/user/balance")
    @Operation(summary = "用户资金统计", description = "用户资金统计")
    public ApiResult<Object> userBalance(){
        MwUser mwUser = LocalUser.getUser();
        Map<String,Object> map = Maps.newHashMap();
        Double[] userMoneys = mwUserService.getUserMoney(mwUser.getUid());
        map.put("now_money",mwUser.getNowMoney());
        map.put("orderStatusSum",userMoneys[0]);
        map.put("recharge",userMoneys[1]);
        map.put("is_hide",systemConfigService.getData(MSHOP_SHOW_RECHARGE));
        return ApiResult.ok(map);
    }


    /**
     * 签到用户信息
     */
    @AppLog(value = "签到用户信息", type = 1)
    @AuthCheck
    @PostMapping("/sign/user")
    @Operation(summary = "签到用户信息", description = "签到用户信息")
    public ApiResult<MwUserQueryVo> sign(){
        MwUser mwUser = LocalUser.getUser();
        return ApiResult.ok(userSignService.userSignInfo(mwUser));
    }

    /**
     * 签到配置
     */
    @GetMapping("/sign/config")
    @Operation(summary = "签到配置", description = "签到配置")
    public ApiResult<Object> signConfig(){
        return ApiResult.ok(systemGroupDataService.getDatas(ShopConstants.MSHOP_SIGN_DAY_NUM));
    }

    /**
     * 签到列表
     */
    @AppLog(value = "查看签到列表", type = 1)
    @AuthCheck
    @GetMapping("/sign/list")
    @Parameters({
            @Parameter(name = "page", description = "页码,默认为1"),
            @Parameter(name = "limit", description = "页大小,默认为10")
    })
    @Operation(summary = "签到列表", description = "签到列表")
    public ApiResult<List<SignVo>> signList(@RequestParam(value = "page",defaultValue = "1") int page,
                                            @RequestParam(value = "limit",defaultValue = "10") int limit){
        Long uid = LocalUser.getUser().getUid();
        return ApiResult.ok(userSignService.getSignList(uid,page,limit));
    }

    /**
     * 签到列表（年月）
     */

    @AuthCheck
    @GetMapping("/sign/month")
    @Parameters({
            @Parameter(name = "page", description = "页码,默认为1"),
            @Parameter(name = "limit", description = "页大小,默认为10")
    })
    @Operation(summary = "签到列表（年月）", description = "签到列表（年月）")
    public ApiResult<Object> signMonthList(@RequestParam(value = "page",defaultValue = "1") int page,
                                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                                           @RequestParam(required = false, value = "month") String month){
        Long uid = LocalUser.getUser().getUid();
        return ApiResult.ok(userBillService.getUserBillList(page, limit,uid, BillInfoEnum.SIGN_INTEGRAL.getValue(), month));
    }

    /**
     * 开始签到
     */
    @AppLog(value = "开始签到", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/sign/integral")
    @Operation(summary = "开始签到", description = "开始签到")
    public ApiResult<Object> signIntegral(){
        MwUser mwUser = LocalUser.getUser();
        int integral = userSignService.sign(mwUser);

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("integral",integral);
        return ApiResult.ok(map,"签到获得" + integral + "积分");
    }
    @AppLog(value = "用户绑定淘宝渠道id", type = 1)
    @AuthCheck
    @PostMapping("/user/binding")
    @Operation(summary = "用户绑定淘宝渠道id", description = "用户绑定淘宝渠道id")
    public ApiResult<String> binding(@Validated @RequestBody UserBindingParam param) throws ApiException {
        if (StringUtils.isBlank(param.getSession())) {
            return ApiResult.fail("请先在淘宝完成实名认证后再授权。");
        }
        TaobaoClient client = new DefaultTaobaoClient(tbConfig.getUrl(),
                tbConfig.getAppKey(), tbConfig.getAppSecret());
        Long uid =LocalUser.getUser().getUid();
        TbkScPublisherInfoSaveRequest req = new TbkScPublisherInfoSaveRequest();
        req.setRelationFrom("1");
        req.setOfflineScene("1");
        req.setOnlineScene("1");
        req.setInviterCode(tbConfig.getInviterCode());
        req.setInfoType(1L);
        req.setNote(uid.toString());
        TbkScPublisherInfoSaveResponse rsp = client.execute(req, param.getSession());
        log.info("淘宝{}渠道授权 {}", uid, JsonUtil.toJSONString(rsp));
        if (rsp == null) {
            return ApiResult.fail("授权失败");
        }
        if (!rsp.isSuccess()) {
            String reason = rsp.getSubMessage();
            if (StringUtils.isBlank(reason)) reason = rsp.getSubMsg();
            if (StringUtils.isBlank(reason)) reason = rsp.getMsg();
            return ApiResult.fail(StringUtils.isBlank(reason)
                    ? "授权失败" : reason);
        }
        if (rsp.getData() == null || rsp.getData().getRelationId() == null) {
            return ApiResult.fail("授权失败");
        }
        //校验渠道id是否已被绑定
        String channelId = rsp.getData().getRelationId().toString();
        MwUserUnion findUser = userUnionService.getOne(new LambdaQueryWrapper<MwUserUnion>()
                .eq(MwUserUnion::getTbPid,channelId)
                .eq(MwUserUnion::getName, PAY_NAME)
                .ne(MwUserUnion::getUid, uid));
        if(findUser != null) {
            return ApiResult.fail("授权失败，该淘宝号已经被授权");
        }

        MwUserUnion userUnion = userUnionService.getOne(uid);
        if(userUnion == null) {
            userUnion = new MwUserUnion();
            userUnion.setUid(uid);
        }
        userUnion.setTbPid(channelId);
        userUnion.setName(PAY_NAME);
        userUnionService.saveOrUpdate(userUnion);
        return ApiResult.ok("授权成功");
    }
    @AuthCheck
    @GetMapping(value = "/user/auth/query")
    public ApiResult authQuery() {
        Long uid =LocalUser.getUser().getUid();
        MwUserUnion userUnion = userUnionService.getOne(uid);
        JsonObject res = new JsonObject();
        if(userUnion != null && StringUtils.isNotBlank(userUnion.getTbPid())) {
            res.put("auth", true);
        } else {
            res.put("auth", false);
        }
        return ApiResult.ok(res);
    }
    @AppLog(value = "用户修改信息", type = 1)
    @AuthCheck
    @PostMapping("/user/edit")
    @Operation(summary = "用户修改信息", description = "用修改信息")
    public ApiResult<String> edit(@Validated @RequestBody UserEditParam param){
        MwUser mwUser = mwUserService.getById(LocalUser.getUser().getUid());
        if(StringUtils.isNotBlank(param.getAvatar())) {
            mwUser.setAvatar(param.getAvatar());
        }
        if(StringUtils.isNotBlank(param.getNickname())) {
            checkSensitive(param.getNickname());
            mwUser.setNickname(param.getNickname());
        }
        if(StringUtils.isNotBlank(param.getPhone())) {
            LambdaQueryWrapper<MwUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MwUser::getPhone, param.getPhone());
            MwUser user = mwUserService.getOne(wrapper);
            if(user != null) {
                return ApiResult.fail("手机号已存在，修改失败");
            }
            mwUser.setPhone(param.getPhone());
        }
        if(StringUtils.isNotBlank(param.getRealName())) {
            checkSensitive(param.getRealName());
            mwUser.setRealName(param.getRealName());
        }
        if(StringUtils.isNotBlank(param.getCode())) {
            //绑定邀请码
            //根据code找到用户
            LambdaQueryWrapper<MwUser> wrapper = new LambdaQueryWrapper<>();
            String code = param.getCode();
            boolean isMobile = StringUtils.isPhone(code);
            //如果11位就是手机号
            if(isMobile) {
                wrapper.eq(MwUser::getPhone, code);
            } else {
                wrapper.eq(MwUser::getCode, code);
            }
            MwUser parentUser = mwUserService.getOne(wrapper);
            //getSpreadUid() == 0 用户没有正确被邀请，不允许被邀请
            if(parentUser == null) {
                return ApiResult.fail(isMobile? "手机号不存在":"邀请码不存在");
            }
            //设置上级id
            boolean success = mwUserService.setSpread(parentUser.getUid().toString(), mwUser.getUid());
            if(success) {
                mwUserService.spreadUserHb(parentUser,mwUser);
                return ApiResult.ok("修改成功");
            } else {
                return ApiResult.fail("绑定失败");
            }
        }
        if(StringUtils.isNotBlank(param.getChangedCode())) {
            //校验敏感词
            checkSensitive(param.getChangedCode());
            if(StringUtils.isSameChars(param.getChangedCode())) {
                throw new MshopException("邀请口令已被使用，请尝试其他");
            }
            LambdaQueryWrapper<MwUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MwUser::getCode, param.getChangedCode());
            long count = mwUserService.count(wrapper);
            if(count > 0) {
                throw new MshopException(param.getChangedCode() + " 邀请口令已被使用，请尝试其他");
            }
            MwUserQueryVo mwUserQueryVo = mwUserService.getNewMwUserById(mwUser);
            if(mwUserQueryVo.getCanChangeCode() == 0) {
                throw new MshopException("不允许修改");
            }
            mwUser.setCode(param.getChangedCode());
            mwUser.setChangeWord(mwUser.getChangeWord() + 1);
        }
        mwUserService.updateById(mwUser);

        return ApiResult.ok("修改成功");
    }

    protected void checkSensitive(String word) {
        initSensitive();
        boolean containsBadWord = SensitiveWordUtil.contains(word, SensitiveWordUtil.MinMatchTYpe);
        if(containsBadWord) {
            throw new MshopException(word + " 修改失败，请尝试其他");
        }
    }
    protected void initSensitive() {
        //首次初始化敏感词数据
        if(sensitiveWordSet.isEmpty()) {
            List<SensitiveWord> badWords = sensitiveWordService.findAll();
            badWords.stream().forEach(sensitiveWord -> {
                sensitiveWordSet.add(sensitiveWord.getBadword());
            });
            SensitiveWordUtil.init(sensitiveWordSet);
        }
    }

    @AppLog(value = "保存搜索历史", type = 1)
    @AuthCheck
    @PostMapping("/user/history/save")
    @Operation(summary = "保存搜索历史", description = "保存搜索历史")
    public ApiResult<String> historySave(@Validated @RequestBody String keyword){

        RedisUtil.setHistory(LocalUser.getUser().getUid(), keyword);

        return ApiResult.ok("保存成功");
    }

    @AppLog(value = "获取搜索历史", type = 1)
    @AuthCheck
    @GetMapping("/user/history")
    @Operation(summary = "获取搜索历史", description = "获取搜索历史")
    public ApiResult<List> history(){

        List data = RedisUtil.getHistory(LocalUser.getUser().getUid());

        return ApiResult.ok(data);
    }

    @AppLog(value = "清空搜索历史", type = 1)
    @AuthCheck
    @DeleteMapping("/user/history/clear")
    @Operation(summary = "清空搜索历史", description = "清空搜索历史")
    public ApiResult<List> historyClear(){

        boolean deleted = RedisUtil.clearHistory(LocalUser.getUser().getUid());

        return ApiResult.ok(Collections.emptyList());
    }

    /**
     * 绑定手机号
     * */
    @AuthCheck
    @Operation(summary = "绑定手机号")
    @PostMapping(value = "/user/bind/mobile")
    public ApiResult<Map<String, Object>> loginVerify(@Validated @RequestBody MobileBindParam bindParam, HttpServletRequest request) {
        Object codeObj = redisUtil.get("code_" + bindParam.getAccount());
        if(systemConfigService.getAppLoginWhitelist().contains(bindParam.getAccount())) {
            if(!"1234".equals(bindParam.getCaptcha())) {
                throw new MshopException("验证码错误");
            }
        } else {
            if(codeObj == null){
                throw new MshopException("请先获取验证码");
            }
            String code = codeObj.toString();
            if (!StrUtil.equals(code, bindParam.getCaptcha())) {
                throw new MshopException("验证码错误");
            }
        }
        MwUser findUser = mwUserService.getOne(Wrappers.<MwUser>lambdaQuery().eq(MwUser::getPhone, bindParam.getAccount()));
        if(findUser != null) {
            throw new MshopException("手机号已绑定");
        }
        MwUser mwUser = mwUserService.getById(LocalUser.getUser().getUid());
        mwUser.setPhone(bindParam.getAccount());
        mwUserService.updateById(mwUser);

        Integer loginMustCode = Integer.parseInt(systemConfigService.getData(SystemConfigConstants.LOGIN_MUST_CODE));
        //校验是否开启强制邀请码
        String redirect = "0";
        if(loginMustCode != null && loginMustCode == 1) {
            if(mwUser.getSpreadUid() == null || mwUser.getSpreadUid() == 0){
                redirect = "1";
            }
        }
        String finalRedirect = redirect;
        Map<String, Object> map = new HashMap<String, Object>(3) {{
            put("redirect", finalRedirect);
        }};
        return ApiResult.ok(map).setMsg("绑定成功");
    }

//    @AnonymousAccess
//    @GetMapping(value = "/user/redis/delete")
//    public ApiResult<Object> loginVerify(@RequestParam String platform, @RequestParam Long uid) {
//        redisUtil.del(OrderUtil.getCouponKey(platform, uid));
//        return ApiResult.ok("ok");
//    }

    @AuthCheck
    @GetMapping("/user/share")
    @Operation(summary = "获取用户分享图", description = "获取用户分享图")
    public ApiResult<List> userShare(){

        return ApiResult.ok(systemConfigService.getAppShareConfig());
    }

    @AuthCheck
    @GetMapping("/user/bank/list")
    @Operation(summary = "用户签约银行卡列表", description = "用户签约银行卡列表")
    public ApiResult<List<MwUserBankQueryVo>> getMwUserAddressPageList(@RequestParam(value = "page",defaultValue = "1") int page,
                                                                       @RequestParam(value = "limit",defaultValue = "10") int limit,
                                                                       @RequestParam(value = "extract",defaultValue = "0", required = false) int extract){
        Long uid = LocalUser.getUser().getUid();
        List<MwUserBankQueryVo> bankQueryVos;
        if(extract == 1) {
            bankQueryVos = bankService.getExtractList(uid,page,limit);
        } else {
            bankQueryVos = bankService.getList(uid,page,limit);
        }
        bankQueryVos.forEach(bankQueryVo -> {
            String bankNo = bankQueryVo.getBankNo();
            bankQueryVo.setBankNoFull(bankNo);
            int length = bankNo.length();
            bankQueryVo.setBankNo(bankNo.substring(length-4, length));

        });
        return ApiResult.ok(bankQueryVos);
    }

}

