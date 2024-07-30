package com.mailvor.modules.mp.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.message.WxMaMessageHandler;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.ShopKeyUtils;
import com.google.common.collect.Maps;
import me.chanjar.weixin.common.api.WxConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Binary Wang
 */
@Configuration(proxyBeanMethods = false)
public class WxMaConfiguration {
    private static Map<String, WxMaService> maServices = Maps.newHashMap();
    private static Map<String, WxMaMessageRouter> routers = Maps.newHashMap();
    private static RedisUtils redisUtils;
    private static WxMaMessageHandler wxMaMessageHandler;

    public static WxMaMessageRouter getRouter(String appid) {
        return routers.get(appid);
    }
    @Autowired
    public WxMaConfiguration(RedisUtils redisUtils) {
        WxMaConfiguration.redisUtils = redisUtils;
    }

    public static WxMaService getWxMaService() {
        WxMaService wxMaService = maServices.get(ShopKeyUtils.getMshopWeiXinMaSevice());
        //增加一个redis标识
        if(wxMaService == null || redisUtils.get(ShopKeyUtils.getMshopWeiXinMaSevice()) == null){
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setMsgDataFormat("JSON");
            config.setAppid(RedisUtil.get(ShopKeyUtils.getWxAppAppId()));
            config.setSecret(RedisUtil.get(ShopKeyUtils.getWxAppSecret()));
            config.setToken(RedisUtil.get(ShopKeyUtils.getWechatMaToken()));
            config.setAesKey(RedisUtil.get(ShopKeyUtils.getWechatMaEncodingAESKey()));
            wxMaService = new WxMaServiceImpl();
            wxMaService.setWxMaConfig(config);
            maServices.put(ShopKeyUtils.getMshopWeiXinMaSevice(), wxMaService);
            routers.put(ShopKeyUtils.getMshopWeiXinMaSevice(), newRouter(wxMaService));
            //增加标识
            redisUtils.set(ShopKeyUtils.getMshopWeiXinMaSevice(),"mshop");

        }
        return wxMaService;
    }
    /**
     * 移除WxMpService
     */
    public static void removeWxMaService(){
        redisUtils.del(ShopKeyUtils.getMshopWeiXinMaSevice());
        maServices.remove(ShopKeyUtils.getMshopWeiXinMaSevice());
        routers.remove(ShopKeyUtils.getMshopWeiXinMaSevice());
    }
    private static WxMaMessageRouter newRouter(WxMaService service) {
        final WxMaMessageRouter router = new WxMaMessageRouter(service);
        router
                .rule().handler(wxMaMessageHandler).next()
                .rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(SystemConfigConstants.BINDSTATECHANGE).handler(BINDSTATECHANGE_HANDLER).end();
        return router;
    }
    private static final WxMaMessageHandler BINDSTATECHANGE_HANDLER = (wxMessage, context, service, sessionManager) -> {
        wxMessage.getFromUser();
        wxMessage.getContent();
        return null;
    };
}

