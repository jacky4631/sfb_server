/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest;

import cn.hutool.core.util.ObjectUtil;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.mp.domain.MwWechatReply;
import com.mailvor.modules.mp.service.MwWechatReplyService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author huangyu
* @date 2019-10-10
*/
@Api(tags = "微信：微信回复管理")
@RestController
@RequestMapping("api")
public class WechatReplyController {

    private final MwWechatReplyService mwWechatReplyService;

    public WechatReplyController(MwWechatReplyService mwWechatReplyService) {
        this.mwWechatReplyService = mwWechatReplyService;
    }

    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwWechatReply")
    @PreAuthorize("@el.check('admin','MWWECHATREPLY_ALL','MWWECHATREPLY_SELECT')")
    public ResponseEntity getMwWechatReplys(){
        return new ResponseEntity<>(mwWechatReplyService.isExist("subscribe"),HttpStatus.OK);
    }


    @ForbidSubmit
    @ApiOperation(value = "新增自动回复")
    @PostMapping(value = "/mwWechatReply")
    @PreAuthorize("@el.check('admin','MWWECHATREPLY_ALL','MWWECHATREPLY_CREATE')")
    public ResponseEntity create(@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        MwWechatReply mwWechatReply = new MwWechatReply();
        MwWechatReply isExist = mwWechatReplyService.isExist(jsonObject.get("key").toString());
        mwWechatReply.setKey(jsonObject.get("key").toString());
        mwWechatReply.setStatus(Integer.valueOf(jsonObject.get("status").toString()));
        mwWechatReply.setData(jsonObject.get("data").toString());
        mwWechatReply.setType(jsonObject.get("type").toString());
        if(ObjectUtil.isNull(isExist)){
            mwWechatReplyService.create(mwWechatReply);
        }else{
            mwWechatReply.setId(isExist.getId());
            mwWechatReplyService.upDate(mwWechatReply);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }





}
