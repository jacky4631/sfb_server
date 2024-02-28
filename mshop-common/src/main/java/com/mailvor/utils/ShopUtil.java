/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.utils;

import java.io.*;
import java.util.Base64;
import java.util.UUID;

/**
 * 分页工具
 * @author Zheng Jie
 * @date 2023-03-10
 */
public class ShopUtil{
    public static final Integer MAX_TB = 1800;
    public static final Integer MAX_JD = 500;
    public static final Integer MAX_PDD = 550;
    public static final Integer MAX_DY = 200;
    public static final Integer MAX_VIP = 110;


    /**
     * 根据uid计算得到起始页
     */
    public static Integer getPage(Long uid, String platform) {

        uid = uid+1;
        int length = uid.toString().length();
        int platformValue = MAX_PDD;
        switch (platform) {
            case "tb":
                platformValue = MAX_TB;
                break;
            case "jd":
                platformValue = MAX_JD;
                break;
            case "dy":
                platformValue = MAX_DY;
                break;
            case "vip":
                platformValue = MAX_VIP;
                break;
        }
        char[] chars = uid.toString().toCharArray();
        int page = 0;
        if(length <= 3) {
            for(int i = 0; i< chars.length; i++) {
                page += chars[i];
            }
        } else if(length >= 4){
            page = (chars[0]-'0')*(chars[1]-'0') + (chars[2]-'0')*(chars[3]-'0') + (chars[3]-'0');
            if(length > 4) {
                page += (chars[4]-'0');
            }
        }
        if(page > platformValue) {
            return page -platformValue;
        }
        page += uid/Math.pow(10, length);

        return page;
    }

    public static String generateImage(String base64, String path) {
        // 解密
        try {
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }
//            String savePath = "D:/imgtest/";
            // 图片分类路径+图片名+图片后缀
            String imgClassPath = path.concat(UUID.randomUUID().toString()).concat(".jpg");
            // 去掉base64前缀 data:image/jpeg;base64,
            base64 = base64.substring(base64.indexOf(",", 1) + 1);
            // 解密，解密的结果是一个byte数组
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] imgbytes = decoder.decode(base64);
            for (int i = 0; i < imgbytes.length; ++i) {
                if (imgbytes[i] < 0) {
                    imgbytes[i] += 256;
                }
            }

            // 保存图片
            OutputStream out = new FileOutputStream(imgClassPath);
            out.write(imgbytes);
            out.flush();
            out.close();
            // 返回图片的相对路径 = 图片分类路径+图片名+图片后缀
            return imgClassPath;
        } catch (IOException e) {
            return null;
        }
    }


    public static ByteArrayInputStream base64ToIs(String base64) {
        // 解密
        ByteArrayInputStream stream = null;
        try {

            // 去掉base64前缀 data:image/jpeg;base64,
            base64 = base64.substring(base64.indexOf(",", 1) + 1);
            // 解密，解密的结果是一个byte数组
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] imgbytes = decoder.decode(base64);
            for (int i = 0; i < imgbytes.length; ++i) {
                if (imgbytes[i] < 0) {
                    imgbytes[i] += 256;
                }
            }
            stream = new ByteArrayInputStream(imgbytes);

        } catch (Exception e) {
            return null;
        }
        return stream;
    }
    public static void main(String[] args) {
        for(long i = 0; i<10000; i++) {
            System.out.println("uid: " + i + " page： " + ShopUtil.getPage(i, "vip"));
        }
//        System.out.println("uid: " + 1606 + " page" + ShopUtil.getPage(1606L, "dy"));
    }
}
