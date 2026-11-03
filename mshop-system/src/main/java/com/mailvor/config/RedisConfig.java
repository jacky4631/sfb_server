/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer;
import com.mailvor.common.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.core.JacksonException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zheng Jie
 * @date 2018-11-24
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(DataRedisProperties.class)
public class RedisConfig implements CachingConfigurer {

    /**
     * keepAlive 必须用细粒度重载：keepAlive(true) 只打开 SO_KEEPALIVE 开关，探测间隔取操作系统
     * 默认值（7200s），远大于公网链路上 NAT 的空闲会话老化时间（实测约 250s）。连接会在空闲期被
     * 中间设备静默丢弃且不回 RST，形成半开黑洞，下一个业务请求要等 TCP 重传耗尽（实测 18s）才失败。
     * 探测参数 60s/15s/3 次可持续刷新 NAT 会话表项，实测空闲 300s 后连接依然健康。
     * timeoutOptions 必须显式开启：下面整体替换 ClientOptions 会覆盖 spring-data-redis 构造时默认
     * 注入的 TimeoutOptions.enabled()，不显式设置将回落到 timeoutCommands=false，
     * 导致 Lettuce 命令过期机制 CommandExpiryWriter 不生效（键过期订阅等异步路径将失去超时保护）。
     */
    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceConnectionCustomizer() {
        return builder -> builder.clientOptions(ClientOptions.builder()
                .autoReconnect(true)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .socketOptions(SocketOptions.builder()
                        .keepAlive(SocketOptions.KeepAliveOptions.builder()
                                .enable()
                                .idle(Duration.ofSeconds(60))
                                .interval(Duration.ofSeconds(15))
                                .count(3)
                                .build())
                        .build())
                .timeoutOptions(TimeoutOptions.enabled())
                .build());
    }

    /**
     *  设置 redis 数据默认过期时间，默认2小时
     *  设置@cacheable 序列化方式
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        JacksonRedisSerializer<Object> jacksonRedisSerializer = new JacksonRedisSerializer<>(Object.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jacksonRedisSerializer)).entryTtl(Duration.ofHours(2));
        return configuration;
    }

    @Bean(name = "redisTemplate")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        //序列化
        JacksonRedisSerializer<Object> jacksonRedisSerializer = new JacksonRedisSerializer<>(Object.class);
        // value值的序列化采用jacksonRedisSerializer
        template.setValueSerializer(jacksonRedisSerializer);
        template.setHashValueSerializer(jacksonRedisSerializer);
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 自定义缓存key生成策略，默认将使用该策略
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            Map<String,Object> container = new HashMap<>(3);
            Class<?> targetClassClass = target.getClass();
            // 类地址
            container.put("class",targetClassClass.toGenericString());
            // 方法名称
            container.put("methodName",method.getName());
            // 包名称
            container.put("package",targetClassClass.getPackage());
            // 参数列表
            for (int i = 0; i < params.length; i++) {
                container.put(String.valueOf(i),params[i]);
            }
            // 转为JSON字符串
            String jsonString = JsonUtil.toJSONString(container);
            // 做SHA256 Hash计算，得到一个SHA256摘要作为Key
            return DigestUtils.sha256Hex(jsonString);
        };
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
        log.info("初始化 -> [{}]", "Redis CacheErrorHandler");
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }
        };
    }

}

/**
 * Value 序列化。
 * <p>
 * 取代原 FastJsonRedisSerializer。新写入的类型信息仍然放在 {@code @type} 属性上（而不是
 * Jackson 默认的 {@code @class}），并使用白名单校验代替 fastjson 的全局 autoType。
 * <p>
 * 读取采用两段式，因为线上存量缓存是 fastjson 写的，形态并不统一（实测 DB2 的 94 个 JSON 值）：
 * <ol>
 *     <li>先按 {@code @type} 还原具体类型（只有 5 个能直接读通）</li>
 *     <li>失败后按无类型读——线上大量值是 fastjson 对 String / Long 不写 {@code @type}
 *         的裸标量（如 online-token 存的是 Long），这一档能再读回 88 个，合计 93/94</li>
 *     <li>若文本里的 {@code @type} 指向已不存在的类（实测 9 个值是
 *         {@code com.alibaba.fastjson.JSONObject}），宁可报缓存未命中也不能返回 Map——
 *         否则调用方会在拿到 Map 后 ClassCastException，而 {@code CacheErrorHandler}
 *         只能兜住取缓存阶段的异常，兜不住后续的强转</li>
 * </ol>
 * 判定为未命中的条目会由 {@code CacheErrorHandler} 记日志并回源查库，属于可接受的性能降级；
 * 写入新值后逐步自然收敛。
 *
 * @author /
 * @param <T>
 */
class JacksonRedisSerializer<T> implements RedisSerializer<T> {

    /** 提取 JSON 文本里所有的 {@code @type} 类名，用于判断能否还原 */
    private static final Pattern TYPE_HINT = Pattern.compile("\"@type\"\\s*:\\s*\"([\\w.$]+)\"");

    private final Class<T> clazz;

    JacksonRedisSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) {
        if (t == null) {
            return new byte[0];
        }
        return JsonUtil.typedMapper().writeValueAsBytes(t);
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String json = new String(bytes, StandardCharsets.UTF_8);
        try {
            return JsonUtil.typedMapper().readValue(json, clazz);
        } catch (JacksonException ignored) {
            // 存量数据没有可用的类型信息，继续往下走无类型读取
        }
        if (hasUnresolvableTypeHint(json)) {
            throw new SerializationException("Redis 缓存值的 @type 无法解析，按未命中处理");
        }
        try {
            return JsonUtil.mapper().readValue(json, clazz);
        } catch (JacksonException e) {
            // 交给 Spring Data Redis 的异常体系，业务侧可按 SerializationException 捕获后回源
            throw new SerializationException("Redis 缓存值反序列化失败", e);
        }
    }

    /**
     * 文本里出现过的 {@code @type} 类名只要有一个加载不到，就不能当无类型对象返回。
     */
    private static boolean hasUnresolvableTypeHint(String json) {
        Matcher m = TYPE_HINT.matcher(json);
        while (m.find()) {
            try {
                Class.forName(m.group(1), false, JacksonRedisSerializer.class.getClassLoader());
            } catch (ClassNotFoundException | LinkageError e) {
                return true;
            }
        }
        return false;
    }
}
