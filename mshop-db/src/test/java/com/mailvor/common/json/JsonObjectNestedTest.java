package com.mailvor.common.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 锁定嵌套结构的可强转语义。
 * <p>
 * 线上 MeituanService.order() 会 {@code for (Object obj : dataList)} 然后
 * {@code (JsonObject) obj} 强转；Jackson 对无类型 JSON 只给 LinkedHashMap，
 * shim 必须自己把嵌套的 Map / List 归一化成 JsonObject / JsonArray，
 * 否则抛 {@code ClassCastException: LinkedHashMap cannot be cast to JsonObject}。
 */
class JsonObjectNestedTest {

    /** 形状照抄美团订单接口的返回：data.dataList[] 里是对象，对象里还嵌一层数组 */
    private static final String JSON = "{\"code\":0,\"data\":{\"code\":0,\"scrollId\":\"s-1\",\"dataList\":"
            + "[{\"sid\":\"a\",\"orderId\":11,\"nested\":{\"k\":\"v\"},\"tags\":[{\"t\":1}]},"
            + "{\"sid\":\"b\",\"orderId\":12}]},"
            + "\"list\":[{\"id\":1},{\"id\":2}]}";

    @Test
    @DisplayName("遍历 JsonArray 后强转 JsonObject 不报错（线上崩溃的那条路径）")
    void arrayElementsAreJsonObject() {
        JsonObject res = JsonUtil.parseObject(JSON);
        JsonObject data = res.getJSONObject("data");
        JsonArray dataList = data.getJSONArray("dataList");

        for (Object obj : dataList) {
            assertInstanceOf(JsonObject.class, obj, "数组元素必须是 JsonObject");
        }

        JsonObject first = (JsonObject) dataList.get(0);
        assertEquals("a", first.getString("sid"));
        assertEquals(11L, first.getLong("orderId"));

        // 再往里两层也要保持可强转
        assertInstanceOf(JsonObject.class, first.get("nested"));
        JsonObject nested = (JsonObject) first.get("nested");
        assertEquals("v", nested.getString("k"));
        assertInstanceOf(JsonArray.class, first.get("tags"));
        assertInstanceOf(JsonObject.class, ((JsonArray) first.get("tags")).get(0));
    }

    @Test
    @DisplayName("顶层数组同样归一化")
    void topLevelArray() {
        JsonArray list = JsonUtil.parseArray("[{\"id\":1},{\"id\":2}]");
        assertEquals(2, list.size());
        for (Object obj : list) {
            assertInstanceOf(JsonObject.class, obj);
        }
        assertEquals(1, list.getJSONObject(0).getIntValue("id"));
    }

    @Test
    @DisplayName("Jackson 直接填充 JsonObject（走不到构造器）时，读取仍能拿到 JsonObject")
    void jacksonPopulatedInstanceNormalizesOnRead() {
        JsonObject res = JsonUtil.parseObject(JSON, JsonObject.class);
        assertNotNull(res);
        // 元素是 Jackson 塞进来的 LinkedHashMap，靠 get() 的归一化兜住
        Object data = res.get("data");
        assertInstanceOf(JsonObject.class, data);
        List<?> rawList = (List<?>) res.get("list");
        assertInstanceOf(JsonObject.class, rawList.get(0));
    }

    @Test
    @DisplayName("parse() 未知结构入口同样归一化")
    void parseEntryNormalizes() {
        Object parsed = JsonUtil.parse(JSON);
        assertInstanceOf(JsonObject.class, parsed);
        JsonObject res = (JsonObject) parsed;
        assertInstanceOf(JsonObject.class, res.getJSONArray("list").get(0));
    }

    @Test
    @DisplayName("toJSON 转换 POJO 后嵌套结构可强转")
    void toJSONNormalizesNested() {
        JsonObject res = JsonUtil.toJSON(JsonUtil.parseObject(JSON));
        assertInstanceOf(JsonObject.class, res.getJSONObject("data").getJSONArray("dataList").get(0));
    }
}
