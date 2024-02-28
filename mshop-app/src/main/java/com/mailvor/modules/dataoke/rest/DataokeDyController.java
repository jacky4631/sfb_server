package com.mailvor.modules.dataoke.rest;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsSearchDyParam;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dy")
@Slf4j
public class DataokeDyController {
    @Resource
    private DataokeService service;


    @Resource
    private MwUserService userService;
    @GetMapping(value = "/goods/search")
    public JSONObject getGoodsSearch(GoodsSearchDyParam goodQueryParam) {

        return service.dyGoodsSearch(goodQueryParam);
    }
    @GetMapping(value = "/goods/detail")
    public JSONObject getGoodsDetail(String goodsId) {

        return service.dyGoodsDetail(goodsId);
    }

    @UserCheck
    @GetMapping(value = "/word")
    public JSONObject dyWord(String productUrl, @RequestParam(required = false) Long uid) {
        MwUser mwUser;
        if(uid != null && uid > 0) {
            mwUser = userService.getById(uid);
        } else {
            mwUser = LocalUser.getUser();
        }
        String externalInfo = "0";
        if(mwUser != null) {
            externalInfo = mwUser.getUid().toString();
        }
        return service.dyWord(productUrl, externalInfo);
    }
}
