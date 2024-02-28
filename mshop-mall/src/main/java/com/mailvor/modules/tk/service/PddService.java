package com.mailvor.modules.tk.service;

import com.mailvor.modules.tk.config.PddConfig;
import com.mailvor.modules.tk.param.QueryPddParam;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkMemberAuthorityQueryRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkOrderListRangeGetRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkRpPromUrlGenerateRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkMemberAuthorityQueryResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkRpPromUrlGenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2019/04/24 14:55
 * @description:
 */
@Slf4j
@Component
public class PddService {
    @Resource
    private PddConfig pddConfig;

    private PopClient client;

    protected PopClient getClient() {
        if(client == null) {
            client = new PopHttpClient(pddConfig.getClientId(), pddConfig.getClientSecret());
        }
        return client;
    }

    /**
     * 查询拼多多订单
     *
     * @param param the param
     * @return the pdd ddk order list range get response
     */
    public PddDdkOrderListRangeGetResponse queryPddOrderList(QueryPddParam param) {
        try {
            PddDdkOrderListRangeGetRequest request = new PddDdkOrderListRangeGetRequest();
            if(param.getLastOrderId() != null) {
                request.setLastOrderId(param.getLastOrderId());
            }
            request.setEndTime(param.getEndTime());
            request.setPageSize(param.getPageSize());
            request.setStartTime(param.getStartTime());

            return getClient().syncInvoke(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询是否授权
     *
     * @param uid the uid
     * @return the int
     */
    public int authQuery(Long uid) {
        try {
            PddDdkMemberAuthorityQueryRequest request = new PddDdkMemberAuthorityQueryRequest();
            request.setCustomParameters(pddConfig.getParam(uid));
            request.setPid(pddConfig.getPid());
            PddDdkMemberAuthorityQueryResponse response = getClient().syncInvoke(request);
            return response.getAuthorityQueryResponse().getBind();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<PddDdkRpPromUrlGenerateResponse.RpPromotionUrlGenerateResponseUrlListItem> auth(Long uid) {
        try {
            PddDdkRpPromUrlGenerateRequest request = new PddDdkRpPromUrlGenerateRequest();
            request.setChannelType(10);
            request.setCustomParameters(pddConfig.getParam(uid));
            request.setPIdList(Collections.singletonList(pddConfig.getPid()));

            return getClient().syncInvoke(request).getRpPromotionUrlGenerateResponse().getUrlList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        String clientId = "64d7e6e0e3b94b52bad28e721b4e9c0f";
        String clientSecret = "0caeffa8e131687f1aac5af2821528ba99c92639";
          String pid = "1784892_252288888";
        PopClient client = new PopHttpClient(clientId, clientSecret);
//
//        PddDdkOrderListRangeGetRequest request = new PddDdkOrderListRangeGetRequest();
//        request.setEndTime("2022-06-23 23:00:00");
//        request.setPageSize(300);
//        request.setStartTime("2022-06-23 00:00:00");
//        PddDdkOrderListRangeGetResponse response = client.syncInvoke(request);
//        System.out.println(JsonUtil.transferToJson(response));

//        PopClient client = new PopHttpClient(clientId, clientSecret);
//
//        PddDdkMemberAuthorityQueryRequest request = new PddDdkMemberAuthorityQueryRequest();
//        request.setCustomParameters("{\"uid\":\""+2 + "\"}");
//        request.setPid(pid);
//        PddDdkMemberAuthorityQueryResponse response = client.syncInvoke(request);
//        System.out.println(JsonUtil.transferToJson(response));

        PddDdkRpPromUrlGenerateRequest request = new PddDdkRpPromUrlGenerateRequest();
        request.setChannelType(10);
        request.setCustomParameters("{\"uid\":\""+2 + "\"}");
        request.setPIdList(Collections.singletonList(pid));

        PddDdkRpPromUrlGenerateResponse response = client.syncInvoke(request);
        System.out.println(JsonUtil.transferToJson(response));
    }
}
