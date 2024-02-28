/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.utils;

import java.util.Locale;
import java.util.UUID;

/**
 * 时间工具类
 */
public class SuUtils{
    //生成8位随机字符串（重复概率1/218万亿）
    public static String generateShortUuid() {
        //todo 注释暂时只生成数字的
//        String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
//                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
//                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "5",
//                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
//                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
//                "W", "X", "Y", "Z"};
//        StringBuffer shortBuffer = new StringBuffer();
//        String uuid = UUID.randomUUID().toString().replace("-", "");
//        for (int i = 0; i < 8; i++) {
//            String str = uuid.substring(i * 4, i * 4 + 4);
//            int x = Integer.parseInt(str, 16);
//            shortBuffer.append(chars[x % 0x3D]);
//        }
//        return shortBuffer.toString().toUpperCase();

        String str = String.valueOf(System.currentTimeMillis());
//        System.out.println(str);
        return str.substring(5, str.length());
    }

    public static void main(String[] args) {
        for(int i = 0; i <100; i++) {
            String id = SuUtils.generateShortUuid();
            System.out.println(id.toUpperCase());
        }

    }
}
