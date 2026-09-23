package com.dtk.api.client;

import com.dtk.api.utils.Assert;
import com.dtk.api.exception.DtkResultEnum;

/**
 * dtk api: dtkApiClient
 *
 * @author 1
 * @date 2020/11/9 16:20
 */
public class DtkApiClient extends AbstractDtkApiClient {
    private static volatile DtkApiClient dtkApiClient;

    private DtkApiClient() {
        super(null, null);
    }

    private DtkApiClient(String appKey, String appSecret) {
        super(appKey, appSecret);
    }

    public static DtkApiClient getInstance(ApiRequest apiRequest) {
        return getInstance(apiRequest.getAppKey(), apiRequest.getAppSecret());
    }

    public static DtkApiClient getInstance(String appKey, String appSecret) {
        Assert.notBank(appKey, DtkResultEnum.VERSION_NOT_EMPTY);
        Assert.notBank(appSecret, DtkResultEnum.APP_SECRET_EMPTY);
        if (dtkApiClient == null) {
            synchronized (DtkApiClient.class) {
                if (dtkApiClient == null) {
                    // 加锁
                    dtkApiClient = new DtkApiClient(appKey, appSecret);
                }
            }
        }
        return dtkApiClient;
    }
}
