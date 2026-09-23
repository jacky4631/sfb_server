package com.dtk.api.http;

import com.dtk.api.constant.DtkApiConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 日志打印
 *
 * @author xx
 * @date 2020/12/2 18:08
 */
@Slf4j
public class Logger {
    private static final String OS_NAME = System.getProperties().getProperty("os.name");
    private static boolean needEnableLogger = true;
    private static String ip = null;

    public static void setNeedEnableLogger(boolean needEnableLogger) {
        Logger.needEnableLogger = needEnableLogger;
    }

    private Logger() {
    }

    public static void logCommError(Exception e, String url, String appKey, String method,
                                    Map<String, String> params) {
        if (!needEnableLogger) {
            return;
        }
        _logCommError(e, null, url, appKey, method, params);
    }

    public static void logCommError(Exception e, HttpURLConnection conn, String appKey,
                                    String method, Map<String, String> params) {
        if (!needEnableLogger) {
            return;
        }
        _logCommError(e, conn, null, appKey, method, params);
    }

    public static void logCommError(Exception e, String url, String appKey, String method,
                                    byte[] content) {
        if (!needEnableLogger) {
            return;
        }

        String contentString = new String(content, StandardCharsets.UTF_8);
        logCommError(e, url, appKey, method, contentString);
    }

    private static void logCommError(Exception e, String url, String appKey, String method,
                                     String content) {
        Map<String, String> params = parseParam(content);
        _logCommError(e, null, url, appKey, method, params);
    }

    /**
     * 通讯错误日志
     */
    private static void _logCommError(Exception e, HttpURLConnection conn, String url,
                                      String appKey, String method, Map<String, String> params) {
        DateFormat df = new SimpleDateFormat(DtkApiConstant.DATE_TIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone(DtkApiConstant.DATE_TIMEZONE));
        String sdkName = DtkApiConstant.SDK_VERSION;
        String urlStr = null;
        String rspCode = "";
        if (conn != null) {
            try {
                urlStr = conn.getURL().toString();
                rspCode = "HTTP_ERROR_" + conn.getResponseCode();
            } catch (IOException ioe) {
                log.error(ioe.getMessage());
            }
        } else {
            urlStr = url;
            rspCode = "";
        }
        StringBuilder sb = new StringBuilder();
        // 时间
        sb.append(df.format(new Date()));
        sb.append("^_^");
        // API
        sb.append(method);
        sb.append("^_^");
        // APP
        sb.append(appKey);
        sb.append("^_^");
        // IP地址
        sb.append(getIp());
        sb.append("^_^");
        // 操作系统
        sb.append(OS_NAME);
        // SDK名字,这是例子，请换成其他名字
        sb.append("^_^");
        sb.append(sdkName);
        sb.append("^_^");
        // 请求URL
        sb.append(urlStr);
        sb.append("^_^");
        sb.append(rspCode);
        sb.append("^_^");
        sb.append((e.getMessage() + "").replaceAll("\r\n", " "));
        log.error(sb.toString());
    }

    public static void logCommError(Exception e, HttpURLConnection conn, String appKey,
                                    String method, byte[] content) {
        if (!needEnableLogger) {
            return;
        }

        String contentString;
        try {
            contentString = new String(content, StandardCharsets.UTF_8);
            logCommError(e, conn, appKey, method, contentString);
        } catch (Exception e1) {
            log.error(e1.getMessage());
        }
    }

    private static Map<String, String> parseParam(String contentString) {
        Map<String, String> params = new HashMap<>(16);
        if (contentString == null || "".equals(contentString.trim())) {
            return params;
        }
        String[] paramsArray = contentString.split("&");
        for (String param : paramsArray) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }

    /**
     * 通讯错误日志
     */
    private static void logCommError(Exception e, HttpURLConnection conn, String appKey,
                                     String method, String content) {
        Map<String, String> params = parseParam(content);
        logCommError(e, conn, null, appKey, method, params);
    }

    /**
     * 通讯错误日志
     */
    private static void logCommError(Exception e, HttpURLConnection conn, String url,
                                     String appKey, String method, Map<String, String> params) {
        DateFormat df = new SimpleDateFormat(DtkApiConstant.DATE_TIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone(DtkApiConstant.DATE_TIMEZONE));
        String sdkName = DtkApiConstant.SDK_VERSION;
        String urlStr = null;
        String rspCode = "";
        if (conn != null) {
            try {
                urlStr = conn.getURL().toString();
                rspCode = "HTTP_ERROR_" + conn.getResponseCode();
            } catch (IOException ioe) {
                log.error(ioe.getMessage());
            }
        } else {
            urlStr = url;
            rspCode = "";
        }

        StringBuilder sb = new StringBuilder();
        // 时间
        sb.append(df.format(new Date()));
        sb.append("^_^");
        // API
        sb.append(method);
        sb.append("^_^");
        // APP
        sb.append(appKey);
        sb.append("^_^");
        // IP地址
        sb.append(getIp());
        sb.append("^_^");
        // 操作系统
        sb.append(OS_NAME);
        sb.append("^_^");
        // SDK名字,这是例子，请换成其他名字
        sb.append(sdkName);
        sb.append("^_^");
        // 请求URL
        sb.append(urlStr);
        sb.append("^_^");
        sb.append(rspCode);
        sb.append("^_^");
        sb.append((e.getMessage() + "").replaceAll("\r\n", " "));
        log.error(sb.toString());
    }

    public static String getIp() {
        if (ip == null) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ip;
    }
}
