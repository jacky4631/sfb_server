package com.mailvor.modules.push.service;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.push.dto.JPushDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2019/04/24 14:55
 * @description:
 */
@Slf4j
@Component
public class JPushService {

    @Value("${jpush.key}")
    private String key;
    @Value("${jpush.secret}")
    private String secret;

    @Resource
    private RestTemplate restTemplate;

    public JSONObject push(String content, Long uid) {
        try {
            HttpHeaders headers = new HttpHeaders();
            // 以表单的方式提交
            headers.setContentType(MediaType.APPLICATION_JSON);
            String sign = Base64Utils.encodeToString((key + ":" + secret).getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + sign);
            JPushDto jPushDto = JPushDto.build(content, uid);
            HttpEntity<JSONObject> requestParam = new HttpEntity(jPushDto,headers);
            ResponseEntity<JSONObject> re = restTemplate.postForEntity(
                    "https://api.jpush.cn/v3/push",
                    requestParam,
                    JSONObject.class);
            return re.getBody();
        }catch (Exception e) {
            log.error("极光推送识别 用户uid：{} 推送信息：{} 错误：{}", uid, content, e.getMessage());
        }
        return new JSONObject();
    }

    @Async
    public void push(String content, List<Long> uids) {
        for(Long uid : uids) {
            push(content, uid);
        }
    }
}
