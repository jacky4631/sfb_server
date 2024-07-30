/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.rest;

import cn.hutool.core.bean.BeanUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.enums.SpecTypeEnum;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.category.domain.MwStoreCategory;
import com.mailvor.modules.category.service.MwStoreCategoryService;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.domain.MwStoreProductAttrResult;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.service.MwStoreProductAttrResultService;
import com.mailvor.modules.product.service.MwStoreProductAttrValueService;
import com.mailvor.modules.product.service.MwStoreProductRuleService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.service.dto.ProductDto;
import com.mailvor.modules.product.service.dto.ProductFormatDto;
import com.mailvor.modules.product.service.dto.StoreProductDto;
import com.mailvor.modules.product.service.dto.MwStoreProductQueryCriteria;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangyu
 * @date 2019-10-04
 */
@Api(tags = "商城:商品管理")
@RestController
@RequestMapping("api")
public class StoreProductController {

    private final MwStoreProductService mwStoreProductService;
    private final MwStoreCategoryService mwStoreCategoryService;
    private final MwShippingTemplatesService mwShippingTemplatesService;
    private final MwStoreProductRuleService mwStoreProductRuleService;
    private final MwStoreProductAttrResultService mwStoreProductAttrResultService;
    private final MwStoreProductAttrValueService storeProductAttrValueService;
    private final IGenerator generator;
    public StoreProductController(MwStoreProductService mwStoreProductService,
                                  MwStoreCategoryService mwStoreCategoryService,
                                  MwShippingTemplatesService mwShippingTemplatesService,
                                  MwStoreProductRuleService mwStoreProductRuleService,
                                  MwStoreProductAttrResultService mwStoreProductAttrResultService, MwStoreProductAttrValueService storeProductAttrValueService, IGenerator generator) {
        this.mwStoreProductService = mwStoreProductService;
        this.mwStoreCategoryService = mwStoreCategoryService;
        this.mwShippingTemplatesService = mwShippingTemplatesService;
        this.mwStoreProductRuleService = mwStoreProductRuleService;
        this.mwStoreProductAttrResultService = mwStoreProductAttrResultService;
        this.storeProductAttrValueService = storeProductAttrValueService;
        this.generator = generator;
    }

