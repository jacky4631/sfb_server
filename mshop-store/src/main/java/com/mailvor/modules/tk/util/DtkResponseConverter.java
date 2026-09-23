package com.mailvor.modules.tk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtk.api.response.base.DtkApiResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 大淘客SDK响应转换器
 * 将官方SDK的类型化响应转换为JSONObject，保持与旧代码兼容
 *
 * @author mailvor
 */
@Slf4j
public class DtkResponseConverter {

    /**
     * 将SDK响应对象转换为JSONObject
     * 通过FastJSON序列化再反序列化，确保结构一致
     *
     * @param response SDK响应对象
     * @return JSONObject格式响应
     */
    public static JSONObject toJsonObject(Object response) {
        if (response == null) {
            log.warn("转换空响应对象");
            return new JSONObject();
        }

        try {
            // 序列化为JSON字符串
            String jsonStr = JSON.toJSONString(response);
            
            // 反序列化为JSONObject
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            
            // 如果是DtkApiResponse类型，提取data字段
            if (response instanceof DtkApiResponse) {
                DtkApiResponse<?> apiResponse = (DtkApiResponse<?>) response;
                
                // 检查响应码
                Integer code = apiResponse.getCode();
                if (code != null && code != 0) {
                    log.warn("大淘客API返回错误码: {}, 消息: {}", code, apiResponse.getMsg());
                }
                
                // 如果data不为null，直接返回data内容
                if (apiResponse.getData() != null) {
                    Object data = apiResponse.getData();
                    if (data instanceof JSONObject) {
                        return (JSONObject) data;
                    } else {
                        // 将data对象转换为JSONObject
                        String dataJson = JSON.toJSONString(data);
                        return JSON.parseObject(dataJson);
                    }
                }
            }
            
            return jsonObject;
        } catch (Exception e) {
            log.error("转换SDK响应失败: {}", response, e);
            // 返回包含错误信息的JSONObject
            JSONObject errorObj = new JSONObject();
            errorObj.put("code", -1);
            errorObj.put("msg", "响应转换失败: " + e.getMessage());
            errorObj.put("data", null);
            return errorObj;
        }
    }

    /**
     * 将SDK响应转换为指定类型的JSONObject（保留外层结构）
     * 适用于需要保持完整响应结构的场景
     *
     * @param response SDK响应对象
     * @return 完整的JSONObject响应（包含code、msg、data）
     */
    public static JSONObject toFullJsonObject(Object response) {
        if (response == null) {
            log.warn("转换空响应对象");
            return new JSONObject();
        }

        try {
            String jsonStr = JSON.toJSONString(response);
            return JSON.parseObject(jsonStr);
        } catch (Exception e) {
            log.error("转换SDK完整响应失败: {}", response, e);
            JSONObject errorObj = new JSONObject();
            errorObj.put("code", -1);
            errorObj.put("msg", "响应转换失败: " + e.getMessage());
            return errorObj;
        }
    }
}
