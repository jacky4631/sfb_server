package com.mailvor.modules.tk.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


/**
 *
 * */
@Slf4j
@Component
public class ClipboardParseUtil {
    private static Pattern tbPattern = Pattern.compile("taobao|tb");
    private static Pattern jdPattern = Pattern.compile("京|jd|JD");
    private static Pattern pddPattern = Pattern.compile("yangkeduo");
    private static Pattern dyPattern = Pattern.compile("抖音|douyin");
    private static Pattern vipPattern = Pattern.compile("vip");
    private static Pattern excludePattern = Pattern.compile("零撸|提现");
    private static Long contentLimit = 1200L;

    public static Pattern getTbPattern() {
        return tbPattern;
    }

    public static Pattern getJdPattern() {
        return jdPattern;
    }

    public static Pattern getPddPattern() {
        return pddPattern;
    }

    public static Pattern getDyPattern() {
        return dyPattern;
    }

    public static Pattern getVipPattern() {
        return vipPattern;
    }

    public static Pattern getExcludePattern() {
        return excludePattern;
    }

    public static Long getContentLimit() {
        return contentLimit;
    }
}
