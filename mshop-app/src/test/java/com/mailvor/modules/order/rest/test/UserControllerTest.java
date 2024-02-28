package com.mailvor.modules.order.rest.test;

import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.rest.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController orderController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void userInfo(){
        MwUser mwUser = new MwUser();
        mwUser.setUid(8L);
        LocalUser.set(mwUser, 0);
        ApiResult result = orderController.userInfo(false);
    }
}
