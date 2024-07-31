/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.SpecTypeEnum;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.domain.MwStoreSeckill;
import com.mailvor.modules.activity.service.MwStoreSeckillService;
import com.mailvor.modules.activity.service.dto.MwStoreSeckillDto;
import com.mailvor.modules.activity.service.dto.MwStoreSeckillQueryCriteria;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.product.domain.MwStoreProductAttrResult;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.service.MwStoreProductAttrResultService;
import com.mailvor.modules.product.service.MwStoreProductAttrValueService;
import com.mailvor.modules.product.service.MwStoreProductRuleService;
import com.mailvor.modules.product.service.dto.ProductFormatDto;
import com.mailvor.modules.template.domain.MwShippingTemplates;
import com.mailvor.modules.template.service.MwShippingTemplatesService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangyu
 * @date 2019-12-14
 */
@Api(tags = "商城：秒杀管理")
@RestController
@RequestMapping("api")
public class StoreSeckillController {

    private final IGenerator generator;
    private final MwStoreSeckillService mwStoreSeckillService;
    private final MwShippingTemplatesService mwShippingTemplatesService;
    private final MwStoreProductRuleService mwStoreProductRuleService;
    private final MwStoreProductAttrValueService storeProductAttrValueService;
    private final MwStoreProductAttrResultService mwStoreProductAttrResultService;

    public StoreSeckillController(IGenerator generator, MwStoreSeckillService mwStoreSeckillService, MwShippingTemplatesService mwShippingTemplatesService,
                                  MwStoreProductRuleService mwStoreProductRuleService, MwStoreProductAttrValueService storeProductAttrValueService,
                                  MwStoreProductAttrResultService mwStoreProductAttrResultService) {
        this.generator = generator;
        this.mwStoreSeckillService = mwStoreSeckillService;
        this.mwShippingTemplatesService = mwShippingTemplatesService;
        this.mwStoreProductRuleService = mwStoreProductRuleService;
        this.storeProductAttrValueService = storeProductAttrValueService;
        this.mwStoreProductAttrResultService = mwStoreProductAttrResultService;
    }

    @Log("列表")
    @ApiOperation(value = "列表")
    @GetMapping(value = "/mwStoreSeckill")
    @PreAuthorize("hasAnyRole('admin','MWSTORESECKILL_ALL','MWSTORESECKILL_SELECT')")
    public ResponseEntity getMwStoreSeckills(MwStoreSeckillQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(mwStoreSeckillService.queryAll(criteria, pageable), HttpStatus.OK);
    }


    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @Log("发布")
    @ApiOperation(value = "发布")
    @PutMapping(value = "/mwStoreSeckill")
    @PreAuthorize("hasAnyRole('admin','MWSTORESECKILL_ALL','MWSTORESECKILL_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreSeckill resources){
        if(ObjectUtil.isNull(resources.getId())){
            return new ResponseEntity<>(mwStoreSeckillService.save(resources),HttpStatus.CREATED);
        }else{
            mwStoreSeckillService.saveOrUpdate(resources);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @ForbidSubmit
    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwStoreSeckill/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTORESECKILL_ALL','MWSTORESECKILL_DELETE')")
    public ResponseEntity delete(@PathVariable Long id) {
        mwStoreSeckillService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY, allEntries = true)
    @Log("新增秒杀")
    @ApiOperation(value = "新增秒杀")
    @PostMapping(value = "/mwStoreSeckill")
    @PreAuthorize("hasAnyRole('admin','MWSTORESECKILL_ALL','MWSTORESECKILL_EDIT')")
    public ResponseEntity add(@Validated @RequestBody MwStoreSeckillDto resources) {
        return new ResponseEntity<>(mwStoreSeckillService.saveSeckill(resources), HttpStatus.CREATED);
    }

    @ApiOperation(value = "获取商品信息")
    @GetMapping(value = "/mwStoreSecKill/info/{id}")
    public ResponseEntity info(@PathVariable Long id) {
        Map<String, Object> map = new LinkedHashMap<>(3);

        //运费模板
        List<MwShippingTemplates> shippingTemplatesList = mwShippingTemplatesService.list();
        map.put("tempList", shippingTemplatesList);

        //商品规格
        map.put("ruleList", mwStoreProductRuleService.list());


        if (id == 0) {
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        //处理商品详情
        MwStoreSeckill mwStoreSeckill = mwStoreSeckillService.getById(id);
        MwStoreSeckillDto productDto = new MwStoreSeckillDto();
        BeanUtil.copyProperties(mwStoreSeckill, productDto, "images");
        productDto.setSliderImage(Arrays.asList(mwStoreSeckill.getImages().split(",")));
        MwStoreProductAttrResult storeProductAttrResult = mwStoreProductAttrResultService
                .getOne(Wrappers.<MwStoreProductAttrResult>lambdaQuery()
                        .eq(MwStoreProductAttrResult::getProductId, mwStoreSeckill.getProductId()).last("limit 1"));
        JSONObject result = JSON.parseObject(storeProductAttrResult.getResult());

        List<MwStoreProductAttrValue> attrValues = storeProductAttrValueService.list(new LambdaQueryWrapper<MwStoreProductAttrValue>().eq(MwStoreProductAttrValue::getProductId, mwStoreSeckill.getProductId()));
        List<ProductFormatDto> productFormatDtos = attrValues.stream().map(i -> {
            ProductFormatDto productFormatDto = new ProductFormatDto();
            BeanUtils.copyProperties(i, productFormatDto);
            productFormatDto.setPic(i.getImage());
            return productFormatDto;
        }).collect(Collectors.toList());
        if (SpecTypeEnum.TYPE_1.getValue().equals(mwStoreSeckill.getSpecType())) {
            productDto.setAttr(new ProductFormatDto());
            productDto.setAttrs(productFormatDtos);
            productDto.setItems(result.getObject("attr", ArrayList.class));
        } else {
            productFormat(productDto, result);
        }


        map.put("productInfo", productDto);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * 获取商品属性
     * @param productDto
     * @param result
     */
    private void productFormat(MwStoreSeckillDto productDto, JSONObject result) {
        Map<String, Object> mapAttr = (Map<String, Object>) result.getObject("value", ArrayList.class).get(0);
        ProductFormatDto productFormatDto = ProductFormatDto.builder()
                .pic(mapAttr.get("pic").toString())
                .price(Double.valueOf(mapAttr.get("price").toString()))
                .cost(Double.valueOf(mapAttr.get("cost").toString()))
                .otPrice(Double.valueOf(mapAttr.get("otPrice").toString()))
                .stock(Integer.valueOf(mapAttr.get("stock").toString()))
                .barCode(mapAttr.get("barCode").toString())
                .weight(Double.valueOf(mapAttr.get("weight").toString()))
                .volume(Double.valueOf(mapAttr.get("volume").toString()))
                .value1(mapAttr.get("value1").toString())
                .brokerage(Double.valueOf(mapAttr.get("brokerage").toString()))
                .brokerageTwo(Double.valueOf(mapAttr.get("brokerageTwo").toString()))
                .pinkPrice(Double.valueOf(mapAttr.get("pinkPrice").toString()))
                .pinkStock(Integer.valueOf(mapAttr.get("pinkStock").toString()))
                .seckillPrice(Double.valueOf(mapAttr.get("seckillPrice").toString()))
                .seckillStock(Integer.valueOf(mapAttr.get("seckillStock").toString()))
                .build();
        productDto.setAttr(productFormatDto);
    }
}
