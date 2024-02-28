/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest.controller;

import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.mp.service.MwWechatLiveService;
import com.mailvor.modules.mp.service.dto.MwWechatLiveQueryCriteria;
import com.mailvor.modules.mp.vo.WechatLiveVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @author huangyu
* @date 2020-08-10
*/
@AllArgsConstructor
@Api(tags = "wxlive管理")
@RestController
@RequestMapping
public class WechatLiveController {

    private final MwWechatLiveService mwWechatLiveService;


    @GetMapping("mwWechatLive")
    @ApiOperation("查询所有直播间")
    public ApiResult<WechatLiveVo> getmwWechatLives(MwWechatLiveQueryCriteria criteria, Pageable pageable){
        return ApiResult.ok(mwWechatLiveService.queryAll(criteria,pageable));
    }
    @GetMapping("mwWechatLive/getLiveReplay/{id}")
    @ApiOperation("获取直播回放")
    public ApiResult<List<WxMaLiveResult.LiveReplay>>  getLiveReplay(@PathVariable Integer id){
        return ApiResult.ok(mwWechatLiveService.getLiveReplay(id));
    }
}
