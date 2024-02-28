package com.mailvor.modules.shanyan.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Description: AES加密解密方法
 */
public class AESUtils {

    private static final String charset = "UTF-8";


    /**
     * Description: AES解密
     */
    public static String decrypt(String sSrc, String sKey, String siv) throws Exception {
        try {
            if (sSrc == null || sSrc.length() == 0) {
                return null;
            }
            if (sKey == null) {
                throw new Exception("decrypt key is null");
            }
            if (sKey.length() != 16) {
                throw new Exception("decrypt key length error");
            }
            byte[] Decrypt = ByteFormat.hexToBytes(sSrc);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(charset), "AES");
            IvParameterSpec iv = new IvParameterSpec(siv.getBytes(charset));//new IvParameterSpec(getIV());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);//使用解密模式初始化 密
            return new String(cipher.doFinal(Decrypt), charset);
        } catch (Exception ex) {
            throw new Exception("decrypt errot", ex);
        }
    }
}
