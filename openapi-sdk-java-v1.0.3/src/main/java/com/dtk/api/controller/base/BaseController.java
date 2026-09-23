package com.dtk.api.controller.base;

import com.dtk.api.client.DtkApiClient;
import com.dtk.api.client.DtkClient;
import com.dtk.api.constant.DtkApiConstant;
import com.dtk.api.exception.DtkResultEnum;
import com.dtk.api.request.mastertool.DtkGetOrderDetailsRequest;
import com.dtk.api.request.mastertool.DtkPddTransformLinkRequest;
import com.dtk.api.request.mastertool.ShearPlateDistinguishRequest;
import com.dtk.api.request.putstorage.DtkGoodsListRequest;
import com.dtk.api.request.special.DtkDdqGoodsListRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.mastertool.DtkGetOrderDetailsResponse;
import com.dtk.api.response.mastertool.DtkPddTransformLinkResponse;
import com.dtk.api.response.mastertool.ShearPlateDistinguishResponse;
import com.dtk.api.response.putstorage.DtkGoodsListItemResponse;
import com.dtk.api.response.special.DtkDdqGoodsListResponse;
import com.dtk.api.utils.Assert;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * dtk api: base controller
 *
 * @author baige
 * @date 2020/11/13 14:21
 */
@Component
public class BaseController {
    @Resource
    private HttpServletRequest request;
    private DtkApiClient instance;

    protected DtkClient getDtkClient() {
        String appKey = getClientHeader(DtkApiConstant.RequestCommonParam.APP_KEY);
        Assert.notBank(appKey, DtkResultEnum.APP_KEY_EMPTY);
        String appSecret = getClientHeader(DtkApiConstant.RequestCommonParam.APP_SECRET);
        Assert.notBank(appSecret, DtkResultEnum.APP_SECRET_EMPTY);
        if (instance == null) {
            synchronized (BaseController.class) {
                if (instance == null) {
                    this.instance = DtkApiClient.getInstance(appKey, appSecret);
                }
            }
        }

        return this.instance;
    }

    /**
     * 从header获取appKey和appSecret
     */
    private String getClientHeader(String headerKey) {
        return request.getHeader(headerKey);
    }

    protected String getRequestUrl() {
        String requestUrl = request.getRequestURI();
        return String.format("%s%s", DtkApiConstant.Domain.PROD, requestUrl);
    }

    public static void main(String[] args) {
        String appKey = "xxx";
        String appSecret = "xxx";
        DtkApiClient client = DtkApiClient.getInstance(appKey,appSecret);
        DtkGoodsListRequest request = new DtkGoodsListRequest();
        request.setVersion("v1.2.4");
        DtkApiResponse<DtkPageResponse<DtkGoodsListItemResponse>> execute = client.execute(request);
        System.out.println(execute);
    }
}
