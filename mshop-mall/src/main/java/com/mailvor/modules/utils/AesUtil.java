package com.mailvor.modules.utils;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.mailvor.utils.StringUtils;

public class AesUtil {
    private static SymmetricCrypto aes;
    private static String aesKey;
    public static void init(String key) {
        aesKey = key;
    }

    public static String decrypt(String str) {
        if(StringUtils.isBlank(str)) {
            return str;
        }
        if(aes == null) {
            aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
        }
        return aes.decryptStr(str);
    }
    public static String encrypt(String str) {
        if(StringUtils.isBlank(str)) {
            return str;
        }
        if(aes == null) {
            aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
        }

        return aes.encryptBase64(str);
    }

    public static void main(String[] args) {
        AesUtil.init("LwGop09a6WLonrZNzaihBY==");
        String encStr = AesUtil.encrypt("https://oss.mailvor.cn/dlb/card/2411/3/E2411T1682921050.pdf");
        System.out.println("encStr:  " + encStr);
        String decStr = AesUtil.decrypt(encStr);
        System.out.println(decStr);
    }
}
