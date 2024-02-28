/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.handler;

import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;


@Component
public class MenuHandler extends AbstractHandler {
    @Resource
    private RedisUtils redisUtil;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        String msg = String.format("type:%s, event:%s, key:%s",
            wxMessage.getMsgType(), wxMessage.getEvent(),
            wxMessage.getEventKey());
        if (MenuButtonType.VIEW.equals(wxMessage.getEvent())) {
            return null;
        }
        String res = getRes(wxMessage.getEventKey());
        if(res != null) {
            msg = res;
        }

        return WxMpXmlOutMessage.TEXT().content(msg)
            .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
            .build();
    }

    public String getRes(String key) {
        if("1111".equals(key)) {
            return "提现请使用苏分宝app\n━┉┉┉┉∞┉┉┉┉━\n<a href='https://www.pgyer.com/sufenbao'>点击下载app</a>\n━┉┉┉┉∞┉┉┉┉━\n原公众号余额和积分会在注册app账号之日起两周内迁移！";
        } else if("2222".equals(key)) {
            return "━┉┉┉┉∞┉┉┉┉━\n请输入苏分宝APP绑定的手机号\n━┉┉┉┉∞┉┉┉┉━\n";
        }
        String value = redisUtil.getY("wechat:keyword:" + key);
        if(StringUtils.isBlank(value)) {
            return "";
        }
        return value;

    }

    public static void main(String[] args) {
        MenuHandler menuHandler = new MenuHandler();
        String res = menuHandler.getRes("1111");
        System.out.println(res);
        res = menuHandler.getRes("2222");
        System.out.println(res);


    }

}
