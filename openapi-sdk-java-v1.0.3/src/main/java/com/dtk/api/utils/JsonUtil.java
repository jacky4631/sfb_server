package com.dtk.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * dtk api: JsonUtil
 *
 * @author 1
 * @date 2020/11/10 15:54
 */
@Slf4j
public class JsonUtil {
    /**
     * 定义jackson对象
     */
    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setDateFormat(format);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * 将对象转换成json字符串
     *
     * @param data data
     * @return String
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData     json数据
     * @param valueTypeRef 对象中的object类型
     * @return TypeReference实例化对象
     */
    public static <T> T jsonToPojoByTypeReference(String jsonData, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        return MAPPER.readValue(jsonData, valueTypeRef);
    }

    public static <T> T jsonToPojoByJavaType(String jsonData, JavaType valueType) {
        try {
            return MAPPER.readValue(jsonData, valueType);
        } catch (Exception e) {
            log.error("json对象--->" + jsonData + ",转换的对象--->" + valueType, e);
            log.error("json to obj error: {}", e.getMessage());
        }

        return null;
    }

    public static <T> T jsonToPojoByClass(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            log.error("json对象--->" + jsonData + ",转换的对象--->" + beanType, e);
            log.error("json to obj error: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData jsonData
     * @param beanType 数据元素类型
     * @return List<T>
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            System.out.println("jsonData" + jsonData);
            log.error("json to list error", e);
        }

        return null;
    }
}
