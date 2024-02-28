package com.mailvor.modules.meituan;


import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.meituan.config.MeituanConfig;
import com.mailvor.modules.meituan.utils.MeituanUtil;
import com.mailvor.modules.tk.vo.MtResVo;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.mailvor.modules.meituan.constants.MeituanConstants.*;

/**
 * 美团官方接口
 * @author:
 * @createTime: 2024/01/24 14:55
 * @description:
 */
@Slf4j
@Component
public class MeituanService {

    @Resource
    private MeituanConfig meituanConfig;
    @Resource
    private RestTemplate restTemplate;

    public JSONObject getProvinces(String platformId){
        Map<String, Object> params = initPublicParams();
        if(StringUtils.isNotBlank(platformId)) {
            params.put("platformId", platformId);
        }
//        String sign = AesUtil.generateSignature(params, appKey);
//        params.put("sign", sign);
        ResponseEntity<JSONObject> response = restTemplate
                .getForEntity(MeituanUtil.concatUrl(MT_URL_PROVINCE_ALL, params), JSONObject.class);
        return response.getBody().getJSONObject("msg");
    }


    public JSONObject getCities(String platformId, String provinceId){
        Map<String, Object> params = initPublicParams();
        if(StringUtils.isNotBlank(platformId)) {
            params.put("platformId", platformId);
        }
        ResponseEntity<JSONObject> response = restTemplate
                .getForEntity(MeituanUtil.concatUrl(String.format(MT_URL_CITY_ALL, provinceId), params), JSONObject.class);
        return response.getBody().getJSONObject("msg");
    }
    public JSONObject getCategories(String platformId, String cityId, String cat0Id){
        Map<String, Object> params = initPublicParams();
        if(StringUtils.isNotBlank(platformId)) {
            params.put("platformId", platformId);
        }
        if(StringUtils.isNotBlank(cat0Id)) {
            params.put("cat0Id", cat0Id);
        }
        ResponseEntity<JSONObject> response = restTemplate
                .getForEntity(MeituanUtil.concatUrl(String.format(MT_URL_CATEGORY_ALL, cityId), params), JSONObject.class);
        return response.getBody().getJSONObject("msg");
    }

    public JSONObject goodsList(JSONObject body){
        body.put("utmSource", meituanConfig.getUtmSource());
        body.put("utmMedium", MeituanUtil.encryptHex("uid0", meituanConfig.getAppKey()));
        Map<String, Object> params = initPublicParams();
        ResponseEntity<JSONObject> response = restTemplate
                .postForEntity(MeituanUtil.concatUrl(MT_URL_GOODS_LIST, params), body, JSONObject.class);
        return response.getBody().getJSONObject("msg");
    }

