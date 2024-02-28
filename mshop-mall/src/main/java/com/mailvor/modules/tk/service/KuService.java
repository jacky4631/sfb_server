package com.mailvor.modules.tk.service;


import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.tk.param.ParseContentParam;
import com.mailvor.modules.tk.param.QueryDyKuParam;
import com.mailvor.modules.tk.service.dto.DyLifeCityDto;
import com.mailvor.modules.tk.util.HttpUtils;
import com.mailvor.modules.tk.util.SignMD5Util;
import com.mailvor.modules.tk.vo.DyKuResVo;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2023/01/24 14:55
 * @description:
 */
@Slf4j
@Component
public class KuService {
    public static final String DY_ANALYZE_CODE = "https://v2.api.haodanku.com/dy_analyze_code";
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String KU_API_BASE = "https://v3.api.haodanku.com";


    public static final String KU_API_V3 = KU_API_BASE + "/rest";

    public static final String LOCAL_LIFE_API = KU_API_BASE + "/hv_ratesurl";
    public static final String LOCAL_LIFE_ORDER_API = KU_API_BASE + "/hv_order_list";


    public static final String MEITUAN_ACTIVITY_LIST_API = KU_API_BASE + "/meituan_activity_list";
    public static final String MEITUAN_ACTIVITY_WORD_API = KU_API_BASE + "/meituan_ratesurl";

    public static final String ELE_ACTIVITY_LIST_API = KU_API_BASE + "/elm_activity_list";
    public static final String ELE_ACTIVITY_WORD_API = KU_API_BASE + "/elm_activity_ratesurl";

    public static final String MEITUAN_ORDER_LIST_API = KU_API_BASE + "/meituan_order_list";
    @Resource
    private RestTemplate restTemplate;

    @Value("${haodanku.key}")
    private String key;
    @Value("${haodanku.waimaiKey}")
    private String waimaiKey;

    @Value(("${haodanku.vip.appId}"))
    private String appId;
    @Value(("${haodanku.vip.appSecret}"))
    private String appSecret;



