package com.mailvor.modules.dataoke.rest;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsListVipParam;
import com.mailvor.modules.tk.param.GoodsSearchVipParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.vo.vip.*;
import com.mailvor.modules.utils.TkUtil;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vip")
public class DataokeVipController {
    @Resource private DataokeService service;

    @UserCheck
    @GetMapping("/goods/list")
    public ApiResult<Map<String, Object>> getGoodList(GoodsListVipParam param) {
        param.setOpenId(TkUtil.getVipOpenId(LocalUser.getUser()));
        return productList(service.goodsListVip(param));
    }

    @UserCheck
    @GetMapping("/goods/search")
    public ApiResult<Map<String, Object>> getGoodsSearch(GoodsSearchVipParam param) {
        return postGoodsSearch(param);
    }

    // JSON request bodies preserve keywords containing %, +, &, or non-ASCII text.
    @UserCheck
    @PostMapping("/goods/search")
    public ApiResult<Map<String, Object>> postGoodsSearch(@RequestBody GoodsSearchVipParam param) {
        param.setOpenId(TkUtil.getVipOpenId(LocalUser.getUser()));
        JSONObject response = service.vipGoodsSearch(param);
        return productList(response == null ? null : response.toJavaObject(VipSearchListVO.class));
    }

    private ApiResult<Map<String, Object>> productList(VipSearchListVO result) {
        if (result == null || !Integer.valueOf(0).equals(result.getCode())) {
            throw new IllegalStateException(result == null ? "唯品会商品查询失败" : result.getMsg());
        }
        VipSearchDataVO source = result.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("total", source == null || source.getTotal() == null ? 0 : source.getTotal());
        data.put("list", source == null || source.getList() == null ? Collections.emptyList() :
                source.getList().stream().map(BeanUtil::beanToMap).collect(Collectors.toList()));
        return ApiResult.ok(data);
    }

    @UserCheck
    @GetMapping("/goods/detail")
    public ApiResult<Map<String, Object>> getGoodsDetail(@RequestParam String goodsId) {
        VipGoodsDetailDataVo response = service.goodsDetailVIP(goodsId, TkUtil.getVipOpenId(LocalUser.getUser()));
        if (response == null || !Integer.valueOf(0).equals(response.getCode())) {
            throw new IllegalStateException(response == null ? "唯品会商品查询失败" : response.getMsg());
        }
        VipGoodsDetailVO goods = response.getGoods();
        // Also tolerate provider responses not yet reduced to a single product.
        if (goods == null && response.getData() != null && !response.getData().isEmpty()) {
            goods = response.getData().get(0);
        }
        return ApiResult.ok(goods == null ? Collections.emptyMap() : BeanUtil.beanToMap(goods));
    }

    @UserCheck
    @GetMapping("/goods/word")
    public ApiResult<Map<String, Object>> goodsWord(@RequestParam String goodsId,
                                                   @RequestParam(required = false) String adCode) {
        String uid = TkUtil.getVipOpenId(LocalUser.getUser());
        VipWordCodeVO result = service.goodsWordVIP(goodsId, uid, TkUtil.getVipGenRequest(uid, adCode));
        if (result == null || !Integer.valueOf(0).equals(result.getCode()) || result.getWord() == null) {
            throw new IllegalStateException(result == null ? "唯品会转链失败" : result.getMsg());
        }
        VipWordVO word = result.getWord();
        if ((word.getLink() == null || word.getLink().isEmpty()) &&
                (word.getLongLink() == null || word.getLongLink().isEmpty())) {
            throw new IllegalStateException("唯品会转链失败，请稍后重试");
        }
        return ApiResult.ok(BeanUtil.beanToMap(word));
    }
}
