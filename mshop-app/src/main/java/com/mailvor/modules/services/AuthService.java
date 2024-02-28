/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.services;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.mailvor.api.MshopException;
import com.mailvor.common.util.IpUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.AppFromEnum;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.auth.param.RegParam;
import com.mailvor.modules.auth.param.WechatLoginParam;
import com.mailvor.modules.mp.config.WxMpConfiguration;
import com.mailvor.modules.shop.domain.MwSystemAttachment;
import com.mailvor.modules.shop.service.MwSystemAttachmentService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.AliUserDto;
import com.mailvor.modules.user.service.dto.WechatUserDto;
import com.mailvor.modules.user.vo.OnlineUser;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mailvor.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.mailvor.config.PayConfig.PAY_NAME;

/**
 * @ClassName 登陆认证服务类
 * @author huangyu
 * @Date 2020/6/14
 **/
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthService {

    private final MwUserService userService;
    private final RedisUtils redisUtils;
    private static Integer expiredTimeIn;
    private final MwSystemAttachmentService systemAttachmentService;

    private final MwUserUnionService userUnionService;

    private final MwUserExtractService extractService;
    @Value("${mshop.security.token-expired-in}")
    public void setExpiredTimeIn(Integer expiredTimeIn) {
        AuthService.expiredTimeIn = expiredTimeIn;
    }

    /**
     * 注册
     *
     * @param param RegDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(RegParam param) {

        String account = param.getAccount();
        String ip = IpUtil.getRequestIp();

        MwUser user = MwUser.builder()
                .username(account)
                .nickname(account)
                .password(SecureUtil.md5(param.getPassword()))
                .phone(account)
                .avatar(ShopConstants.MSHOP_DEFAULT_AVATAR)
                .addIp(ip)
                .lastIp(ip)
                .level(3)
                .levelJd(3)
                .levelPdd(3)
                .levelVip(3)
                .levelDy(3)
                .userType(AppFromEnum.H5.getValue())
                .code(getCode())
                .build();

        userService.save(user);

        //设置推广关系
        if (StrUtil.isNotBlank(param.getInviteCode())) {
            MwSystemAttachment systemAttachment = systemAttachmentService.getByCode(param.getInviteCode());
            if (systemAttachment != null) {
                userService.setSpread(String.valueOf(systemAttachment.getUid()),
                        user.getUid());
            }
        }

    }


    /**
     * 保存在线用户信息
     *
     * @param mwUser  /
     * @param token   /
     * @param request /
     */
    public void save(MwUser mwUser, String token, HttpServletRequest request) {
        String ip = StringUtils.getIp(request);
        String address = StringUtils.getCityInfo(ip);
        OnlineUser onlineUser = null;
        try {
            onlineUser = new OnlineUser(mwUser.getUsername(), mwUser.getNickname(),
                    ip, address, EncryptUtils.desEncrypt(token), new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisUtils.set(ShopConstants.MSHOP_APP_LOGIN_USER +onlineUser.getUserName() + ":" + token, onlineUser, AuthService.expiredTimeIn);
    }

    /**
     * 检测用户是否在之前已经登录，已经登录踢下线
     *
     * @param userName 用户名
     */
    @Async
    public void checkLoginOnUser(String userName, String igoreToken) {
        List<OnlineUser> onlineUsers = this.getAll(userName);
        if (onlineUsers == null || onlineUsers.isEmpty()) {
            return;
        }
        for (OnlineUser onlineUser : onlineUsers) {
            try {
                String token = EncryptUtils.desDecrypt(onlineUser.getKey());
                if (StringUtils.isNotBlank(igoreToken) && !igoreToken.equals(token)) {
                    this.kickOut(userName, onlineUser.getKey());
                } else if (StringUtils.isBlank(igoreToken)) {
                    this.kickOut(userName, onlineUser.getKey());
                }
            } catch (Exception e) {
                log.error("checkUser is error", e);
            }
        }
    }

    /**
     * 踢出用户
     *
     * @param key /
     */
    public void kickOut(String userName, String key) throws Exception {
        key = ShopConstants.MSHOP_APP_LOGIN_USER + userName + ":" + EncryptUtils.desDecrypt(key);
        redisUtils.del(key);
    }

    /**
     * 退出登录
     *
     * @param token /
     */
    public void logout(String userName, String token) {
        String key = ShopConstants.MSHOP_APP_LOGIN_USER + userName + ":" + token;
        redisUtils.del(key);
    }

    /**
     * 查询全部数据，不分页
     *
     * @param uName /
     * @return /
     */
    private List<OnlineUser> getAll(String uName) {
        List<String> keys = null;
        keys = redisUtils.scan(ShopConstants.MSHOP_APP_LOGIN_USER + uName + ":" + "*");

        Collections.reverse(keys);
        List<OnlineUser> onlineUsers = new ArrayList<>();
        for (String key : keys) {
            OnlineUser onlineUser = (OnlineUser) redisUtils.get(key);
            onlineUsers.add(onlineUser);
        }
        onlineUsers.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUsers;
    }

    /**
     * 根据手机号查询用户注册状态
     * @param phone 手机号
     * @return /
     */
    public MwUser authPhone(String phone) {
        return userService.getOne(Wrappers.<MwUser>lambdaQuery().eq(MwUser::getPhone, phone));
    }

    /**
     * app登陆
     *
     * @param code   code码
     * @return uid
     */
    @Transactional(rollbackFor = Exception.class)
    public WechatLoginParam wechatAppLogin(String code) {
        WxMpService wxService = WxMpConfiguration.getWxAppService();
        return appLogin(wxService, code, PAY_NAME);
    }

    public WechatLoginParam appLogin(WxMpService wxService, String code, String mark) {
        try {
            WxOAuth2AccessToken wxMpOAuth2AccessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo wxMpUser = wxService.getOAuth2Service().getUserInfo(wxMpOAuth2AccessToken, null);
            String openid = wxMpUser.getOpenid();

            //如果开启了UnionId
            if (StrUtil.isNotBlank(wxMpUser.getUnionId())) {
                openid = wxMpUser.getUnionId();
            }

            WechatLoginParam loginParam = new WechatLoginParam();
            MwUserUnion userUnion = userUnionService.getByOpenId(openid);

            if (userUnion == null) {
                loginParam.setRegister(true);
                loginParam.setOpenId(openid);
                String nickname = wxMpUser.getNickname();
                log.debug("昵称：{}", nickname);
                //用户保存
                String ip = IpUtil.getRequestIp();

                MwUser user = MwUser.builder()
                        .username(openid)
                        .nickname(nickname)
                        .avatar(wxMpUser.getHeadImgUrl())
                        .addIp(ip)
                        .lastIp(ip)
                        .level(3)
                        .levelJd(3)
                        .levelPdd(3)
                        .levelVip(3)
                        .levelDy(3)
                        .userType(AppFromEnum.APP.getValue())
                        .code(getCode())
                        .status(0)
                        .build();

                //构建微信用户
                WechatUserDto wechatUserDTO = WechatUserDto.builder()
                        .nickname(nickname)
                        .openid(wxMpUser.getOpenid())
                        .unionId(wxMpUser.getUnionId())
                        .language("")
                        .headimgurl(wxMpUser.getHeadImgUrl())
                        .subscribe(false)
                        .subscribeTime(0L)
                        .build();
                user.setMark(mark);

                //微信授权信息保存到redis，在手机验证码登录时获取
                redisUtils.set("wechat_login:" + openid, user, 120);
                redisUtils.set("wechat_login_wx_profile:" + openid, wechatUserDTO, 120);
            } else {
                loginParam.setRegister(false);
                MwUser mwUser = userService.getById(userUnion.getUid());
                loginParam.setUser(mwUser);

            }
            return loginParam;

        } catch (WxErrorException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new MshopException(e.toString());
        }
    }
    /**
     * 苹果登陆
     *
     * @param credential   用户信息
     * @return uid
     */
    @Transactional(rollbackFor = Exception.class)
    public MwUser appleLogin(JSONObject credential) {
        /**
         *     @required this.userIdentifier,
         *     @required this.givenName,
         *     @required this.familyName,
         *     required this.authorizationCode,
         *     @required this.email,
         *     @required this.identityToken,
         *     @required this.state,
         * */
        String userId = credential.getString("userIdentifier");
        String realName = credential.getString("realName");
        String email = credential.getString("email");

        MwUser mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getUsername, userId), false);

        //long uid = 0;
        MwUser returnUser;
        if (mwUser == null) {
            String nickname = StringUtils.isBlank(realName) ? email : realName;
            log.debug("昵称：{}", nickname);
            //用户保存
            String ip = IpUtil.getRequestIp();
            MwUser user = MwUser.builder()
                    .username(userId)
                    .nickname(nickname)
                    .avatar(ShopConstants.MSHOP_DEFAULT_AVATAR)
                    .addIp(ip)
                    .lastIp(ip)
                    .level(3)
                    .levelJd(3)
                    .levelPdd(3)
                    .levelVip(3)
                    .levelDy(3)
                    .userType(AppFromEnum.APP.getValue())
                    .code(getCode())
                    .build();

            userService.save(user);

            returnUser = user;
        } else {
            returnUser = mwUser;

        }

        return returnUser;

    }
    /**
     * 支付宝绑定
     *
     * @param spread 上级用户
     * @return uid
     */
    @Transactional(rollbackFor = Exception.class)
    public MwUser alipayAppLogin(Long uid, AlipayUserInfoShareResponse res, String spread) {
        MwUser mwUser = userService.getById(uid);
        if(res == null ) {
            return mwUser;
        }
        //todo 校验支付宝是否已经绑定

        AliUserDto aliUser = mwUser.getAliProfile();
        if(aliUser == null) {
            aliUser = new AliUserDto();

        }
        aliUser.setUserId(res.getUserId());
        aliUser.setAvatar(res.getAvatar());
        aliUser.setGender(res.getGender());
        aliUser.setNickname(res.getNickName());
        aliUser.setProvince(res.getProvince());
        aliUser.setCity(res.getCity());
        mwUser.setAliProfile(aliUser);
        userService.updateById(mwUser);

        //找到未提现的支付宝订单 更新userid
        extractService.updateUnpaidAliCode(uid, res.getUserId());

        if(StringUtils.isNotBlank(spread)) {
            userService.setSpread(spread, mwUser.getUid());

            log.error("spread:{}", spread);

        }
        return mwUser;

    }
    /**
     * 微信绑定
     *
     * @param code   code码
     * @return uid
     */
    @Transactional(rollbackFor = Exception.class)
    public MwUser wechatAppBinding(String code, MwUser loginUser) {
            WxMpService wxService = WxMpConfiguration.getWxAppService();
            return appBinding(wxService, code, loginUser);

    }

    /**
     * 微信绑定
     *
     * @param code   code码
     * @return uid
     */
    public MwUser appBinding(WxMpService wxService, String code, MwUser loginUser) {
        try {

            MwUser mwUser = userService.getById(loginUser.getUid());
            if (mwUser == null) {
                throw new MshopException("用户不存在");
            }

            MwUserUnion userUnion = userUnionService.getOne(loginUser.getUid());
            //校验用户是否绑定过微信
            if(userUnion != null && userUnion.getWxProfile()!=null) {
                throw new MshopException("用户已经绑定过微信");
            }
            WxOAuth2AccessToken wxMpOAuth2AccessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo wxMpUser = wxService.getOAuth2Service().getUserInfo(wxMpOAuth2AccessToken, null);

            String nickname = wxMpUser.getNickname();
            log.debug("昵称：{}", nickname);
            //用户保存
            String ip = IpUtil.getRequestIp();
            if(StringUtils.isBlank(mwUser.getNickname())) {
                mwUser.setNickname(nickname);
            }

            //微信授权替换头像
            mwUser.setAvatar(wxMpUser.getHeadImgUrl());

            mwUser.setLastIp(ip);

            userService.updateById(mwUser);

            //构建微信用户
            WechatUserDto wechatUserDTO = WechatUserDto.builder()
                    .nickname(nickname)
                    .openid(wxMpUser.getOpenid())
                    .unionId(wxMpUser.getUnionId())
                    .language("")
                    .headimgurl(wxMpUser.getHeadImgUrl())
                    .subscribe(false)
                    .subscribeTime(0L)
                    .build();
            //保存多平台微信微信
            if(userUnion == null) {
                userUnionService.save(mwUser.getUid(), wechatUserDTO);
            } else {
                userUnionService.update(userUnion, wechatUserDTO);
            }

            return mwUser;

        } catch (WxErrorException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new MshopException(e.toString());
        }
    }
    protected String getCode() {
        String code = SuUtils.generateShortUuid();
        MwUser mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getCode, code).last("limit 1"));
        if(mwUser == null) {
            return code;
        } else {
            return getCode();
        }
    }

    /**
     * 公众号登陆
     *
     * @param code   code码
     * @return uid
     */
    @Transactional(rollbackFor = Exception.class)
    public MwUser wechatLogin(String code) {
        WxMpService wxService = WxMpConfiguration.getWxMpService();
        try {
            WxOAuth2AccessToken wxMpOAuth2AccessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo wxMpUser = wxService.getOAuth2Service().getUserInfo(wxMpOAuth2AccessToken, null);
            String openid = wxMpUser.getOpenid();

            //如果开启了UnionId
            if (StrUtil.isNotBlank(wxMpUser.getUnionId())) {
                openid = wxMpUser.getUnionId();
            }

            WechatLoginParam loginParam = new WechatLoginParam();
            MwUserUnion userUnion = userUnionService.getByOpenId(openid);

            if (userUnion == null) {
                loginParam.setRegister(true);
                loginParam.setOpenId(openid);
                String nickname = wxMpUser.getNickname();
                log.debug("昵称：{}", nickname);
                //用户保存
                String ip = IpUtil.getRequestIp();

                MwUser user = MwUser.builder()
                        .username(openid)
                        .nickname(nickname)
                        .avatar(wxMpUser.getHeadImgUrl())
                        .addIp(ip)
                        .lastIp(ip)
                        .level(3)
                        .levelJd(3)
                        .levelPdd(3)
                        .levelVip(3)
                        .levelDy(3)
                        .userType(AppFromEnum.APP.getValue())
                        .code(getCode())
                        .status(0)
                        .build();

                //构建微信用户
                WechatUserDto wechatUserDTO = WechatUserDto.builder()
                        .nickname(nickname)
                        .openid(wxMpUser.getOpenid())
                        .unionId(wxMpUser.getUnionId())
                        .language("")
                        .headimgurl(wxMpUser.getHeadImgUrl())
                        .subscribe(false)
                        .subscribeTime(0L)
                        .build();
                user.setMark(PAY_NAME);
                loginParam.setUser(user);

                //微信登录直接注册用户
                userService.save(user);
                userUnionService.save(user.getUid(), wechatUserDTO);
                return user;
            }
            return userService.getById(userUnion.getUid());

        } catch (WxErrorException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new MshopException(e.toString());
        }

    }


}
