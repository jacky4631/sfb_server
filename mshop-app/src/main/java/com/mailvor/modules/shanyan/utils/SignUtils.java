package com.mailvor.modules.shanyan.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Map;

/**
 * Description:签名工具类
 * User: liutao
 * Date: 2019-09-11
 * Time: 10:29
 */
public class SignUtils {

    public static String getSign(Map<String, String> requestMap, String appKey) {
        return hmacSHA256Encrypt(requestMap2Str(requestMap), appKey);
    }


    private static String hmacSHA256Encrypt(String encryptText, String encryptKey) {
        byte[] result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(encryptKey.getBytes("UTF-8"), "HmacSHA256");
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA256");
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            byte[] rawHmac = mac.doFinal(encryptText.getBytes("UTF-8"));
            return ByteFormat.bytesToHexString(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String requestMap2Str(Map<String, String> requestMap) {
        String[] keys = requestMap.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : keys) {
            if (!str.equals("sign")) {
                stringBuilder.append(str).append(requestMap.get(str));
            }
        }
        return stringBuilder.toString();
    }

}
