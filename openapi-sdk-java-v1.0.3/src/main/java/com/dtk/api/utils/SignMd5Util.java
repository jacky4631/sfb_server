package com.dtk.api.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * dtk api: SignMD5Util
 *
 * @author 1
 * @date 2020/11/10 11:55
 */
@Slf4j
public class SignMd5Util {

    private SignMd5Util() {
    }

    public static String sign(String content, String key) {
        String signStr = String.format("%s&key=%s", content, key);
        String cipherText;
        return (cipherText = md5(signStr)) == null ? null : cipherText;
    }

    public static void main(String[] args) {

    }

    /**
     * MD5加密算法
     *
     * @param str 待加密字符串
     * @return 密文
     * @see [类、类#方法、类#成员]
     */
    public static String md5(String str) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = str.getBytes(StandardCharsets.UTF_8);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] strArr = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                strArr[k++] = hexDigits[byte0 >>> 4 & 0xf];
                strArr[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(strArr);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}

