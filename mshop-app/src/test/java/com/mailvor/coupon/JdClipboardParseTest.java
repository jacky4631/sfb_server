package com.mailvor.coupon;

import com.mailvor.modules.tk.param.ParseContentParam;
import com.mailvor.modules.tk.param.jd.GoodsListJDParam;
import com.mailvor.modules.tk.service.JdProductFetcher;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.service.TkService;
import com.mailvor.modules.tk.vo.TkParseCodeVO;
import com.mailvor.modules.tk.vo.jd.JdKuGoodsDetailVO;
import com.mailvor.modules.tk.vo.jd.JdKuSearchListVO;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JdClipboardParseTest {
    private static final String CONTENT = "【京东】https://3.jd.hk/10-3zJkl 「【鱼油NO.1】斯维诗4倍深海鱼油」\n点击链接直接打开";
    private static final String TITLE = "斯维诗4倍深海鱼油";

    private JdKuGoodsDetailVO product(String id, String title) {
        JdKuGoodsDetailVO product = new JdKuGoodsDetailVO();
        product.setGoodsId(id); product.setTitle(title);
        product.setStartPrice(100D); product.setEndPrice(80D);
        return product;
    }

    private JdKuSearchListVO result(JdKuGoodsDetailVO... goods) {
        JdKuSearchListVO result = new JdKuSearchListVO();
        result.setCode(200); result.setData(Arrays.asList(goods));
        return result;
    }

    @Test
    public void exactUserShareExtractsCleanTitleWithoutRequestingShortUrl() {
        assertEquals(TITLE, JdProductFetcher.extractShareTitle(CONTENT));
        assertEquals(TITLE, JdProductFetcher.getTitle(CONTENT));
        assertNull(JdProductFetcher.extractShareTitle("https://3.jd.hk/10-3zJkl"));
    }

    @Test
    public void emptyOrFailedLinkSearchRetriesUsingShareTitle() {
        for (boolean throwsError : new boolean[] {false, true}) {
            KuService provider = mock(KuService.class);
            List<String> keywords = new ArrayList<>();
            when(provider.searchJD(any(GoodsListJDParam.class))).thenAnswer(invocation -> {
                String keyword = ((GoodsListJDParam) invocation.getArgument(0)).getKeyword();
                keywords.add(keyword);
                if (CONTENT.equals(keyword)) {
                    if (throwsError) throw new IllegalStateException("provider failed");
                    return result();
                }
                return result(product("fish-oil", TITLE));
            });
            TkService service = new TkService();
            ReflectionTestUtils.setField(service, "kuService", provider);
            TkParseCodeVO response = service.mixParse(new ParseContentParam(CONTENT), null);
            assertEquals(Arrays.asList(CONTENT, TITLE), keywords);
            assertEquals(Integer.valueOf(0), response.getCode());
            assertEquals("fish-oil", response.getData().getGoodsId());
            assertEquals(CONTENT, response.getData().getOriginContent());
            verify(provider, never()).clipboard(any(ParseContentParam.class));
        }
    }

    @Test
    public void ambiguousLinkResultsAreCorrectedByShareTitle() {
        KuService provider = mock(KuService.class);
        when(provider.searchJD(any(GoodsListJDParam.class))).thenAnswer(invocation -> {
            String keyword = ((GoodsListJDParam) invocation.getArgument(0)).getKeyword();
            return CONTENT.equals(keyword)
                    ? result(product("unrelated", "其他商品"), product("also-unrelated", "另一商品"))
                    : result(product("fish-oil", TITLE));
        });
        TkService service = new TkService();
        ReflectionTestUtils.setField(service, "kuService", provider);
        assertEquals("fish-oil", service.mixParse(new ParseContentParam(CONTENT), null).getData().getGoodsId());
    }

    @Test
    public void mobileHtmlParsesSkuNameBeforeGenericPageTitle() {
        assertEquals(TITLE, ReflectionTestUtils.invokeMethod(JdProductFetcher.class, "parseProductInfo",
                "<title>京东首页</title><script>{\"skuName\":\"" + TITLE + "\"}</script>"));
        assertEquals("123456789", ReflectionTestUtils.invokeMethod(JdProductFetcher.class, "extractSkuId",
                "https://item.m.jd.com/product/123456789.html"));
        assertEquals("https://item.jd.com/123456789.html", ReflectionTestUtils.invokeMethod(JdProductFetcher.class,
                "toAbsoluteUrl", "https://item.jd.com/path/a", "/123456789.html"));
    }
}
