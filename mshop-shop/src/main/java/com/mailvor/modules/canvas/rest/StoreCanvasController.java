/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.canvas.rest;
import java.util.Arrays;

import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.canvas.domain.StoreCanvas;
import com.mailvor.modules.canvas.service.StoreCanvasService;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.tools.domain.QiniuContent;
import com.mailvor.modules.tools.service.LocalStorageService;
import com.mailvor.modules.tools.service.QiNiuService;
import com.mailvor.modules.tools.service.dto.LocalStorageDto;
import com.mailvor.utils.RedisUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
* @author mshop
* @date 2021-02-01
*/
@AllArgsConstructor
@Api(tags = "画布管理")
@RestController
@RequestMapping("/api/canvas")
public class StoreCanvasController {

    private final StoreCanvasService storeCanvasService;
    private final LocalStorageService localStorageService;
    private final QiNiuService qiNiuService;
    private final RedisUtils redisUtils;


    @PostMapping("/saveCanvas")
    @Log("新增或修改画布")
    @ApiOperation("新增或修改画布")
    public ResponseEntity<Object> create(@Validated @RequestBody StoreCanvas resources){
        return new ResponseEntity<>(storeCanvasService.saveOrUpdate(resources),HttpStatus.CREATED);
    }


    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public ResponseEntity<Object> create(@RequestParam(defaultValue = "") String name,
                                         @RequestParam(defaultValue = "") String type,
                                         @RequestParam("file") MultipartFile file) {

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
            LocalStorageDto localStorageDTO = localStorageService.create(name, file);
            if ("".equals(url.toString())) {
                url = url.append(localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
            } else {
                url = url.append(","+localUrl + "/file/" + localStorageDTO.getType() + "/" + localStorageDTO.getRealName());
            }
        } else {//走七牛云
            QiniuContent qiniuContent = qiNiuService.upload(file, qiNiuService.find());
            if ("".equals(url.toString())) {
                url = url.append(qiniuContent.getUrl());
            }else{
                url = url.append(","+qiniuContent.getUrl());
            }
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("errno", 0);
        map.put("link", url);
        return new ResponseEntity(map, HttpStatus.CREATED);
    }


    @GetMapping("/getCanvas")
    @ApiOperation(value = "读取画布数据")
    public ResponseEntity<StoreCanvas> getCanvas(StoreCanvas storeCanvas){
        StoreCanvas canvas = storeCanvasService.lambdaQuery().eq(StoreCanvas::getTerminal, storeCanvas.getTerminal()).one();
        return new ResponseEntity<>(canvas,HttpStatus.OK);
    }

    @Log("删除画布")
    @ApiOperation("删除画布")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            storeCanvasService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
