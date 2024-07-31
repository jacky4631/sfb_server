/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.rest;

import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.enums.CouponEnum;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.activity.domain.MwStoreCoupon;
import com.mailvor.modules.activity.service.MwStoreCouponService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponQueryCriteria;
import com.mailvor.modules.aop.ForbidSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

/**
* @author huangyu
* @date 2019-11-09
*/
@Api(tags = "商城：优惠券管理")
@RestController
@RequestMapping("api")
public class StoreCouponController {

    private final MwStoreCouponService mwStoreCouponService;

    public StoreCouponController(MwStoreCouponService mwStoreCouponService) {
        this.mwStoreCouponService = mwStoreCouponService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwStoreCoupon")
    @PreAuthorize("@el.check('admin','MWSTORECOUPON_ALL','MWSTORECOUPON_SELECT')")
    public ResponseEntity getMwStoreCoupons(MwStoreCouponQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreCouponService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增")
    @ApiOperation(value = "新增")
    @PostMapping(value = "/mwStoreCoupon")
    @PreAuthorize("@el.check('admin','MWSTORECOUPON_ALL','MWSTORECOUPON_CREATE')")
    public ResponseEntity create(@Validated @RequestBody MwStoreCoupon resources){
        if(CouponEnum.TYPE_1.getValue().equals(resources.getType())
                && StrUtil.isEmpty(resources.getProductId())){
            throw new MshopException("请选择商品");
        }
        if(resources.getCouponPrice().compareTo(resources.getUseMinPrice()) >= 0) {
            throw new MshopException("优惠券金额不能高于最低消费金额");
        }
        return new ResponseEntity<>(mwStoreCouponService.save(resources),HttpStatus.CREATED);
    }

    @Log("修改")
    @ApiOperation(value = "修改")
    @PutMapping(value = "/mwStoreCoupon")
    @PreAuthorize("@el.check('admin','MWSTORECOUPON_ALL','MWSTORECOUPON_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreCoupon resources){
        if(CouponEnum.TYPE_1.getValue().equals(resources.getType())
                && StrUtil.isEmpty(resources.getProductId())){
            throw new MshopException("请选择商品");
        }
        if(resources.getCouponPrice().compareTo(resources.getUseMinPrice()) >= 0) {
            throw new MshopException("优惠券金额不能高于最低消费金额");
        }
        mwStoreCouponService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwStoreCoupon/{id}")
    @PreAuthorize("@el.check('admin','MWSTORECOUPON_ALL','MWSTORECOUPON_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id){
        mwStoreCouponService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
