package com.dtk.api.client;

import com.dtk.api.constant.DtkApiConstant;
import com.dtk.api.exception.DtkApiException;
import com.dtk.api.exception.DtkResultEnum;
import com.dtk.api.http.WebUtils;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.utils.Assert;
import com.dtk.api.utils.JsonUtil;
import com.dtk.api.utils.RequiredCheck;
import com.dtk.api.utils.SignMd5Util;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * dtk api: AbstractDtkApiClient
 *
 * @author 1
 * @date 2020/11/10 11:37
 */
@Slf4j
public abstract class AbstractDtkApiClient implements DtkClient {
    private final String appKey;
    private final String appSecret;

    private static final String BASE_URL = "https://openapi.dataoke.com/api";

    public AbstractDtkApiClient(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(DtkApiRequest<T> request) {
        String requestUrl = request.requestUrl();
        T tRsp;
        Map<String, String> requestHolderWithSign = null;
        String resultJson = null;
        try {
            // 这是一个已经设置好所有参数的key-value集
            requestHolderWithSign = getRequestHolderWithSign(request);
            Assert.notBank(requestUrl, DtkResultEnum.URL_NOT_EMPTY);
            Assert.notBank(request.apiVersion(), DtkResultEnum.VERSION_NOT_EMPTY);
            // http请求
            resultJson = WebUtils.doGet(BASE_URL + requestUrl, requestHolderWithSign);
            Assert.notBank(resultJson, DtkResultEnum.UNKNOWN_ERROR);

            TypeReference<T> responseType = request.responseType();
            tRsp = JsonUtil.jsonToPojoByTypeReference(resultJson, responseType);
        } catch (Exception ex) {
            log.error("dtk_sdk处理异常：请求地址：{}，请求参数：{}", requestUrl,
                    Optional.ofNullable(requestHolderWithSign).orElse(Collections.emptyMap()).toString());
            log.error("dtk_sdk处理异常：响应结果：{}", resultJson);
            log.error("dtk_sdk处理异常：异常信息：", ex);
            if (ex instanceof DtkApiException) {
                DtkApiException exception = (DtkApiException) ex;
                return (T) DtkApiResponse.buildFail(exception.getDtkResultEnum());
            } else {
                return (T) DtkApiResponse.buildFail(DtkResultEnum.UNKNOWN_ERROR);
            }
        }

        return tRsp;
    }

    /**
     * 组装接口参数，处理加密、签名逻辑
     *
     * @param request 请求参数对象
     * @return 组装完成的完整参数
     */
    @SneakyThrows
    private Map<String, String> getRequestHolderWithSign(DtkApiRequest<?> request)
            throws IllegalAccessException {
        //校验必填参数
        Field[] fields = request.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(RequiredCheck.class)) {
                if (StringUtils.isEmpty(field.get(request))) {
                    throw new DtkApiException("请求参数必填项缺失！");
                }
            }
        }
        Map<String, String> textParams = request.getTextParams();
        textParams.put(DtkApiConstant.RequestCommonParam.APP_KEY, appKey);
        // 生成签名
        String urlParams =
                textParams.entrySet().stream().filter(x -> !StringUtils.isEmpty(x.getValue())).map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(
                        "&"));
        String serverSign = SignMd5Util.sign(urlParams, appSecret);
        textParams.put(DtkApiConstant.RequestCommonParam.SIGN, serverSign);
        return textParams;
    }

    /**
     * 把一个字符串的第一个字母大写
     */
    private static String getMethodName(String fieldName) throws Exception {
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
