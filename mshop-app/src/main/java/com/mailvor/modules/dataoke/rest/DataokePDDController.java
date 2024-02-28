package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsListPddParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.PddService;
import com.mailvor.modules.user.domain.MwUser;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkRpPromUrlGenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/pdd")
@Slf4j
public class DataokePDDController {
    @Resource
    private DataokeService service;

    @Resource
    private PddService pddService;

    @GetMapping(value = "/goods/list")
    public JSONObject getGoodList(GoodsListPddParam goodQueryParam) {

        return service.goodsListPdd(goodQueryParam);
    }
    @GetMapping(value = "/goods/cate")
    public JSONObject getGoodList(Integer parentId) {

        return service.goodsCatePdd(parentId);
    }
    @GetMapping(value = "/goods/detail")
    public JSONObject getGoodsDetail(String goodsSign) {

        return service.goodsDetailPDD(goodsSign);
    }
    @AuthCheck
    @GetMapping(value = "/auth/query")
    public ApiResult authQuery() {
        int auth = pddService.authQuery(LocalUser.getUser().getUid());
        return ApiResult.ok(auth);
    }
    @AuthCheck
    @GetMapping(value = "/auth")
    public ApiResult auth() {
        List<PddDdkRpPromUrlGenerateResponse.RpPromotionUrlGenerateResponseUrlListItem> urlList = pddService.auth(LocalUser.getUser().getUid());
        return ApiResult.ok(urlList);
    }
    @UserCheck
    @GetMapping(value = "/goods/word")
    public JSONObject goodsWord(String goodsSign, @RequestParam(required = false) Long uid) {
        if(uid == null) {
            MwUser user = LocalUser.getUser();
            uid = user == null ? null : user.getUid();
        }

        return service.goodsWordPDD(goodsSign, uid);
    }
}
