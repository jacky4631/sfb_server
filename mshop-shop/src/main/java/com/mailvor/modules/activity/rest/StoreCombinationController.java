/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.SpecTypeEnum;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.domain.MwStoreCombination;
import com.mailvor.modules.activity.service.MwStoreCombinationService;
import com.mailvor.modules.activity.service.dto.MwStoreCombinationDto;
import com.mailvor.modules.activity.service.dto.MwStoreCombinationQueryCriteria;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author huangyu
* @date 2019-11-18
*/
@Api(tags = "商城:拼团管理")
@RestController
@RequestMapping("api")
public class StoreCombinationController {

    private final MwStoreCombinationService mwStoreCombinationService;
    private final MwShippingTemplatesService mwShippingTemplatesService;
    private final MwStoreProductRuleService mwStoreProductRuleService;
    private final MwStoreProductAttrResultService mwStoreProductAttrResultService;
    private final MwStoreProductAttrValueService storeProductAttrValueService;
    private final IGenerator generator;
    public StoreCombinationController(MwStoreCombinationService mwStoreCombinationService, MwShippingTemplatesService mwShippingTemplatesService, MwStoreProductRuleService mwStoreProductRuleService, MwStoreProductAttrResultService mwStoreProductAttrResultService, MwStoreProductAttrValueService storeProductAttrValueService, IGenerator generator) {
        this.mwStoreCombinationService = mwStoreCombinationService;
        this.mwShippingTemplatesService = mwShippingTemplatesService;
        this.mwStoreProductRuleService = mwStoreProductRuleService;
        this.mwStoreProductAttrResultService = mwStoreProductAttrResultService;
        this.storeProductAttrValueService = storeProductAttrValueService;
        this.generator = generator;
    }

    @Log("查询拼团")
    @ApiOperation(value = "查询拼团")
    @GetMapping(value = "/mwStoreCombination")
    @PreAuthorize("hasAnyRole('admin','MWSTORECOMBINATION_ALL','MWSTORECOMBINATION_SELECT')")
    public ResponseEntity getMwStoreCombinations(MwStoreCombinationQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreCombinationService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @Log("新增拼团")
    @ApiOperation(value = "新增拼团")
    @PostMapping(value = "/mwStoreCombination")
    @PreAuthorize("hasAnyRole('admin','MWSTORECOMBINATION_ALL','MWSTORECOMBINATION_EDIT')")
    public ResponseEntity add(@Validated @RequestBody MwStoreCombinationDto resources){
            return new ResponseEntity<>(mwStoreCombinationService.saveCombination(resources),HttpStatus.CREATED);
    }

    @ApiOperation(value = "获取商品信息")
    @GetMapping(value = "/mwStoreCombination/info/{id}")
    public ResponseEntity info(@PathVariable Long id){
        Map<String,Object> map = new LinkedHashMap<>(3);

        //运费模板
        List<MwShippingTemplates> shippingTemplatesList = mwShippingTemplatesService.list();
        map.put("tempList", shippingTemplatesList);

        //商品规格
        map.put("ruleList",mwStoreProductRuleService.list());


        if(id == 0){
            return new ResponseEntity<>(map,HttpStatus.OK);
        }

        //处理商品详情
        MwStoreCombination mwStoreCombination = mwStoreCombinationService.getById(id);
        MwStoreCombinationDto productDto = new MwStoreCombinationDto();
        BeanUtil.copyProperties(mwStoreCombination,productDto,"images");
        productDto.setSliderImage(Arrays.asList(mwStoreCombination.getImages().split(",")));
        MwStoreProductAttrResult storeProductAttrResult = mwStoreProductAttrResultService
                .getOne(Wrappers.<MwStoreProductAttrResult>lambdaQuery()
                        .eq(MwStoreProductAttrResult::getProductId,mwStoreCombination.getProductId()).last("limit 1"));
        JSONObject result = JSON.parseObject(storeProductAttrResult.getResult());
        List<MwStoreProductAttrValue> attrValues = storeProductAttrValueService.list(new LambdaQueryWrapper<MwStoreProductAttrValue>().eq(MwStoreProductAttrValue::getProductId, mwStoreCombination.getProductId()));
        List<ProductFormatDto> productFormatDtos =attrValues.stream().map(i ->{
            ProductFormatDto productFormatDto = new ProductFormatDto();
            BeanUtils.copyProperties(i,productFormatDto);
            productFormatDto.setPic(i.getImage());
            return productFormatDto;
        }).collect(Collectors.toList());
        if(SpecTypeEnum.TYPE_1.getValue().equals(mwStoreCombination.getSpecType())){
            productDto.setAttr(new ProductFormatDto());
            productDto.setAttrs(productFormatDtos);
            productDto.setItems(result.getObject("attr",ArrayList.class));
        }else{
            productFormat(productDto, result);
        }

        map.put("productInfo",productDto);

        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    /**
     * 获取商品属性
     * @param productDto
     * @param result
     */
    private void productFormat(MwStoreCombinationDto productDto, JSONObject result) {
        Map<String,Object> mapAttr = (Map<String,Object>) result.getObject("value",ArrayList.class).get(0);
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

    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @Log("修改拼团")
    @ApiOperation(value = "新增/修改拼团")
    @PutMapping(value = "/mwStoreCombination")
    @PreAuthorize("hasAnyRole('admin','MWSTORECOMBINATION_ALL','MWSTORECOMBINATION_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreCombination resources){
        if(ObjectUtil.isNull(resources.getId())){
            return new ResponseEntity<>(mwStoreCombinationService.save(resources),HttpStatus.CREATED);
        }else{
            mwStoreCombinationService.saveOrUpdate(resources);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

    }
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @ForbidSubmit
    @ApiOperation(value = "开启关闭")
    @PostMapping(value = "/mwStoreCombination/onsale/{id}")
    public ResponseEntity onSale(@PathVariable Long id,@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Integer status = jsonObject.getInteger("status");
        mwStoreCombinationService.onSale(id,status);
        return new ResponseEntity(HttpStatus.OK);
    }
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @ForbidSubmit
    @Log("删除拼团")
    @ApiOperation(value = "删除拼团")
    @DeleteMapping(value = "/mwStoreCombination/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTORECOMBINATION_ALL','MWSTORECOMBINATION_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        mwStoreCombinationService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
