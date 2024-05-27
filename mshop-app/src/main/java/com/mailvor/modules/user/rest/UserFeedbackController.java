/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.feedback.domain.MwUserFeedback;
import com.mailvor.modules.feedback.service.MwUserFeedbackService;
import com.mailvor.modules.user.param.UserFeedbackParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @author wangjun
* @date 2024-05-27
*/
@RestController
public class UserFeedbackController {

    @Resource
    private MwUserFeedbackService userFeedbackService;

    @AuthCheck
    @PostMapping(path = "/user/feedback/add")
    @ApiOperation("新增用户反馈")
    public ApiResult<Object> create(@Validated @RequestBody UserFeedbackParam param){
        MwUserFeedback userFeedback = new MwUserFeedback();
        userFeedback.setFeedback(param.getFeedback());
        userFeedback.setUid(LocalUser.getUser().getUid());
        userFeedbackService.save(userFeedback);
        return ApiResult.ok("成功");
    }

}
