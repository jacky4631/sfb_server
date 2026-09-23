package com.dtk.api.client;

import lombok.Data;

/**
 * 把所有参数定义在这里，直接传对象初始化client
 *
 * @author 1
 * @date 2020/11/10 11:41
 */
@Data
public class ApiRequest {
    private String appKey;
    private String appSecret;
}
