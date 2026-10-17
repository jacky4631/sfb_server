package com.mailvor.cms;

import com.mailvor.modules.tk.config.KuCmsAddress;
import com.mailvor.modules.tk.constants.TkConstants;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.config.AppDataConfig;
import org.junit.After;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class KuCmsAddressTest {
    @After public void clearCache() { TkConstants.kuCmsUrl = null; }

    @Test public void parsesFullAddressAndIgnoresOtherQueryParametersAndFragment() {
        KuCmsAddress address = KuCmsAddress.parse(" http://jkj.mailvor.com/?source=app&cid=FazG6352#/ ");
        assertEquals("jkj.mailvor.com", address.getDomain());
        assertEquals("FazG6352", address.getCid());
        assertEquals("abc_123", KuCmsAddress.parse("https://shop.example.com/path?cid=abc_123").getCid());
    }

    @Test public void rejectsMissingCidDuplicateCidAndBareLegacyValue() {
        String[] invalid = {null, "", "FazG6352", "https://example.com/", "https://example.com/?cid=", 
                "https://example.com/?cid=a&cid=b", "ftp://example.com/?cid=a", "https://user:pass@example.com/?cid=a"};
        for (String value : invalid) {
            try { KuCmsAddress.parse(value); fail("Accepted invalid address: " + value); }
            catch (IllegalArgumentException expected) { assertTrue(expected.getMessage().contains("完整")); }
        }
    }

    @Test public void gettersSplitTheCachedAddressAndReloadAfterConfigurationInvalidation() {
        TkConstants.kuCmsUrl = null;
        AppDataConfig config = new AppDataConfig(); config.setKuCid("http://jkj.mailvor.com/?cid=FazG6352");
        MwSystemConfigService settings = mock(MwSystemConfigService.class);
        when(settings.getAppDataConfig()).thenReturn(config);
        KuService service = new KuService();
        ReflectionTestUtils.setField(service, "systemConfigService", settings);
        assertEquals("FazG6352", service.getKuCid());
        assertEquals("jkj.mailvor.com", service.getKuHost());
        verify(settings, times(1)).getAppDataConfig();
        config.setKuCid("https://new.example.com/?cid=new123");
        TkConstants.kuCmsUrl = null;
        assertEquals("new123", service.getKuCid());
        assertEquals("new.example.com", service.getKuHost());
    }

    @Test public void promotionRequestUsesDomainAndCidFromTheSameAddress() {
        TkConstants.kuCmsUrl = "http://jkj.mailvor.com/?cid=FazG6352";
        KuService service = new KuService();
        RestTemplate http = new RestTemplate();
        ReflectionTestUtils.setField(service, "restTemplate", http);
        MockRestServiceServer upstream = MockRestServiceServer.createServer(http);
        upstream.expect(request -> {
            String body = ((MockClientHttpRequest) request).getBodyAsString();
            assertTrue(body.contains("domain=jkj.mailvor.com"));
            assertTrue(body.contains("cid=FazG6352"));
            assertFalse(body.contains("domain=http"));
        }).andRespond(withSuccess("{\"code\":200,\"data\":{\"dy_zlink\":\"https://example.com/link\"}}", MediaType.APPLICATION_JSON));
        assertEquals("https://example.com/link", service.dyProductWord("123", "0").getData().getLink());
        upstream.verify();
    }
}
