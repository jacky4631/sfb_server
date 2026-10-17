package com.mailvor.vip;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.modules.dataoke.rest.DataokeVipController;
import com.mailvor.modules.tk.param.GoodsListVipParam;
import com.mailvor.modules.tk.param.GoodsSearchVipParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.vo.vip.*;
import com.mailvor.modules.user.domain.MwUser;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VipProductContractTest {
    private final DataokeService service = mock(DataokeService.class);
    private DataokeVipController controller() {
        DataokeVipController controller = new DataokeVipController();
        ReflectionTestUtils.setField(controller, "service", service);
        return controller;
    }

    @After public void clearUser() { LocalUser.clear(); }

    @Test
    public void listNormalizesFieldsAndUsesCurrentUser() {
        VipSearchListVO result = JSON.parseObject("{\"code\":0,\"data\":{\"total\":1,\"goodsInfoList\":[{\"goodsId\":\"123\",\"goodsName\":\"羊毛衫\",\"vipPrice\":199.9,\"marketPrice\":599,\"commission\":12,\"storeInfo\":{\"storeName\":\"品牌店\"}}]}}", VipSearchListVO.class);
        GoodsListVipParam param = new GoodsListVipParam();
        param.setOpenId("untrusted-client-id");
        when(service.goodsListVip(param)).thenReturn(result);
        Map<String, Object> data = controller().getGoodList(param).getData();
        assertEquals("0", param.getOpenId());
        assertEquals(1, data.get("total"));
        Map<?, ?> item = (Map<?, ?>) ((List<?>)data.get("list")).get(0);
        assertEquals("羊毛衫", item.get("title"));
        assertEquals("品牌店", item.get("shopName"));
        assertEquals(199.9, item.get("endPrice"));
        assertFalse(item.containsKey("vipPrice"));
    }

    @Test
    public void postSearchPreservesSpecialCharactersAndJsonBody() throws Exception {
        when(service.vipGoodsSearch(any(GoodsSearchVipParam.class))).thenReturn(
                JSONObject.parseObject("{\"code\":0,\"data\":{\"total\":0,\"goodsInfoList\":[]}}"));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller()).build();
        String keyword = "100%羊毛 + 大衣&秋冬";
        JSONObject body = new JSONObject();
        body.put("keyword", keyword);
        body.put("page", 2);
        body.put("fieldName", "PRICE");
        body.put("order", 0);
        mvc.perform(post("/vip/goods/search").contentType("application/json;charset=UTF-8")
                .content(body.toJSONString())).andExpect(status().isOk());
        ArgumentCaptor<GoodsSearchVipParam> param = ArgumentCaptor.forClass(GoodsSearchVipParam.class);
        verify(service).vipGoodsSearch(param.capture());
        assertEquals(keyword, param.getValue().getKeyword());
        assertEquals(Integer.valueOf(2), param.getValue().getPage());
        assertEquals("0", param.getValue().getOpenId());
    }

    @Test
    public void detailReturnsOneProductAndCouponMetadata() {
        VipGoodsDetailDataVo result = new VipGoodsDetailDataVo();
        result.setCode(0);
        result.setGoods(JSON.parseObject("{\"goodsId\":\"123\",\"goodsName\":\"衬衫\",\"vipPrice\":129.9,\"marketPrice\":399,\"couponInfo\":{\"fav\":20},\"destUrl\":\"https://example.com/item\",\"adCode\":\"ad-123\"}", VipGoodsDetailVO.class));
        when(service.goodsDetailVIP("123", "0")).thenReturn(result);
        Map<String, Object> data = controller().getGoodsDetail("123").getData();
        assertEquals("衬衫", data.get("title"));
        assertEquals(129.9, data.get("endPrice"));
        assertEquals(20D, ((VipGoodsDetailCouponInfoVO)data.get("couponInfo")).getFav(), 0.001);
        assertEquals("ad-123", data.get("adCode"));
    }

    @Test
    public void emptyDetailReturnsEmptyObjectWithoutIndexingAnArray() {
        VipGoodsDetailDataVo result = new VipGoodsDetailDataVo();
        result.setCode(0);
        result.setData(Collections.emptyList());
        when(service.goodsDetailVIP("missing", "0")).thenReturn(result);
        assertTrue(controller().getGoodsDetail("missing").getData().isEmpty());
    }

    @Test
    public void promotionUsesLoginAttributionAndPreservesAdCode() {
        MwUser user = new MwUser();
        user.setUid(42L);
        LocalUser.set(user, 4);
        VipWordCodeVO result = new VipWordCodeVO();
        result.setCode(0);
        result.setWord(JSON.parseObject("{\"deeplinkUrl\":\"vipshop://item\",\"longUrl\":\"https://example.com/item\",\"vipWxUrl\":\"pages/item\"}", VipWordVO.class));
        when(service.goodsWordVIP(eq("https://example.com/item"), eq("42"), any(JSONObject.class))).thenReturn(result);
        Map<String, Object> word = controller().goodsWord("https://example.com/item", "ad-123").getData();
        assertEquals("vipshop://item", word.get("link"));
        assertEquals("pages/item", word.get("wechatPath"));
        ArgumentCaptor<JSONObject> request = ArgumentCaptor.forClass(JSONObject.class);
        verify(service).goodsWordVIP(eq("https://example.com/item"), eq("42"), request.capture());
        assertEquals("42", request.getValue().getString("openId"));
        assertEquals("ad-123", request.getValue().getString("adCode"));
    }

    @Test(expected = IllegalStateException.class)
    public void missingPromotionIsNotReportedAsSuccess() {
        when(service.goodsWordVIP(anyString(), anyString(), any(JSONObject.class))).thenReturn(new VipWordCodeVO());
        controller().goodsWord("https://example.com/item", "ad");
    }
}
