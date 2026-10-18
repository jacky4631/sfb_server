package com.mailvor.coupon;

import com.mailvor.modules.tk.config.TbConfig;
import com.mailvor.modules.tk.param.GoodsListDyParam;
import com.mailvor.modules.tk.param.GoodsListPddParam;
import com.mailvor.modules.tk.param.ParseContentParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.service.TkService;
import com.mailvor.modules.tk.vo.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Collections;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CouponParseContractTest {
    private final DataokeService dataoke = mock(DataokeService.class);
    private final KuService ku = mock(KuService.class);

    private TkService service() {
        TkService service = new TkService();
        TbConfig config = new TbConfig();
        config.setChannelPid("channel-pid");
        ReflectionTestUtils.setField(service, "dataokeService", dataoke);
        ReflectionTestUtils.setField(service, "kuService", ku);
        ReflectionTestUtils.setField(service, "tbConfig", config);
        return service;
    }

    @Test
    public void emptyContentDoesNotCallProviders() {
        assertEquals(Integer.valueOf(-1), service().mixParse(new ParseContentParam(""), null).getCode());
        assertEquals(Integer.valueOf(-1), service().mixParse(null, null).getCode());
        verifyNoInteractions(dataoke, ku);
    }

    @Test
    public void nullTaobaoAndFallbackResponsesPreserveOriginalForSearch() {
        String content = "https://item.taobao.com/item.htm?id=123";
        TkParseCodeVO result = service().mixParse(new ParseContentParam(content), null);
        assertEquals(Integer.valueOf(-1), result.getCode());
        assertEquals(content, result.getData().getOriginContent());
        verify(ku).clipboard(any(ParseContentParam.class));
    }

    @Test
    public void taobaoStripsPercentAndRetainsFallbackPromotionLink() {
        String content = "淘宝商品 50% https://item.taobao.com/item.htm?id=123";
        when(dataoke.goodsParse(content.replace("%", ""), "channel-pid", null))
                .thenThrow(new IllegalStateException("provider unavailable"));
        TkParseCodeVO fallback = new TkParseCodeVO();
        fallback.setCode(0);
        fallback.setData(TkParseVO.builder().goodsId("123").title("商品")
                .shortUrl("https://s.click.taobao.com/link").build());
        when(ku.clipboard(any(ParseContentParam.class))).thenReturn(fallback);
        TkParseCodeVO result = service().mixParse(new ParseContentParam(content), null);
        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals(content, result.getData().getOriginContent());
        assertEquals("https://s.click.taobao.com/link", result.getData().getShortUrl());
        verify(dataoke).goodsParse(content.replace("%", ""), "channel-pid", null);
    }

    @Test
    public void douyinUsesRawKeywordAndToleratesMissingOriginalPrice() {
        String content = "抖音 https://v.douyin.com/example/?q=50% + &";
        DySearchVO product = new DySearchVO();
        product.setGoodsId("dy-product"); product.setTitle("商品"); product.setEndPrice(19D);
        DySearchListVO response = new DySearchListVO();
        response.setCode(200); response.setData(Collections.singletonList(product));
        when(ku.dyProductList(any(GoodsListDyParam.class))).thenReturn(response);
        TkParseCodeVO result = service().mixParse(new ParseContentParam(content), null);
        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals("dy-product", result.getData().getGoodsId());
        assertEquals(Double.valueOf(0), result.getData().getCoupon());
        ArgumentCaptor<GoodsListDyParam> param = ArgumentCaptor.forClass(GoodsListDyParam.class);
        verify(ku).dyProductList(param.capture());
        assertEquals(content, param.getValue().getKeyword());
    }

    @Test
    public void pinduoduoShortLinksReachThePddParser() {
        String content = "https://p.pinduoduo.com/example";
        TkParseCodeVO result = service().mixParse(new ParseContentParam(content), null);
        ArgumentCaptor<GoodsListPddParam> param = ArgumentCaptor.forClass(GoodsListPddParam.class);
        verify(dataoke).goodsListPdd(param.capture());
        assertEquals(content, param.getValue().getKeyword());
        assertEquals(content, result.getData().getOriginContent());
    }

    @Test
    public void nullJdSearchAndFallbackResponsesPreserveOriginalForSearch() {
        String content = "【京东】https://3.jd.hk/10-3zJkl 「【鱼油NO.1】斯维诗4倍深海鱼油」";
        TkParseCodeVO result = service().mixParse(new ParseContentParam(content), null);
        assertEquals(Integer.valueOf(-1), result.getCode());
        assertEquals(content, result.getData().getOriginContent());
    }
}
