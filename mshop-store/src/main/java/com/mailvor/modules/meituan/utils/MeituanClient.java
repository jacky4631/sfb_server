package com.mailvor.modules.meituan.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.meituan.config.MeituanConfig;
import com.mailvor.modules.meituan.param.MeituanBaseParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/** 使用 TSQ 的美团网关签名规则，避免依赖旧 SDK 及其运行时依赖。 */
@Component
public class MeituanClient {
    private static final String HOST = "https://media.meituan.com";

    @Resource
    private MeituanConfig meituanConfig;
    @Resource
    private RestTemplate restTemplate;

    public JSONObject request(MeituanBaseParam param, String path) {
        if (StringUtils.isBlank(meituanConfig.getAppKey())
                || StringUtils.isBlank(meituanConfig.getAppSecret())) {
            throw new IllegalStateException("请先配置美团 appKey 和 appSecret");
        }
        byte[] body = JSON.toJSONString(param).getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json; charset=UTF-8");
        headers.set("Media-Request-Id", String.valueOf(System.currentTimeMillis()));
        headers.set("S-Ca-App", meituanConfig.getAppKey());
        headers.set("S-Ca-Timestamp", String.valueOf(System.currentTimeMillis()));
        try {
            String digest = Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(body));
            headers.set("Content-MD5", digest);
            // TSQ: POST + 换行 + Body MD5 + 换行 + 排序后的签名 Header + Path。
            String toSign = "POST\n" + digest + "\n"
                    + "S-Ca-App:" + headers.getFirst("S-Ca-App") + "\n"
                    + "S-Ca-Timestamp:" + headers.getFirst("S-Ca-Timestamp") + "\n" + path;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(meituanConfig.getAppSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            headers.set("S-Ca-Signature-Headers", "S-Ca-App,S-Ca-Timestamp");
            headers.set("S-Ca-Signature", Base64.getEncoder().encodeToString(mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8))));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("美团请求签名失败", e);
        }
        String response = restTemplate.postForObject(HOST + path, new HttpEntity<>(body, headers), String.class);
        JSONObject result = JSON.parseObject(response);
        if (result == null) {
            throw new IllegalStateException("美团接口返回为空");
        }
        return result;
    }
}