    @Log("查询商品")
    @ApiOperation(value = "查询商品")
    @GetMapping(value = "/mwStoreProduct")
    @PreAuthorize("hasAnyRole('admin','MWSTOREPRODUCT_ALL','MWSTOREPRODUCT_SELECT')")
    public ResponseEntity getMwStoreProducts(MwStoreProductQueryCriteria criteria, Pageable pageable){
        //商品分类
        List<MwStoreCategory> storeCategories = mwStoreCategoryService.lambdaQuery()
                .eq(MwStoreCategory::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .orderByAsc(MwStoreCategory::getPid)
                .list();
        List<Map<String,Object>> cateList = new ArrayList<>();
        Map<String, Object> queryAll = mwStoreProductService.queryAll(criteria, pageable);
        queryAll.put("cateList", this.makeCate(storeCategories,cateList,0,1));
        return new ResponseEntity<>(queryAll,HttpStatus.OK);
    }

    @ForbidSubmit
    @Log("新增/修改商品")
    @ApiOperation(value = "新增/修改商品")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PostMapping(value = "/mwStoreProduct/addOrSave")
    @PreAuthorize("hasAnyRole('admin','MWSTOREPRODUCT_ALL','MWSTOREPRODUCT_CREATE')")
    public ResponseEntity create(@Validated @RequestBody StoreProductDto storeProductDto){
        mwStoreProductService.insertAndEditMwStoreProduct(storeProductDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @ForbidSubmit
    @Log("删除商品")
    @ApiOperation(value = "删除商品")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @DeleteMapping(value = "/mwStoreProduct/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTOREPRODUCT_ALL','MWSTOREPRODUCT_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        mwStoreProductService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }



    @ApiOperation(value = "商品上架/下架")
    @CacheEvict(cacheNames = ShopConstants.MSHOP_REDIS_INDEX_KEY,allEntries = true)
    @PostMapping(value = "/mwStoreProduct/onsale/{id}")
    public ResponseEntity onSale(@PathVariable Long id,@RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Integer status = jsonObject.getInteger("status");
        mwStoreProductService.onSale(id,status);
        return new ResponseEntity(HttpStatus.OK);
    }
    @ApiOperation(value = "生成属性（添加活动产品专用）")
    @PostMapping(value = "/mwStoreProduct/isFormatAttrForActivity/{id}")
    public ResponseEntity isFormatAttrForActivity(@PathVariable Long id,@RequestBody String jsonStr){
        return new ResponseEntity<>(mwStoreProductService.getFormatAttr(id,jsonStr,true),HttpStatus.OK);
    }

    @ApiOperation(value = "生成属性")
    @PostMapping(value = "/mwStoreProduct/isFormatAttr/{id}")
    public ResponseEntity isFormatAttr(@PathVariable Long id,@RequestBody String jsonStr){
        return new ResponseEntity<>(mwStoreProductService.getFormatAttr(id,jsonStr,false),HttpStatus.OK);
    }



    @ApiOperation(value = "获取商品信息")
    @GetMapping(value = "/mwStoreProduct/info/{id}")
    public ResponseEntity info(@PathVariable Long id){
        Map<String,Object> map = new LinkedHashMap<>(3);

        //运费模板
        List<MwShippingTemplates> shippingTemplatesList = mwShippingTemplatesService.list();
        map.put("tempList", shippingTemplatesList);

        //商品分类
        List<MwStoreCategory> storeCategories = mwStoreCategoryService.lambdaQuery()
                .eq(MwStoreCategory::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .orderByAsc(MwStoreCategory::getPid)
                .list();

        List<Map<String,Object>> cateList = new ArrayList<>();
        map.put("cateList", this.makeCate(storeCategories,cateList,0,1));

        //商品规格
        map.put("ruleList",mwStoreProductRuleService.list());


        if(id == 0){
            return new ResponseEntity<>(map,HttpStatus.OK);
        }

        //处理商品详情
        MwStoreProduct mwStoreProduct = mwStoreProductService.getById(id);
        ProductDto productDto = new ProductDto();
        BeanUtil.copyProperties(mwStoreProduct,productDto,"sliderImage");
        productDto.setSliderImage(Arrays.asList(mwStoreProduct.getSliderImage().split(",")));
        MwStoreProductAttrResult storeProductAttrResult = mwStoreProductAttrResultService
                .getOne(Wrappers.<MwStoreProductAttrResult>lambdaQuery()
                        .eq(MwStoreProductAttrResult::getProductId,id).last("limit 1"));
        JSONObject result = JSON.parseObject(storeProductAttrResult.getResult());
        List<MwStoreProductAttrValue> attrValues = storeProductAttrValueService.list(new LambdaQueryWrapper<MwStoreProductAttrValue>().eq(MwStoreProductAttrValue::getProductId, mwStoreProduct.getId()));
        List<ProductFormatDto> productFormatDtos =attrValues.stream().map(i ->{
            ProductFormatDto productFormatDto = new ProductFormatDto();
            BeanUtils.copyProperties(i,productFormatDto);
            productFormatDto.setPic(i.getImage());
            return productFormatDto;
        }).collect(Collectors.toList());
        if(SpecTypeEnum.TYPE_1.getValue().equals(mwStoreProduct.getSpecType())){
            productDto.setAttr(new ProductFormatDto());
            productDto.setAttrs(productFormatDtos);
            productDto.setItems(result.getObject("attr",ArrayList.class));
        }else{

            productFromat(productDto, result);
        }

        map.put("productInfo",productDto);

        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    /**
     * 获取商品属性
     * @param productDto
     * @param result
     */
    private void productFromat(ProductDto productDto, JSONObject result) {
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
                .integral(mapAttr.get("integral") !=null ? Integer.valueOf(mapAttr.get("integral").toString()) : 0)
                .brokerage(Double.valueOf(mapAttr.get("brokerage").toString()))
                .brokerageTwo(Double.valueOf(mapAttr.get("brokerageTwo").toString()))
                .pinkPrice(Double.valueOf(mapAttr.get("pinkPrice").toString()))
                .pinkStock(Integer.valueOf(mapAttr.get("pinkStock").toString()))
                .seckillPrice(Double.valueOf(mapAttr.get("seckillPrice").toString()))
                .seckillStock(Integer.valueOf(mapAttr.get("seckillStock").toString()))
                .build();
        productDto.setAttr(productFormatDto);
    }


    /**
     *  分类递归
     * @param data 分类列表
     * @param pid 附件id
     * @param level d等级
     * @return list
     */
    private List<Map<String,Object>> makeCate(List<MwStoreCategory> data, List<Map<String,Object>> cateList, int pid, int level)
    {
        String html = "|-----";
        String newHtml = "";
        List<MwStoreCategory> storeCategories = mwStoreCategoryService.lambdaQuery().eq(MwStoreCategory::getPid, 0).list();

        for (int i = 0; i < data.size(); i++) {
            MwStoreCategory storeCategory = data.get(i);
            int catePid =  storeCategory.getPid();
            Map<String,Object> map = new HashMap<>();
            if(catePid == pid){
                newHtml = String.join("", Collections.nCopies(level,html));
                map.put("value",storeCategory.getId());
                map.put("label",newHtml + storeCategory.getCateName());
                if(storeCategory.getPid() == 0){
                    map.put("disabled",0);
                }else{
                    map.put("disabled",1);
                }
                cateList.add(map);
                data.remove(i);

                i--;
                if(storeCategory.getPid() > 0){
                    this.makeCate(data,cateList,storeCategory.getPid(),level);
                }else{
                    this.makeCate(data,cateList,storeCategory.getId(),level + 1);
                }

            }
        }


        return cateList;
    }

}
