package com.mailvor.modules.tk.util;


import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class HttpUtil {
    private static PoolingHttpClientConnectionManager cm;
    private static String EMPTY_STR = "";
    private static String UTF_8 = "UTF-8";
    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            // 整个连接池最大连接数
            cm.setMaxTotal(1000);
            // 每路由最大连接数，默认值是2
            cm.setDefaultMaxPerRoute(cm.getMaxTotal());
        }
    }

    /**
     * 通过连接池获取HttpClient
     *
     * @return
     */
    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom()
                .setRetryHandler(new HttpRequestRetryHandler(){
                    @Override
                    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                        if (executionCount >= 1) {
                            // Do not retry if over max retry count
                            return false;
                        }
                        return true;
                    }
                })
                .setConnectionManager(cm).setConnectionManagerShared(true).build();
    }

    /**
     * @param url
     * @return
     */
    public static String httpGetRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)   //设置连接超时时间
                .setConnectionRequestTimeout(5000) // 设置请求超时时间
                .setSocketTimeout(5000)
                .setRedirectsEnabled(false)//默认允许自动重定向
                .build();
        httpGet.setConfig(config);
        httpGet.setHeader("Accept", "application/json");
        httpGet.addHeader("Accept-Encoding" ,"gzip"); //请求使用数据压缩
        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, String> params) throws URISyntaxException {
        String paramsStr = covertParamsForStr(params);
        url = url.contains("?") ? url : url + "?";
        HttpGet httpGet = new HttpGet(url + paramsStr);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)   //设置连接超时时间
                .setConnectionRequestTimeout(5000) // 设置请求超时时间
                .setSocketTimeout(5000)
                .setRedirectsEnabled(false)//默认允许自动重定向
                .build();
        httpGet.setConfig(config);
        httpGet.setHeader("Accept", "application/json");
        httpGet.addHeader("Accept-Encoding" ,"gzip"); //请求使用数据压缩
        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws URISyntaxException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);
        HttpGet httpGet = new HttpGet(ub.build());
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    public static String httpPostRequest(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url,String json) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(json,"utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));

        return getResult(httpPost);
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    private static ArrayList<NameValuePair> covertParams2NVPSForStr(Map<String, String> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> param : params.entrySet()) {
            try {
                pairs.add(new BasicNameValuePair(param.getKey(), URLEncoder.encode(param.getValue(), "utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return pairs;
    }

    private static String covertParamsForStr(Map<String, String> paraMap) {
        if(paraMap == null){
            paraMap = new HashMap<>();
        }
        paraMap= new TreeMap<>(paraMap);
        StringBuilder sb = new StringBuilder();
        paraMap.entrySet().stream().forEach(entry ->{
            sb.append(entry.getKey());
            sb.append("=");
            try {
                sb.append(URLEncoder.encode(entry.getValue(),"utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb.append("&");
        });
        return sb.toString();
    }

    /**
     * 处理Http请求
     */
    private static String getResult(HttpRequestBase request) {
        CloseableHttpClient httpClient = getHttpClient();
        String result = "";
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            response.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }finally {
            try{
                if(httpClient != null){
                    httpClient.close(); //释放资源
                }
            }catch (Exception e){

            }
        }
        return result;
    }

    public static String parseDyLink(String dyLink) throws UnsupportedEncodingException {
        if(StringUtils.isBlank(dyLink)){
            return null;
        }
        dyLink = URLDecoder.decode(dyLink, "UTF-8");
        String dyAddiStr = dyLink.substring(dyLink.indexOf("request_additions=") + "request_additions=".length());
        JSONObject dyAddi = JSON.parseObject(dyAddiStr);
        String goodsId = dyAddi.getString("product_id");
        return goodsId;
    }

    public static JSONObject parseDyDetail(JSONObject dyDetail, String goodsId, String origContent, String dyPassword) {
        JSONObject dyData = new JSONObject();
        dyData.put("parseStatus", 3);
        dyData.put("originContent", origContent);
        dyData.put("platType", "dy");
        dyData.put("itemId", goodsId);
        dyData.put("itemLink", dyDetail.getString("detailUrl"));
        dyData.put("itemName", dyDetail.getString("title"));
        dyData.put("mainPic", dyDetail.getString("cover"));
        dyData.put("originalPrice", dyDetail.getDouble("price") + dyDetail.getDouble("couponPrice"));
        dyData.put("actualPrice", dyDetail.getDouble("price"));
        dyData.put("couponPrice", dyDetail.getDouble("couponPrice"));
        dyData.put("commissionRate", dyDetail.getDouble("cosRatio")/100);
        dyData.put("commissionAmount", dyDetail.getDouble("cosFee"));
        dyData.put("parsedContent", dyPassword);
        return dyData;
    }
    public static String parseVipLink(String vipLink) {
        if(StringUtils.isBlank(vipLink)){
            return null;
        }
        if(vipLink.contains("mst.vip.com")) {
            String parseContent = ReUtil.get("goodsId=\\d{10,25}", vipLink, 0);
            return parseContent.split("=")[1];
        } else {
            String url = vipLink.split("\\.html\\?")[0];
            return url.substring(url.lastIndexOf("-") + 1);
        }

    }


    public static JSONObject parseVipDetail(JSONObject vipDetail, String goodsId, String origContent, String buyLink) {
        JSONObject dyData = new JSONObject();
        Double origPrice = Double.parseDouble(vipDetail.getString("marketPrice"));
        Double actPrice = Double.parseDouble(vipDetail.getString("vipPrice"));
        dyData.put("parseStatus", 3);
        dyData.put("originContent", origContent);
        dyData.put("platType", "vip");
        dyData.put("itemId", goodsId);
        dyData.put("itemLink", vipDetail.getString("destUrl"));
        dyData.put("itemName", vipDetail.getString("goodsName"));
        dyData.put("mainPic", vipDetail.getString("goodsMainPicture"));
        dyData.put("originalPrice", origPrice);
        dyData.put("actualPrice", actPrice);
        dyData.put("couponPrice", origPrice - actPrice);
        dyData.put("commissionRate", Double.parseDouble(vipDetail.getString("commissionRate"))/100);
        dyData.put("commissionAmount", Double.parseDouble(vipDetail.getString("commission")));
        dyData.put("parsedContent", buyLink);
        return dyData;
    }


    public static JSONObject parseVipKuDetail(JSONObject vipDetail, String goodsId, String origContent, String buyLink) {
        JSONObject dyData = new JSONObject();
        dyData.put("originContent", origContent);
        dyData.put("plat_type", 5);
        dyData.put("itemId", goodsId);
        dyData.put("item_title", vipDetail.getString("goodsName"));
        dyData.put("item_pic", vipDetail.getString("goodsMainPicture"));
        dyData.put("item_price", vipDetail.getString("marketPrice"));
        dyData.put("item_end_price", vipDetail.getString("vipPrice"));
        dyData.put("rates", vipDetail.getString("commissionRate"));
        dyData.put("click_url", buyLink);
        return dyData;
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        String pa = HttpUtil.parseDyLink("snssdk1128://ec_goods_detail/?promotion_id=3495731132006296371&kol_id=99514375927&source_page=outer_product_share&request_additions=%7B%22enter_from%22%3A%22outer_product_share%22%2C%22sec_author_id%22%3A%22MS4wLjABAAAA2I9NdgAKZrz9e0tLm1csyDMNqLESPDm34TdYYqXe8-I%22%2C%22cps_track%22%3A%22ClMI972T3PICEOgIGihkeV8xMDcwNTYxNDMyMDQ3MzA4MTQ3NTJfMzczN18xODQ3Mzk1OTYyIg43NzMwMjZfMjhfMDIwMDAAOgBCB01idEs1SDFIABIMCISHieT_9Y-3MBAC%22%2C%22product_id%22%3A3489796786346935172%7D");
        System.out.println(pa);
        pa = HttpUtil.parseVipLink("https://m.vip.com/product-1710615488-6919190883148094528.html?nmsns=shop_iphone-7.81.2-link&nst=product&nsbc=&nct=link&ncid=077f877d71a19ee0dd096be34e0d3a3074df66ed&nabtid=13&nuid=&nchl_param=share:077f877d71a19ee0dd096be34e0d3a3074df66ed:1667057660560&mars_cid_a=077f877d71a19ee0dd096be34e0d3a3074df66ed&chl_type=share");
        System.out.println(pa);
        String content = "https://mst.vip.com/x6M88WQc9Abq5h0CHNg1uw.php?wapid=mst_100099181&amp;_src=mst&amp;extra_banner=115099181&amp;nova=1&amp;nova_platform=1&amp;mst_page_type=guide&amp;goodsId=6920488271067019663&amp;brandId=1712388815&amp;actType=normal&amp;launchId=&amp;nmsns=shop_android-9.7.6-link&amp;nst=product&amp;nsbc=&amp;nct=link&amp;ncid=0cab4d50-cf65-343b-8f94-1d56adb43b0d&amp;nabtid=13&amp;nuid=64484205&amp;nchl_param=share:0cab4d50-cf65-343b-8f94-1d56adb43b0d:1693994114511&amp;mars_cid_a=0cab4d50-cf65-343b-8f94-1d56adb43b0d&amp;chl_type=share";
        String goodsId = ReUtil.get("goodsId=\\d{15,25}", content, 0);
        System.out.println(goodsId);
    }

}
