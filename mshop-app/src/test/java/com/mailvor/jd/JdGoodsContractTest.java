package com.mailvor.jd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailvor.modules.dataoke.rest.DataokeJDController;
import com.mailvor.modules.tk.param.QueryJdParam;
import com.mailvor.modules.tk.param.jd.GoodsListJDParam;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.vo.jd.*;
import com.mailvor.utils.OrderUtil;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JdGoodsContractTest {
    private DataokeJDController controller(KuService provider) {
        DataokeJDController controller = new DataokeJDController();
        ReflectionTestUtils.setField(controller, "kuService", provider);
        return controller;
    }

    private JdKuGoodsDetailVO product() {
        JdKuGoodsDetailVO product = new JdKuGoodsDetailVO();
        product.setGoodsId("encrypted-product");
        product.setTitle("测试商品");
        product.setImg("https://example.com/item.jpg");
        product.setEndPrice(80D);
        product.setFeeRatio(10D);
        return product;
    }

    @Test
    public void listUsesNormalizedFieldsAndPreservesProviderCursor() throws Exception {
        KuService provider = mock(KuService.class);
        JdKuSearchListVO response = new JdKuSearchListVO();
        response.setCode(200);
        response.setMinId("17");
        response.setData(Collections.singletonList(product()));
        GoodsListJDParam param = new GoodsListJDParam();
        when(provider.searchJD(param)).thenReturn(response);
        Map<String, Object> data = controller(provider).goodsList(param).getData();
        assertEquals("17", data.get("minId"));
        Map<?, ?> item = (Map<?, ?>) ((List<?>) data.get("list")).get(0);
        assertEquals("encrypted-product", item.get("goodsId"));
        assertEquals("encrypted-product", item.get("itemId"));
        assertEquals("测试商品", item.get("title"));
        assertEquals(80D, item.get("endPrice"));
        assertEquals(8D, item.get("fee"));
        assertFalse(item.containsKey("itemid"));
        // Serialization must not reintroduce provider aliases through nested DTOs.
        String json = new ObjectMapper().writeValueAsString(data);
        assertTrue(json.contains("\"title\""));
        assertFalse(json.contains("\"goodsname\""));
    }

    @Test
    public void detailReturnsAnObjectAndToleratesMissingOptionalProviderFields() {
        KuService provider = mock(KuService.class);
        JdKuCommonGoodsDetailDataVo response = new JdKuCommonGoodsDetailDataVo();
        response.setCode(200);
        response.setData(JdKuCommonGoodsDetailVO.convert(product()));
        when(provider.detailJD(any(GoodsListJDParam.class))).thenReturn(response);
        Map<String, Object> data = controller(provider).getGoodsDetail("encrypted-product").getData();
        assertEquals(Collections.emptyList(), data.get("detailImages"));
        assertEquals(Collections.singletonList("https://example.com/item.jpg"), data.get("banners"));
        assertEquals("encrypted-product", data.get("itemId"));
    }

    @Test
    public void attributionKeepsLegacyChannelAndFallsBackToPosition() {
        assertEquals(Long.valueOf(12), OrderUtil.getJdOrderUser("12", 34L));
        assertEquals(Long.valueOf(34), OrderUtil.getJdOrderUser(null, 34L));
        assertEquals(Long.valueOf(34), OrderUtil.getJdOrderUser("invalid", 34L));
        assertEquals(Long.valueOf(0), OrderUtil.getJdOrderUser(null, null));
        assertEquals(Integer.valueOf(1), new QueryJdParam().getPageNo());
    }

    @Test
    public void conversionRetainsOptionalUserContextInterceptor() throws Exception {
        assertNotNull(DataokeJDController.class.getMethod("goodsWord2",
                com.mailvor.modules.tk.param.GoodsJdWordParam.class)
                .getAnnotation(com.mailvor.common.interceptor.UserCheck.class));
    }

    @Test(expected = IllegalStateException.class)
    public void providerFailureIsNotReportedAsAnEmptySuccessfulDetail() {
        KuService provider = mock(KuService.class);
        JdKuCommonGoodsDetailDataVo response = new JdKuCommonGoodsDetailDataVo();
        response.setCode(500);
        when(provider.detailJD(any(GoodsListJDParam.class))).thenReturn(response);
        controller(provider).getGoodsDetail("encrypted-product");
    }
}
