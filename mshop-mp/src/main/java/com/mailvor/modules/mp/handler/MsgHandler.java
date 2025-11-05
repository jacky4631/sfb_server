/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mailvor.modules.mp.builder.TextBuilder;
import com.mailvor.modules.tk.param.ParseContentParam;
import com.mailvor.modules.tk.service.TkService;
import com.mailvor.modules.tk.vo.TkParseCodeVO;
import com.mailvor.modules.tk.vo.TkParseVO;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

@Component
@Slf4j
public class MsgHandler extends AbstractHandler {
    @Resource
    private TkService service;

    @Resource
    private MwUserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                && weixinService.getKefuService().kfOnlineList()
                .getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        log.info("微信用户信息：{}", JSON.toJSONString(wxMessage));
        String openId = wxMessage.getFromUser();
//        AuthService.wechatAppLogin

        String content = wxMessage.getContent();
        String resMsg = "您查询的宝贝没有优惠！";
        if(content.startsWith("找")||content.startsWith("查")){
            resMsg = "请通过苏分宝APP查找商品\n━┉┉┉┉∞┉┉┉┉━\n<a href='https://www.pgyer.com/sufenbao'>点击下载app</a>\n━┉┉┉┉∞┉┉┉┉━\n";
        } else if(checkMobile(content)) {
            //绑定用户
            MwUser findUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getPhone, content), false);
            if(findUser == null) {
                resMsg = "请先去苏分宝APP绑定手机号！";
            } else {
                if(StringUtils.isNoneEmpty(findUser.getWechatOpenId())) {
                    resMsg = "该手机号已经绑定，如需解绑，联系在线客服！";
                } else {
                    findUser.setWechatOpenId(openId);
                    userService.updateById(findUser);
                    resMsg = "苏分宝绑定成功";
                }
            }
        } else if(checkOrder(content)) {
            //todo 调用订单找回接口
            //todo 返回相应结果
            resMsg = "请通过苏分宝APP提交订单\n━┉┉┉┉∞┉┉┉┉━\n<a href='https://www.pgyer.com/sufenbao'>点击下载app</a>\n━┉┉┉┉∞┉┉┉┉━\n";
        } else {
            //查券 调用 mixParse接口
            MwUser findUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getWechatOpenId, openId), false);
            //todo 如果是抖音和唯品会 mixParse 需要直接转链
            TkParseCodeVO res = service.mixParse(new ParseContentParam(content), findUser);
            log.info("查券识别结果 {}", res == null ? "空" : JSON.toJSONString(res));
            //解析查券结果 //解析内容 名字为空或者 状态不是3 4 5 说明是无券商品，弹出全网搜索
            if(res != null && res.getData() != null) {
                TkParseVO data = res.getData();
                if(StringUtils.isEmpty(data.getTitle()) || StringUtils.isBlank(data.getShortPwd())) {
                    resMsg = "您查询的宝贝没有优惠！";
                } else {
                    //todo 识别查券结果
                    resMsg = getParsedContent(data, findUser);
                }
            }

        }

        return new TextBuilder().build(resMsg, wxMessage, weixinService);

        //TODO 组装回复消息
//        String content = "mshop收到信息内容：" + wxMessage.getContent();
//
//        return new TextBuilder().build(content, wxMessage, weixinService);

    }

    public String getParsedContent(TkParseVO data, MwUser findUser) {
        String itemName = data.getTitle();
        String parsedContent = data.getShortPwd();

        Integer platType = data.getPlatType();
        boolean notShowWords = "tb".equals(platType) || "dy".equals(platType);
        return (notShowWords ? "8": "") +itemName + "\n" +
                "━┉┉┉┉∞┉┉┉┉━\n" +
                "【原价】" + data.getStartPrice() + "元\n" +
                "【现价】<a href='https://kzurl10.cn/DqW0z?rl=\n" + parsedContent + "\n'>"
                + data.getEndPrice() + "元</a>\n" +
                "【奖励】"+ getAmount(findUser, data.getFeeRatio()*data.getEndPrice()/100) + "元\n" +
                "━┉┉┉┉∞┉┉┉┉━\n" +
                (notShowWords
                        ? "长按复制这条消息" + ("tb".equals(platType) ? "[掏]宝":"抖音") +"下单:/"
                        : "<a href='" +parsedContent +  "'>下单链接</a>\n"
                );
    }

    public String getAmount(MwUser mwUser, Double amount){
        if(mwUser == null) {
            return String.format("%.2f",amount* 0.6);
        }
        return String.format("%.2f",amount* (0.3 + 0.15*(mwUser.getLevel()-1)));
    }

    public boolean checkMobile(String mobile) {
        return mobile.matches("^1(2|3|4|5|6|7|8|9)\\d{9}$");

    }
    public boolean checkOrder(String order) {
        return order.matches("[0-9-]+");

    }
