package com.mailvor.modules.pay.rest;

import com.mailvor.modules.pay.domain.MwPayChannel;
import com.mailvor.modules.pay.domain.MwPayCompany;
import com.mailvor.modules.pay.enums.PayChannelEnum;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.MwPayCompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Random;
@RunWith(SpringRunner.class)
@SpringBootTest
class PayControllerTest {
    @Resource
    private MwPayChannelService payChannelService;
//    @Resource
//    private PayController payController;
    @Resource
    private PayNotifyController payNotifyController;

    @Resource
    private MwPayCompanyService payCompanyService;
    @BeforeEach
    void setUp() {
        MwPayCompany payCompany1 = MwPayCompany.builder()
                .id(10000l)
                .company("苏州xxx公司")
                .phone("ccc")
                .sealUrl("bbb")
                .licenseUrl("aaa")
                .build();
        payCompanyService.saveOrUpdate(payCompany1);

        MwPayCompany payCompany2 = MwPayCompany.builder()
                .id(20000l)
                .company("苏州yyy公司")
                .phone("ccc")
                .sealUrl("bbb")
                .licenseUrl("aaa")
                .build();
        payCompanyService.saveOrUpdate(payCompany2);


        //todo 生成10条通道 每条额度10万 5条支付宝 5条微信
        for(long i = 0; i < 5; i++) {
            MwPayChannel payChannel = MwPayChannel.builder()
                    .id(i+1)
                    .channelName("支付宝"+i)
                    .channelKey(PayChannelEnum.adapay.getKey())
                    .notifyUrl("abc")
                    .maxAmount(100000d)
                    .amount(100000d)
                    .status(8)
                    .companyId(10000l)
                    .type(2)
                    .build();
            payChannelService.saveOrUpdate(payChannel);
        }
        for(long i = 0; i < 5; i++) {
            MwPayChannel payChannel = MwPayChannel.builder()
                    .id(i+100)
                    .channelKey(PayChannelEnum.adapay.getKey())
                    .channelName("微信"+i)
                    .notifyUrl("abc")
                    .maxAmount(100000d)
                    .amount(100000d)
                    .status(8)
                    .companyId(10000l)
                    .type(7)
                    .build();
            payChannelService.saveOrUpdate(payChannel);
        }
    }
    @Test
    void aliPay() throws InterruptedException {
        Random random = new Random();
        int price = 2000;
        for(long i = 0; i<100000; i++) {
            int dec = random.nextInt(100);
            Integer payType = i/2 == 0 ? 1 : 2;
            MwPayChannel payChannel;
            try{
                payChannel = payChannelService.channel(i, payType);
            }catch (Exception e) {
                break;
            }
            if(payChannel != null) {
                //todo 支付 回调
                MwPayChannel newChannel = payChannelService.decPrice(BigDecimal.valueOf(price - dec), payChannel.getId());
                System.out.println("i：" + i + " 通道:"+newChannel.getChannelName() + " id:" + newChannel.getId()
                        + " 剩余额度：" + newChannel.getAmount() + " 状态：" + newChannel.getStatus());
            }
            Thread.sleep(100);
        }

    }
}
