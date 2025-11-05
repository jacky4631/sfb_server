package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsJdWordParam;
import com.mailvor.modules.tk.param.jd.GoodsListJDParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.tk.service.JdService;
import com.mailvor.modules.tk.vo.jd.JdUnionCommonGoodsListVO;
import com.mailvor.modules.tk.vo.jd.JdUnionCommonGoodsWordVO;
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

    @Resource
    private JdService jdService;

    @GetMapping(value = "/goods/detail")
    public JSONObject getGoodsDetail(@RequestParam(required = false) String goodsId,
                                     @RequestParam(required = false) String itemId) {

        return service.goodsDetailJD(goodsId, itemId);
    }

    @UserCheck
    @GetMapping(value = "/goods/word")
    public ApiResult<JdUnionCommonGoodsWordVO> goodsWord2(@Valid GoodsJdWordParam param) {
        String positionId;

        MwUser user = LocalUser.getUser();
        if(user != null) {
            positionId = user.getUid().toString();
        } else {
            positionId = "0";
        }
        JdUnionCommonGoodsWordVO daRes = jdService.goodsWord(param.getGoodsId(), param.getCouponLink(), positionId);

        return ApiResult.ok(daRes);

    }
    @GetMapping(value = "/rank/list")
    public JdUnionCommonGoodsListVO getRankList(GoodsListJDParam param) throws Exception {
        return jdService.listRank(param);
    }
}
