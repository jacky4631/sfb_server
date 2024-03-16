package com.mailvor.modules.tk.service;


import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.tk.param.GoodsListDyParam;
import com.mailvor.modules.tk.param.ParseContentParam;
import com.mailvor.modules.tk.param.QueryDyKuParam;
import com.mailvor.modules.tk.param.QueryEleKuParam;
import com.mailvor.modules.tk.service.dto.DyLifeCityDto;
import com.mailvor.modules.tk.util.HttpUtils;
import com.mailvor.modules.tk.util.SignMD5Util;
import com.mailvor.modules.tk.vo.DyKuResVo;
import com.mailvor.modules.tk.vo.EleKuResVo;
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
    public static final String ELE_ACTIVITY_ORDER_API = KU_API_BASE + "/elm_order_list";

    public static final String MEITUAN_ORDER_LIST_API = KU_API_BASE + "/meituan_order_list";

    /**
     *  抖音本地生活分类
     *  参照这个链接做了精简 KU_API_BASE + "/dy_life_category
     *  因为有些分类下没有商品
     * */
    private static String DY_LIFE_CATE_STR = "{\"code\":200,\"msg\":\"success\",\"data\":{\"list\":[{\"title\":\"餐饮美食\",\"val\":1000000,\"children\":[{\"title\":\"饮品\",\"val\":1015000},{\"title\":\"地方菜\",\"val\":1001000},{\"title\":\"火锅\",\"val\":1003000},{\"title\":\"烧烤\",\"val\":1004000},{\"title\":\"烤肉\",\"val\":1005000},{\"title\":\"自助餐\",\"val\":1009000},{\"title\":\"东南亚菜\",\"val\":1010000},{\"title\":\"日韩料理\",\"val\":1011000},{\"title\":\"西餐\",\"val\":1012000},{\"title\":\"其他美食\",\"val\":1013000},{\"title\":\"面包甜点\",\"val\":1014000},{\"title\":\"音乐餐厅\",\"val\":1016000},{\"title\":\"快餐小吃\",\"val\":1017000},{\"title\":\"热门特色菜\",\"val\":1018000},{\"title\":\"生鲜果蔬\",\"val\":5004000}]},{\"title\":\"休闲娱乐\",\"val\":4000000,\"children\":[{\"title\":\"洗浴按摩\",\"val\":4001000},{\"title\":\"酒吧\",\"val\":4003000},{\"title\":\"DIY手工坊\",\"val\":4009000},{\"title\":\"其他休闲娱乐\",\"val\":4013000},{\"title\":\"电影演出\",\"val\":4014000},{\"title\":\"休闲运动\",\"val\":4015000},{\"title\":\"新奇体验\",\"val\":4017000},{\"title\":\"推理桌游\",\"val\":4019000},{\"title\":\"传统娱乐\",\"val\":4020000},{\"title\":\"户外玩乐\",\"val\":4021000},{\"title\":\"图书馆\",\"val\":4022000},{\"title\":\"茶馆\",\"val\":4024000},{\"title\":\"演出\",\"val\":4025000}]},{\"title\":\"商城购物\",\"val\":5000000,\"children\":[{\"title\":\"花鸟鱼虫市场\",\"val\":5001000},{\"title\":\"综合商场\",\"val\":5002000},{\"title\":\"日用商超\",\"val\":5003000},{\"title\":\"眼镜店\",\"val\":5005000},{\"title\":\"免税店\",\"val\":5006000},{\"title\":\"商业街步行街\",\"val\":5007000},{\"title\":\"家居建材\",\"val\":5008000},{\"title\":\"配饰\",\"val\":5009000},{\"title\":\"服饰鞋帽\",\"val\":5010000},{\"title\":\"数码家电\",\"val\":5011000},{\"title\":\"美妆个护\",\"val\":5012000},{\"title\":\"交通工具\",\"val\":5013000},{\"title\":\"运动户外\",\"val\":5014000}]},{\"title\":\"亲子乐园\",\"val\":21000000,\"children\":[{\"title\":\"儿童乐园\",\"val\":4016000},{\"title\":\"儿童才艺\",\"val\":7006000},{\"title\":\"STEAM\",\"val\":7007000},{\"title\":\"婴幼服务\",\"val\":21001000},{\"title\":\"儿童运动\",\"val\":21002000},{\"title\":\"亲子活动\",\"val\":21003000},{\"title\":\"孕婴童摄影\",\"val\":21004000},{\"title\":\"早教\",\"val\":21005000},{\"title\":\"母婴购物\",\"val\":21006000}]},{\"title\":\"生活服务\",\"val\":6000000,\"children\":[{\"title\":\"印刷摄影\",\"val\":6001000},{\"title\":\"房产家政\",\"val\":6003000},{\"title\":\"维修服务\",\"val\":6004000},{\"title\":\"洗涤护理\",\"val\":6005000},{\"title\":\"旅行社\",\"val\":6006000},{\"title\":\"孕产服务\",\"val\":6007000},{\"title\":\"搬家运输\",\"val\":6009000},{\"title\":\"法律财会\",\"val\":6010000},{\"title\":\"房产服务\",\"val\":6020000},{\"title\":\"养老服务\",\"val\":6021000},{\"title\":\"汽车服务\",\"val\":19005000}]},{\"title\":\"运动健身\",\"val\":3000000,\"children\":[{\"title\":\"游泳馆\",\"val\":3001000},{\"title\":\"综合体育馆\",\"val\":3002000},{\"title\":\"瑜伽\",\"val\":3003000},{\"title\":\"舞蹈\",\"val\":3004000},{\"title\":\"健身房\",\"val\":3005000},{\"title\":\"武术搏击\",\"val\":3006000},{\"title\":\"球类运动\",\"val\":3007000},{\"title\":\"其他运动健身\",\"val\":3008000}]},{\"title\":\"丽人美发\",\"val\":17000000,\"children\":[{\"title\":\"美发\",\"val\":17001000},{\"title\":\"美甲美睫\",\"val\":17002000},{\"title\":\"美容美体\",\"val\":17003000},{\"title\":\"其他丽人\",\"val\":17005000},{\"title\":\"医疗美容\",\"val\":17006000},{\"title\":\"纹眉纹绣\",\"val\":17007000}]},{\"title\":\"礼仪婚庆\",\"val\":22000000,\"children\":[{\"title\":\"结婚旅拍\",\"val\":22001000},{\"title\":\"婚宴\",\"val\":22002000},{\"title\":\"婚庆策划\",\"val\":22003000},{\"title\":\"婚纱礼服\",\"val\":22004000},{\"title\":\"彩妆造型\",\"val\":22005000},{\"title\":\"婚车租赁\",\"val\":22006000},{\"title\":\"婚礼跟拍\",\"val\":22007000},{\"title\":\"司仪主持\",\"val\":22008000},{\"title\":\"其他婚礼服务\",\"val\":22009000},{\"title\":\"婚纱摄影\",\"val\":22010000},{\"title\":\"婚礼喜品\",\"val\":22011000},{\"title\":\"珠宝首饰\",\"val\":22012000}]}]}}";

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


    public EleKuResVo eleOrder(QueryEleKuParam param) {

        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(waimaiKey);
        sb.append("&min_id=");
        sb.append(param.getPage());
        sb.append("&back=");
        sb.append(param.getSize());
        sb.append("&start_date=");
        sb.append(param.getStart());
        sb.append("&end_date=");
        sb.append(param.getEnd());
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<String> re = restTemplate.postForEntity(
                ELE_ACTIVITY_ORDER_API,
                requestParam,
                String.class);
        return JSON.parseObject(re.getBody(), EleKuResVo.class);
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
            sb.append("&city_code=");
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
        return JSON.parseObject(DY_LIFE_CATE_STR, DyLifeCityDto.class);
//        String data = HttpRequest
//                .get(KU_API_BASE + "/dy_life_category")
//                .execute().body();
//        return JSON.parseObject(data, DyLifeCityDto.class);
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

    public JSONObject dyProductList(GoodsListDyParam param) {
        StringBuilder sb = new StringBuilder();
        sb.append(KU_API_BASE);
        sb.append("/dy_item_list?");
        sb.append("apikey=");
        sb.append(key);
        sb.append("&min_id=");
        sb.append(param.getPageId());
        sb.append("&back=");
        sb.append(param.getPageSize());
        if(StringUtils.isNotBlank(param.getKeyword())) {
            sb.append("&keyword=");
            sb.append(param.getKeyword());
        }

        if(param.getSort() != null) {
            sb.append("&sort=");
            sb.append(param.getSort());
        }
        if(param.getCateId() != null) {
            sb.append("&cate_id=");
            sb.append(param.getCateId());
        }

        if(param.getPriceMin() != null) {
            sb.append("&price_min=");
            sb.append(param.getPriceMin());
        }
        if(param.getPriceMax() != null) {
            sb.append("&price_max=");
            sb.append(param.getPriceMax());
        }
        if(param.getSalesMin() != null) {
            sb.append("&sales_min=");
            sb.append(param.getSalesMin());
        }
        if(param.getSalesMax() != null) {
            sb.append("&sales_max=");
            sb.append(param.getSalesMax());
        }
        ResponseEntity<JSONObject> re = restTemplate.getForEntity(
                sb.toString(),
                JSONObject.class);
        return re.getBody();
    }

    public JSONObject dyProductDetail(String itemId) {
        StringBuilder sb = new StringBuilder();
        sb.append(KU_API_BASE);
        sb.append("/dy_detail?");
        sb.append("apikey=");
        sb.append(key);
        sb.append("&itemid=");
        sb.append(itemId);
        ResponseEntity<JSONObject> re = restTemplate.getForEntity(
                sb.toString(),
                JSONObject.class);
        return re.getBody();
    }

    public JSONObject dyProductWord(String itemId, String channel) {
        StringBuilder sb = new StringBuilder();
        sb.append("apikey=");
        sb.append(key);
        sb.append("&itemid=");
        sb.append(itemId);
        sb.append("&channel=");
        sb.append(channel);
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestParam = new HttpEntity<>(sb.toString(),headers);
        ResponseEntity<JSONObject> re = restTemplate.postForEntity(
                KU_API_BASE+"/get_dyitem_link",
                requestParam,
                JSONObject.class);
        return re.getBody();
    }
    public JSONObject dyProductCateList() {
        return getCateList(4, null);
    }


    public JSONObject getCateList(Integer type, Integer labelId) {
        StringBuilder sb = new StringBuilder();
        sb.append(KU_API_BASE);
        sb.append("/category_list?");
        sb.append("apikey=");
        sb.append(key);
        sb.append("&type=");
        sb.append(type);
        if(labelId != null) {
            sb.append("&label_id=");
            sb.append(labelId);
        }

        ResponseEntity<JSONObject> re = restTemplate.getForEntity(
                sb.toString(),
                JSONObject.class);
        return re.getBody();
    }
}
