package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsListVipParam;
import com.mailvor.modules.tk.param.GoodsSearchVipParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.utils.TkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/vip")
@Slf4j
public class DataokeVipController {
    @Resource
    private DataokeService service;

    @UserCheck
    @GetMapping(value = "/goods/list")
    public JSONObject getGoodList(GoodsListVipParam param) {
        MwUser mwUser = LocalUser.getUser();
        //获取pid为了实现自动追单
        if(mwUser != null) {
            param.setOpenId(mwUser.getUid().toString());
        } else {
            param.setOpenId("0");
        }

        return service.goodsListVip(param);
    }

    @UserCheck
    @GetMapping(value = "/goods/search")
    public JSONObject getGoodsSearch(GoodsSearchVipParam param) {
        MwUser mwUser = LocalUser.getUser();
        //获取pid为了实现自动追单
        if(mwUser != null) {
            param.setOpenId(mwUser.getUid().toString());
        } else {
            param.setOpenId("0");
        }
        return service.vipGoodsSearch(param);
    }

    @UserCheck
    @GetMapping(value = "/goods/detail")
    public JSONObject getGoodsDetail(String goodsId) {
        MwUser mwUser = LocalUser.getUser();
        return service.goodsDetailVIP(goodsId, TkUtil.getVipOpenId(mwUser));
    }
    @UserCheck
    @GetMapping(value = "/goods/word")
    public JSONObject goodsWord(String itemUrl, String statParam, String adCode) {
        MwUser mwUser = LocalUser.getUser();
        //获取pid为了实现自动追单
        if(mwUser != null) {
            statParam = mwUser.getUid().toString();
        }

        return service.goodsWordVIP(itemUrl, statParam, TkUtil.getVipGenRequest(TkUtil.getVipOpenId(mwUser), adCode));
    }
}
