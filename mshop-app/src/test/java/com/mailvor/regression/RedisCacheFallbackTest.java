package com.mailvor.regression;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.mailvor.modules.dataoke.rest.HaodankuController;
import com.mailvor.modules.tk.service.KuService;
import org.springframework.web.client.RestTemplate;
import com.mailvor.config.RedisConfig;
import com.mailvor.modules.dataoke.rest.DataokeController;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.impl.MwSystemConfigServiceImpl;
import com.mailvor.modules.shop.service.mapper.SystemConfigMapper;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.utils.RedisUtils;
import io.lettuce.core.ClientOptions;
import org.junit.Test;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static com.mailvor.modules.tk.constants.TkConstants.HOME_DATA_CATEGORY_TB;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RedisCacheFallbackTest {
    private final RedisUtils redis = mock(RedisUtils.class);
    private final DataokeService upstream = mock(DataokeService.class);

    private DataokeController categoryController() {
        DataokeController controller = new DataokeController();
        ReflectionTestUtils.setField(controller, "redisUtils", redis);
        ReflectionTestUtils.setField(controller, "service", upstream);
        return controller;
    }

    @Test
    public void categoryCacheHitDoesNotCallUpstream() {
        JSONObject cached = new JSONObject();
        when(redis.getCache(HOME_DATA_CATEGORY_TB)).thenReturn(cached);
        assertSame(cached, categoryController().getCategory());
        verifyNoInteractions(upstream);
    }

    @Test
    public void categoryCacheTimeoutUsesUpstreamWithoutRetryingRedis() {
        JSONObject response = new JSONObject();
        when(redis.getCache(HOME_DATA_CATEGORY_TB)).thenThrow(new QueryTimeoutException("timeout"));
        when(upstream.getCategory()).thenReturn(response);
        assertSame(response, categoryController().getCategory());
        verify(redis, never()).setCache(anyString(), any(), anyLong());
    }

    @Test
    public void categoryCacheMissKeepsExistingCacheLifetime() {
        JSONObject response = new JSONObject();
        when(upstream.getCategory()).thenReturn(response);
        assertSame(response, categoryController().getCategory());
        verify(redis).setCache(HOME_DATA_CATEGORY_TB, response, 24 * 3600);
    }

    @Test(expected = IllegalStateException.class)
    public void upstreamFailureIsNotConvertedToEmptySuccess() {
        when(redis.getCache(HOME_DATA_CATEGORY_TB)).thenThrow(new QueryTimeoutException("timeout"));
        when(upstream.getCategory()).thenThrow(new IllegalStateException("upstream unavailable"));
        categoryController().getCategory();
    }

    @Test
    public void configurationCacheTimeoutFallsBackToDatabase() {
        when(redis.getCache("commission")).thenThrow(new QueryTimeoutException("timeout"));
        SystemConfigMapper mapper = mock(SystemConfigMapper.class);
        MwSystemConfig record = new MwSystemConfig();
        record.setValue("configured-value");
        when(mapper.selectOne(any())).thenReturn(record);
        MwSystemConfigServiceImpl service = new MwSystemConfigServiceImpl(null, redis);
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        assertEquals("configured-value", service.getData("commission"));
    }

    @Test
    public void configurationCacheHitDoesNotCallDatabase() {
        when(redis.getCache("commission")).thenReturn("cached-value");
        SystemConfigMapper mapper = mock(SystemConfigMapper.class);
        MwSystemConfigServiceImpl service = new MwSystemConfigServiceImpl(null, redis);
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        assertEquals("cached-value", service.getData("commission"));
        verifyNoInteractions(mapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void stringLookupUsesSingleGetAndHandlesExpiredKey() {
        RedisTemplate<Object, Object> template = mock(RedisTemplate.class);
        ValueOperations<Object, Object> values = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(values);
        RedisUtils utils = new RedisUtils(template);
        assertEquals("", utils.getY("expired"));
        when(values.get("present")).thenReturn(123);
        assertEquals("123", utils.getY("present"));
        assertEquals("", utils.getY(null));
        verify(template, never()).hasKey(any());
        verify(values).get("expired");
        verify(values).get("present");
        verifyNoMoreInteractions(values);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void optionalCacheFailureSkipsRepeatedReadsAndWritesButKeepsStrictRedis() {
        RedisTemplate<Object, Object> template = mock(RedisTemplate.class);
        ValueOperations<Object, Object> values = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(values);
        when(values.get("first")).thenThrow(new QueryTimeoutException("timeout"));
        RedisUtils utils = new RedisUtils(template);
        assertNull(utils.getCache("first"));
        assertNull(utils.getCache("next"));
        assertFalse(utils.setCache("next", "value", 60));
        verify(values, never()).get("next");
        verify(values, never()).set(any(), any(), anyLong(), any());
        when(values.get("session")).thenReturn("token");
        assertEquals("token", utils.get("session"));
        ReflectionTestUtils.setField(utils, "optionalCacheRetryAtNanos", System.nanoTime() - 1);
        when(values.get("recovered")).thenReturn("value");
        assertEquals("value", utils.getCache("recovered"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void optionalCacheWriteFailureAlsoStartsBackoff() {
        RedisTemplate<Object, Object> template = mock(RedisTemplate.class);
        ValueOperations<Object, Object> values = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(values);
        doThrow(new QueryTimeoutException("timeout")).when(values)
                .set("key", "value", 60, java.util.concurrent.TimeUnit.SECONDS);
        RedisUtils utils = new RedisUtils(template);
        assertFalse(utils.setCache("key", "value", 60));
        assertNull(utils.getCache("next"));
        verify(values, never()).get("next");
    }

    @Test
    public void bannerAndTilesFallBackToUpstreamWhenOptionalCacheIsSkipped() {
        HaodankuController controller = new HaodankuController();
        RestTemplate rest = mock(RestTemplate.class);
        KuService ku = mock(KuService.class);
        ReflectionTestUtils.setField(controller, "redisUtils", redis);
        ReflectionTestUtils.setField(controller, "restTemplate", rest);
        ReflectionTestUtils.setField(controller, "kuService", ku);
        when(ku.getKuCid()).thenReturn("test-cid");
        JSONArray banners = new JSONArray();
        JSONArray tiles = new JSONArray();
        JSONObject payload = new JSONObject();
        payload.put("banners", banners);
        payload.put("tile_long", tiles);
        JSONObject response = new JSONObject();
        response.put("data", payload);
        when(rest.getForObject(anyString(), eq(JSONObject.class))).thenReturn(response);
        assertSame(banners, controller.banners());
        assertSame(tiles, controller.tiles());
    }

    @Test
    public void appConfigurationUsesDatabaseWhenOptionalCacheIsSkipped() {
        SystemConfigMapper mapper = mock(SystemConfigMapper.class);
        MwSystemConfig record = new MwSystemConfig();
        record.setValue("{}");
        when(mapper.selectOne(any())).thenReturn(record);
        MwSystemConfigServiceImpl service = new MwSystemConfigServiceImpl(null, redis);
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        assertNotNull(service.getAppDataConfig());
        verify(mapper).selectOne(any());
    }

    @Test
    public void disconnectedRedisRejectsCommandsAndReconnects() {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
        new RedisConfig().lettuceConnectionCustomizer().customize(builder);
        ClientOptions options = builder.build().getClientOptions().get();
        assertTrue(options.isAutoReconnect());
        assertTrue(options.getSocketOptions().isKeepAlive());
        assertEquals(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS, options.getDisconnectedBehavior());
    }
}
