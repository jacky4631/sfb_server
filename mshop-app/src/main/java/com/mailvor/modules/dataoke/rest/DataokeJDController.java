package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsJdWordParam;
import com.mailvor.modules.tk.param.GoodsListJDParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.user.domain.MwUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

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

    @GetMapping(value = "/goods/list")
    public JSONObject getGoodList(GoodsListJDParam param) {

        return service.goodsListJD(param);
    }

    @GetMapping(value = "/goods/detail")
    public JSONObject getGoodsDetail(@RequestParam(required = false) String goodsId,
                                     @RequestParam(required = false) String itemId) {

        return service.goodsDetailJD(goodsId, itemId);
    }
//
//    @GetMapping(value = "/goods/comment/list")
//    public JSONObject getCommentList(GoodsCommentParam param) {
//
//        return service.getCommentList(param);
//    }
//

    @UserCheck
    @GetMapping(value = "/goods/word")
    public ApiResult<String> goodsWord(@Valid GoodsJdWordParam param) {
        String positionId;

        MwUser user = LocalUser.getUser();
        if(user != null) {
            positionId = user.getUid().toString();
        } else {
            positionId = "0";
        }

        log.debug("jd parse positionId {}", positionId);
        JSONObject daRes = service.goodsWordJD(param.getMaterialUrl(), null, positionId);

        String url = "";
        if(daRes != null) {
            try {
                url = daRes.getJSONObject("data").getString("shortUrl");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ApiResult.ok(url);

    }

    @GetMapping(value = "/brand/list")
    public JSONObject getBrandList(GoodsListJDParam param) {

        return service.brandListJD(param);
    }
    @GetMapping(value = "/nines/list")
    public JSONObject getNinesList(GoodsListJDParam param) {

        return service.ninesListJD(param);
    }
    @GetMapping(value = "/rank/list")
    public JSONObject getRankList(GoodsListJDParam param) {

        return service.rankListJD(param);
    }
}
