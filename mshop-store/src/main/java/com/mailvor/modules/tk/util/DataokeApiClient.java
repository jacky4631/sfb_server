package com.mailvor.modules.tk.util;

import cn.hutool.http.HttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * @projectName:dtk-items-openapi
 * @createTime: 2019年11月28日14:13:21
 * @description:
 */
public class DataokeApiClient {

    public static String sendReqNew(String url, String secret, Map<String, Object> paraMap){
        if(null == url || "".equals(url)){
            return "请求地址不能为空";
        }
        if(null == secret || "".equals(secret)){
            return "secret不能为空";
        }
        if(null == paraMap || paraMap.size() < 1){
            return "参数不能为空";
        }

        String timer = String.valueOf(System.currentTimeMillis());
        paraMap.put("timer", timer);
        paraMap.put("nonce", "110505");
        paraMap.put("signRan", SignMD5Util.getSignStrNew(paraMap, secret));
        String data = HttpUtil.get(url, paraMap);

        return data;
    }

    public static String sendReq(String url, String secret, Map<String, Object> paraMap){
        if(null == url || "".equals(url)){
            return "请求地址不能为空";
        }
        if(null == secret || "".equals(secret)){
            return "secret不能为空";
        }
        if(null == paraMap || paraMap.size() < 1){
            return "参数不能为空";
        }

        paraMap.put("sign", SignMD5Util.getSignStr(paraMap, secret));
        if(paraMap.containsKey("keyWords")) {
            try {
                paraMap.put("keyWords", URLEncoder.encode((String)paraMap.get("keyWords"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        String data = HttpUtil.get(url, paraMap);

        return data;
    }

    public static String sendReq(String url, String key, String secret, String version, Map<String, Object> paraMap) {
        paraMap.put("version", version);
        paraMap.put("appKey", key);
        return DataokeApiClient.sendReq(url, secret, paraMap);
    }

    public static void main(String[] args) {

//        String url = "https://openapi.dataoke.com/api/goods/nine/op-goods-list";//9块9
        String url = "https://openapi.dataoke.com/api/goods/get-ranking-list";//热力主播推荐

        String appKey = "5d8d8ceb094a5";
        String appSecret = "f4ff27cee4a4b9579561ec0722cbd734";
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.3.0");
        paraMap.put("appKey", appKey);
        paraMap.put("pageId", "1");
        paraMap.put("pageSize", "20");
//        paraMap.put("keyWords", "");
        paraMap.put("rankType", "1");
        long start = System.currentTimeMillis();
        String data = DataokeApiClient.sendReq(url, appSecret, paraMap);
        System.out.println("耗时："+ (System.currentTimeMillis() - start));
        System.out.println(data);

    }

}
