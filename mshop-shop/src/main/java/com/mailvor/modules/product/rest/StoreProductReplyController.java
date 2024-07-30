/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.rest;

import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.product.domain.MwStoreProductReply;
import com.mailvor.modules.product.service.MwStoreProductReplyService;
import com.mailvor.modules.product.service.dto.MwStoreProductReplyQueryCriteria;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
* @author huangyu
* @date 2019-11-03
*/
@Api(tags = "商城:评论管理")
@RestController
@RequestMapping("api")
public class StoreProductReplyController {


    private final MwStoreProductReplyService mwStoreProductReplyService;

    public StoreProductReplyController(MwStoreProductReplyService mwStoreProductReplyService) {
        this.mwStoreProductReplyService = mwStoreProductReplyService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/mwStoreProductReply")
    @PreAuthorize("hasAnyRole('admin','MWSTOREPRODUCTREPLY_ALL','MWSTOREPRODUCTREPLY_SELECT')")
    public ResponseEntity getMwStoreProductReplys(MwStoreProductReplyQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(mwStoreProductReplyService.queryAll(criteria,pageable),HttpStatus.OK);
    }



    @Log("修改")
    @ApiOperation(value = "修改")
    @PutMapping(value = "/mwStoreProductReply")
    @PreAuthorize("hasAnyRole('admin','MWSTOREPRODUCTREPLY_ALL','MWSTOREPRODUCTREPLY_EDIT')")
    public ResponseEntity update(@Validated @RequestBody MwStoreProductReply resources){
        resources.setMerchantReplyTime(new Date());
        resources.setIsReply(ShopCommonEnum.REPLY_1.getValue());
        mwStoreProductReplyService.updateById(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ForbidSubmit
    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/mwStoreProductReply/{id}")
    @PreAuthorize("hasAnyRole('admin','MWSTOREPRODUCTREPLY_ALL','MWSTOREPRODUCTREPLY_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        mwStoreProductReplyService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
