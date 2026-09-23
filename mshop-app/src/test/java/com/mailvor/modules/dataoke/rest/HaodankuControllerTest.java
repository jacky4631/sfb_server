package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.mailvor.modules.tk.constants.TkConstants.HOME_DATA_BANNER;
import static com.mailvor.modules.tk.constants.TkConstants.HOME_DATA_TILES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HaodankuControllerTest {

    @Test
    void bannersShouldReturnEmptyArrayWhenUpstreamDataIsNotObject() {
        HaodankuController controller = new HaodankuController();
        RedisUtils redisUtils = mock(RedisUtils.class);
        KuService kuService = mock(KuService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        JSONObject upstream = new JSONObject();
        upstream.put("code", 400);
        upstream.put("msg", "51002-商城参数错误");
        upstream.put("data", new JSONArray());

        when(redisUtils.get(HOME_DATA_BANNER)).thenReturn(null);
        when(kuService.getKuCid()).thenReturn("invalid-cid");
        when(restTemplate.getForObject(anyString(), eq(JSONObject.class))).thenReturn(upstream);

        ReflectionTestUtils.setField(controller, "redisUtils", redisUtils);
        ReflectionTestUtils.setField(controller, "kuService", kuService);
        ReflectionTestUtils.setField(controller, "restTemplate", restTemplate);

        JSONArray result = controller.banners();

        assertTrue(result.isEmpty());
        verify(redisUtils, never()).set(eq(HOME_DATA_BANNER), eq(result), eq(86400L));
    }

    @Test
    void tilesShouldSupportCachedListDataWithoutClassCastException() {
        HaodankuController controller = new HaodankuController();
        RedisUtils redisUtils = mock(RedisUtils.class);

        when(redisUtils.get(HOME_DATA_TILES)).thenReturn(Collections.singletonList(new JSONObject()));

        ReflectionTestUtils.setField(controller, "redisUtils", redisUtils);

        JSONArray result = controller.tiles();

        assertEquals(1, result.size());
    }
}