    /**
     * Order cps json object.
     *
     * @param body
     * queryType	Integer	否	订单查询时间区间类型，详见下方QueryTypeEnum
     * 1	按照子订单支付时间查询
     * 2	按照子订单核验时间查询（注: 等同于startVerifyDate和endVerifyDate查询，切勿混用）
     * 3	按照子订单结算时间查询
     * 4	按照子订单账期时间查询
     * startTime	String	否	订单查询开始时间（注: 配置queryType使用才会生效）
     * 日期：2019-05-08 时间：2019-05-08 00:00:00
     * endTime	String	否	订单查询结束时间（注: 配置queryType使用才会生效）
     * page	Integer	是	页数，从1开始
     * size	Integer	是	每页数据
     *
     * @return the json object
     * {
     *     "status": 200,
     *     "success": true,
     *     "msg": "操作成功",
     *     "total": null,
     *     "totalPage": null,
     *     "data": {
     *         "recordCount": 16,
     *         "page": 2,
     *         "pageSize": 1,
     *         "records": [
     *             {
     *                 "verifyDate": "2023-07-24",
     *                 "verifyTime": "2023-07-24 16:18:30",
     *                 "itemId": 1683387868217012319,
     *                 "uniqueItemId": 1683387868217012319,
     *                 "orderPayTime": "2023-07-24 16:05:17",
     *                 "orderId": "1100647520865043992",
     *                 "actualItemAmount": "21.2",
     *                 "actualOrderAmount": "21.2",
     *                 "shopUuid": "1110404959672129",
     *                 "shopName": "食客蛋炒饭",
     *                 "brandName": "",
     *                 "cityName": "未知",
     *                 "cat0Name": "未知",
     *                 "cat1Name": "未知",
     *                 "orderType": "外卖",
     *                 "couponId": "PvyPPPPsPsPyAOk&source=tsid=vasPtsvyP",
     *                 "couponGroupId": 0,
     *                 "couponDiscountAmount": "3",
     *                 "couponPriceLimit": "15",
     *                 "balanceAmount": "0.64",
     *                 "balanceCommissionRatio": "0.03",
     *                 "orderUserId": "29*****77",
     *                 "itemStatus": 1,
     *                 "balanceStatus": 1,
     *                 "settlementType": "未知",
     *                 "couponSource": "cube",
     *                 "orderPlatform": "美团",
     *                 "utmSource": 124866,
     *                 "utmMedium": "CF8F570C06C93519E80A3FC0B781F96E",
     *                 "modifyTime": "2023-08-01 02:32:15",
     *                 "itemBizStatus": 3,
     *                 "settleTime": "2023-08-01 02:32:15",
     *                 "billingDate": "2023-07-24 00:00:00",
     *                 "promotionId": "261024",
     *                 "dealId": 0,
     *                 "launchTag": 0
     *             }
     *         ],
     *         "positionIndex": "KAL6e0XiHCUHZIaYsXh31w" //如果最后没有订单显示end
     *     },
     *     "time": "2024-02-04 11:41:29"
     * }
     */
    public MtResVo order(String url, JSONObject body){
        Map<String, Object> params = initPublicParams();
        params.put("signMethod", "hmac");
        params.put("version", "2.0");
        params.remove("accessToken");

        Map<String, Object> signMap = new HashMap<>();
        signMap.putAll(params);
        signMap.putAll(body);
        String sign = MeituanUtil.generateSignature(signMap, meituanConfig.getAppKey());
        params.put("sign", sign);

//        ResponseEntity<MtResVo> response = restTemplate
//                .postForEntity(MeituanUtil.concatUrl(url, params), body, MtResVo.class);
//        return response.getBody();
        ResponseEntity<String> response = restTemplate
                .postForEntity(MeituanUtil.concatUrl(url, params), body, String.class);
        return JSON.parseObject(response.getBody(), MtResVo.class);
    }
    public MtResVo orderCPS(JSONObject body){
        return order(MT_URL_ORDER_CPS, body);
    }
    public MtResVo orderCPA(JSONObject body){
        return order(MT_URL_ORDER_CPA, body);
    }
    public MtResVo orderRefund(JSONObject body){
        return order(MT_URL_ORDER_REFUND, body);
    }
    protected Map<String, Object> initPublicParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("requestId", IdUtil.objectId());
        params.put("utmSource", meituanConfig.getUtmSource());
        params.put("version", "1.0");
        long times = System.currentTimeMillis()/1000;
        params.put("accessToken", MeituanUtil.encryptHex(meituanConfig.getUtmSource() + times, meituanConfig.getAppKey()));
        params.put("timestamp", times);
        return params;
    }
    public JSONObject getCode(String activityId, Long uid){
        return getCode(activityId, uid, null);
    }


    public JSONObject getCode(String activityId, Long uid, Integer pageLevel){
        JSONObject body = new JSONObject();
        body.put("utmSource", meituanConfig.getUtmSource());
        body.put("activity", activityId);
        body.put("utmMedium", MeituanUtil.encryptHex(MT_MEDIUM_PREFIX + uid, meituanConfig.getAppKey()));
        body.put("promotionId", meituanConfig.getPromotionId());
        if(pageLevel == null) {
            pageLevel = 1;
        }
        body.put("pageLevel", pageLevel);
        Map<String, Object> params = initPublicParams();
        ResponseEntity<JSONObject> response = restTemplate
                .postForEntity(MeituanUtil.concatUrl(MT_URL_ACTIVITY_CDOE, params), body, JSONObject.class);
        return response.getBody().getJSONObject("data");
    }
}
