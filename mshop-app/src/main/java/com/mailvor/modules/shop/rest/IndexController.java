/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ClassPathResource;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.ProductEnum;
import com.mailvor.modules.activity.service.MwStoreCombinationService;
import com.mailvor.modules.activity.service.MwStoreSeckillService;
import com.mailvor.modules.canvas.domain.StoreCanvas;
import com.mailvor.modules.canvas.service.StoreCanvasService;
import com.mailvor.modules.mp.service.MwWechatLiveService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.vo.MwSystemStoreQueryVo;
import com.mailvor.modules.shop.param.MwSystemStoreQueryParam;
import com.mailvor.modules.shop.service.MwAppVersionService;
import com.mailvor.modules.shop.service.MwSystemGroupDataService;
import com.mailvor.modules.shop.service.MwSystemStoreService;
import com.mailvor.modules.shop.vo.IndexVo;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.ShopKeyUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName IndexController
 * @author huangyu
 * @Date 2019/10/19
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "首页模块", tags = "商城:首页模块")
public class IndexController {
    private final MwAppVersionService appVersionService;
    private final MwSystemGroupDataService systemGroupDataService;
    private final MwStoreProductService storeProductService;
    private final MwSystemStoreService systemStoreService;
    private final MwStoreCombinationService storeCombinationService;
    private final MwStoreSeckillService storeSeckillService;
    private final MwWechatLiveService wechatLiveService;

    private final StoreCanvasService storeCanvasService;

    @GetMapping("/getCanvas")
    @ApiOperation(value = "读取画布数据")
    public ResponseEntity<StoreCanvas> getCanvas(StoreCanvas storeCanvas){
        StoreCanvas canvas = storeCanvasService.getOne(new LambdaQueryWrapper<StoreCanvas>()
                .eq(StoreCanvas::getTerminal, storeCanvas.getTerminal())
                .orderByDesc(StoreCanvas::getCanvasId).last("limit 1"));
        return new ResponseEntity<>(canvas, HttpStatus.OK);
    }

    @Cacheable(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY)
    @GetMapping("/index")
    @ApiOperation(value = "首页数据",notes = "首页数据")
    public ApiResult<IndexVo> index(){
        IndexVo indexVo = IndexVo.builder()
                .banner(systemGroupDataService.getDatas(ShopConstants.MSHOP_HOME_BANNER))
                .bastList(storeProductService.getList(1,6, ProductEnum.TYPE_1.getValue()))
                .benefit(storeProductService.getList(1,10,ProductEnum.TYPE_4.getValue()))
                .combinationList(storeCombinationService.getList(1,8).getStoreCombinationQueryVos())
                .firstList(storeProductService.getList(1,6,ProductEnum.TYPE_3.getValue()))
                .likeInfo(storeProductService.getList(1,8,ProductEnum.TYPE_2.getValue()))
                .mapKey(RedisUtil.get(ShopKeyUtils.getTengXunMapKey()))
                .menus(systemGroupDataService.getDatas(ShopConstants.MSHOP_HOME_MENUS))
                .roll(systemGroupDataService.getDatas(ShopConstants.MSHOP_HOME_ROLL_NEWS))
                .seckillList(storeSeckillService.getList(1, 4))
                .liveList(wechatLiveService.getList(1,4,0))
                .build();
        return ApiResult.ok(indexVo);
    }

    @GetMapping("/search/keyword")
    @ApiOperation(value = "热门搜索关键字获取",notes = "热门搜索关键字获取")
    public ApiResult<List<String>> search(){
        List<JSONObject> list = systemGroupDataService.getDatas(ShopConstants.MSHOP_HOT_SEARCH);
        List<String>  stringList = new ArrayList<>();
        for (JSONObject object : list) {
            stringList.add(object.getString("title"));
        }
        return ApiResult.ok(stringList);
    }


    @PostMapping("/image_base64")
    @ApiOperation(value = "获取图片base64",notes = "获取图片base64")
    @Deprecated
    public ApiResult<List<String>> imageBase64(){
        return ApiResult.ok(null);
    }


    @GetMapping("/citys")
    @ApiOperation(value = "获取城市json",notes = "获取城市json")
    public ApiResult<JSONObject> cityJson(){
        String path = "city.json";
        String name = "city.json";
        try {
            File file = FileUtil.inputStreamToFile(new ClassPathResource(path).getStream(), name);
            FileReader fileReader = new FileReader(file,"UTF-8");
            String string = fileReader.readString();
            JSONObject jsonObject = JSON.parseObject(string);
            return ApiResult.ok(jsonObject);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MshopException("无数据");
        }

    }


    @GetMapping("/store_list")
    @ApiOperation(value = "获取门店列表",notes = "获取门店列表")
    public ApiResult<Map<String,Object>> storeList( MwSystemStoreQueryParam param){
        Map<String,Object> map = new LinkedHashMap<>();
        List<MwSystemStoreQueryVo> lists = systemStoreService.getStoreList(
                param.getLatitude(),
                param.getLongitude(),
                param.getPage(),param.getLimit());
        map.put("list",lists);
        return ApiResult.ok(map);
    }

}
