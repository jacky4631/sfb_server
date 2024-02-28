package com.mailvor.modules.ele;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.top.DefaultTopApiClient;
import com.taobao.top.TopApiClient;
import com.taobao.top.defaultability.Defaultability;
import com.taobao.top.defaultability.domain.AlibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest;
import com.taobao.top.defaultability.request.AlibabaAlscUnionElemePromotionOfficialactivityGetRequest;
import com.taobao.top.defaultability.response.AlibabaAlscUnionElemePromotionOfficialactivityGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 饿了么官方接口
 * @author:
 * @createTime: 2024/01/24 14:55
 * @description:
 */
@Slf4j
@Component
public class EleService {
    @Value(("${ele.api}"))
    private String api;
    @Value(("${ele.appKey}"))
    private String appKey;
    @Value(("${ele.appSecret}"))
    private String appSecret;



    public JSONObject contentParse(String activityId, String pid) throws IOException {

        // create Client
        TopApiClient client = new DefaultTopApiClient(appKey,appSecret,api);
        Defaultability apiPackage = new Defaultability(client);
        // create domain
        AlibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest alibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest = new AlibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest();
        alibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest.setPid(pid);
        alibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest.setActivityId(activityId);
        alibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest.setSid("1111");
        alibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest.setIncludeWxImg(true);
        alibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest.setIncludeQrCode(false);

        // create request
        AlibabaAlscUnionElemePromotionOfficialactivityGetRequest request = new AlibabaAlscUnionElemePromotionOfficialactivityGetRequest();
        request.setQueryRequest(alibabaAlscUnionElemePromotionOfficialactivityGetActivityRequest);

        AlibabaAlscUnionElemePromotionOfficialactivityGetResponse response = apiPackage.alibabaAlscUnionElemePromotionOfficialactivityGet(request);
        if(!response.isSuccess()){
            System.out.println(response.getSubMsg());
        }
        return JSON.parseObject(response.getBody());
    }


}
