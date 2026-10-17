package com.mailvor.modules.dataoke.rest;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsListPddParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.PddService;
import com.mailvor.modules.tk.vo.pdd.*;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.utils.RedisUtils;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkRpPromUrlGenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import static com.mailvor.modules.tk.constants.TkConstants.HOME_DATA_CATEGORY_PDD;

@RestController
@RequestMapping("/pdd")
@Slf4j
public class DataokePDDController {
    @Resource private DataokeService service;
    @Resource private PddService pddService;
    @Resource private RedisUtils redisUtils;

    @GetMapping("/goods/list")
    public ApiResult<Map<String, Object>> getGoodList(GoodsListPddParam param) {
        PddSearchListVO result = service.goodsListPdd(param);
        if (result == null || !Integer.valueOf(0).equals(result.getCode())) {
            throw new IllegalStateException(result == null ? "拼多多商品查询失败" : result.getMsg());
        }
        PddSearchDataVO source = result.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("total", source == null || source.getTotal() == null ? 0 : source.getTotal());
        data.put("minId", source == null ? null : source.getListId());
        data.put("listId", source == null ? null : source.getListId());
        data.put("searchId", source == null ? null : source.getSearchId());
        data.put("list", source == null || source.getList() == null ? Collections.emptyList() :
                source.getList().stream().map(BeanUtil::beanToMap).collect(Collectors.toList()));
        return ApiResult.ok(data);
    }

    @GetMapping("/goods/cate")
    public ApiResult<List<Map<String, Object>>> getGoodList(@RequestParam(defaultValue = "0") Integer parentId) {
        String key = HOME_DATA_CATEGORY_PDD + ":v2:" + parentId;
        JSONObject result = null;
        try {
            Object cached = redisUtils.get(key);
            if (cached instanceof JSONObject) result = (JSONObject) cached;
        } catch (RuntimeException e) {
            log.warn("拼多多分类缓存读取失败", e);
        }
        if (result == null) {
            result = service.goodsCatePdd(parentId);
            if (result != null && Integer.valueOf(0).equals(result.getInteger("code"))) {
                try { redisUtils.set(key, result, 24 * 3600); }
                catch (RuntimeException e) { log.warn("拼多多分类缓存写入失败", e); }
            }
        }
        if (result == null || !Integer.valueOf(0).equals(result.getInteger("code"))) {
            throw new IllegalStateException(result == null ? "拼多多分类查询失败" : result.getString("msg"));
        }
        PddCateDataVO categories = result.toJavaObject(PddCateDataVO.class);
        List<PddCateVO> list = categories.getData() == null ? Collections.emptyList() : categories.getData();
        return ApiResult.ok(list.stream().sorted(Comparator.comparing(PddCateVO::getCid,
                Comparator.nullsLast(Comparator.naturalOrder())))
                .map(BeanUtil::beanToMap).collect(Collectors.toList()));
    }

    @GetMapping("/goods/detail")
    public ApiResult<PddGoodsDetailVO> getGoodsDetail(@RequestParam String goodsId) {
        return ApiResult.ok(pddService.getGoodsDetail(goodsId));
    }

    @AuthCheck
    @GetMapping("/auth/query")
    public ApiResult<Integer> authQuery() {
        return ApiResult.ok(pddService.authQuery(LocalUser.getUser().getUid()));
    }

    @AuthCheck
    @GetMapping("/auth")
    public ApiResult<List<PddDdkRpPromUrlGenerateResponse.RpPromotionUrlGenerateResponseUrlListItem>> auth() {
        return ApiResult.ok(pddService.auth(LocalUser.getUser().getUid()));
    }

    @UserCheck
    @GetMapping("/goods/word")
    public ApiResult<Map<String, Object>> goodsWord(@RequestParam String goodsId) {
        return wordResult(pddService.getGoodsWordById(currentUid(), goodsId));
    }

    @UserCheck
    @GetMapping("/super-red-package")
    public ApiResult<Map<String, Object>> superRedPackageLink(@RequestParam(required = false) String goodsSign) {
        return wordResult(pddService.getSuperRedPackageLink(currentUid(), goodsSign));
    }

    private Long currentUid() {
        MwUser user = LocalUser.getUser();
        return user == null ? null : user.getUid();
    }

    private ApiResult<Map<String, Object>> wordResult(PddWordVO word) {
        if (word == null || word.getLink() == null || word.getLink().isEmpty()) {
            throw new IllegalStateException("拼多多转链失败，请稍后重试");
        }
        return ApiResult.ok(BeanUtil.beanToMap(word));
    }
}
