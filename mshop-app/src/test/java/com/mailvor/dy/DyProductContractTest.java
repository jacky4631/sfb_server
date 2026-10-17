package com.mailvor.dy;

import com.alibaba.fastjson.JSON;
import com.mailvor.modules.dataoke.rest.KuDyProductController;
import com.mailvor.modules.tk.param.GoodsListDyParam;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.vo.*;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class DyProductContractTest {
    private KuDyProductController controller(KuService service) {
        KuDyProductController controller = new KuDyProductController();
        ReflectionTestUtils.setField(controller, "kuService", service);
        return controller;
    }

    @Test
    public void listNormalizesCmsFieldsAndPreservesNonconsecutiveCursor() {
        KuService service = mock(KuService.class);
        GoodsListDyParam param = new GoodsListDyParam();
        DySearchListVO response = JSON.parseObject("{\"code\":200,\"min_id\":17,\"data\":[{\"product_id\":\"123\",\"product_title\":\"商品\",\"price\":99,\"end_price\":29.9,\"dymoney\":3,\"item_pic\":\"photo\"}]}", DySearchListVO.class);
        when(service.dyProductList(param)).thenReturn(response);
        Map<String, Object> data = controller(service).list(param).getData();
        assertEquals(17, data.get("minId"));
        Map<?, ?> item = (Map<?, ?>)((List<?>)data.get("list")).get(0);
        assertEquals("123", item.get("goodsId"));
        assertEquals("商品", item.get("title"));
        assertEquals(29.9, item.get("endPrice"));
        assertEquals(3D, item.get("fee"));
        assertFalse(item.containsKey("product_title"));
    }

    @Test
    public void detailIsSingleObjectWithGalleryAndOptionalFields() {
        KuService service = mock(KuService.class);
        DyGoodsDetailDataVo response = new DyGoodsDetailDataVo();
        response.setCode(0);
        response.setDetail(JSON.parseObject("{\"product_id\":\"123\",\"product_title\":\"名称\",\"item_pic\":\"main\",\"douyin_images\":[\"gallery\"],\"end_price\":19.9}", DyGoodsDetailVO.class));
        when(service.dyProductDetail("123")).thenReturn(response);
        Map<String, Object> result = controller(service).detail("123").getData();
        assertEquals(Collections.singletonList("gallery"), result.get("banners"));
        assertEquals("main", result.get("img"));
        assertEquals(19.9, result.get("endPrice"));
    }

    @Test
    public void promotionUsesProductIdAndReturnsCommonLinkAndPasswordFields() {
        KuService service = mock(KuService.class);
        DyWordGoodsDataVO response = JSON.parseObject("{\"code\":200,\"data\":{\"dy_zlink\":\"https://example.com\",\"dy_deeplink\":\"snssdk1128://item\",\"dy_password\":\"口令\"}}", DyWordGoodsDataVO.class);
        when(service.dyProductWord("123", "0")).thenReturn(response);
        Map<String, Object> result = controller(service).word("123").getData();
        assertEquals("https://example.com", result.get("link"));
        assertEquals("snssdk1128://item", result.get("longLink"));
        assertEquals("口令", result.get("tpwd"));
    }

    @Test
    public void providerSearchEscapesKeywordAndIncludesCategorySortAndCursor() {
        KuService service = spy(new KuService());
        doReturn("cms-cid").when(service).getKuCid();
        RestTemplate http = new RestTemplate();
        ReflectionTestUtils.setField(service, "restTemplate", http);
        MockRestServiceServer server = MockRestServiceServer.createServer(http);
        server.expect(request -> {
            String query = request.getURI().getRawQuery();
            assertTrue(query.contains("min_id=17"));
            assertTrue(query.contains("cate_id=8"));
            assertTrue(query.contains("sort=4"));
            assertTrue(query.contains("keyword=100%25%20%2B%20%26"));
        }).andRespond(withSuccess("{\"code\":200,\"data\":{\"min_id\":29,\"products\":[]}}", MediaType.APPLICATION_JSON));
        GoodsListDyParam param = new GoodsListDyParam();
        param.setPageId(17); param.setCateId(8); param.setSort(4); param.setKeyword("100% + &");
        assertEquals(Integer.valueOf(29), service.dyProductList(param).getMinId());
        server.verify();
    }

    @Test
    public void categoriesDecodeHttpJsonWithoutLinkedHashMapCasts() {
        KuService service = spy(new KuService());
        doReturn("cms-cid").when(service).getKuCid();
        RestTemplate http = new RestTemplate();
        ReflectionTestUtils.setField(service, "restTemplate", http);
        MockRestServiceServer server = MockRestServiceServer.createServer(http);
        server.expect(request -> assertTrue(request.getURI().getQuery().contains("cid=cms-cid")))
                .andRespond(withSuccess("{\"code\":200,\"data\":{\"category\":[{\"id\":8,\"name\":\"食品\"},{\"id\":\"9\",\"name\":\"服装\"}]}}", MediaType.APPLICATION_JSON));
        List<Map<String, Object>> categories = controller(service).categories().getData();
        assertEquals(2, categories.size());
        assertEquals(8, categories.get(0).get("cid"));
        assertEquals("食品", categories.get(0).get("title"));
        assertEquals(9, categories.get(1).get("cid"));
        server.verify();
    }

    @Test(expected = IllegalStateException.class)
    public void categoryProviderErrorIsNotConvertedToEmptySuccess() {
        KuService service = spy(new KuService());
        doReturn("cms-cid").when(service).getKuCid();
        RestTemplate http = new RestTemplate();
        ReflectionTestUtils.setField(service, "restTemplate", http);
        MockRestServiceServer server = MockRestServiceServer.createServer(http);
        server.expect(request -> {}).andRespond(withSuccess("{\"code\":500,\"msg\":\"service unavailable\"}", MediaType.APPLICATION_JSON));
        service.dyProductCateList();
    }

    @Test(expected = IllegalStateException.class)
    public void failedProviderResponseIsNotAnEmptySuccessfulList() {
        KuService service = mock(KuService.class);
        GoodsListDyParam param = new GoodsListDyParam();
        DySearchListVO response = new DySearchListVO(); response.setCode(500);
        when(service.dyProductList(param)).thenReturn(response);
        controller(service).list(param);
    }
}
