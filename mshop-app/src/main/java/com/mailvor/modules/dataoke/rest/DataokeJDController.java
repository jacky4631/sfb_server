package com.mailvor.modules.dataoke.rest;

import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.service.JdProductFetcher;
import com.mailvor.modules.tk.vo.jd.*;
import cn.hutool.core.bean.BeanUtil;
import java.util.*;
import java.util.stream.Collectors;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsJdWordParam;
import com.mailvor.modules.tk.param.jd.GoodsListJDParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.JdService;
import com.mailvor.modules.user.domain.MwUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.mailvor.utils.JdAttribution.POSITION_BASE;

/**
 *
 * @author shenji
 * @date 2023/3/20
 */
@RestController
@RequestMapping("/jd")
@Slf4j
public class DataokeJDController {
    @Resource
    private DataokeService service;

    @Resource
    private JdService jdService;

    @Resource
    private KuService kuService;

    @GetMapping("/goods/list")
    public ApiResult<Map<String, Object>> goodsList(GoodsListJDParam param) {
        JdKuSearchListVO result = kuService.searchJD(param);
        if (result == null || result.getData() == null || result.getData().isEmpty()) {
            String title = JdProductFetcher.getTitle(param.getKeyword());
            if (title != null && !title.isEmpty()) {
                param.setKeyword(title.substring(0, Math.min(15, title.length())));
                result = kuService.searchJD(param);
            }
        }
        if (result == null || !Integer.valueOf(200).equals(result.getCode())) {
            throw new IllegalStateException(result == null ? "京东商品查询失败" : result.getMsg());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("minId", result.getMinId());
        data.put("list", result.getData() == null ? Collections.emptyList() : result.getData().stream()
                .map(JdKuCommonGoodsDetailVO::convert).map(this::goodsMap).collect(Collectors.toList()));
        return ApiResult.ok(data);
    }

    private Map<String, Object> goodsMap(JdKuCommonGoodsDetailVO goods) {
        Map<String, Object> map = BeanUtil.beanToMap(goods);
        map.put("detailImages", goods.getDetails());
        return map;
    }

    @GetMapping("/goods/detail")
    public ApiResult<Map<String, Object>> getGoodsDetail(@RequestParam String goodsId) {
        GoodsListJDParam param = new GoodsListJDParam();
        param.setKeyword(goodsId);
        param.setPageId(1);
        param.setPageSize(1);
        JdKuCommonGoodsDetailDataVo result = kuService.detailJD(param);
        if (result == null || !Integer.valueOf(200).equals(result.getCode())) {
            throw new IllegalStateException(result == null ? "京东商品查询失败" : result.getMsg());
        }
        return ApiResult.ok(result.getData() == null ? Collections.emptyMap() : goodsMap(result.getData()));
    }

    @GetMapping("/brand/list")
    public ApiResult<Map<String, Object>> brandList(GoodsListJDParam param) {
        JdGoodsListVO result = service.brandListJD(param);
        return productList(result);
    }

    @GetMapping("/nines/list")
    public ApiResult<Map<String, Object>> ninesList(GoodsListJDParam param) {
        JdGoodsListVO result = service.ninesListJD(param);
        return productList(result);
    }

    private ApiResult<Map<String, Object>> productList(JdGoodsListVO result) {
        if (result == null || !Integer.valueOf(0).equals(result.getCode())) {
            throw new IllegalStateException(result == null ? "京东商品查询失败" : result.getMsg());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getData() == null ? 0 : result.getData().getTotal());
        data.put("list", result.getData() == null || result.getData().getList() == null ? Collections.emptyList() :
                result.getData().getList().stream().map(BeanUtil::beanToMap).collect(Collectors.toList()));
        return ApiResult.ok(data);
    }

    @GetMapping("/rank/cate")
    public ApiResult<List<Map<String, Object>>> rankCate() {
        String[] titles = {"全部", "食品酒水", "家庭清洁", "个护美妆", "医药保健", "生鲜", "数码家电", "家居日用", "时尚生活"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> cate = new HashMap<>();
            cate.put("rankId", 200000L + i);
            cate.put("title", titles[i]);
            result.add(cate);
        }
        return ApiResult.ok(result);
    }

    @UserCheck
    @GetMapping(value = "/goods/word")
    public ApiResult<JdUnionCommonGoodsWordVO> goodsWord2(@Valid GoodsJdWordParam param) {
        Long positionId;

        MwUser user = LocalUser.getUser();
        if(user != null) {
            positionId = user.getUid();
        } else {
            positionId = POSITION_BASE;
        }
        JdUnionCommonGoodsWordVO daRes = jdService.goodsWord(param.getGoodsId(), param.getCouponLink(), positionId);

        return ApiResult.ok(daRes);

    }
    @GetMapping(value = "/rank/list")
    public ApiResult<Map<String, Object>> getRankList(GoodsListJDParam param) {
        JdUnionCommonGoodsListVO result = jdService.listRank(param);
        if (result == null || !Integer.valueOf(200).equals(result.getCode())) {
            throw new IllegalStateException(result == null ? "京东榜单查询失败" : result.getMsg());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getData().getTotal());
        data.put("list", result.getData().getList() == null ? Collections.emptyList() :
                result.getData().getList().stream().map(BeanUtil::beanToMap).collect(Collectors.toList()));
        return ApiResult.ok(data);
    }
}
