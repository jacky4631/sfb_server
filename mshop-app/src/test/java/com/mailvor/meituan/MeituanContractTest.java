package com.mailvor.meituan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.modules.meituan.MeituanService;
import com.mailvor.modules.meituan.config.MeituanConfig;
import com.mailvor.modules.meituan.param.*;
import com.mailvor.modules.meituan.utils.MeituanClient;
import com.mailvor.modules.meituan.utils.MeituanSidUtil;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.vo.MtResVo;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class MeituanContractTest {
    private MeituanService service(MeituanClient client) {
        MeituanService service = new MeituanService();
        ReflectionTestUtils.setField(service, "meituanClient", client);
        return service;
    }

    private MtResVo orders(String rows) {
        MeituanClient client = mock(MeituanClient.class);
        when(client.request(any(MeituanOrderParam.class), anyString())).thenReturn(JSON.parseObject(
                "{\"code\":0,\"data\":{\"code\":0,\"scrollId\":\"next-page\",\"dataList\":" + rows + "}}"));
        return service(client).order(new MeituanOrderParam());
    }

    @Test
    public void requestSignsExactUtf8BodyUsingTsqGatewayHeaders() throws Exception {
        RestTemplate http = new RestTemplate();
        MeituanConfig config = new MeituanConfig();
        config.setAppKey("test-key"); config.setAppSecret("test-secret");
        MeituanClient client = new MeituanClient();
        ReflectionTestUtils.setField(client, "restTemplate", http);
        ReflectionTestUtils.setField(client, "meituanConfig", config);
        MockRestServiceServer server = MockRestServiceServer.createServer(http);
        MeituanGoodsParam param = new MeituanGoodsParam();
        param.setSearchText("美食 & 火锅"); param.setSearchId("cursor");
        param.setCityId("10"); param.setBusinessAreaId("20");
        String path = "/cps_open/common/api/v1/query_coupon";
        server.expect(request -> {
            assertEquals("https://media.meituan.com" + path, request.getURI().toString());
            assertEquals(HttpMethod.POST, request.getMethod());
            byte[] body = ((MockClientHttpRequest) request).getBodyAsBytes();
            assertEquals(JSON.toJSONString(param), new String(body, StandardCharsets.UTF_8));
            try {
            String digest = Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(body));
            assertEquals(digest, request.getHeaders().getFirst("Content-MD5"));
            assertEquals("S-Ca-App,S-Ca-Timestamp", request.getHeaders().getFirst("S-Ca-Signature-Headers"));
            String timestamp = request.getHeaders().getFirst("S-Ca-Timestamp");
            String canonical = "POST\n" + digest + "\nS-Ca-App:test-key\nS-Ca-Timestamp:" + timestamp + "\n" + path;
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec("test-secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            assertEquals(Base64.getEncoder().encodeToString(hmac.doFinal(canonical.getBytes(StandardCharsets.UTF_8))),
                    request.getHeaders().getFirst("S-Ca-Signature"));
            } catch (java.security.GeneralSecurityException e) {
                throw new AssertionError(e);
            }
        }).andRespond(withSuccess("{\"code\":0,\"data\":[]}", MediaType.APPLICATION_JSON));
        assertEquals(Integer.valueOf(0), service(client).goodsList(param).getInteger("code"));
        server.verify();
    }

    @Test
    public void conversionUsesSfbSidAndPreservesResponseContract() throws Exception {
        MeituanClient client = mock(MeituanClient.class);
        MeituanLinkParam param = new MeituanLinkParam();
        param.setActId("activity"); param.setLinkType(4);
        JSONObject response = JSON.parseObject("{\"code\":0,\"data\":\"pages/path\"}");
        when(client.request(param, "/cps_open/common/api/v1/get_referral_link")).thenReturn(response);
        assertSame(response, service(client).getCode(param, 123L));
        assertEquals("sfbuid_123", param.getSid());
    }

    @Test
    public void orderMapsCommissionDatesStatusAndFullSfbPrefix() {
        MtResVo response = orders("[{\"orderId\":123,\"sid\":\"sfbuid_42\",\"commissionRate\":300,\"status\":6,"
                + "\"payTime\":100,\"updateTime\":200,\"refundTime\":300,\"profit\":3,\"payPrice\":100}]");
        MailvorMtOrder order = response.getMsg().getRecords().get(0);
        assertEquals(Long.valueOf(42), order.getUid());
        assertEquals("sfbuid_42", order.getSid());
        assertEquals(Double.valueOf(3), order.getBalanceCommissionRatio());
        assertEquals(Double.valueOf(3), order.getBalanceAmount());
        assertEquals(100000L, order.getOrderPayTime().getTime());
        assertEquals(200000L, order.getModifyTime().getTime());
        assertEquals(Integer.valueOf(1), order.getItemStatus());
        assertEquals(Integer.valueOf(3), order.getItemBizStatus());
    }

    @Test
    public void malformedAndForeignSidsDoNotDropValidOrdersOrCursor() {
        MtResVo response = orders("[{\"sid\":\"tenant_1_uid_42\"},{\"sid\":\"uid_42\"},"
                + "{\"sid\":\"sfbuid_bad\"},{\"sid\":\"sfbuid_42\",\"orderId\":123}]");
        assertEquals(1, response.getMsg().getRecords().size());
        assertEquals(Long.valueOf(42), response.getMsg().getRecords().get(0).getUid());
        assertEquals(Double.valueOf(0), response.getMsg().getRecords().get(0).getBalanceCommissionRatio());
        assertEquals("next-page", response.getMsg().getPositionIndex());
    }

    @Test
    public void emptyOrEntirelyForeignPagePreservesCursor() {
        for (String rows : new String[] {"[]", "null", "[{\"sid\":\"uid_42\"}]"}) {
            MtResVo response = orders(rows);
            assertNotNull(response);
            assertTrue(response.getMsg().getRecords().isEmpty());
            assertEquals("next-page", response.getMsg().getPositionIndex());
        }
    }

    @Test
    public void sidParserRejectsBadValuesAndRetainsAnonymousSid() {
        for (String sid : new String[] {null, "", "uid_42", "sfbuid_", "sfbuid_-1", "sfbuid_0042", "sfbuid_9223372036854775808"}) {
            assertNull(MeituanSidUtil.parse(sid));
        }
        assertEquals("sfbuid_0", MeituanSidUtil.build(null));
        assertEquals(Long.valueOf(0), MeituanSidUtil.parse("sfbuid_0"));
        assertNull(orders("[{\"sid\":\"sfbuid_0\",\"orderId\":123}]").getMsg().getRecords().get(0).getUid());
    }
}
