package com.mailvor.modules.order.rest.test;

import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.modules.activity.param.UserExtParam;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.rest.UserExtractController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserExtractControllerTest {
    @Autowired
    private UserExtractController orderController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addMwUserExtract(){
        MwUser mwUser = new MwUser();
        mwUser.setUid(8L);
        mwUser.setRealName("杨玲");
        mwUser.setSpreadUid(1L);
        LocalUser.set(mwUser, 0);
        UserExtParam param = new UserExtParam();
        param.setExtractType("weixin");
        param.setMoney("24.76");
        param.setName("杨玲");
        ApiResult result = orderController.addMwUserExtract(null, param);
    }
}
