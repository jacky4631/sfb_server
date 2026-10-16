package com.mailvor.modules.tk.util;

import cn.hutool.http.HttpUtil;

import java.util.Map;
import java.util.TreeMap;

import static com.mailvor.modules.tk.util.DataokeApi.GOODS_SEARCH;

/**
 * @projectName:dtk-items-openapi
 * @createTime: 2019年11月28日14:13:21
 * @description:
 */
public class DataokeApiClient {

    public static String sendReq(String url, String secret, Map<String, Object> paraMap){
        if(null == url || url.isEmpty()){
            return "请求地址不能为空";
        }
        if(null == secret || secret.isEmpty()){
            return "secret不能为空";
        }
        if(null == paraMap || paraMap.size() < 1){
            return "参数不能为空";
        }

        // 签名基于原始参数值计算（与官方 SDK 一致）。
        // 传输时的 URL 编码统一交由 HttpUtil.get 完成（内部只会编码一次），
        // 切勿在此再手动 URLEncoder.encode，否则会造成双重编码，服务端解出的值与加签时的原始值不一致，导致验签失败。
        paraMap.put("sign", SignMD5Util.getSignStr(paraMap, secret));
        String data = HttpUtil.get(url, paraMap);

        return data;
    }

    public static String sendReq(String url, String key, String secret, String version, Map<String, Object> paraMap) {
        paraMap.put("version", version);
        paraMap.put("appKey", key);
        return DataokeApiClient.sendReq(url, secret, paraMap);
    }

    public static void main(String[] args) {

        String url = GOODS_SEARCH.getUrl();//热力主播推荐

        String appKey = "5d8d";
        String appSecret = "f4ff27cee4";
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("version", "v2.1.2");
        paraMap.put("appKey", appKey);
        paraMap.put("pageId", "1");
        paraMap.put("pageSize", "20");
        paraMap.put("keyWords", "抽纸");
        long start = System.currentTimeMillis();
        String data = DataokeApiClient.sendReq(url, appSecret, paraMap);
        System.out.println("耗时："+ (System.currentTimeMillis() - start));
        System.out.println(data);

    }

}
