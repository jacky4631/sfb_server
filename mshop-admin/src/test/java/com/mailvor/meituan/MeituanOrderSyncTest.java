package com.mailvor.meituan;

import com.mailvor.modules.meituan.MeituanService;
import com.mailvor.modules.meituan.config.MeituanConfig;
import com.mailvor.modules.meituan.param.MeituanOrderParam;
import com.mailvor.modules.meituan.utils.MeituanUtil;
import com.mailvor.modules.quartz.task.OrderTask;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.param.QueryMtParam;
import com.mailvor.modules.tk.service.MailvorMtOrderService;
import com.mailvor.modules.tk.vo.MtDataVo;
import com.mailvor.modules.tk.vo.MtResVo;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.SpringContextUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MeituanOrderSyncTest {
    private MtResVo response(MailvorMtOrder... records) {
        MtDataVo data = new MtDataVo();
        data.setPositionIndex("next-page");
        data.setRecords(new ArrayList<>(Arrays.asList(records)));
        MtResVo response = new MtResVo();
        response.setCode(200); response.setMsg(data);
        return response;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void officialSidPersistsWithoutUtmMediumAndForeignAppIsRejected() {
        RedisTemplate<String, Object> redis = mock(RedisTemplate.class);
        SetOperations<String, Object> sets = mock(SetOperations.class);
        when(redis.opsForSet()).thenReturn(sets);
        ApplicationContext previous = SpringContextUtils.applicationContext;
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBean("redisTemplate", RedisTemplate.class)).thenReturn(redis);
        SpringContextUtils.applicationContext = context;
        Object previousRedis = ReflectionTestUtils.getField(RedisUtil.class, "redisTemplate");
        ReflectionTestUtils.setField(RedisUtil.class, "redisTemplate", redis);
        try {
            OrderTask task = new OrderTask();
            MailvorMtOrderService database = mock(MailvorMtOrderService.class);
            ReflectionTestUtils.setField(task, "mtOrderService", database);
            MailvorMtOrder sfb = MailvorMtOrder.builder().orderId(123L).sid("sfbuid_42").build();
            MailvorMtOrder foreign = MailvorMtOrder.builder().orderId(124L).sid("tenant_1_uid_42").build();
            assertEquals("next-page", ReflectionTestUtils.invokeMethod(task, "saveMtOrder", response(sfb, foreign)));
            assertEquals(Long.valueOf(42), sfb.getUid());
            assertEquals(Long.valueOf(123), sfb.getUniqueItemId());
            verify(database).saveOrUpdateBatch(Collections.singletonList(sfb));
            verify(sets).add(anyString(), eq(42L));
        } finally {
            ReflectionTestUtils.setField(RedisUtil.class, "redisTemplate", previousRedis);
            SpringContextUtils.applicationContext = previous;
        }
    }

    @Test
    public void foreignOnlyPageKeepsCursorAndDoesNotPersist() {
        OrderTask task = new OrderTask();
        MailvorMtOrderService database = mock(MailvorMtOrderService.class);
        ReflectionTestUtils.setField(task, "mtOrderService", database);
        MeituanService provider = mock(MeituanService.class);
        ReflectionTestUtils.setField(task, "meituanService", provider);
        when(provider.order(any(MeituanOrderParam.class))).thenReturn(response());
        assertEquals("next-page", ReflectionTestUtils.invokeMethod(task, "saveMtCPS", new QueryMtParam()));
        MailvorMtOrder foreign = MailvorMtOrder.builder().sid("uid_42").build();
        assertEquals("next-page", ReflectionTestUtils.invokeMethod(task, "saveMtOrder", response(foreign)));
        verifyNoInteractions(database);
    }

    @Test
    public void legacyEncryptedMediumIsStillRecognizedAndInvalidMediumIsSafe() {
        OrderTask task = new OrderTask();
        MeituanConfig config = new MeituanConfig(); config.setAppKey("test-key");
        ReflectionTestUtils.setField(task, "meituanConfig", config);
        MailvorMtOrder legacy = MailvorMtOrder.builder()
                .utmMedium(MeituanUtil.encryptHex("sfbuid_42", "test-key")).build();
        assertEquals(Long.valueOf(42), ReflectionTestUtils.invokeMethod(task, "getMtOrderUid", legacy));
        legacy.setUtmMedium("bad-hex");
        assertNull(ReflectionTestUtils.invokeMethod(task, "getMtOrderUid", legacy));
        legacy.setSid("uid_42");
        legacy.setUtmMedium(MeituanUtil.encryptHex("sfbuid_42", "test-key"));
        assertNull(ReflectionTestUtils.invokeMethod(task, "getMtOrderUid", legacy));
    }
}