//
    public static void main(String[] args) {
//        boolean checked = MsgHandler.checkOrder("220623-028568514123514");
//        System.out.println(checked);
//        checked = MsgHandler.checkOrder("22090607084633");
//        System.out.println(checked);
//        checked = MsgHandler.checkOrder("4975566642865516284");
//        System.out.println(checked);
//        checked = MsgHandler.checkOrder("252127104266");
//        System.out.println(checked);
//        checked = MsgHandler.checkOrder("2919750519521732536");
//        System.out.println(checked);
//        checked = MsgHandler.checkOrder("22090607a084633");
//        System.out.println(checked);
        MsgHandler msgHandler = new MsgHandler();
        JSONObject res = JSONObject.parseObject("{\"msg\":\"成功\",\"cache\":false,\"code\":0,\"data\":{\"kuaiZhanUrl\":\"https://07mls.kuaizhan.com/?_s=pIVeE1\",\"commissionRate\":40.02,\"schemaPromotionShortUrl\":\"\",\"originalPrice\":89.9,\"actualPrice\":29.9,\"itemLink\":\"https://s.click.taobao.com/1ciwdQu\",\"errorCode\":0,\"shopName\":\"\",\"activityStartTime\":\"\",\"subErrorCode\":0,\"couponId\":\"ed18491d942b49b6819ed222393dab05\",\"haiTao\":0,\"plusCommissionRate\":0,\"parseStatus\":3,\"monthSales\":30000,\"skuName\":\"\",\"activityId\":\"\",\"couponLink\":\"https://uland.taobao.com/quan/detail?sellerId=210785179&activityId=ed18491d942b49b6819ed222393dab05\",\"itemName\":\"【南京同仁堂】官方旗舰店正品暴汗足丸男女通用买一送一盒包邮\",\"cpaRewardAmount\":\"\",\"couponStartTime\":\"2022-11-16 00:00:00\",\"plusCommissionAmount\":0,\"mainPic\":\"https://img.alicdn.com/imgextra/i1/210785179/O1CN01kY8bNG1o820G3zwdW_!!210785179.jpg\",\"marketMainPic\":\"https://sr.ffquan.cn/dtk_www_855587/20220914/ccgu295emv7haelvutv00.jpg\",\"tchaoshi\":0,\"tmall\":0,\"skuId\":\"\",\"dataType\":\"goods\",\"subErrorMsg\":\"\",\"sensitiveWords\":0,\"url\":\"\",\"errorMsg\":\"\",\"couponEndTime\":\"2022-11-22 23:59:59\",\"couponTotalCount\":100000,\"itemId\":\"7yK9WzP7y7u4t7dMq9Fnk8CMtV-ZrGpqKAIA36V9WkpCR9\",\"activityEndTime\":\"\",\"originContent\":\"17￥ CZ0001 JUeddcPrDOI￥ https://m.tb.cn/h.UhgeYCM  【南京同仁堂】官方旗舰店正品暴汗足丸男女通用买一送一盒包邮\",\"parsedContent\":\"17￥ CZ0001 JUeddcPrDOI￥ https://m.tb.cn/h.UhgeYCM  【南京同仁堂】官方旗舰店正品暴汗足丸男女通用买一送一盒包邮\",\"promotionShortUrl\":\"https://s.click.taobao.com/1ciwdQu\",\"couponReceiveNum\":0,\"couponPrice\":60,\"platType\":\"taobao\",\"longTpwd\":\"49￥ CZ0001 otnEdcPIGwe￥ https://m.tb.cn/h.US5ngTw  【南京同仁堂】官方旗舰店正品暴汗足丸男女通用买一送一盒包邮\",\"commissionAmount\":11.96,\"tpwd\":\"0￥otnEdcPIGwe￥/\"},\"requestId\":\"60df9ecf3fbb7a678b22662134b0ca08\",\"time\":1668606208147657}");

//        String con = msgHandler.getParsedContent(res.getJSONObject("data"), null);
//        System.out.println(con);

        boolean checked = msgHandler.checkOrder("303338066717959006");
        System.out.println(checked);
    }



}
