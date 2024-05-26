/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.pay.yeepay.YeePayService;
import com.mailvor.modules.pay.yeepay.dto.CardBinDto;
import com.mailvor.modules.tools.service.AliOssService;
import com.mailvor.modules.tools.service.AlipayConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserCard;
import com.mailvor.modules.user.param.CardBindParam;
import com.mailvor.modules.user.service.MwUserCardService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.dto.CardDto;
import com.mailvor.modules.user.service.dto.UserCardDto;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.mailvor.modules.utils.AesUtil;
import com.mailvor.modules.utils.IDCardUtil;
import com.mailvor.utils.ImgCompressUtil;
import com.mailvor.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 发送邮件
 *
 * @author 郑杰
 * @date 2018 /09/28 6:55:53
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "上传用户身份信息")
public class UserCardController {

    @Resource
    private AliOssService ossService;

    @Resource
    private MwUserCardService cardService;

    @Resource
    private AlipayConfigService alipayConfigService;

    @Resource
    private MwUserService userService;

    @Resource
    private YeePayService yeePayService;

    @Resource
    private AliPayService aliPayService;

    private static final String regEx="[\n`~!@#$%^&*()+=|{}';',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";

    /**
     * type 1=身份证正面 2=身份证反面 3=合同 4=签名
     * angle 翻转的角度
     * */
    @ApiOperation("上传用户身份信息")
    @AuthCheck
    @PostMapping(value = "/card/upload")
    public ApiResult<Object> upload(@RequestPart MultipartFile file,
                                    @RequestParam Integer type,
                                    @RequestParam Integer angle) throws Exception {
        //todo 需要校验用户是否已经身份认证成功，认证成功不允许再次认证

        //index=1相机 需要旋转270度
        byte[] rotateBytes;
        if(angle > 0) {
            BufferedImage image = (BufferedImage) ImgUtil.rotate(ImageIO.read(file.getInputStream()), angle);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image,"jpg",baos);
            rotateBytes = baos.toByteArray();
        } else {
            rotateBytes = file.getBytes();
        }

        //压缩图片到700k，支付宝身份证识别最大支持1M
        byte[] bytes = ImgCompressUtil.compressPicForScale(rotateBytes, 700);
        InputStream is = new ByteArrayInputStream(bytes);
        String filename = file.getOriginalFilename();
        String fileSuffix = filename.substring(filename.lastIndexOf("."));
        Map map = new HashMap();
        MwUser user = LocalUser.getUser();
        Long uid = user.getUid();
        String path = uid.toString() + "/" + type + "/";
        //4 上传签名 生成合同返回
        if (type == 4) {

            filename = uid + "_SIGN_" + System.currentTimeMillis() + fileSuffix;
            String signatureUrl = ossService.uploadCard(is, filename, path);
            MwUserCard card = cardService.getById(uid);
            //校验身份证手机号是否为空
            if(StringUtils.isBlank(card.getCardName())
                    || StringUtils.isBlank(card.getCardNoMd5())
                    || StringUtils.isBlank(card.getPhone())
                    || StringUtils.isBlank(card.getFacePath())) {
                throw new MshopException("请完成身份认证和人脸核验");
            }
            card.setSignaturePath(AesUtil.encrypt(signatureUrl));
            //这里生成电子合同
            String contractUrl = cardService.generateContract(user.getUid(), card);

            map.put("url", contractUrl);
            return ApiResult.ok(map);
        }
        String ocrType = null;
        if(type == 1) {
            ocrType = "ID_CARD_FRONT";
        } else if(type == 2) {
            ocrType = "ID_CARD_BACK";
        }
        //识别用户身份信息
        filename = filename.replaceAll(regEx,"");
        filename = URLEncoder.encode(filename, "UTF-8");
        JSONObject json = alipayConfigService.cardOCR(aliPayService.getAlipayConfig(),ocrType, filename, bytes);
        JSONObject res = json.getJSONObject("datadigital_fincloud_generalsaas_ocr_server_detect_response");
        log.info("用户uid: {} {}身份识别结果: {}", user.getUid(), ocrType, res.toJSONString());
        String code = res.getString("code");
        //code ==10000成功 同时要判断是否为空
        if(!"10000".equals(code)) {
            throw new MshopException("无法正确识别身份信息");
        }
        JSONObject ocrData = res.getJSONObject("ocr_data");
        MwUserCard card = new MwUserCard();
        card.setUid(user.getUid());
        String url = null;
        if(type == 1) {
            filename = uid + "_F_" + System.currentTimeMillis() + fileSuffix;
            String cardNo = ocrData.getString("identity_no");
            String cardName = ocrData.getString("name");
            if(StringUtils.isBlank(cardNo) || StringUtils.isBlank(cardName)) {
                throw new MshopException("无法正确识别身份信息");
            }
            cardNo = cardNo.toUpperCase();
            if(!IDCardUtil.isAdult(cardNo)) {
                throw new MshopException("未成年人不支持加盟");
            }
            //验证身份证是否已经被使用
            String cardNoMd5 = MD5.create().digestHex(cardNo);
            MwUserCard userCard = cardService.getByCardNo(cardNoMd5);
            if(userCard != null && !userCard.getUid().equals(user.getUid())){
                throw new MshopException("该身份证已被实名认证");
            }

            url = ossService.uploadCard(is, filename, path);
            //设置身份证人像面
            card.setCardFPath(AesUtil.encrypt(url));

            card.setCardNoMd5(cardNoMd5);
            card.setCardNoEnc(AesUtil.encrypt(cardNo));
            card.setCardName(cardName);
            map.put("cardNo", cardNo);
            map.put("cardName", cardName);
            //更新用户真实姓名
            MwUser mwUser = userService.getById(user.getUid());
            mwUser.setRealName(cardName);
            userService.updateById(mwUser);
        } else if (type == 2) {
            filename = uid + "_B_" + System.currentTimeMillis() + fileSuffix;
            String expiryDate = ocrData.getString("expiry_date");
            if(StringUtils.isBlank(expiryDate)) {
                throw new MshopException("无法正确识别身份信息:" + JSON.toJSONString(ocrData));
            }
            //校验身份证是否过期
            if(checkCardExpired(expiryDate)) {
                throw new MshopException("身份证已过期");
            }

            url = ossService.uploadCard(is, filename, path);
            card.setCardExpired(expiryDate);
            card.setCardBPath(AesUtil.encrypt(url));
        }
        cardService.saveOrUpdate(card);

