/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.rest;

import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.tools.service.AliOssService;
import com.mailvor.modules.tools.service.dto.LocalStorageDto;
import com.mailvor.modules.tools.service.LocalStorageService;
import com.mailvor.modules.tools.service.QiNiuService;
import com.mailvor.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangyu
 * @date 2020-01-09
 */
@Api(tags = "工具：上传文件管理")
@RestController
@RequestMapping("/api/upload")
@Slf4j
@SuppressWarnings("unchecked")
public class UploadController {
    @Resource
    private LocalStorageService localStorageService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private AliOssService ossService;

    public UploadController(LocalStorageService localStorageService, QiNiuService qiNiuService,
                            RedisUtils redisUtils) {
        this.localStorageService = localStorageService;
        this.redisUtils = redisUtils;
    }


    @ApiOperation("上传文件")
    @PostMapping
    public ResponseEntity<Object> create(@RequestParam(defaultValue = "") String name,
                                         @RequestParam(defaultValue = "") String type,
                                         @RequestParam("file") MultipartFile[] files) {

        String localUrl = redisUtils.getY(ShopConstants.ADMIN_API_URL);
        if(StrUtil.isBlank(type)){
            localUrl = redisUtils.getY(SystemConfigConstants.API_URL) + "/api";
        }
        String mode = redisUtils.getY(SystemConfigConstants.FILE_STORE_MODE);
        StringBuilder url = new StringBuilder();
        if (ShopCommonEnum.STORE_MODE_1.getValue().toString().equals(mode)) { //存在走本地
            if(StrUtil.isBlank(localUrl)){
                throw new MshopException("本地上传,请先登陆系统配置后台/移动端API地址");
            }
            for (MultipartFile file : files) {
                LocalStorageDto localStorageDTO = localStorageService.create(name, file);
                if ("".equals(url.toString())) {
                    url = url.append(localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                } else {
                    url = url.append(","+localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
                }
            }
        } else {//走阿里云oss

            List<String> paths = ossService.uploadImages(files);
            url = new StringBuilder(String.join(",", paths));
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("errno", 0);
        map.put("link", url);
        return new ResponseEntity(map, HttpStatus.CREATED);
    }


}
