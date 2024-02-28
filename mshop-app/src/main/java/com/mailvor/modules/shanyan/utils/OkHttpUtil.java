package com.mailvor.modules.shanyan.utils;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.utils.StringUtils;
import okhttp3.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * okhttp连接池单例
 *
 * @author duanzm
 * @create 2018-10-11 16:15
 **/
public class OkHttpUtil {
    private OkHttpUtil() {
    }

    public static OkHttpClient getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private static enum Singleton {
        INSTANCE;
        private OkHttpClient singleton;

        private Singleton() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(6L, TimeUnit.SECONDS);
            builder.readTimeout(6L, TimeUnit.SECONDS);
            builder.writeTimeout(6L, TimeUnit.SECONDS);
            ConnectionPool connectionPool = new ConnectionPool(50, 60, TimeUnit.SECONDS);
            builder.connectionPool(connectionPool);
            singleton = builder.build();
        }

        public OkHttpClient getInstance() {
            return singleton;
        }
    }


    /**
     * token置换手机号后台接口
     */
    public static JSONObject postRequest(String url, Map<String, String> params) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> m : params.entrySet()) {
                builder.add(m.getKey(), m.getValue());
            }
            RequestBody body = builder.build();
            Request request = new Request.Builder().post(body).url(url).build();
            Response response = OkHttpUtil.getInstance().newCall(request).execute();
            if (response.isSuccessful()) {
                String content = response.body().string();
                //System.out.println("response:"+content);
                if (StringUtils.isNotBlank(content)) {
                    JSONObject jsonObject = JSONObject.parseObject(content);
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
