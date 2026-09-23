package com.dtk.api.http;


import com.dtk.api.constant.DtkApiConstant;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.net.Proxy.Type;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 网络工具类。
 *
 * @author xx
 * @since xx
 */
@Slf4j
public class WebUtils {

    private static final String DEFAULT_CHARSET = DtkApiConstant.Charset.UTF_8;
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";

    private static SSLContext ctx = null;

    private static HostnameVerifier verifier = null;

    private static SSLSocketFactory socketFactory = null;

    private static int keepAliveTimeout = 0;

    /**
     * 是否校验SSL服务端证书，默认为需要校验
     */
    private static volatile boolean needCheckServerTrusted = true;

    /**
     * 设置是否校验SSL服务端证书
     *
     * @param needCheckServerTrusted true：需要校验（默认，推荐）；
     *                               <p>
     *                               false：不需要校验（仅当部署环境不便于进行服务端证书校验，且已有其他方式确保通信安全时，可以关闭SSL服务端证书校验功能）
     */
    public static void setNeedCheckServerTrusted(boolean needCheckServerTrusted) {
        WebUtils.needCheckServerTrusted = needCheckServerTrusted;
    }

    /**
     * 设置KeepAlive连接超时时间，一次HTTP请求完成后，底层TCP连接将尝试尽量保持该超时时间后才关闭，以便其他HTTP请求复用TCP连接
     * <p>
     * KeepAlive连接超时时间设置为0，表示使用默认的KeepAlive连接缓存时长（目前为5s）
     * <p>
     * 连接并非一定能保持指定的KeepAlive超时时长，比如服务端断开了连接
     * <p>
     * 注：该方法目前只在JDK8上测试有效
     *
     * @param timeout KeepAlive超时时间，单位秒
     */
    public static void setKeepAliveTimeout(int timeout) {
        if (timeout < 0 || timeout > 60) {
            throw new RuntimeException("keep-alive timeout value must be between 0 and 60.");
        }
        keepAliveTimeout = timeout;
    }

    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    }

    static {

        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()},
                    new SecureRandom());

            ctx.getClientSessionContext().setSessionTimeout(15);
            ctx.getClientSessionContext().setSessionCacheSize(1000);

            socketFactory = ctx.getSocketFactory();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        verifier = (hostname, session) -> {
            return false; //不允许URL的主机名和服务器的标识主机名不匹配的情况
        };

    }

    private WebUtils() {
    }


    /**
     * 执行HTTP GET请求。
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, DEFAULT_CHARSET);
    }

    /**
     * 执行HTTP GET请求。
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params,
                               String charset) throws IOException {
        HttpURLConnection conn = null;
        String rsp;

        try {
            String ctype = "application/x-www-form-urlencoded;charset=" + charset;
            String query = buildQuery(params, charset);
            try {
                conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype);
            } catch (IOException e) {
                Logger.logCommError(e, url, params.get("appKey"), null, params);
                throw e;
            }

            try {
                rsp = getResponseAsString(conn);
            } catch (IOException e) {
                Logger.logCommError(e, conn, params.get("appKey"), null, params);
                throw e;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    public static HttpURLConnection getConnection(URL url, String method, String ctype) throws IOException {
        return getConnection(url, method, ctype, null);
    }

    public static HttpURLConnection getConnection(URL url, String method, String ctype,
                                                  String proxyHost, int proxyPort) throws IOException {
        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        return getConnection(url, method, ctype, proxy);
    }

    private static HttpURLConnection getConnection(URL url, String method, String ctype, Proxy proxy) throws IOException {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            HttpsURLConnection connHttps = null;
            if (proxy != null) {
                connHttps = (HttpsURLConnection) url.openConnection(proxy);
            } else {
                connHttps = (HttpsURLConnection) url.openConnection();
            }
            if (!needCheckServerTrusted) {
                //设置不校验服务端证书的SSLContext
                connHttps.setSSLSocketFactory(socketFactory);
                connHttps.setHostnameVerifier(verifier);
            }
            conn = connHttps;
        } else {
            if (proxy != null) {
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/plain,text/xml,text/javascript,text/html,application/json");
        conn.setRequestProperty("User-Agent", "aop-sdk-java");
        conn.setRequestProperty("Content-Type", ctype);
        conn.setRequestProperty("Client-Sdk-Type","java");
        return conn;
    }

    private static URL buildGetUrl(String strUrl, String query) throws IOException {
        URL url = new URL(strUrl);
        if (StringUtils.isEmpty(query)) {
            return url;
        }

        if (StringUtils.isEmpty(url.getQuery())) {
            if (strUrl.endsWith("?")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "?" + query;
            }
        } else {
            if (strUrl.endsWith("&")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "&" + query;
            }
        }

        return new URL(strUrl);
    }

    public static String buildQuery(Map<String, String> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (StringUtils.areNotEmpty(name, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }

        return query.toString();
    }

    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
        String charset = getResponseCharset(conn.getContentType());

        //此时设置KeepAlive超时所需数据结构才刚初始化完整，可以通过反射修改
        //同时也不宜将修改时机再滞后，因为可能后续连接缓存类已经消费了默认的KeepAliveTimeout值，再修改已经无效
        setKeepAliveTimeout(conn);

        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtils.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }

    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
            StringWriter writer = new StringWriter();

            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;

        if (!StringUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

    /**
     * 使用默认的UTF-8字符集反编码请求参数值。
     *
     * @param value 参数值
     * @return 反编码后的参数值
     */
    public static String decode(String value) {
        return decode(value, DEFAULT_CHARSET);
    }

    /**
     * 使用默认的UTF-8字符集编码请求参数值。
     *
     * @param value 参数值
     * @return 编码后的参数值
     */
    public static String encode(String value) {
        return encode(value, DEFAULT_CHARSET);
    }

    /**
     * 使用指定的字符集反编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 反编码后的参数值
     */
    public static String decode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLDecoder.decode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 使用指定的字符集编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 编码后的参数值
     */
    public static String encode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLEncoder.encode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> map = null;
        if (url != null && url.indexOf('?') != -1) {
            map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
        }
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    /**
     * 从URL中提取所有的参数。
     *
     * @param query URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query) {
        Map<String, String> result = new HashMap<String, String>();

        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }

        return result;
    }

    public static String buildForm(String baseUrl, Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form name=\"punchout_form\" method=\"post\" action=\"");
        sb.append(baseUrl);
        sb.append("\">\n");
        sb.append(buildHiddenFields(parameters));

        sb.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n");
        sb.append("</form>\n");
        sb.append("<script>document.forms[0].submit();</script>");
        String form = sb.toString();
        return form;
    }

    private static String buildHiddenFields(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            String value = parameters.get(key);
            // 除去参数中的空值
            if (key == null || value == null) {
                continue;
            }
            sb.append(buildHiddenField(key, value));
        }
        return sb.toString();
    }

    private static String buildHiddenField(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<input type=\"hidden\" name=\"");
        sb.append(key);

        sb.append("\" value=\"");
        //转义双引号
        String a = value.replace("\"", "&quot;");
        sb.append(a).append("\">\n");
        return sb.toString();
    }

    /**
     * 由于HttpUrlConnection不支持设置KeepAlive超时时间，该方法通过反射机制设置
     *
     * @param connection 需要设置KeepAlive的连接
     */
    private static void setKeepAliveTimeout(HttpURLConnection connection) {
        if (keepAliveTimeout == 0) {
            return;
        }
        try {

            Field delegateHttpsUrlConnectionField = Class.forName("sun.net.www.protocol.https.HttpsURLConnectionImpl").getDeclaredField(
                    "delegate");
            delegateHttpsUrlConnectionField.setAccessible(true);
            Object delegateHttpsUrlConnection = delegateHttpsUrlConnectionField.get(connection);

            Field httpClientField = Class.forName("sun.net.www.protocol.http.HttpURLConnection").getDeclaredField("http");
            httpClientField.setAccessible(true);
            Object httpClient = httpClientField.get(delegateHttpsUrlConnection);

            Field keepAliveTimeoutField = Class.forName("sun.net.www.http.HttpClient").getDeclaredField("keepAliveTimeout");
            keepAliveTimeoutField.setAccessible(true);
            keepAliveTimeoutField.setInt(httpClient, keepAliveTimeout);
        } catch (Throwable ignored) {
            //设置KeepAlive超时只是一种优化辅助手段，设置失败不应阻塞主链路，设置失败不应影响功能
        }
    }
}
