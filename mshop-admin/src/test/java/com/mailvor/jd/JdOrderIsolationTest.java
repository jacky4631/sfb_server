package com.mailvor.jd;

import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowQueryResult;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.mailvor.modules.quartz.task.OrderTask;
import com.mailvor.modules.quartz.task.JdOrderBindTask;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.domain.MailvorJdOrder;
import java.util.Arrays;
import com.mailvor.modules.tk.param.QueryJdParam;
import com.mailvor.modules.tk.service.JdService;
import com.mailvor.modules.tk.service.MailvorJdOrderService;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JdOrderIsolationTest {
    @Test
    public void filteredPageDoesNotPersistOrStopPagination() {
        assertFilteredPage(true, 1);
        assertFilteredPage(false, 0);
    }

    @Test
    public void rebindingRejectsForeignOrdersAndUsesDecodedUid() {
        MailvorJdOrderService orders = mock(MailvorJdOrderService.class);
        SuStoreOrderService binder = mock(SuStoreOrderService.class);
        JdOrderBindTask task = new JdOrderBindTask();
        ReflectionTestUtils.setField(task, "orderService", orders);
        ReflectionTestUtils.setField(task, "suStoreOrderService", binder);
        MailvorJdOrder legacy = new MailvorJdOrder();
        legacy.setPositionId(123L);
        MailvorJdOrder foreign = new MailvorJdOrder();
        foreign.setPositionId(2_000_000_000_123L);
        foreign.setSubUnionId("other_123");
        MailvorJdOrder sfb = new MailvorJdOrder();
        sfb.setPositionId(1_000_000_000_123L);
        when(orders.getUnbindOrderList(600)).thenReturn(Arrays.asList(legacy, foreign, sfb));
        ReflectionTestUtils.invokeMethod(task, "run", "{}");
        verify(binder).bindOrder(123L, sfb);
        verifyNoMoreInteractions(binder);
    }

    private void assertFilteredPage(boolean hasMore, int expected) {
        JdService provider = mock(JdService.class);
        MailvorJdOrderService orders = mock(MailvorJdOrderService.class);
        OrderTask task = new OrderTask();
        ReflectionTestUtils.setField(task, "jdService", provider);
        ReflectionTestUtils.setField(task, "jdOrderService", orders);
        OrderRowResp legacy = new OrderRowResp();
        legacy.setPositionId(123L);
        OrderRowResp otherApp = new OrderRowResp();
        otherApp.setPositionId(2_000_000_000_123L);
        OrderRowQueryResult response = new OrderRowQueryResult();
        response.setData(new OrderRowResp[] {legacy, otherApp});
        response.setHasMore(hasMore);
        QueryJdParam param = new QueryJdParam();
        when(provider.order(param)).thenReturn(response);
        assertEquals(Integer.valueOf(expected), task.saveJd(param));
        verifyNoInteractions(orders);
    }
}
