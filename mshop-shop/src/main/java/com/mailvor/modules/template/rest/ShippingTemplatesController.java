/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.rest;

import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.template.domain.MwSystemCity;
import com.mailvor.modules.template.service.MwShippingTemplatesService;
import com.mailvor.modules.template.service.MwSystemCityService;
import com.mailvor.modules.template.service.dto.ShippingTemplatesDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesQueryCriteria;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
* @author huangyu
* @date 2020-06-29
*/
@AllArgsConstructor
@Api(tags = "商城：运费模板管理")
@RestController
@RequestMapping("/api/mwShippingTemplates")
public class ShippingTemplatesController {

    private final MwShippingTemplatesService mwShippingTemplatesService;
    private final MwSystemCityService mwSystemCityService;
    private final IGenerator generator;
    private final MwStoreProductService mwStoreProductService;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('admin','mwShippingTemplates:list')")
    public void download(HttpServletResponse response, MwShippingTemplatesQueryCriteria criteria) throws IOException {
        mwShippingTemplatesService.download(generator.convert(mwShippingTemplatesService.queryAll(criteria), MwShippingTemplatesDto.class), response);
    }

    @GetMapping
    @Log("查询运费模板")
    @ApiOperation("查询运费模板")
    @PreAuthorize("@el.check('admin','mwShippingTemplates:list')")
    public ResponseEntity<Object> getMwShippingTemplatess(MwShippingTemplatesQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwShippingTemplatesService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ForbidSubmit
    @PostMapping("/save/{id}")
    @Log("新增/保存运费模板")
    @ApiOperation("新增/保存运费模板")
    @PreAuthorize("hasAnyRole('admin','mwShippingTemplates:add','mwShippingTemplates:edit')")
    public ResponseEntity<Object> create(@PathVariable Integer id,
                                         @Validated @RequestBody ShippingTemplatesDto shippingTemplatesDto){
        mwShippingTemplatesService.addAndUpdate(id,shippingTemplatesDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @ForbidSubmit
    @Log("删除运费模板")
    @ApiOperation("删除运费模板")
    @PreAuthorize("@el.check('admin','mwShippingTemplates:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
        List<MwStoreProduct> productList = mwStoreProductService.list();
        Arrays.asList(ids).forEach(id->{
            for (MwStoreProduct mwStoreProduct : productList) {
                if(id.equals(mwStoreProduct.getTempId())){
                    throw new BadRequestException("运费模板存在商品关联，请删除对应商品");
                }
            }
            mwShippingTemplatesService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 获取城市列表
     */
    @Cacheable(cacheNames = ShopConstants.MSHOP_REDIS_SYS_CITY_KEY)
    @GetMapping("/citys")
    public ResponseEntity<Object> cityList()
    {
        List<MwSystemCity> cityList = mwSystemCityService.list(Wrappers.<MwSystemCity>lambdaQuery()
                .eq(MwSystemCity::getParentId,0));

        for (MwSystemCity systemCity : cityList){
            List<MwSystemCity> children = mwSystemCityService.list(Wrappers
                    .<MwSystemCity>lambdaQuery()
                    .eq(MwSystemCity::getParentId,systemCity.getCityId()));

            systemCity.setChildren(children);
        }

        return new ResponseEntity<>(cityList,HttpStatus.OK);
    }

}