    public JSONObject contentParse(String content) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(key);
        sb.append("&content=");
        sb.append(content);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                DY_ANALYZE_CODE,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody());
    }



    public JSONObject clipboard(ParseContentParam daParam) {
        /**
         * 获取时间
         */
        Date currentDate = new Date();
        String date = formatter.format(currentDate);
        /**
         * 生成签名
         */
        StringBuilder key = new StringBuilder();
        Map<String, String> map = new TreeMap<>(Comparator.naturalOrder());
        map.put("app_id", appId);
        map.put("date", date);
        map.put("method", "analyze.clipboard");
        map.put("content", daParam.getContent());
//        try {
//            map.put("pdd_custom_parameters", URLEncoder.encode(daParam.getCustomerParameters(),"UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        map.put("pdd_pid", daParam.getPddPid());
//        map.put("tb_pid",daParam.getTbPid());
//        map.put("tb_rid", daParam.getTbChannelId());
//        map.put("jd_pid",daParam.getJdPid());
//        map.put("jd_union",daParam.getJdUnionId());

        for (Map.Entry<String, String> entry : map.entrySet()) {
            key.append(entry.getKey()).append(entry.getValue());
        }
        key.append(appSecret);
        String sign = SignMD5Util.MD5(key.toString()).toUpperCase();

        map.put("sign",sign);
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        String jsonStr = jsonObject.toJSONString();
        log.info("提交的JSOn字符串:"+jsonStr);

        return JSON.parseObject(convertUnicodeToCh(HttpUtils.doPost(KU_API_V3, jsonStr.toString())));

    }


    public JSONObject shortLink(String link) throws UnsupportedEncodingException {
        /**
         * 获取时间
         */
        Date currentDate = new Date();
        String date = formatter.format(currentDate);
        /**
         * 生成签名
         */
        StringBuilder key = new StringBuilder();
        Map<String, String> map = new TreeMap<>(Comparator.naturalOrder());
        map.put("app_id", appId);
        map.put("date", date);
        map.put("method", "short.link");
        map.put("link", link);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            key.append(entry.getKey()).append(entry.getValue());
        }
        key.append(appSecret);
        String sign = SignMD5Util.MD5(key.toString()).toUpperCase();

        map.put("sign",sign);
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        String jsonStr = jsonObject.toJSONString();
        log.info("转短链 提交的jsonn字符串:"+jsonStr);

        return JSON.parseObject(convertUnicodeToCh(HttpUtils.doPost(KU_API_V3, jsonStr.toString()))).getJSONObject("data");

    }
    /**
     * 将unicode转换为中文
     *
     * @param str
     * @return
     */
    private static String convertUnicodeToCh(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\w{4}))");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String unicodeFull = matcher.group(1);
            String unicodeNum = matcher.group(2);
            char singleChar = (char) Integer.parseInt(unicodeNum, 16);
            str = str.replace(unicodeFull, singleChar + "");
        }
        return str;
    }



    public JSONObject localLife(String platform, String channel) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(key);
        sb.append("&platform=");
        sb.append(platform);
        sb.append("&channel=");
        sb.append(channel);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                LOCAL_LIFE_API,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody());
    }


    public JSONObject localLifeOrder(int page, int size, long startTime, long endTime, int platform) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(key);
        sb.append("&page=");
        sb.append(page);
        sb.append("&size=");
        sb.append(size);
        sb.append("&startTime=");
        sb.append(startTime);
        sb.append("&endTime=");
        sb.append(endTime);
        sb.append("&platform=");
        sb.append(platform);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                LOCAL_LIFE_ORDER_API,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody());
    }



    public JSONObject meiTuanActivityList(int page, int size) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(waimaiKey);
        sb.append("&min_id=");
        sb.append(page);
        sb.append("&back=");
        sb.append(size);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.getForEntity(
                MEITUAN_ACTIVITY_LIST_API + "?" + sb,
                String.class);
        return JSON.parseObject(re.getBody());
    }


    public JSONObject meiTuanWord(String activityId) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(waimaiKey);
        sb.append("&activity_id=");
        sb.append(activityId);
        sb.append("&link_type=");
        sb.append(4);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                MEITUAN_ACTIVITY_WORD_API,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody());
    }


    public JSONObject eleActivityList(int page, int size) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(waimaiKey);
        sb.append("&min_id=");
        sb.append(page);
        sb.append("&back=");
        sb.append(size);
        ResponseEntity<String> re = restTemplate.getForEntity(
                ELE_ACTIVITY_LIST_API + "?" + sb,
                String.class);
        return JSON.parseObject(re.getBody());
    }


    public JSONObject eleWord(String activityId, Long uid) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(waimaiKey);
        sb.append("&activity_id=");
        sb.append(activityId);
        sb.append("&sid=");
        sb.append(uid);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                ELE_ACTIVITY_WORD_API,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody());
    }
    /**
     * Dy life list json object.
     *
     * @param page       the page
     * @param size       the size
     * @param cityCode   the city code
     * @param categoryId the category id
     * @param sort       排序：1销量降序，2销量升序，3距离升序，4距离降序，5价格升序，6价格降序
     * @param keyword    the keyword
     * @param longitude  经度
     * @param latitude   纬度
     * @return the json object
     */
    public JSONObject dyLifeList(int page, int size, String cityCode, String categoryId, Integer sort, String keyword, Double longitude, Double latitude) {
        StringBuilder sb = new StringBuilder();
        sb.append(KU_API_BASE);
        sb.append("/dy_life_list?");
        sb.append("apikey=");
        sb.append(key);
        sb.append("&min_id=");
        sb.append(page);
        sb.append("&back=");
        sb.append(size);
        if(StringUtils.isNotBlank(cityCode)) {
            sb.append("&cityCode=");
            sb.append(cityCode);
        }
        if(StringUtils.isNotBlank(categoryId)) {
            sb.append("&category_id=");
            sb.append(categoryId);
        }
        if(sort!=null) {
            sb.append("&sort=");
            sb.append(sort);
        }
        if(StringUtils.isNotBlank(keyword)) {
            sb.append("&keyword=");
            sb.append(keyword);
        }
        if(longitude!=null) {
            sb.append("&longitude=");
            sb.append(longitude);
        }
        if(latitude!=null) {
            sb.append("&latitude=");
            sb.append(latitude);
        }
        ResponseEntity<JSONObject> re = restTemplate.getForEntity(
                sb.toString(),
                JSONObject.class);
        return re.getBody();
    }


    public JSONObject dyLifeWord(String id, String channel) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(key);
        sb.append("&id=");
        sb.append(id);
        sb.append("&channel=");
        sb.append(channel);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<JSONObject> re = restTemplate.postForEntity(
                KU_API_BASE+"/dy_life_share",
                requestParam,
                JSONObject.class);
        return re.getBody();
    }

    public DyLifeCityDto dyLifeCityList() {
        String data = HttpRequest
                .get(KU_API_BASE + "/dy_life_city")
                .execute().body();
        return JSON.parseObject(data, DyLifeCityDto.class);
    }

    public DyLifeCityDto dyLifeCategoryList() {
        String data = HttpRequest
                .get(KU_API_BASE + "/dy_life_category")
                .execute().body();
        return JSON.parseObject(data, DyLifeCityDto.class);
    }


    public DyKuResVo dyLifeOrder(QueryDyKuParam param) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(key);
        sb.append("&min_id=");
        sb.append(param.getPage());
        sb.append("&back=");
        sb.append(param.getSize());
        sb.append("&start_date=");
        sb.append(param.getStart().getTime()/1000);
        sb.append("&end_date=");
        sb.append(param.getEnd().getTime()/1000);
        sb.append("&media_type=");
        sb.append(param.getMediaType());
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                KU_API_BASE+"/dy_order_list",
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody(), DyKuResVo.class);
    }
}
