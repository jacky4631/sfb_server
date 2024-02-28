package com.mailvor.modules.shanyan;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.shanyan.config.ShanyanConfig;
import com.mailvor.modules.shanyan.utils.AESUtils;
import com.mailvor.modules.shanyan.utils.MD5;
import com.mailvor.modules.shanyan.utils.OkHttpUtil;
import com.mailvor.modules.shanyan.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 一键登录功能：token置换手机号
 *
 * @author Administrator*/
public class ShanyanMobileUtil {

    public static String login(String token, Integer type, ShanyanConfig config) {
        try {
            String appId;
            String appKey;
            if(type == 1) {
                appId = config.getAndroidAppId();
                appKey = config.getAndroidAppKey();
            } else {
                appId = config.getIosAppId();
                appKey = config.getIosAppKey();
            }

            Map<String, String> params = new HashMap();
            params.put("token", token);
            params.put("appId", appId);
            params.put("sign", SignUtils.getSign(params, appKey));
            JSONObject jsonObject = OkHttpUtil.postRequest(config.getLoginUrl(), params);
            if (null != jsonObject) {
                //返回码 200000为成功
                String code = jsonObject.getString("code");
                if ("200000".equals(code)) {
                    String dataStr = jsonObject.getString("data");
                    JSONObject dataObj = JSONObject.parseObject(dataStr);
                    String mobile = dataObj.getString("mobileName");
                    String key = MD5.getMD5Code(appKey);
                    //解密后的手机号码
                    mobile = AESUtils.decrypt(mobile, key.substring(0, 16), key.substring(16));
                    System.out.println("mobile:" + mobile);
                    return mobile;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
