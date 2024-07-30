package com.mailvor.modules.utils;

import com.mailvor.utils.StringUtils;

import java.util.Calendar;


/**
 * 身份证号码验证
 */
public class IDCardUtil {

    /**
     * 判断是否成年
     *
     * @param str
     * @return
     */
    public static boolean isAdult(String str)
    {
        if (StringUtils.isEmpty(str) || (15 != str.length() && 18 != str.length()))
        {
            return false;
        } else
        {
            String birthday = str.substring(6, str.length() - 4);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(birthday.substring(0, 4)) + 18,
                    Integer.parseInt(birthday.substring(4, 6)) - 1,
                    Integer.parseInt(birthday.substring(6)), 23, 59);
            long l = System.currentTimeMillis() - calendar.getTimeInMillis();
            return 0 <= l;
        }
    }

    public static void main(String[] args) {
        boolean adult = IDCardUtil.isAdult("320586200505161212");
        System.out.println(adult);
    }
}

