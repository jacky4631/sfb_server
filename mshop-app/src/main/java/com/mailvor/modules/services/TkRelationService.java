/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.services;

import com.alibaba.fastjson.JSONObject;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.modules.product.service.MwStoreProductRelationService;
import com.mailvor.modules.tk.service.DataokeService;
import com.mailvor.modules.user.domain.MwUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @ClassName 足迹保存
 * @author huangyu
 * @Date 2020/6/14
 **/
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TkRelationService {

    private final DataokeService service;
    private final MwStoreProductRelationService relationService;

    @Async
    public void saveJdRelation(String itemUrl) {
        MwUser user = LocalUser.getUser();
        if(user != null) {
            JSONObject parseUrl = service.parseUrlJD(itemUrl);
            if(parseUrl.getInteger("code") == 0) {
                relationService.saveOrUpdateRelation(parseUrl.getJSONObject("data").getString("skuId"), user.getUid(), "jd");

            }
        }
    }

}
