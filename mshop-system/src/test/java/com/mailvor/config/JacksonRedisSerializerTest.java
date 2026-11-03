package com.mailvor.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.mailvor.common.json.JsonObject;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 覆盖线上存量缓存的真实形态（从生产 Redis 取样得到），
 * 确保新序列化器要么读回可用对象，要么干净地判为未命中，
 * 不能把 LinkedHashMap 交出去让业务层 ClassCastException。
 */
class JacksonRedisSerializerTest {

    private final JacksonRedisSerializer<Object> serializer = new JacksonRedisSerializer<>(Object.class);

    private Object read(String json) {
        return serializer.deserialize(json.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("新写入的值带 @type，能原样读回具体类型")
    void roundTripTypedValue() {
        JsonObject cached = new JsonObject();
        cached.put("name", "楼层A");
        byte[] bytes = serializer.serialize(cached);
        assertInstanceOf(JsonObject.class, serializer.deserialize(bytes));
    }

    @Test
    @DisplayName("存量 @type=java.util.LinkedHashMap 读回应是 JsonObject（业务有 (JsonObject) 强转）")
    void legacyLinkedHashMapBecomesJsonObject() {
        Object value = read("{\"@type\":\"java.util.LinkedHashMap\",\"id\":1,\"title\":\"实时榜\"}");
        assertInstanceOf(JsonObject.class, value);
        assertEquals("实时榜", ((JsonObject) value).getString("title"));
    }

    @Test
    @DisplayName("存量裸标量（online-token 存的是 Long）不能报错")
    void legacyBareScalar() {
        assertEquals(19984840025L, read("19984840025"));
    }

    @Test
    @DisplayName("存量 @type 指向已不存在的类时判为未命中，而不是返回 Map")
    void unresolvableTypeHintIsCacheMiss() {
        assertThrows(org.springframework.data.redis.serializer.SerializationException.class,
                () -> read("{\"@type\":\"com.alibaba.fastjson.JSONObject\",\"code\":1}"));
    }

    @Test
    @DisplayName("fastjson 专有编码（数字带 D 后缀）不是合法 JSON，判为未命中")
    void fastjsonNumericLiteralIsCacheMiss() {
        assertThrows(org.springframework.data.redis.serializer.SerializationException.class,
                () -> read("[{\"@type\":\"java.util.LinkedHashMap\",\"actualPrice\":29.9D}]"));
    }
}
