/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.tools.domain.AlipayConfig;
import com.mailvor.modules.tools.service.AliOssService;
import com.mailvor.modules.tools.service.AlipayConfigService;
import com.mailvor.modules.user.domain.MwUserCard;
import com.mailvor.modules.user.param.FaceParam;
import com.mailvor.modules.user.service.MwUserCardService;
import com.mailvor.modules.utils.AesUtil;
import com.mailvor.modules.utils.RsaUtil;
import com.mailvor.utils.ShopUtil;
import com.mailvor.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

/**
 * <p>
 * 用户充值 前端控制器
 * </p>
 *
 * @author huangyu
 * @since 2020-03-01
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "用户认证", tags = "用户:用户认证")
public class UserVerificationController {
    private final AlipayConfigService alipayConfigService;
    private final AliPayService aliPayService;

    private final AliOssService ossService;
    private final MwUserCardService carService;
    @Value("${rsa.private_key}")
    private String privateKey;
    @Value("${aliyun.oss.file.avatar}")
    private String avatarPath;

    @Resource
    private MwUserCardService cardService;

    @AppLog(value = "支付宝人脸认证", type = 1)
    @AuthCheck
    @ApiOperation("支付宝人脸认证")
    @PostMapping(value = "/user/face")
    public ApiResult<JSONObject> faceVerification(@Valid @RequestBody FaceParam param) throws Exception{
        Long uid = LocalUser.getUser().getUid();
        MwUserCard card = cardService.getById(uid);
        if(card == null) {
            throw new MshopException("未找到实名信息");
        }
        String cardNo = param.getCertNo();
        String cardName = param.getCertName();
        String phone = param.getPhone();
        String cardNoMd5 = MD5.create().digestHex(cardNo);
        card.setCardNoMd5(cardNoMd5);
        card.setCardNoEnc(AesUtil.encrypt(cardNo));
        card.setCardName(cardName);
        card.setPhone(AesUtil.encrypt(phone));
        carService.updateById(card);

        AlipayConfig alipay = aliPayService.getAlipayConfig();

        JSONObject payUrl = alipayConfigService.faceVerification(alipay, cardName, cardNo, phone);
        return ApiResult.ok(payUrl.getJSONObject("datadigital_fincloud_generalsaas_face_verification_initialize_response"));
    }
    @AppLog(value = "支付宝人脸认证结果查询", type = 1)
    @AuthCheck
    @ApiOperation("支付宝人脸认证结果查询")
    @PostMapping(value = "/user/face/result")
    @Transactional
    public ApiResult<JSONObject> faceVerificationResult(@RequestBody String str) throws Exception{
        String dec = RsaUtil.decrypt(str, privateKey);
        JSONObject param = JSON.parseObject(dec);

        String phone = param.getString("phone");
        if(avatarPath.startsWith("sfb")) {
            if(StringUtils.isBlank(phone)) {
                throw new MshopException("手机号不能为空");
            }
        } else {
            if(StringUtils.isBlank(phone)) {
                throw new MshopException("手机号不能为空");
            }
        }

        Long uid = LocalUser.getUser().getUid();
        AlipayConfig alipay = aliPayService.getAlipayConfig();

        JSONObject payUrl = alipayConfigService.faceVerificationResult(alipay, param.getString("certifyId"));

        //打印需要移除alive_phone字段 数据量太大
        String payUrlStr = payUrl.toJSONString();
        log.info("用户uid:{} 人脸识别结果: {}", uid, payUrlStr.substring(0, payUrlStr.indexOf("alive_photo")));
        //TODO 验证是否成功
        JSONObject res = payUrl.getJSONObject("datadigital_fincloud_generalsaas_face_verification_query_response");
        if("10000".equals(res.getString("code"))) {
            //4 人脸路径
            String path = uid.toString() + "/4/";
            String base64 = res.getString("alive_photo");
            String url = ossService.uploadCard(ShopUtil.base64ToIs(base64),
                    "face"+ System.currentTimeMillis() + ".jpg",
                    path);
            MwUserCard card = new MwUserCard();
            card.setUid(uid);
            card.setFacePath(AesUtil.encrypt(url));
            card.setPhone(AesUtil.encrypt(phone));

            carService.saveOrUpdate(card);

            MwUserCard userCard = carService.getById(uid);
            userCard.setCreateTime(new Date());
            carService.generateContract(uid, userCard);
        }

        return ApiResult.ok(res);
    }

}

