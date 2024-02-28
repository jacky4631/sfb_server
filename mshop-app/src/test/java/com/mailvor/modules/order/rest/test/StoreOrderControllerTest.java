package com.mailvor.modules.order.rest.test;

import com.mailvor.api.ApiResult;
import com.mailvor.modules.order.param.SubmitOrderParam;
import com.mailvor.modules.order.rest.StoreOrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
class StoreOrderControllerTest {
    @Autowired
    private StoreOrderController orderController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void submitOrder() throws ExecutionException, InterruptedException {
        SubmitOrderParam param = new SubmitOrderParam();
        param.setOrderId("252938129656");
        ApiResult result = orderController.submitOrder(param);
    }
}