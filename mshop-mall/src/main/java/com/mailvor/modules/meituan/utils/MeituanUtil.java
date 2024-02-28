package com.mailvor.modules.meituan.utils;

import com.aliyun.oss.ServiceException;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
public class MeituanUtil {
    private static final String ENCRY_ALGORITHM = "AES";

    /**
     * 加密算法/加密模式/填充类型
     */
    private static final String CIPHER_MODE = "AES/ECB/PKCS5Padding";

    /**
     * 设置iv偏移量
     */
    private static final String IV_ = null;

    /**
     * 设置加密字符集
     */
    private static final String CHARACTER = "UTF-8";

    /**
     * 设置加密密码处理长度。
     */
    private static final int PWD_SIZE = 16;

    /**
     * 加密
     */
    public static String encryptHex(String text, String password) {
        try {
            byte[] cipherTextBytes = encrypt(text.getBytes(CHARACTER), pwdHandler(password));
            String cipherText = byte2hex(cipherTextBytes);
            return cipherText;
        } catch (Exception e) {
            log.warn("[AESUtil] encrypt exception, text:" + text, e);
        }
        return null;
    }
    public static byte[] encrypt(byte[] clearTextBytes, byte[] pwdBytes) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] cipherTextBytes = cipher.doFinal(clearTextBytes);
        return cipherTextBytes;
    }

    /**
     * HEX解密
     */
    public static String decryptHex(String text, String password) {
        try {
            byte[] cipherTextBytes = hex2byte(text);
            byte[] clearTextBytes = decrypt(cipherTextBytes, pwdHandler(password));
            return new String(clearTextBytes, CHARACTER);
        } catch (Exception e) {
            log.warn("[AESUtil] encrypt exception, text:" + text, e);
        }
        return null;
    }

    /**
     * 原始解密
     */
    public static byte[] decrypt(byte[] cipherTextBytes, byte[] pwdBytes) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] clearTextBytes = cipher.doFinal(cipherTextBytes);
        return clearTextBytes;
    }

    private static byte[] hex2byte(String str) {
        if (str == null || str.length() < 2) {
            return new byte[0];
        }
        str = str.toLowerCase();
        int l = str.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = str.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    /**
     * 密码处理方法
     */
    private static byte[] pwdHandler(String password) throws UnsupportedEncodingException {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(PWD_SIZE);
        sb.append(password);
        while (sb.length() < PWD_SIZE) {
            sb.append("0");
        }
        if (sb.length() > PWD_SIZE) {
            sb.setLength(PWD_SIZE);
        }
        data = sb.toString().getBytes("UTF-8");
        return data;
    }
    public static String byte2hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        String tmp = "";
        for (int n = 0; n < bytes.length; n++) {
            tmp = (java.lang.Integer.toHexString(bytes[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * @param params 待签名数据
     * @param secretKey 秘钥
     * @return
     */
    public static String generateSignature(Map<String, Object> params, String secretKey) {
        // 第一步：参数排序
        Set<String> keySet = params.keySet();
        String[] keys = keySet.toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            if ("sign".equals(key)) {
                continue;
            }
            // 参数为空不参与签名
            Object value = params.get(key);
            if (value != null) {
                if(value instanceof String) {
                    if(StringUtils.isNotBlank((String)value)) {
                        query.append(key).append(value);
                    }
                } else {
                    query.append(key).append(value);
                }

            }
        }

        // 第三步：使用hmac加密
        byte[] bytes = encryptHMAC(query.toString(), secretKey);

        // 第四步：把二进制转化为大写的十六进制(正确签名应该为32大写字符串)
        return byte2hex(bytes);
    }

    public static byte[] encryptHMAC(String data, String secret) {
        byte[] bytes = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(CHARACTER), "HmacMD5");
            Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
            mac.init(secretKeySpec);
            bytes = mac.doFinal(data.getBytes(CHARACTER));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return bytes;
    }
    public static String concatUrl(String basePath, Map<String,Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(basePath);
        sb.append("?");
        Set<String> keySet = params.keySet();
        Iterator<String> iter = keySet.iterator();
        int index = 0;
        while (iter.hasNext()) {
            String key = iter.next();
            if(index == 0) {
                sb.append(key + "=" + params.get(key));
            } else {
                sb.append("&"+ key + "=" + params.get(key));
            }
            index++;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String test = encryptHex("test", "testpublic");
        System.out.println(test);

        String dec = decryptHex("CF8F570C06C93519E80A3FC0B781F96E", "VZdAnUy9Sn1wxMXn");
        System.out.println(dec);
    }
}