        map.put("url", url);
        return ApiResult.ok(map);

    }

    protected boolean checkCardExpired(String expiredStr) {
        String[] date = expiredStr.split("-");
        String endStr = date[1];
        if("长期".equals(endStr)) {
            return false;
        }
        DateTime endDate = DateUtil.parse(endStr);
        int com = DateUtil.compare(new Date(), endDate);
        if(com >= 0) {
            return true;
        }
        return false;
    }
//
//    public static void main(String[] args) {
//        boolean expired = UserCardController.checkCardExpired("2020.10.10-长期");
//        System.out.println(expired);
//    }

    @AuthCheck
    @GetMapping("/card")
    @ApiOperation(value = "获取用户信息",notes = "获取用户信息",response = MwUserCardQueryVo.class)
    public ApiResult<Object> userInfo(@RequestParam(required = false) Long uid){
        Long checkUid;
        if(uid != null && uid > 0) {
            checkUid = uid;
        } else {
            checkUid = LocalUser.getUser().getUid();
        }
        return ApiResult.ok(cardService.getUserCardById(checkUid));
    }

    @AuthCheck
    @GetMapping("/card/bin")
    @ApiOperation(value = "银行卡识别",notes = "银行卡识别",response = CardBinDto.class)
    public ApiResult<CardBinDto> cardBin(@RequestParam String card){
        return ApiResult.ok(yeePayService.getCardBin(card));
    }

    @AuthCheck
    @GetMapping("/card/list")
    @ApiOperation(value = "银行卡列表",notes = "银行卡列表",response = UserCardDto.class)
    public ApiResult<List> cardList(){
        MwUserCard userCard = cardService.getById(LocalUser.getUser().getUid());

        if(userCard == null) {
            return ApiResult.ok(Collections.EMPTY_LIST);
        }
        if(userCard.getCards() != null) {
            return ApiResult.ok(userCard.getCards().getCards());
        }
        MwUserCardQueryVo cardQueryVo = cardService.getUserCardById(LocalUser.getUser().getUid());
        if(StringUtils.isBlank(cardQueryVo.getBankNo())) {
            return ApiResult.ok(Collections.EMPTY_LIST);
        }
        CardBinDto binDto = yeePayService.getCardBin(cardQueryVo.getBankNo());
        //如果卡不支持返回
        if(binDto.getCode() != 1) {
            return ApiResult.ok(Collections.EMPTY_LIST);
        }
        CardDto cardDto = CardDto.builder().no(cardQueryVo.getBankNo()).name(binDto.getBankName()).build();
        return ApiResult.ok(Arrays.asList(cardDto));
    }

    @AuthCheck
    @PostMapping("/card/bind")
    @ApiOperation(value = "银行卡绑定",notes = "银行卡绑定",response = CardBinDto.class)
    public ApiResult<Map> cardBind(@Valid @RequestBody CardBindParam param){
        //todo
//        Map map = yeePayService.bindCard(param.getNo());
//        return ApiResult.ok(map);
        return ApiResult.ok(new HashMap());
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String filename = "https://oss.mailvor.cn/dlb/card/12575/1/IMG_20230418_173201~2.jpg"
                .replaceAll(regEx,"");
        System.out.println(filename);
        filename = URLEncoder.encode(filename, "UTF-8");
        System.out.println(filename);
    }
}
