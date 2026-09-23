package com.dtk.api.utils;

import com.dtk.api.exception.DtkApiException;
import com.dtk.api.exception.DtkResultEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * dtk api: Assert util
 *
 * @author baige
 * @date 2020/11/13 9:12
 */
public class Assert {

    private Assert() {
    }

    public static void notNull(Object object, DtkResultEnum dtkResultEnum) {
        if (object == null) {
            throw new DtkApiException(dtkResultEnum);
        }
    }

    public static void notTrue(boolean flag, DtkResultEnum dtkResultEnum) {
        if (flag) {
            throw new DtkApiException(dtkResultEnum);
        }
    }

    public static void notBank(String str, DtkResultEnum dtkResultEnum) {
        if (StringUtils.isBlank(str)) {
            throw new DtkApiException(dtkResultEnum);
        }
    }

    public static void notBank(String str, DtkResultEnum dtkResultEnum, String... msg) {
        if (StringUtils.isBlank(str)) {
            throw new DtkApiException(String.format("%s：%s", dtkResultEnum.getMsg(), Arrays.toString(msg)));
        }
    }
}
