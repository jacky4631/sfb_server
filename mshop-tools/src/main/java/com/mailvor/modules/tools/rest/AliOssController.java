/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.rest;

import com.mailvor.modules.tools.service.AliOssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 发送邮件
 * @author 郑杰
 * @date 2018/09/28 6:55:53
 */
@Slf4j
@RestController
@RequestMapping("/api/alioss")
@Api(tags = "阿里OSS存储管理")
public class AliOssController {

    @Resource
    private AliOssService ossService;

    @ApiOperation("上传文件")
    @PostMapping(value = "/upload")
    public ResponseEntity<Object> upload(@RequestParam MultipartFile file){
        String url = ossService.uploadFileAvatar(file);
        return new ResponseEntity<>(url,HttpStatus.OK);
    }

}
