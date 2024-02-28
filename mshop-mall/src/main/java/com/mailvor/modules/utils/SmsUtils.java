/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.utils;


import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static com.mailvor.modules.tools.config.AliSmsConfig.*;

/**
 * @ClassName 阿里云短信
 * @author huangyu
 * @Date 2020/6/26
 **/
@Slf4j
@Component
@Configuration(proxyBeanMethods = false)
public class SmsUtils {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }
    /**
     * 发送短信
     * @param phoneNumbers 手机号
     * @param templateParam 短信模板变量对应的实际值，JSON格式
     */
    public void sendSmsNow(String phoneNumbers, String templateParam) throws Exception {
        String accessKeyId = SMS_ACCESS_KEY;
        String accessKeySecret = SMS_ACCESS_SECRET;
        String sign = SMS_SIGN;
        String templateId = SMS_TEMPLATE_ID;
        Client client = SmsUtils.createClient(accessKeyId, accessKeySecret);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumbers)
                .setSignName(sign)
                .setTemplateCode(templateId)
                .setTemplateParam(templateParam);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            log.info("手机 {} 发送验证码 返回{}", phoneNumbers, JSON.toJSONString(response.getBody()));
        } catch (TeaException error) {
            // 如有需要，请打印 error
            log.info("手机 {} 发送验证码 返回错误1{}", phoneNumbers, JSON.toJSONString(error));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.info("手机 {} 发送验证码 返回错误2{}", phoneNumbers, JSON.toJSONString(_error));
        }

    }
}
